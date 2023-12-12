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

public class RoomControlUIUpdateServer extends ServerTemplate {
    public RoomControlUIUpdateServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient() {
        RoomControlUIUpdateServerThread roomControlUIUpdateServerThread = new RoomControlUIUpdateServerThread(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); //플레이어 ID값으로 Player 객체를 찾음
        player.addServer(ServerName.ROOM_CONTROL_UI_UPDATE_SERVER, dataTranslator); //찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가함.
        roomControlUIUpdateServerThread.start();
    }
}

class RoomControlUIUpdateServerThread extends ServerThread {

    public RoomControlUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
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
            if (command.equals("방 입장") || command.equals("게임 준비") || command.equals("게임 준비 미완료") | command.equals("방 나가기")) {

                long roomId = (long) request.get("roomId");

                GameRoom gameRoom = gameRoomManager.getGameRoom(roomId);

                Map<String, Object> response = new HashMap<>();
                response.put("command", "게임 시작 UI 업데이트");

                if (command.equals("방 나가기") && gameRoom == null) {
                    continue;
                }

                if (gameRoom.isAllReady()) {
                    response.put("result", "OK");
                }else {
                    response.put("result", "FAIL");
                }

                List<Player> players = gameRoom.getPlayers();

                for (Player player : players) {
                    if (player == gameRoom.getLeader()) {
                        DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ROOM_CONTROL_UI_UPDATE_SERVER);
                        playerDataTranslator.sendData(response);
                    }
                }
            } else if (command.equals("게임 시작")) { // 버튼 초기화를 위함
                long roomId = (long) request.get("roomId"); // 클라이언트가 보낸 방 id 가져옴.
                GameRoom gameRoom = gameRoomManager.getGameRoom(roomId);
                Map<String, Object> response = new HashMap<>();
                response.put("command", "init_btn");

                List<Player> players = gameRoom.getPlayers();

                for(Player player : players) {
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ROOM_CONTROL_UI_UPDATE_SERVER);
                    playerDataTranslator.sendData(response);
                }

            }
        }
    }
}