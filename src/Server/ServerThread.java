package Server;

import Network.DataTranslator;
import Server.Data.GameRoom;
import Server.Data.GameWaitingRoom;
import Server.Data.Player;
import Server.Manager.GameRoomManager;
import Server.Manager.GameWaitingRoomManager;
import Server.Manager.PlayerManager;

import java.util.Map;
import java.util.concurrent.CyclicBarrier;

public abstract class ServerThread extends Thread {

    protected DataTranslator dataTranslator;
    protected CyclicBarrier cyclicBarrier;
    protected volatile boolean running = true;  // 쓰레드 제어를 위한 변수

    public ServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        this.dataTranslator = dataTranslator;
        this.cyclicBarrier = cyclicBarrier;
    }

    public Map<String, Object> checkexit(){ // 클라이언트가 게임을 종료했는지 확인하는 메서드
        Map<String, Object> request = dataTranslator.receiveData();

        if(request == null) return null; // 클라이언트 측 소켓이 close된 경우 루프를 빠져나간다.

        String command = (String) request.get("command");
        if (command.equals("exit") && running == true) {
            running = false;  // "게임 종료" 명령이 들어오면 running을 false로 설정하여 while문을 종료
            if(running == true) dataTranslator.closeSocket();  // socket 연결 종료
            long playerId = (long) request.get("playerId");

            // 플레이어를 찾아 게임 방에서 나가게 하고 플레이어 데이터 삭제함
            Player player = PlayerManager.getInstance().getPlayer(playerId);
            GameRoom gameRoom = GameRoomManager.getInstance().getGameRoom(player);
            if(gameRoom != null) gameRoom.leave(player);

            GameWaitingRoom gameWaitingRoom = GameWaitingRoomManager.getInstance().getGameWaitingRoom();
            gameWaitingRoom.leave(player);

            System.out.println("SERVER: DELETE PLAYER ID = " + playerId);
            PlayerManager.getInstance().removePlayer(playerId);

            return null;
        }

        return request;
    }
}
