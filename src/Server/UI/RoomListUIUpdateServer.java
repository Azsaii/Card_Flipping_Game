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

    private GameRoomManager gameRoomManager = GameRoomManager.getInstance();
    private GameWaitingRoomManager gameWaitingRoomManager = GameWaitingRoomManager.getInstance();
    private PlayerManager playerManager = PlayerManager.getInstance();

    public RoomListUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    /**
     * 명령어에 따라 플레이어에게 응답을 돌려준다.
     */
    @Override
    public void sendResponse(Map<String, Object> request, String command) {

        if (command.equals("room_add") || command.equals("room_enter") || command.equals("room_exit")) {

            Map<String, Object> response = new HashMap<>();
            response.put("command", "room_list_update");
            response.put("roomList", gameRoomManager.getRoomsList());

            GameWaitingRoom gameWaitingRoom = gameWaitingRoomManager.getGameWaitingRoom();

            List<Player> players = gameWaitingRoom.getPlayers();

            for (Player player : players) {  //대기 방에 있는 모든 플레이어에 방 목록을 데이터를 보냅니다.
                DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ROOM_LIST_UI_UPDATE_SERVER);
                playerDataTranslator.sendData(response);
            }
        } else if (command.equals("game_enter")) {
            Map<String, Object> response = new HashMap<>();
            response.put("command", "room_list_update");
            response.put("roomList", gameRoomManager.getRoomsList());

            long playerId = (long) request.get("playerId");

            Player player = playerManager.getPlayer(playerId);

            DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ROOM_LIST_UI_UPDATE_SERVER);
            playerDataTranslator.sendData(response);
        }
    }
}
