package GamePanel.Score;

import javax.swing.*;
import java.awt.*;

import static GamePanel.GameScreenPanel.PLAYER1;
import static GamePanel.GameScreenPanel.PLAYER2;

public class ScorePanel extends JPanel {

    private long playerId = PLAYER1;   // 플레이어 식별. 임시로 1로 지정됨.

    static final int DEFAULT_SCORE_ADD = 10; // 카드 뒤집을 시 점수

    private ScoreStrategy strategy; // 스코어 추가 모드

    JLabel score1;  // 플레이어1 스코어
    JLabel score2;  // 플레이어2 스코어

    public ScorePanel() {
        strategy = new DefaultScoreStrategy();  // 게임모드 초기화

        setLayout(new GridLayout(0,3));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel p1 = new JPanel(new GridBagLayout());
        JPanel p2 = new JPanel(new GridBagLayout());
        JPanel p3 = new JPanel(new GridBagLayout());

        score1 = new JLabel("0");
        score2 = new JLabel("0");
        JLabel scoreText = new JLabel("SCORE");

        addScorePanel(p1, score1, JLabel.CENTER);
        addScorePanel(p2, scoreText, JLabel.CENTER);
        addScorePanel(p3, score2, JLabel.CENTER);

        add(p1);
        add(p2);
        add(p3);
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
    public void setStrategy(ScoreStrategy strategy) {
        this.strategy = strategy;
    }

    // 패널에 레이블 추가
    private void addScorePanel(JPanel panel, JLabel scoreLable, int alignOption){
        scoreLable.setHorizontalAlignment(alignOption); // 텍스트 정렬
        Font labelFont = scoreLable.getFont();
        scoreLable.setFont(new Font(labelFont.getName(), Font.PLAIN, 40)); // 글꼴 크기를 20으로 설정
        scoreLable.setHorizontalAlignment(JLabel.CENTER);
        scoreLable.setVerticalAlignment(JLabel.CENTER);
        panel.add(scoreLable);
    }

    // 스코어 갱신
    private void setScore(int addScore) {
        JLabel targetLable = score1;
        if(playerId == PLAYER1) targetLable = score1;
        else if(playerId == PLAYER2) targetLable = score2;
        
        int newScore = Integer.valueOf(targetLable.getText()) + addScore;
        targetLable.setText(String.valueOf(newScore));
    }

    // 모드에 따라 스코어 추가
    public void updateScore(int playerId) {
        this.playerId = playerId;
        setScore(strategy.getScore());
    }

    // 스코어 반환
    public String getScore() {
        if(playerId == PLAYER1) return score1.getText();
        else if(playerId == PLAYER2) return score2.getText();
        else return "0";
    }
}




