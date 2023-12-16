package Server.Data;

import Server.Manager.GameWaitingRoomManager;
import Server.Manager.PlayerManager;
import Server.ServerTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

/**
 * ServerTemplate 을 상속하는 서버들을 실행시키는 서버 클래스
 */
public  class GameDataInitialServer extends ServerTemplate {

    private List<ServerTemplate> serverTemplates = new ArrayList<>(); //나머지 서버을 담기 위한 ArrayList

    public GameDataInitialServer(int port) {
        super(port);
    }

    public void attachTemplateServer(ServerTemplate serverTemplate) { //서버를 ArrayList에 추가
        serverTemplates.add(serverTemplate);
    }

    public void notify(long playerId, CyclicBarrier cyclicBarrier) { //ArrayList에 담긴 서버의 플레이어 ID와 CyclicBarrier 객체 설정
        for (ServerTemplate serverTemplate : serverTemplates) {
            serverTemplate.update(playerId, cyclicBarrier);
        }
    }

    public void startAllServer() {  //ArrayList에 담긴 서버들의 스레드을 시작하기 위한 코드
        for (ServerTemplate serverTemplate : serverTemplates) {
            new Thread(serverTemplate).start();
        }
    }

    @Override
    protected void handleClient() {
        long playerId = PlayerManager.getInstance().getNextPlayerId(); // 플레이어 ID 값을 PlayerManager 에게서 얻음.

        Player player = new Player(playerId, "플레이어 " + playerId); // 플레이어 객체 생성.

        PlayerManager.getInstance().addPlayer(playerId, player); // 플레이어 객체를 PlayerManager 에 추가

        GameWaitingRoomManager.getInstance().enter(player); // 처음 게임에 입장한 플레이어는 게임 대기 방 객체에 입장

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10); // 스레드의 수에 맞게 CyclicBarrier 설정

        notify(playerId, cyclicBarrier); // 모든 서버에 플레이어 ID 값와 CyclicBarrier 객체 설정

        // 클라이언트 쪽으로 플레이어 ID 값을 보냄
        Map<String, Object> request = new HashMap<>();
        request.put("playerId", playerId);

        dataTranslator.sendData(request);  // 클라이언트쪽으로 request 매팅 데이터를 보냄
        dataTranslator = null; // 더 이상 사용하지 않으므로 제거
    }
}