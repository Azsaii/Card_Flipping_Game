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
            System.out.println("client: received msg. command = " + command);
            switch (command){
                // 랜덤 뒤집개
                case COMMAND_RANDOM_FLIP: {
                    boolean[] randomCardArray = (boolean[]) response.get("randomCardArray");
                    cardPanel.setCardsByBoolArray(randomCardArray); // 카드 뒤집기

                    price = (int)Math.round(RANDOM_FLIP.getItemPrice() * -0.1);
                    cardPanel.updateScoreByRandomFilp(senderId, randomCardArray); // 아이템 사용으로 인한 스코어 변경
                    break;
                }

                // 검은 안개
                case COMMAND_BLACK_FOG: {
                    System.out.println("black fog start");
                    price = (int)Math.round(BLACK_FOG.getItemPrice() * -0.1);
                    if(senderId == playerId) continue; // 자신이 사용한 경우 영향 없음
                    cardPanel.activateBlackFog();

                    int delay = BLACK_FOG.getCoolTime().intValue();
                    new Timer(delay * 1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cardPanel.deActiveCardEffectByItem(COMMAND_BLACK_FOG);
                            ((Timer) e.getSource()).stop();
                        }
                    }).start();
                    break;
                }

                // 황금 뒤집개
                case COMMAND_GOLD_FLIP: {
                    boolean[] cardArray = cardPanel.updateScoreByGoldFlip(senderId);
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
                    price = (int)Math.round(ICE_AGE.getItemPrice() * -0.1);

                    new Timer(delay * 1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cardPanel.deActiveCardEffectByItem(COMMAND_ICE_AGE);
                            ((Timer) e.getSource()).stop();
                        }
                    }).start();
                }
            }

            scorePanel.addScore(price, senderType); // 아이템 구입으로 인한 스코어 소모
        }
    }
}
