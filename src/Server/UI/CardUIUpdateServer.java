package Server.UI;

import Network.DataTranslator;
import Network.ServerName;
import Server.Data.GameRoom;
import Server.Data.Player;
import Server.GameServer;
import Server.Manager.PlayerManager;
import Server.ServerTemplate;
import Server.ServerThread;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 카드 데이터 처리용 서버입니다.
 */
public class CardUIUpdateServer extends ServerTemplate {

    public CardUIUpdateServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient() {
        CardUIUpdateServerThread gameDataStatusUIUpdateServerThread = new CardUIUpdateServerThread(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); // Player 객체 찾기
        player.addServer(ServerName.CARD_UI_UPDATE_SEREVR, dataTranslator); // 찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가
        gameDataStatusUIUpdateServerThread.start(); // 서버 스레드 실행

    }
}

/**
 * 플레이어 1명의 카드/아이템 입출력 담당 스레드
 * 플레이어가 보낸 카드데이터를 같은 방의 다른 플레이어에게 전송한다.
 */
class CardUIUpdateServerThread extends ServerThread {

    public static final String command_card = "CARD_UPDATE";
    // public static final String command_item = "ITEM_UPDATE";

    public CardUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {
        PlayerManager playerManager = PlayerManager.getInstance();

        while(true) {

            /* 메시지 인터셉트 방지 */
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            /* 요청을 받아 처리 */
            Map<String, Object> request = dataTranslator.receiveData();
            String command = (String) request.get("command");

            System.out.println("GameDataStatusUIUpdateServerThread 실행");

            /* 카드 상태 업데이트 관련 요청 처리 */
            if (command.equals(command_card)) {

                Map<String, Object> response = new HashMap<>(); // 요청에 대한 응답 객체

                long playerId = (long) request.get("playerId"); // 클라이언트가 보낸 플레이어 ID 파싱
                Player currentPlayer = playerManager.getPlayer(playerId); //현재 플레이어를 찾기
                GameRoom gameRoom = currentPlayer.getGameRoom(); // 현재 플레이어가 속한 GameRoom 객체 얻기

                /* 응답 객체에 데이터 추가 */
                response.put("command", command_card); // 카드 상태 업데이트 명령 추가
                response.put("senderId", playerId); // 카드 뒤집은 플레이어 ID 추가
                response.put("location", request.get("location")); // 뒤집은 카드 좌표 데이터 추가

                /* 상대에게 카드 업데이트 메시지 전송 */
                List<Player> players = gameRoom.getPlayers();
                for(Player player : players) { ////현재 플레이어가 속한 게임 방에 필요한 데이터를 보냅니다.
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.CARD_UI_UPDATE_SEREVR);
                    playerDataTranslator.sendData(response);
                }
            }
        }
    }
}