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

    private GameRoomManager gameRoomManager = GameRoomManager.getInstance();

    public RoomControlUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    /**
     * 명령어에 따라 플레이어에게 응답을 돌려준다.
     */
    @Override
    public void sendResponse(Map<String, Object> request, String command) {

        if (command.equals("room_enter") || command.equals("game_ready") || command.equals("game_not_ready") | command.equals("room_exit")) {

            long roomId = (long) request.get("roomId");

            GameRoom gameRoom = gameRoomManager.getGameRoom(roomId);

            Map<String, Object> response = new HashMap<>();
            response.put("command", "game_start_update");

            if (command.equals("room_exit") && gameRoom == null) {
                return;
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
        } else if (command.equals("game_start")) { // 버튼 초기화를 위함
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