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

public class ItemUIUpdateServer extends ServerTemplate {

    public ItemUIUpdateServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient() {
        ItemUIUpdateServerThread itemUIUpdateServerThread = new ItemUIUpdateServerThread(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); // Player 객체 찾기
        player.addServer(ServerName.ITEM_UI_UPDATE_SERVER, dataTranslator); // 찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가
        itemUIUpdateServerThread.start(); // 서버 스레드 실행

    }
}
/**
 * 플레이어 1명의 아이템 입출력 담당 스레드
 * 플레이어가 보낸 아이템 관련 데이터를 같은 방의 다른 플레이어에게 전송
 * 랜덤 뒤집개: boolean 배열을 받아 상대에게 전송
 * 황금 뒤집개, 더블 이벤트, 아이스 에이지는 아이템 사용자 id만 전송
 */
class ItemUIUpdateServerThread extends ServerThread {

    static final String RANDOM_FLIP = "RANDOM_FLIP";
    static final String BLACK_FOG = "BLACK_FOG";
    static final String GOLD_FLIP = "GOLD_FLIP";
    static final String DOUBLE_EVENT = "DOUBLE_EVENT";
    static final String ABSORB = "ABSORB";
    static final String ICE_AGE = "ICE_AGE";
    public ItemUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
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
            Map<String, Object> request = checkexit();
            if(request == null) break; // 클라이언트가 게임 종료한 경우 루프 빠져나간다.

            String command = (String) request.get("command");
            if(command.equals(RANDOM_FLIP) || command.equals(BLACK_FOG) || command.equals(GOLD_FLIP) || command.equals(DOUBLE_EVENT) || command.equals(ABSORB) ||command.equals(ICE_AGE)){

                Map<String, Object> response = new HashMap<>(); // 요청에 대한 응답 객체

                System.out.println("서버: 아이템 사용됨 - " + command);
                long playerId = (long) request.get("playerId"); // 클라이언트가 보낸 플레이어 ID 파싱
                Player currentPlayer = playerManager.getPlayer(playerId); //현재 플레이어를 찾기
                GameRoom gameRoom = GameRoomManager.getInstance().getGameRoom(currentPlayer);

                /* 아이템 종류 별로 응답 객체에 데이터 추가 */
                response.put("command", command); // 카드 상태 업데이트 명령 추가
                response.put("senderId", playerId); // 카드 뒤집은 플레이어 ID 추가

                if(command.equals(RANDOM_FLIP)) response.put("randomCardArray", request.get("randomCardArray"));

                /* 상대에게 카드 업데이트 메시지 전송 */
                List<Player> players = gameRoom.getPlayers();
                for(Player player : players) { ////현재 플레이어가 속한 게임 방에 필요한 데이터를 보냅니다.
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.ITEM_UI_UPDATE_SERVER);
                    playerDataTranslator.sendData(response);
                }
            }
        }
    }
}
