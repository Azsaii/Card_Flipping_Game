package Client.GamePanel.Card;

import Client.GamePanel.Card.CardLabel;
import Client.GamePanel.Card.CardPanel;
import Client.GamePanel.ItemStore.ItemPurchasePanel;
import Client.GamePanel.Score.DefaultScoreStrategy;
import Client.GamePanel.Score.DoubleScoreStrategy;
import Client.GamePanel.Score.ScorePanel;
import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import static Client.GamePanel.Card.CardPanel.*;
import static Client.GamePanel.GameScreenPanel.PLAYER1;
import static Client.GamePanel.GameScreenPanel.PLAYER2;
import static Client.GamePanel.ItemStore.ItemPurchasePanel.*;

/**
 * 상대방이 보낸 아이템 정보 메시지 받아 처리하는 스레드
 * 아이템 효과가 CardPanel 에 반영된다.
 */
public class ItemEffectThread extends Thread{

    private CardPanel cardPanel;
    private ScorePanel scorePanel;
    private ItemPurchasePanel itemPurchasePanel;
    private final CardLabel[][] cardLabels;
    private long playerId;
    private int playerType;

    public ItemEffectThread(CardPanel cardPanel, ScorePanel scorePanel, ItemPurchasePanel itemPurchasePanel){
        this.cardPanel = cardPanel;
        this.cardLabels = cardPanel.cardLabels;
        this.playerId = cardPanel.playerId;
        this.playerType = cardPanel.playerType;
        this.scorePanel = scorePanel;
        this.itemPurchasePanel = itemPurchasePanel;
    }
    @Override
    public void run() {
        while (true) {
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.ITEM_UI_UPDATE_SERVER);
            int price = 0;

            Map<String, Object> response = dataTranslator.receiveData();
            if(response == null) break;

            String command = (String) response.get("command");
            long senderId = (long) response.get("senderId"); // 카드 뒤집은 플레이어 ID
            int senderType = (senderId == playerId) ? playerType : 1-playerType;

            // 아이템 사용하기 전 red, green 카드의 수를 구한다.
            int redCount = 0; // 기존 red 카드 수
            for (int i = 0; i < 24; i++){
                int x = i % 6; // 카드 x좌표
                int y = i / 6; // 카드 y좌표
                if(cardLabels[y][x].getColorState() == RED_CARD) redCount++;
            }
            int greenCount = 24 - redCount; // 기존 green 카드 수

            switch (command){
                // 랜덤 뒤집개
                case COMMAND_RANDOM_FLIP: {
                    boolean[] randomCardArray = (boolean[]) response.get("randomCardArray");
                    cardPanel.setCardsByBoolArray(randomCardArray); // 카드 뒤집기

                    price = (int)Math.round(RANDOM_FLIP.getItemPrice() * -0.1);
                    cardPanel.updateScoreByRandomFilp(senderId, randomCardArray); // 아이템 사용으로 인한 스코어 변경
                    break;
                }

                // 황금 뒤집개
                case COMMAND_GOLD_FLIP: {
                    boolean state = true;
                    boolean[] cardArray = new boolean[24];

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
                    price = (int)Math.round(GOLD_FLIP.getItemPrice() * -0.1);
                    break;
                }

                // 더블 이벤트
                case COMMAND_DOUBLE_EVENT: {

                    int targetPlayer = (senderId == playerId) ? playerType : 1-playerType;
                    scorePanel.setStrategy(DoubleScoreStrategy.getInstance(), targetPlayer);
                    int delay = DOUBLE_EVENT.getCoolTime().intValue();
                    price = (int)Math.round(DOUBLE_EVENT.getItemPrice() * -0.1);
                    new Timer(delay * 1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            scorePanel.setStrategy(DefaultScoreStrategy.getInstance(), targetPlayer);
                            ((Timer) e.getSource()).stop();
                        }
                    }).start();
                }

                // 아이스 에이지
                case COMMAND_ICE_AGE: {
                    cardPanel.activeCardIceAge();
                    itemPurchasePanel.deActiveItemPanel(ICE_AGE.getCoolTime()); // 모든 아이템 구입 불가 처리
                    int delay = ICE_AGE.getCoolTime().intValue();

                    new Timer(delay * 1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cardPanel.deActiveCardIceAge();
                            ((Timer) e.getSource()).stop();
                        }
                    }).start();
                }
            }

            scorePanel.addScore(price, senderType); // 아이템 구입으로 인한 스코어 소모
        }
    }
}
