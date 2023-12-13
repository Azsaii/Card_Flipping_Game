package Client.GamePanel.Card;

import Client.GamePanel.ItemStore.ItemPurchasePanel;
import Client.GamePanel.Score.ScorePanel;
import Client.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import static Client.GamePanel.GameScreenPanel.PLAYER1;
import static Client.GamePanel.GameScreenPanel.PLAYER2;

public class CardPanel extends JPanel {

    static final int RED_CARD = 3;
    static final int GREEN_CARD = 4;
    static final String CARD_UPDATE = "CARD_UPDATE";
    public static final String COMMAND_RANDOM_FLIP = "RANDOM_FLIP";
    public static final String COMMAND_GOLD_FLIP = "GOLD_FLIP";
    public static final String COMMAND_DOUBLE_EVENT = "DOUBLE_EVENT";
    public static final String COMMAND_ICE_AGE = "ICE_AGE";

    public long playerId;
    public int playerType; // 1p / 2p 구분. 0, 1
    private ScorePanel scorePanel;
    private ItemPurchasePanel itemPurchasePanel;
    public final CardLabel[][] cardLabels = new CardLabel[4][6];  // 4행 6열의 카드 배열

    public ImageIcon redCardImg;
    public ImageIcon greenCardImg;
    public ImageIcon iceAgeCardImg;

    private CardUpdateThread cardUpdateThread;
    private ItemEffectThread itemEffectThread;

    public boolean isUnClickable = false; // 카드 뒤집을 수 있는지 여부. false일 때 뒤집기 가능

    /**
     * 카드 관련 전담 패널
     * 카드 클릭 시 화면을 업데이트하고 상대방에게 뒤집은 카드 위치정보 전달
     * 카드 클릭 시 스코어 업데이트 메시지 전송
     */
    public CardPanel(ScorePanel scorePanel, long playerId, int playerType) {
        this.scorePanel = scorePanel;
        this.playerId = playerId;
        this.playerType = playerType;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 패널의 하단에 여백 추가
        setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // 레드 카드
        Image scaledImage1 = new ImageIcon("images/cards/CARD_RED.JPG").getImage().getScaledInstance(78, 110, Image.SCALE_DEFAULT);
        redCardImg = new ImageIcon(scaledImage1);

        // 그린 카드
        Image scaledImage2 = new ImageIcon("images/cards/CARD_GREEN.JPG").getImage().getScaledInstance(78, 110, Image.SCALE_DEFAULT);
        greenCardImg = new ImageIcon(scaledImage2);

        // 아이스 에이지 카드
        Image scaledImage4 = new ImageIcon("images/cards/CARD_ICE.JPG").getImage().getScaledInstance(78, 110, Image.SCALE_DEFAULT);
        iceAgeCardImg = new ImageIcon(scaledImage4);

        // 초기 카드 배치
        for(int i = 0; i < 24; i++) {
            CardLabel cardLabel;
            int x = i % 6; // 카드 x좌표
            int y = i / 6; // 카드 y좌표

            if(i / 3 % 2 == 0) {
                // 왼쪽에 레드 카드만 배치 
                cardLabel = new CardLabel(new ImageIcon(redCardImg.getImage()), RED_CARD);
            } else {
                // 오른쪽에 그린 카드만 배치
                cardLabel = new CardLabel(new ImageIcon(greenCardImg.getImage()), GREEN_CARD);
            }

            cardLabel.setBackground(Color.black);
            cardLabel.setOpaque(true);  // 레이블의 배경을 불투명하게 설정

            // 카드 뒤집었을 때 자신의 화면과 스코어 업데이트
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if(isUnClickable) return; // 아이스 에이지 아이템 사용 상태인 경우 카드 뒤집기 불가

                    int cardColor = 0;
                    String location = String.valueOf(x) + "," + String.valueOf(y); // 카드 좌표를 문자열로 전송
                    // 플레이어1이고 레드 카드를 뒤집으면 스코어 업데이트하고 그린 카드로 변경
                    if (playerType == PLAYER1 && cardLabel.getColorState() == RED_CARD) {
                        updateCardData(cardLabel, greenCardImg, GREEN_CARD);
                        cardColor = GREEN_CARD;
                        addCardScore(playerType);
                        sendFlipCardData(location);
                    }

                    // 플레이어2이고 그린 카드를 뒤집으면 스코어 업데이트하고 레드 카드로 변경
                    else if(playerType == PLAYER2 && cardLabel.getColorState() == GREEN_CARD) {
                        updateCardData(cardLabel, redCardImg, RED_CARD);
                        cardColor = RED_CARD;
                        addCardScore(playerType);
                        sendFlipCardData(location);
                    }
                }
            });
            cardLabels[y][x] = cardLabel;  // 카드 레이블을 배열에 저장

            gbc.gridx = i % 6;  // x 좌표 설정
            gbc.gridy = i / 6;  // y 좌표 설정
            gbc.insets = new Insets(20, 20, 20, 20);  // 상, 좌, 하, 우 여백 설정

            add(cardLabel, gbc);
        }
    }

    public void startUpdateThread(){
        // 상대방이 보낸 카드 업데이트 메시지 받아 처리하는 스레드
        cardUpdateThread = new CardUpdateThread(this);
        cardUpdateThread.start();

        // 상대방이 보낸 아이템 정보 메시지 받아 처리하는 스레드
        itemEffectThread = new ItemEffectThread(this, scorePanel, itemPurchasePanel);
        itemEffectThread.start();
    }

    public void setItemPurchasePanel(ItemPurchasePanel itemPurchasePanel) {
        this.itemPurchasePanel = itemPurchasePanel;
    }

    // 기본 requset 객체 생성 메서드
    public Map<String, Object> setDefaultRequest(String command){
        Map<String, Object> request = new HashMap<>();
        request.put("command", command); // 요청 종류
        request.put("playerId", playerId); // 플레이어 id
        return request;
    }

    // 카드 뒤집었을 때 서버로 전송하는 메서드
    public void sendFlipCardData(String location){
        /* 요청 객체를 만들어 CardUIUpdateServer 로 전송 */
        Map<String, Object> request = setDefaultRequest(CARD_UPDATE);
        request.put("location", location); // 뒤집은 카드 좌표 데이터 추가
        MainFrame.dataTranslatorWrapper.broadcast(request);
    }

    // 랜덤, 황금 뒤집개 아이템 사용 시 카드 뒤집는 메서드
    public void setCardsByBoolArray(boolean[] cardArray) {

        for(int i = 0; i < 24; i++) {
            int x = i % 6; // 카드 x좌표
            int y = i / 6; // 카드 y좌표

            // cardArray 값에 따라 카드 색상 설정
            if(cardArray[i]) { // true이면 레드 카드
                updateCardData(cardLabels[y][x], redCardImg, RED_CARD);
            } else { // false이면 그린 카드
                updateCardData(cardLabels[y][x], greenCardImg, GREEN_CARD);
            }
        }
    }

    // 서버가 카드 변경 응답 보냈을 때 화면 업데이트하는 메서드
    public void updateCardData(CardLabel cardLabel, ImageIcon img, int targetColor) {
        cardLabel.setIcon(new ImageIcon(img.getImage()));
        cardLabel.setColorState(targetColor);
    }

    public void addScore(int score, int playerType) {scorePanel.addScore(score, playerType);}

    public void addCardScore(int playerType) {scorePanel.addCardScore(playerType);}

    public void updateScoreByRandomFilp(long senderId, boolean[] randomCardArray){
        int afterRed = 0;

        // 랜덤 뒤집게 적용 후 카드 수 구하기
        for(boolean b : randomCardArray) {
            if(b == true) afterRed++;
        }
        int afterGreen = 24 - afterRed;

        // 본인이 랜덤 뒤집개 사용한 경우 상대 카드 뒤집은 수만큼 스코어를 얻는다.
        // 내가 아이템을 사용한 경우, 내가 얻은 스코어만큼 상대는 스코어를 잃는다.
        if(senderId == playerId) {
            switch(playerType){
                case PLAYER1: {
                    scorePanel.addScore(afterGreen, PLAYER1);
                    scorePanel.addScore(afterGreen * -1, PLAYER2);
                    break;
                }
                case PLAYER2: {
                    scorePanel.addScore(afterRed, PLAYER2);
                    scorePanel.addScore(afterRed * -1, PLAYER1);
                    break;
                }
            }
        } else { // 상대가 랜덤 뒤집개 사용한 경우 상대가 스코어를 얻은만큼 스코어를 잃는다.
            switch(playerType){
                case PLAYER1: {
                    scorePanel.addScore(afterRed, PLAYER2); // 상대 스코어 증가
                    scorePanel.addScore(afterRed * -1, PLAYER1); // 내 스코어 감소
                    break;
                }
                case PLAYER2: {
                    scorePanel.addScore(afterGreen, PLAYER1);
                    scorePanel.addScore(afterGreen * -1, PLAYER2);
                    break;
                }
            }
        }
    }

    // 아이스 에이지 아이템 사용 시 호출되는 메서드
    // 모든 카드를 ice 카드로 변경한다.
    public void activeCardIceAge(){
        isUnClickable = true; // 카드 뒤집기 비활성화

        for(int i = 0; i < 24; i++) {
            int x = i % 6; // 카드 x좌표
            int y = i / 6; // 카드 y좌표
            CardLabel cardLabel = cardLabels[y][x];

            updateCardData(cardLabel, iceAgeCardImg, cardLabel.getColorState());
        }
    }

    // 아이스 에이지 아이템 사용 종료 후 원래대로 돌리는 메서드
    public void deActiveCardIceAge(){
        isUnClickable = false; // 카드 뒤집기 활성화

        for(int i = 0; i < 24; i++) {
            int x = i % 6; // 카드 x좌표
            int y = i / 6; // 카드 y좌표
            CardLabel cardLabel = cardLabels[y][x];
            ImageIcon img = (cardLabels[y][x].getColorState() == RED_CARD) ? redCardImg : greenCardImg;
            updateCardData(cardLabel, img, cardLabel.getColorState());
        }
    }

    public void closeGameThread(){
        cardUpdateThread.stop();
        itemEffectThread.stop();
    }
}