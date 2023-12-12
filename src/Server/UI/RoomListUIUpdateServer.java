package Server.UI;

import Network.DataTranslator;
import Network.ServerName;
import Server.Data.GameRoom;
import Server.Data.GameWaitingRoom;
import Server.Data.Player;
import Server.Manager.GameRoomManager;
import Server.Manager.GameWaitingRoomManager;
import Server.Manager.PlayerManager;
import Server.ServerTemplate;
import Server.ServerThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class RoomListUIUpdateServer extends ServerTemplate {

    public RoomListUIUpdateServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient(){
        RoomListUIUpdateServerThread roomListUIUpdateServerThread = new RoomListUIUpdateServerThread(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); //플레이어 ID값으로 Player 객체를 찾음
        player.addServer(ServerName.ROOM_LIST_UI_UPDATE_SERVER, dataTranslator); //찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가함.
        roomListUIUpdateServerThread.start(); //서버 스레드를 실행 함.

    }
}

class RoomListUIUpdateServerThread extends ServerThread {

    public RoomListUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {

        GameRoomManager gameRoomManager = GameRoomManager.getInstance();
        GameWaitingRoomManager gameWaitingRoomManager = GameWaitingRoomManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();

        while (true) { //각 플레이어들이 보낸 명령어를 처리하는 구간
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            /* 요청을 받아 처리 */
            Map<String, Object> request = checkexit();
            if(request == null) break; // 클라이언트가 게임 종료한 경우 루프 빠져나간다.

            String command = (String) request.get("command");
            if (command.equals("방 생성") || command.equals("방 입장") || command.equals("방 나가기")) {

                Map<String, Object> response = new HashMap<>();
                response.put("command", "방 리스트 업데이트");
                response.put("roomList", gameRoomManager.getRoomsList());

                GameWaitingRoom gameWaitingRoom = gameWaitingRoomManager.getGameWaitingRoom();

                List<Player> players = gameWaitingRoom.getPlayers();

                for (Player player : players) {  //대기 방에 있는 모든 플레이어에 방 목록을 데이터를 보냅니다.
                    System.out.println("playerId = " + player.getId());
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ROOM_LIST_UI_UPDATE_SERVER);
                    playerDataTranslator.sendData(response);
                }
            } else if (command.equals("게임 입장")) {
                Map<String, Object> response = new HashMap<>();
                response.put("command", "방 리스트 업데이트");
                response.put("roomList", gameRoomManager.getRoomsList());

                long playerId = (long) request.get("playerId");

                Player player = playerManager.getPlayer(playerId);

                DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ROOM_LIST_UI_UPDATE_SERVER);
                playerDataTranslator.sendData(response);
            }

        }
    }
}
