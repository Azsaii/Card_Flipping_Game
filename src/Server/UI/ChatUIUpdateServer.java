package Server.UI;

import Network.DataTranslator;
import Network.ServerName;
import Server.Data.Player;
import Server.Manager.GameWaitingRoomManager;
import Server.Manager.PlayerManager;
import Server.ServerTemplate;
import Server.ServerThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ChatUIUpdateServer extends ServerTemplate {

    public ChatUIUpdateServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient() {
        ChatUIUpdateServerThread chatUIUpdateServerThread = new ChatUIUpdateServerThread(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); //플레이어 ID값으로 Player 객체를 찾음
        player.addServer(ServerName.CHAT_UI_UPDATE_SERVER, dataTranslator); //찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가함
        chatUIUpdateServerThread.start(); //서버 스레드를 실행 함.

    }
}


class ChatUIUpdateServerThread extends ServerThread {

    public ChatUIUpdateServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }


    @Override
    public void run() {
        GameWaitingRoomManager gameWaitingRoomManager = GameWaitingRoomManager.getInstance();
        while (true) {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            System.out.println("ChatUIUpdateServerThread 실행 " + this);

            Map<String, Object> request = dataTranslator.receiveData();
            String command = (String) request.get("command");

            if (command.equals("전체 채팅")) {

                Map<String, Object> response = new HashMap<>();


                response.put("command", "전체 채팅 업데이트");
                response.put("message", request.get("message"));

                List<Player> players = gameWaitingRoomManager.getGameWaitingRoom().getPlayers();

                for (Player player : players) {
                    DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.CHAT_UI_UPDATE_SERVER);
                    playerDataTranslator.sendData(response);
                }
            }

        }
    }
}
