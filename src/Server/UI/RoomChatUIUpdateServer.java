package Server.UI;

import Network.DataTranslator;
import Network.ServerName;
import Server.Data.GameRoom;
import Server.Data.Player;
import Server.Manager.GameRoomManager;
import Server.Manager.PlayerManager;
import Server.ServerTemplate;
import Server.ServerThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class RoomChatUIUpdateServer extends ServerTemplate {

    public RoomChatUIUpdateServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient() {
        RoomChatUIUpdateServerThread roomChatUIUpdateServerThread = new RoomChatUIUpdateServerThread(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); //플레이어 ID값으로 Player 객체를 찾음
        player.addServer(ServerName.ROOM_CHAT_UI_UPDATE_SERVER, dataTranslator); //찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가함
        roomChatUIUpdateServerThread.start(); //서버 스레드를 실행 함.

    }
}

class RoomChatUIUpdateServerThread extends ServerThread {


    public RoomChatUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {

        GameRoomManager gameRoomManager = GameRoomManager.getInstance();

        while (true) {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            /* 요청을 받아 처리 */
            Map<String, Object> request = checkexit();
            if(request == null) break; // 클라이언트가 게임 종료한 경우 루프 빠져나간다.

            String command = (String) request.get("command");
            if (command.equals("방 채팅")) {

                Map<String, Object> response = new HashMap<>();

                long roomId = (long) request.get("roomId");

                GameRoom gameRoom = gameRoomManager.getGameRoom(roomId);

                response.put("command", "방 채팅 업데이트");
                response.put("message", request.get("message"));

                List<Player> players = gameRoom.getPlayers();

                for (Player player : players) {
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ROOM_CHAT_UI_UPDATE_SERVER);
                    playerDataTranslator.sendData(response);
                }
            } else if (command.equals("방 나가기")) {
                Map<String, Object> response = new HashMap<>();
                response.put("command", "방 채팅 지우기");
                dataTranslator.sendData(response);
            }

        }

    }



}