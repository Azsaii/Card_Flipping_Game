package Server.Data;

import Network.DataTranslator;
import Network.ServerName;
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

public class GameRoomDataServer extends ServerTemplate {

    public GameRoomDataServer(int port) {
        super(port);
    }

    @Override
    protected void handleClient()  {
        GameRoomDataServerThread gameDataServer = new GameRoomDataServerThread(dataTranslator, cyclicBarrier);
        gameDataServer.start();
    }
}

class GameRoomDataServerThread extends ServerThread {

    private GameRoomManager gameRoomManager = GameRoomManager.getInstance();
    private GameWaitingRoomManager gameWaitingRoomManager = GameWaitingRoomManager.getInstance();
    private PlayerManager playerManager = PlayerManager.getInstance();

    public GameRoomDataServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {

        while (true) { //각 플레이어들이 보낸 명령어를 처리하는 구간

            /* 요청을 받아 처리 */
            Map<String, Object> request = checkexit();
            if(request == null) break; // 클라이언트가 게임 종료한 경우 루프 빠져나간다.

            String command = (String) request.get("command");
            sendResponse(request, command);

            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 명령어에 따라 플레이어에게 응답을 돌려준다.
     */
    @Override
    public void sendResponse(Map<String, Object> request, String command) {

        Map<String, Object> response = new HashMap<>();

        if (command.equals("room_add")) {

            long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
            Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음.
            gameWaitingRoomManager.leave(currentPlayer); //현재 플레이어를 대기 방에서 나가게 함.
            GameRoom gameRoom = gameRoomManager.createRoom(currentPlayer); //현재 플레이어는 게임 방을 생성 하고 게임 방에 들어감.

            response.put("roomId", gameRoom.getId()); //현재 플레이어한테 생성된 GameRoom 객체의 ID를 보내줌
            dataTranslator.sendData(response);

        }else if(command.equals("room_enter")) {

            long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
            long roomId = (long) request.get("roomId"); //클라이언트가 보낸 방 ID를 가져옴.

            Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음.

            if(gameRoomManager.enter(roomId, currentPlayer)) { //현재 플레이어를 해당 ID 값을 가진 게임 방에 입장 시킴.
                gameWaitingRoomManager.leave(currentPlayer); //만약 성공적으로 게임 방에 입장 했다면 현재 플레이어를 대기 방에서 나가게 함.
                response.put("result", "OK");
                dataTranslator.sendData(response);
            }else {
                System.out.println("방이 가득 찼습니다."); //인원 초과 시 출력
                response.put("result", "FAIL");
                dataTranslator.sendData(response);
            }
        } else if (command.equals("game_ready") || command.equals("game_not_ready")) {

            long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
            Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음

            if(command.equals("game_ready")){
                currentPlayer.setReady(true);
            }else if(command.equals("game_not_ready")) {
                currentPlayer.setReady(false);
            }


        }else if(command.equals("room_exit")) {
            long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
            long roomId = (long) request.get("roomId"); //클라이언트가 보낸 방 ID를 가져옴.

            Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음.
            currentPlayer.setReady(false); //준비 미완료 상태로 바꿈
            gameRoomManager.leave(roomId, currentPlayer); //현재 플레이가 roomId에 해당하는 게임 방에서 나감
            gameWaitingRoomManager.enter(currentPlayer); //현재 플레이어를 대기 방에 들어감
        }
    }
}