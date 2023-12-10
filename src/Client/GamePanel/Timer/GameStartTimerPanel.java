package Client.GamePanel.Timer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 게임 시작 전 5초를 세는 패널입니다.
 * 5초가 지나면 게임이 시작됩니다.
 */
public class GameStartTimerPanel extends JPanel {
    private JLabel timerLabel;  // 타이머를 표시할 레이블
    private int timeLeft = 5;   // 남은 시간 (초)
    private Timer timer;        // 타이머

    private ActionListener actionListener;

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public GameStartTimerPanel() {
        setLayout(new BorderLayout());

        timerLabel = new JLabel("", SwingConstants.CENTER);
        Font labelFont = timerLabel.getFont();
        timerLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 200)); // 글꼴 크기 설정
        add(timerLabel, BorderLayout.CENTER);

        // 1초마다 이벤트를 발생시키는 타이머를 생성합니다.
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft > 0) {
                    timerLabel.setText(String.valueOf(timeLeft));
                } else if (timeLeft == 0) {
                    timerLabel.setText("START!");
                } else {
                    ((Timer) e.getSource()).stop();
                    GameStartTimerPanel.this.setVisible(false);
                    if (actionListener != null) {
                        actionListener.actionPerformed(new ActionEvent(GameStartTimerPanel.this, ActionEvent.ACTION_PERFORMED, null));
                    }
                }
                timeLeft--;
            }
        });
    }

    public void startTimer(){
        timer.setInitialDelay(0); // 초기 딜레이를 0으로 설정
        timer.start(); // 타이머를 시작합니다.
    }
}

