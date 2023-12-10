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


public class PlayerStatusUIUpdateServer extends ServerTemplate {

    public PlayerStatusUIUpdateServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient() {
        PlayerStatusUIUpdateServerThread playerStatusUIUpdateServerThread = new PlayerStatusUIUpdateServerThread(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); //플레이어 ID값으로 Player 객체를 찾음
        player.addServer(ServerName.PLAYER_STATUS_UI_UPDATE_SERVER, dataTranslator); //찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가함
        playerStatusUIUpdateServerThread.start(); //서버 스레드를 실행 함.

    }
}

class PlayerStatusUIUpdateServerThread extends ServerThread {


    public PlayerStatusUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {

        GameRoomManager gameRoomManager = GameRoomManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();

        while (true) {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            System.out.println("PlayerStatusUIUpdateServerThread 실행 " + this);
            Map<String, Object> request = dataTranslator.receiveData();
            String command = (String) request.get("command");

            if (command.equals("방 생성") || command.equals("게임 준비") || command.equals("게임 준비 미완료") || command.equals("방 입장") || command.equals("방 나가기")) {

                Map<String, Object> response = new HashMap<>();

                GameRoom gameRoom = null;

                if (command.equals("방 생성")) {
                    long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
                    Player currentPlayer = playerManager.getPlayer(playerId); //현재 플레이어를 찾음.

                    gameRoom = gameRoomManager.getGameRoom(currentPlayer); //현재 플레이어가 속한 GameRoom 객체를 찾음

                }else {
                    long roomId = (long) request.get("roomId"); //roomId 값으로 현재 플레이어가 속한 GameRoom 객체를 찾음
                    gameRoom = gameRoomManager.getGameRoom(roomId);
                }

                if (command.equals("방 나가기") && gameRoom == null) { //방 나가기 명령어를 처리하는 도중 GameRoom 객체 (방에 1명이 남아 있는 상태에서, 나갔을 때 해당 GameRoom 객체는 null 값이므로 PlayerStatusUIUpdate 작업을 하지 않아도 됨)
                    continue;
                }

                response.put("command", "플레이어 정보 업데이트");
                response.put("GameRoom", gameRoom); //GameRoom 객체 정보를 response 매핑 객체에 추가

                List<Player> players = gameRoom.getPlayers();

                for(Player player : players) { //현재 플레이어가 속한 게임 방에 필요한 데이터를 보냅니다.
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.PLAYER_STATUS_UI_UPDATE_SERVER);
                    playerDataTranslator.sendData(response);
                }

            }

        }

    }



}