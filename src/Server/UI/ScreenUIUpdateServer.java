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


public class ScreenUIUpdateServer extends ServerTemplate {

    public ScreenUIUpdateServer(int port) {
        super(port);
    }


    @Override
    protected void handleClient() {

        ScreenUIUpdateServerThread screenUIUpdateServerThread = new ScreenUIUpdateServerThread(dataTranslator, cyclicBarrier);
        Player player = PlayerManager.getInstance().getPlayer(playerId); //플레이어 ID값으로 Player 객체를 찾음
        player.addServer(ServerName.SCREEN_UI_UPDATE_SERVER, dataTranslator); //찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가함.
        screenUIUpdateServerThread.start(); //서버 스레드를 실행 함.

    }
}


class ScreenUIUpdateServerThread extends ServerThread {


    public ScreenUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {

        GameRoomManager gameRoomManager = GameRoomManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();

        while (true) { //각 플레이어들이 보낸 명령어를 처리하는 구간
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            System.out.println("ScreenUIUpdateServerThread 실행" + this);

            Map<String, Object> request = dataTranslator.receiveData();
            String command = (String) request.get("command");

            if (command.equals("방 생성") || command.equals("방 입장")) {

                if (command.equals("방 입장")) {
                    long playerId = (long) request.get("playerId"); // 클라이언트가 보낸 플레이어 ID를 가져옴.
                    long roomId = (long) request.get("roomId"); // 클라이언트가 보낸 방 ID를 가져옴.

                    Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음.

                    if(!(gameRoomManager.isPlayerInRoom(roomId, currentPlayer))) { //현재 플레이어가 해당 ID 값을 가진 게임 방에 입장 되어 있지 않다면 화면 전환을 취소함
                        continue;
                    }
                }

                Map<String, Object> response = new HashMap<>();
                response.put("command", "방 화면 전환");

                dataTranslator.sendData(response);
            }else if (command.equals("방 나가기")) {
                Map<String, Object> response = new HashMap<>();
                response.put("command", "메인 화면 전환");

                dataTranslator.sendData(response);

            }else if (command.equals("게임 시작")) {
                long playerId = (long) request.get("playerId"); // 클라이언트가 보낸 플레이어 id 가져옴
                long roomId = (long) request.get("roomId"); // 클라이언트가 보낸 방 id 가져옴.

                Player sendPlayer = playerManager.getPlayer(playerId);
                GameRoom gameRoom = gameRoomManager.getGameRoom(roomId);
                sendPlayer.setGameRoom(gameRoom);

                Map<String, Object> response = new HashMap<>();
                response.put("command", "게임 화면 전환");
                response.put("player", sendPlayer); // 클라이언트의 플레이어 객체 전달
                response.put("gameRoom", gameRoom); // 클라이언트의 게임 방 객체 전달

                List<Player> players = gameRoom.getPlayers();

                for(Player player : players) {
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.SCREEN_UI_UPDATE_SERVER);
                    playerDataTranslator.sendData(response);
                }

            }

        }
    }
}

