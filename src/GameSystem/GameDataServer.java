package GameSystem;

import java.io.*;
import java.net.*;
import java.util.*;

import static GameSystem.GameDataServer.playerMap;

/**
 * 카드, 아이템 데이터 처리용 서버입니다.
 */
public class GameDataServer extends Thread{
    static int DATAPORT = 5001;
    static HashMap<String, List<Player>> playerMap = new HashMap<>();

    private long playerId = 0;
    private long roomId = 0;
    public GameDataServer() throws IOException {
        ServerSocket ss = new ServerSocket(DATAPORT); // 채팅 서버 소켓

        while (true) { // 플레이어 개인 채팅 스레드 생성
            Socket socket = ss.accept();
            ServerDataThread thread = new ServerDataThread(socket, playerId++, roomId);
            thread.start();

            // 2명의 플레이어가 모일 때마다 방이 1씩 늘어남
            roomId = playerId % 2 == 0 ? ++roomId : roomId;
        }
    }
}

// 플레이어 1명의 채팅 입출력 담당 스레드
class ServerDataThread extends Thread {
    private Socket serverDataSocket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerDataThread(Socket serverDataSocket, long playerCount, long roomId) throws IOException {
        this.serverDataSocket = serverDataSocket;
        in = new BufferedReader(new InputStreamReader(serverDataSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(serverDataSocket.getOutputStream()));

        try{ // 연결 직후 플레이어가 보낸 첫 데이터로 플레이어 정보를 리스트에 저장
            String playerDataMsg = in.readLine();
            String[] playerDatas = playerDataMsg.split("&");

            if(!playerDatas[0].split("=")[1].equals("firstCheck")) return;

            Player player = new Player(playerCount, roomId);
            player.setDataSocket(serverDataSocket);

            // 방 id, 플레이어 객체를 해시맵 객체로 만들고 리스트에 저장
            List<Player> playersInRoom = playerMap.getOrDefault(String.valueOf(roomId), new ArrayList<>());
            playersInRoom.add(player);
            playerMap.put(String.valueOf(roomId), playersInRoom);
            System.out.println("SERVER SAVED PLAYER DATA: PLAEYRID=" + playerCount + " ROOMID=" +roomId + "playerMap size: " + playerMap.size());

            // p1, p2 구분해서 클라이언트에게 전송
            out.write("type=playerDataCheck&playerCount=" + playerCount % 2 + "&roomId=" + roomId +  "\n");
            out.flush();

            System.out.println("SERVER: " + playerCount % 2 + ", " + roomId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() { // 플레이어가 보낸 카드데이터를 같은 방의 다른 플레이어에게 전송
        while(true) {
            try {
                String msg = in.readLine();
                String[] msgSplit = msg.split("&");
                System.out.println("server received msg: " + msg);

                // 카드 데이터만 처리
                if(msgSplit[0].split("=")[1].equals("cardData")){
                    String playerId = msgSplit[4].split("=")[1]; // 플레이어 id 파싱
                    String targetRoomId = msgSplit[5].split("=")[1]; // 룸 id 파싱
                    System.out.println("TARGET PLAYERID=" + playerId+" ROOMID=" + targetRoomId);

                    // targetRoomId key로 가진 HashMap을 찾음
                    List<Player> playersInRoom = playerMap.get(targetRoomId);
                    System.out.println("rooms length: " + playersInRoom.size());
                    for (Player targetPlayer : playersInRoom) {
                        System.out.println(String.valueOf(targetPlayer.getId()));
                        if (!String.valueOf(targetPlayer.getId()).equals(playerId)) {
                            // targetPlayer의 Socket을 가지고 그 Socket에 연결된 OutputStream을 통해 메시지 전송
                            try {
                                String[] targetMsgSplit = Arrays.copyOfRange(msgSplit, 0, 4);
                                String sendCardDataMsg = String.join("&", targetMsgSplit);
                                Socket targetSocket = targetPlayer.getDataSocket();
                                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(targetSocket.getOutputStream()));
                                out.write(sendCardDataMsg + "\n");
                                out.flush();
                                System.out.println("SERVER SEND MSG: " + sendCardDataMsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}