package Server.Data;

import Network.DataTranslator;
import Server.Manager.GameRoomManager;
import Server.Manager.GameWaitingRoomManager;
import Server.Manager.PlayerManager;
import Server.ServerTemplate;
import Server.ServerThread;

import java.util.HashMap;
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

    public GameRoomDataServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {

        GameRoomManager gameRoomManager = GameRoomManager.getInstance();
        GameWaitingRoomManager gameWaitingRoomManager = GameWaitingRoomManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();

        while (true) { //각 플레이어들이 보낸 명령어를 처리하는 구간

            /* 요청을 받아 처리 */
            Map<String, Object> request = checkexit();
            if(request == null) break; // 클라이언트가 게임 종료한 경우 루프 빠져나간다.

            String command = (String) request.get("command");

            if (command.equals("방 생성")) {

                long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.

                Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음.

                gameWaitingRoomManager.leave(currentPlayer); //현재 플레이어를 대기 방에서 나가게 함.

                GameRoom gameRoom = gameRoomManager.createRoom(currentPlayer); //현재 플레이어는 게임 방을 생성 하고 게임 방에 들어감.

                Map<String, Object> response = new HashMap<>();

                response.put("roomId", gameRoom.getId()); //현재 플레이어한테 생성된 GameRoom 객체의 ID를 보내줌
                dataTranslator.sendData(response);

            }else if(command.equals("방 입장")) {

                long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
                long roomId = (long) request.get("roomId"); //클라이언트가 보낸 방 ID를 가져옴.

                Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음.

                Map<String, Object> response = new HashMap<>();

                if(gameRoomManager.enter(roomId, currentPlayer)) { //현재 플레이어를 해당 ID 값을 가진 게임 방에 입장 시킴.
                    System.out.println("SERVER: ROOM ENTERED player: " + currentPlayer + ", roomId: " + roomId);
                    gameWaitingRoomManager.leave(currentPlayer); //만약 성공적으로 게임 방에 입장 했다면 현재 플레이어를 대기 방에서 나가게 함.
                    response.put("result", "OK");
                    dataTranslator.sendData(response);
                }else {
                    System.out.println("방이 꽉 찼습니다."); //인원 초과 시 출력
                    response.put("result", "FAIL");
                    dataTranslator.sendData(response);
                }
            } else if (command.equals("게임 준비") || command.equals("게임 준비 미완료")) {

                long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
                Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음

                if(command.equals("게임 준비")){
                    currentPlayer.setReady(true);
                }else if(command.equals("게임 준비 미완료")) {
                    currentPlayer.setReady(false);
                }


            }else if(command.equals("방 나가기")) {
                long playerId = (long) request.get("playerId"); //클라이언트가 보낸 플레이어 ID를 가져옴.
                long roomId = (long) request.get("roomId"); //클라이언트가 보낸 방 ID를 가져옴.

                Player currentPlayer = playerManager.getPlayer(playerId); //플레이어를 찾음.

                currentPlayer.setReady(false); //준비 미완료 상태로 바꿈

                gameRoomManager.leave(roomId, currentPlayer); //현재 플레이가 roomId에 해당하는 게임 방에서 나감

                gameWaitingRoomManager.enter(currentPlayer); //현재 플레이어를 대기 방에 들어감
            }

            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }
}