package Client.GamePanel.Timer;

import Client.GamePanel.Score.ScorePanel;
import Client.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static Client.GamePanel.GameScreenPanel.PLAYER1;
import static Client.GamePanel.GameScreenPanel.PLAYER2;

public class GameTimerLimitPanel extends JPanel {
    private JLabel timeLimitLabel; // 타이머를 표시할 레이블
    private int timeLeft = 120; // 남은 시간 (초)
    private Timer timer;

    private ScorePanel scorePanel;
    private int playerType;

    public GameTimerLimitPanel(ScorePanel scorePanel, int playerType) {
        this.scorePanel = scorePanel;
        this.playerType = playerType;

        timeLimitLabel = new JLabel();
        Font labelFont = timeLimitLabel.getFont();
        timeLimitLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 30)); // 글꼴 크기 설정
        updateTimerLabel();

        add(timeLimitLabel);
    }

    public void startTimer(){
        // 시작하기 전에 기존 타이머가 있다면 중지
        if(timer != null) {
            timer.stop();
        }

        // 1초마다 이벤트를 발생시키는 타이머를 생성합니다.
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--; // 남은 시간을 1초 줄입니다.
                updateTimerLabel();

                // 시간이 다 되면 타이머를 중지하고 알림창을 표시합니다.
                if (timeLeft <= 0) {
                    ((Timer) e.getSource()).stop();

                    // 승패 판정
                    int myScore = Integer.valueOf(scorePanel.getScore(playerType));
                    int otherScore = Integer.valueOf(scorePanel.getScore(1 - playerType));
                    String result;

                    System.out.println("mysc : " + myScore);
                    System.out.println("othersc : " + otherScore);

                    if(myScore == otherScore) result = "비겼습니다.";
                    else result = (myScore > otherScore) ? "승리!!" : "패배했습니다.";
                    result += "\n나의 스코어: " + myScore;
                    result += "\n상대 스코어: " + otherScore;

                    // '나가기' 버튼을 생성합니다.
                    Object[] options = {"나가기"};

                    // 커스텀 대화 상자를 표시합니다.
                    int choice = JOptionPane.showOptionDialog(null, result, "알림",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,null, options, options[0]);

                    // 게임 종료 처리
                    if (choice == 0 || choice == -1) { // 나가기 버튼을 눌렀거나 x 를 눌렀을 때
                        Map<String, Object> request = new HashMap<>();

                        request.put("command", "방 나가기");
                        request.put("playerId", MainFrame.playerId);
                        request.put("roomId", MainFrame.roomId);
                        MainFrame.dataTranslatorWrapper.broadcast(request);

                        MainFrame.roomId = 0; //현재 플레이어의 roomId를 0으로 초기화
                        timeLeft = 60; // 타이머 시간 초기화
                    }
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

