package Client.GamePanel.Timer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameTimerLimitPanel extends JPanel {
    private JLabel timeLimitLabel; // 타이머를 표시할 레이블
    private int timeLeft = 60; // 남은 시간 (초)

    public GameTimerLimitPanel() {
        timeLimitLabel = new JLabel();
        Font labelFont = timeLimitLabel.getFont();
        timeLimitLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 30)); // 글꼴 크기 설정
        updateTimerLabel();

        add(timeLimitLabel);
    }

    public void startTimer(){
        // 1초마다 이벤트를 발생시키는 타이머를 생성합니다.
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--; // 남은 시간을 1초 줄입니다.
                updateTimerLabel();

                // 시간이 다 되면 타이머를 중지하고 알림창을 표시합니다.
                if (timeLeft <= 0) {
                    ((Timer) e.getSource()).stop();
                    JOptionPane.showMessageDialog(null, "시간이 다 되었습니다!");
                }
            }
        });
        timer.start(); // 타이머를 시작합니다.
    }

    // 남은 시간을 표시하는 레이블을 업데이트하는 메서드입니다.
    private void updateTimerLabel() {
        timeLimitLabel.setText(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));
    }
}

