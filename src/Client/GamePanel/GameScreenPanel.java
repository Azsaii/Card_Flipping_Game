package Client.GamePanel;

import Client.GamePanel.Card.CardPanel;
import Client.GamePanel.ItemStore.ItemStorePanel;
import Client.GamePanel.Score.ScorePanel;
import Client.MainFrame;
import Client.MusicManager;
import Server.Data.GameRoom;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

/**
 * 게임 화면 패널 배치만 하는 클래스입니다.
 */
public class GameScreenPanel extends JPanel {
    
    private long playerId;
    private int playerType;
    public static final int PLAYER1 = 0;   // 플레이어 1 방장
    public static final int PLAYER2 = 1;   // 플레이어 2 방 참여자

    private ScorePanel scorePanel; // 스코어 패널. 임시로 플레이어1로 설정.
    private ItemStorePanel itemStorePanel; // 아이템 상점 패널
    private CardPanel cardPanel; // 카드 패널 임시로 플레이어1로 설정.
    private GameTimerLimitPanel timeLimitPanel; // 타이머 패널
    private GameChatPanel chatPanel; // 채팅 패널

    private Image backgroundImage;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    // 패널 위치, 크기 정하고 붙이기
    public GameScreenPanel(long playerId){
        this.playerId = playerId;
        setLayout(null);    // 배치관리자 제거
    }
    private void setPanelProperties(JPanel panel, int x, int y, int width, int height) {
        panel.setBounds(x, y, width, height);
        add(panel);
    }

    /* 현재 플레이어가 속한 방 객체 찾기 */
    public void startGameRoom(){
        GameRoom gameRoom = MainFrame.getGameRoom();
        playerType = (playerId == gameRoom.getLeader().getId()) ? PLAYER1 : PLAYER2;

        // 순서 바꾸면 안된다.
        scorePanel = new ScorePanel();
        cardPanel = new CardPanel(scorePanel, playerId, playerType);
        itemStorePanel = new ItemStorePanel(scorePanel, cardPanel, playerType);
        timeLimitPanel = new GameTimerLimitPanel(scorePanel, cardPanel, playerType);
        chatPanel = new GameChatPanel();

        cardPanel.startUpdateThread();
        chatPanel.startGameChatThread();

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

        try {
            backgroundImage = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
