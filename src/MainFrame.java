import GamePanel.GameScreenPanel;
import GamePanel.ItemStore.ItemStoreImagePanel;
import GamePanel.Timer.GameStartTimerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * 메인 프레임입니다.
 * 메인 화면, 방 화면, 게임 화면 패널을 붙입니다.
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 패널 생성
        JPanel mainMenu = new MainMenu(mainPanel);               // 메인 메뉴
        GameStartTimerPanel startPanel = new GameStartTimerPanel();  // 게임 시작 대기 패널
        JPanel gameScreen = new GameScreenPanel();

        // 각 패널을 카드 레이아웃에 추가
        mainPanel.add(mainMenu, "MainMenu");
        mainPanel.add(startPanel, "StartPanel");
        mainPanel.add(gameScreen, "GameScreen");

        // startPanel 에서 5초를 센 후 이벤트가 발생하면 GameScreen 으로 넘어간다.
        startPanel.setActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "GameScreen");
            }
        });

        // 메인 화면에 play 버튼을 추가하고, 클릭 시 게임 화면으로 전환하도록 설정
        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /**
                 * 개발 중이므로 타이머 임시 제거함. 작업 끝나고 살려야한다.
                 */
//                cardLayout.show(mainPanel,  "StartPanel");
//                startPanel.startTimer(); // 게임 시작 패널의 타이머 시작

                cardLayout.show(mainPanel,  "GameScreen");
            }
        });
        mainMenu.add(playButton);

        // JFrame에 메인 패널을 추가
        this.add(mainPanel);
    }

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setTitle("Card Flipping Game");
        frame.setSize(1500, 800);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
