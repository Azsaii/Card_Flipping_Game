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
import java.util.concurrent.CyclicBarrier;

/**
 * 게임 채팅 담당 서버
 */
public class GameChatServer extends ServerTemplate {

    public GameChatServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient() {
        GameChatServerThreadTemplate gameChatServerThread = new GameChatServerThreadTemplate(dataTranslator, cyclicBarrier);

        Player player = PlayerManager.getInstance().getPlayer(playerId); //플레이어 ID값으로 Player 객체를 찾음
        player.addServer(ServerName.GAME_CHAT_UI_UPDATE_SERVER, dataTranslator); //찾은 Player 객체에 현재 서버에서 생성한 DataTranslator 객체를 추가함
        gameChatServerThread.start(); //서버 스레드를 실행 함.

    }
}

class GameChatServerThreadTemplate extends ServerThread {

    private GameRoomManager gameRoomManager = GameRoomManager.getInstance();

    public GameChatServerThreadTemplate(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    /**
     * 명령어에 따라 플레이어에게 응답을 돌려준다.
     */
    @Override
    public void sendResponse(Map<String, Object> request, String command) {

        Map<String, Object> response = new HashMap<>(); // 응답 객체 생성
        if (command.equals("game_chat")) {
            long roomId = (long) request.get("roomId");
            GameRoom gameRoom = gameRoomManager.getGameRoom(roomId);

            response.put("command", "game_chat");
            response.put("message", request.get("message"));

            List<Player> players = gameRoom.getPlayers();

            for (Player player : players) {
                DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.GAME_CHAT_UI_UPDATE_SERVER);
                playerDataTranslator.sendData(response);
            }
        } else if(command.equals("game_exit")) {
            long playerId = (long) request.get("playerId");

            response.put("command", "game_exit");
            Player player = PlayerManager.getInstance().getPlayer(playerId);
            DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.GAME_CHAT_UI_UPDATE_SERVER);
            playerDataTranslator.sendData(response);
        }
    }
}