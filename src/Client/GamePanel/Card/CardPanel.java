package Client.GamePanel.Card;

import Client.GamePanel.Score.ScorePanel;
import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;
import Server.Data.GameRoom;
import Server.Data.Player;
import Server.Manager.GameRoomManager;
import Server.Manager.PlayerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static Client.GamePanel.GameScreenPanel.PLAYER1;
import static Client.GamePanel.GameScreenPanel.PLAYER2;

public class CardPanel extends JPanel {

    static final int RED_CARD = 3;
    static final int GREEN_CARD = 4;
    public static final String command_card = "CARD_UPDATE";

    private long playerId;
    private Player player;
    private GameRoom gameRoom;
    private int playerType; // 1p / 2p 구분. 0, 1
    private ScorePanel scorePanel;
    private final CardLabel[][] cardLabels = new CardLabel[4][6];  // 4행 6열의 카드 배열

    public ImageIcon red;
    public ImageIcon green;

    /**
     * 카드 관련 전담 패널
     * 카드 클릭 시 화면을 업데이트하고 상대방에게 뒤집은 카드 위치정보 전달
     * 카드 클릭 시 스코어 업데이트 메시지 전송
     */
    public CardPanel(ScorePanel scorePanel, Player player, GameRoom gameRoom, int playerType) {
        this.scorePanel = scorePanel;
        this.player = player;
        this.gameRoom = gameRoom;
        this.playerType = playerType;
        this.playerId = player.getId();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 패널의 하단에 여백 추가
        setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // 레드 카드
        Image scaledImage1 = new ImageIcon("images/CARD_RED.JPG").getImage().getScaledInstance(78, 110, Image.SCALE_DEFAULT);
        red = new ImageIcon(scaledImage1);

        // 그린 카드
        Image scaledImage2 = new ImageIcon("images/CARD_GREEN.JPG").getImage().getScaledInstance(78, 110, Image.SCALE_DEFAULT);
        green = new ImageIcon(scaledImage2);

//        // 카드, 아이템 정보 주고받는 연결 시작
//        try{
//            new ClientDataThread(playerId).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 초기 카드 배치
        for(int i = 0; i < 24; i++) {
            CardLabel cardLabel;
            int x = i % 6; // 카드 x좌표
            int y = i / 6; // 카드 y좌표

            if(i / 3 % 2 == 0) {
                // 왼쪽에 레드 카드만 배치 
                cardLabel = new CardLabel(new ImageIcon(red.getImage()), RED_CARD);
            } else {
                // 오른쪽에 그린 카드만 배치
                cardLabel = new CardLabel(new ImageIcon(green.getImage()), GREEN_CARD);
            }

            cardLabel.setBackground(Color.black);
            cardLabel.setOpaque(true);  // 레이블의 배경을 불투명하게 설정

            // 카드 뒤집었을 때 자신의 화면과 스코어 업데이트
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int cardColor = 0;
                    // 플레이어1이고 레드 카드를 뒤집으면 스코어 업데이트하고 그린 카드로 변경
                    if (playerType == PLAYER1 && cardLabel.getColorState() == RED_CARD) {
                        updateCardData(cardLabel, green, GREEN_CARD);
                        updateScore(playerType);
                        cardColor = GREEN_CARD;
                    }

                    // 플레이어2이고 그린 카드를 뒤집으면 스코어 업데이트하고 레드 카드로 변경
                    else if(playerType == PLAYER2 && cardLabel.getColorState() == GREEN_CARD) {
                        updateCardData(cardLabel, red, RED_CARD);
                        cardColor = RED_CARD;
                    }

                    updateScore(playerType);

                    // 상대방에게 뒤집은 카드 데이터 전송
                    try {
                        String location = String.valueOf(x) + "," + String.valueOf(y); // 카드 좌표를 문자열로 전송
                        sendCardData(playerId, location);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            cardLabels[y][x] = cardLabel;  // 카드 레이블을 배열에 저장

            gbc.gridx = i % 6;  // x 좌표 설정
            gbc.gridy = i / 6;  // y 좌표 설정
            gbc.insets = new Insets(20, 20, 20, 20);  // 상, 좌, 하, 우 여백 설정

            add(cardLabel, gbc);
        }

        // 상대방이 보낸 카드 업데이트 메시지 받아 처리하는 스레드
        Thread updateCardThread = new Thread(() -> {

            while (true) {
                DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.CARD_UI_UPDATE_SEREVR);

                Map<String, Object> response = dataTranslator.receiveData();
                String command = (String) response.get("command");
                String senderId = (String) response.get("senderId"); // 카드 뒤집은 플레이어 ID

                if (command.equals(command_card) && !senderId.equals(playerId)) { // 상대방이 보낸 카드 업데이트 메시지일 때만 처리

                    String location[] = ((String) response.get("location")).split(","); // 뒤집은 카드 좌표
                    int x = Integer.valueOf(location[0]);
                    int y = Integer.valueOf(location[1]);

                    CardLabel cardLabel = cardLabels[y][x];  // 배열에서 카드 레이블 찾기
                    ImageIcon img = (player == gameRoom.getLeader()) ? green : red;  // 플레이어가 방장이면 해당 위치의 카드를 green 이미지로 변경
                    int targetColor = (player == gameRoom.getLeader()) ? GREEN_CARD : RED_CARD;// 플레이어가 방장이면 카드를 초록색으로 변경
                    int changeScoreTarget = (playerType == PLAYER1) ? PLAYER2 : PLAYER1; // 상대방 스코어 변경

                    updateCardData(cardLabel, img, targetColor);  // 카드 색 변경
                    updateScore(changeScoreTarget); // 스코어 업데이트
                }
            }
        });

        updateCardThread.start();
    }

    // 카드 뒤집었을 때 서버로 정송하는 메서드
    public void sendCardData(long playerId, String location) throws IOException {
        /* 요청 객체를 만들어 GameDataStatusUIUpdateServerThread 로 전송 */
        Map<String, Object> request = new HashMap<>();
        request.put("command", command_card); //
        request.put("location", location); // 뒤집은 카드 좌표 데이터 추가
    }

    // 서버가 카드 변경 응답 보냈을 때 화면 업데이트하는 메서드
    public void updateCardData(CardLabel cardLabel, ImageIcon img, int targetColor) {
        cardLabel.setIcon(new ImageIcon(img.getImage()));
        cardLabel.setColorState(targetColor);
    }

    // 스코어 업데이트하는 메서드
    public void updateScore(int changeScoreTarget){
        scorePanel.updateScore(changeScoreTarget);
    }

//    class ClientDataThread extends Thread {
//        public ClientDataThread(long playerId) throws IOException {
//
//            DataTranslator playerDataTranslator = player.getDataTranslatorWrapper().get(ServerName.CARD_UI_UPDATE_SEREVR);
//            playerDataTranslator.sendData(response);
//
//            clientDataSocket = new Socket("localhost", 5010); // GameDataServer 와 연결
//            din = new BufferedReader(new InputStreamReader(clientDataSocket.getInputStream()));
//            dout= new BufferedWriter(new OutputStreamWriter(clientDataSocket.getOutputStream()));
//            System.out.println("ClientDataThread created");
//
//            // 연결 확인 메시지 전송
//            try {
//                dout.write("type=firstCheck\n");
//                dout.flush();
//
//                // 서버의 확인 메시지 받기
//                String checkMsg = din.readLine();
//                String[] playerDatas = checkMsg.split("&");
//                // 서버 확인 메시지 아니면 리턴
//                if(!playerDatas[0].split("=")[1].equals("playerDataCheck")) return;
//
//                playerType = Integer.valueOf(playerDatas[1].split("=")[1]); // 1p / 2p 구분
//                roomId = Integer.valueOf(playerDatas[2].split("=")[1]); // 방 id 파싱
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        @Override
//        public void run() {
//            while(true){
//                try {
//                    // 상대방이 뒤집은 카드 정보 받기
//                    String cardDataMsg = din.readLine();
//                    String[] cardDatas = cardDataMsg.split("&");
//
//                    System.out.println(playerType + "is received: " + cardDataMsg);
//
//
//                    int x = Integer.parseInt(cardDatas[1].split("=")[1]);
//                    int y = Integer.parseInt(cardDatas[2].split("=")[1]);
//                    int cardColor = Integer.parseInt(cardDatas[3].split("=")[1]);
//
//                    CardLabel cardLabel = cardLabels[y][x];  // 배열에서 카드 레이블 찾기
//                    ImageIcon img = (cardColor == RED_CARD) ? red : green;  // 카드 색에 따른 이미지 선택
//                    int target = (playerType == PLAYER1) ? PLAYER2 : PLAYER1; // 상대방 스코어 변경
//                    updateCardData(cardLabel, img, cardColor, target);  // 카드 색 변경
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        }
//    }
}