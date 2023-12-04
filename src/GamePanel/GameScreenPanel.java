package GamePanel;

import GamePanel.Card.CardPanel;
import GamePanel.ItemStore.ItemStorePanel;
import GamePanel.Score.ScorePanel;
import GamePanel.Timer.GameTimerLimitPanel;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * 게임 화면 패널 배치만 하는 클래스입니다.
 */
public class GameScreenPanel extends JPanel {

    public static final int PLAYER1 = 0;   // 플레이어 1 방장
    public static final int PLAYER2 = 1;   // 플레이어 2 방 참여자

    private ScorePanel scorePanel; // 스코어 패널. 임시로 플레이어1로 설정.
    private ItemStorePanel itemStorePanel; // 아이템 상점 패널
    private CardPanel cardPanel; // 카드 패널 임시로 플레이어1로 설정.
    private GameTimerLimitPanel timeLimitPanel = new GameTimerLimitPanel(); // 타이머 패널
    private GameChatPanel chatPanel = new GameChatPanel(); // 채팅 패널

    // 패널 위치, 크기 정하고 붙이기
    public GameScreenPanel(){
        setLayout(null);    // 배치관리자 제거

        scorePanel = new ScorePanel();
        cardPanel = new CardPanel(scorePanel);
        itemStorePanel = new ItemStorePanel(scorePanel);

        setPanelProperties(itemStorePanel, 0, 0, 350, 800);
        setPanelProperties(scorePanel, 350, 0, 800, 100);
        setPanelProperties(timeLimitPanel, 650, 100, 200, 50);
        setPanelProperties(cardPanel, 350, 100, 800, 700);
        setPanelProperties(chatPanel, 1150, 0, 350, 800);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // GameScreenPanel 이 보일 때 타이머를 시작
                timeLimitPanel.startTimer();
            }
        });
    }
    private void setPanelProperties(JPanel panel, int x, int y, int width, int height) {
        panel.setBounds(x, y, width, height);
        add(panel);
    }
}
