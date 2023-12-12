package Client.GamePanel.Card;

import Client.GamePanel.Card.CardLabel;
import Client.GamePanel.Card.CardPanel;
import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import java.util.Map;

import static Client.GamePanel.Card.CardPanel.*;
import static Client.GamePanel.GameScreenPanel.PLAYER1;
import static Client.GamePanel.GameScreenPanel.PLAYER2;

/**
 * 상대방이 보낸 아이템 정보 메시지 받아 처리하는 스레드
 * 아이템 효과가 CardPanel 에 반영된다.
 */
public class ItemEffectThread extends Thread{

    CardPanel cardPanel;
    private final CardLabel[][] cardLabels;
    private long playerId;
    private int playerType;

    public ItemEffectThread(CardPanel cardPanel){
        this.cardPanel = cardPanel;
        this.cardLabels = cardPanel.cardLabels;
        this.playerId = cardPanel.playerId;
        this.playerType = cardPanel.playerType;
    }
    @Override
    public void run() {
        while (true) {
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.ITEM_UI_UPDATE_SERVER);

            Map<String, Object> response = dataTranslator.receiveData();
            if(response == null) break;

            String command = (String) response.get("command");
            long senderId = (long) response.get("senderId"); // 카드 뒤집은 플레이어 ID

            switch (command){
                case RANDOM_FLIP: { // 랜덤 뒤집개
                    System.out.println("received: Random Flip");
                    boolean[] randomCardArray = (boolean[]) response.get("randomCardArray");
                    cardPanel.setCardsByBoolArray(randomCardArray); // 카드 뒤집기
                    cardPanel.updateScoreByRandomFilp(senderId, randomCardArray); // 스코어 갱신
                    break;
                }
                case GOLD_FLIP: { // 황금 뒤집개
                    System.out.println("received: Gold Flip");
                    boolean state = true;
                    boolean[] cardArray = new boolean[24];

                    // 황금뒤집개를 사용하기 전 red / green카드의 수를 구한다.
                    int redCount = 0; // 기존 red 카드 수
                    for (int i = 0; i < 24; i++){
                        int x = i % 6; // 카드 x좌표
                        int y = i / 6; // 카드 y좌표
                        if(cardLabels[y][x].getColorState() == RED_CARD) redCount++;
                    }
                    int greenCount = 24 - redCount; // 기존 green 카드 수

                    if(playerId == senderId){ // 자신이 사용한 경우
                        switch(playerType){
                            case PLAYER1: state = false; cardPanel.addScore(redCount, PLAYER1); break; // 모든 카드를 green 카드로 변경, 기존 red 카드 수만큼 점수 추가
                            case PLAYER2: state = true; cardPanel.addScore(greenCount, PLAYER2); break; // 모든 카드를 red 카드로 변경, 기존 green 카드 수만큼 점수 추가
                        }
                    } else { // 상대가 사용한 경우
                        switch(playerType){
                            case PLAYER1: state = true; cardPanel.addScore(greenCount, PLAYER2); break;
                            case PLAYER2: state = false; cardPanel.addScore(redCount, PLAYER1); break;
                        }
                    }
                    for (int i = 0; i < 24; i++) cardArray[i] = state;

                    cardPanel.setCardsByBoolArray(cardArray);
                    break;
                }
            }
        }
    }
}
