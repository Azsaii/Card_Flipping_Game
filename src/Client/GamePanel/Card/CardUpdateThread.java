package Client.GamePanel.Card;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.*;
import java.util.Map;

import static Client.GamePanel.Card.CardPanel.*;
import static Client.GamePanel.GameScreenPanel.PLAYER1;
import static Client.GamePanel.GameScreenPanel.PLAYER2;
import static Client.MainFrame.gameRoom;
import static Client.MainFrame.playerId;

/**
 * 상대방이 보낸 카드 업데이트 메시지 받아 처리하는 스레드
 * 뒤집은 카드 정보가 CardPanel 에 반영된다.
 */
public class CardUpdateThread extends Thread{

    CardPanel cardPanel;
    private final CardLabel[][] cardLabels;

    CardUpdateThread(CardPanel cardPanel){
        this.cardPanel = cardPanel;
        this.cardLabels = cardPanel.cardLabels;
    }
    @Override
    public void run(){
        while (true) {
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.CARD_UI_UPDATE_SERVER);

            Map<String, Object> response = dataTranslator.receiveData();
            if(response == null) break;

            String command = (String) response.get("command");
            long senderId = (long) response.get("senderId"); // 카드 뒤집은 플레이어 ID

            if (command.equals(CARD_UPDATE) && senderId != playerId) { // 상대방이 보낸 카드 업데이트 메시지일 때만 처리
                String location[] = ((String) response.get("location")).split(","); // 뒤집은 카드 좌표
                int x = Integer.valueOf(location[0]);
                int y = Integer.valueOf(location[1]);
                System.out.println("loc: " + x + ", " + y);

                CardLabel cardLabel = cardLabels[y][x];  // 배열에서 카드 레이블 찾기
                ImageIcon img = (playerId == gameRoom.getLeader().getId()) ? cardPanel.redCardImg : cardPanel.greenCardImg;  // 플레이어가 방장이면 해당 위치의 카드를 green 이미지로 변경
                int targetColor = (playerId == gameRoom.getLeader().getId()) ? RED_CARD : GREEN_CARD;// 플레이어가 방장이면 카드를 초록색으로 변경
                int changeScoreTarget = (cardPanel.playerType == PLAYER1) ? PLAYER2 : PLAYER1; // 상대방 스코어 변경

                cardPanel.updateCardData(cardLabel, img, targetColor);  // 카드 색 변경
                cardPanel.updateScore(changeScoreTarget); // 스코어 업데이트
            }
        }
    }

}
