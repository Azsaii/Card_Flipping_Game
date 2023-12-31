package Client.GamePanel.Score;

import javax.swing.*;
import java.awt.*;

import static Client.GamePanel.GameScreenPanel.PLAYER1;
import static Client.GamePanel.GameScreenPanel.PLAYER2;

/**
 * 게임 내 스코어를 붙이고 변경을 전담하는 클래스
 */
public class ScorePanel extends JPanel {

    static final int DEFAULT_SCORE_ADD = 10; // 카드 뒤집을 시 점수

    private ScoreStrategy strategy1; // p1 스코어 추가 모드
    private ScoreStrategy strategy2; // p2 스코어 추가 모드

    JLabel score1;  // 플레이어1 스코어
    JLabel score2;  // 플레이어2 스코어

    public ScorePanel() {
        strategy1 = new DefaultScoreStrategy();  // 게임모드 초기화
        strategy2 = new DefaultScoreStrategy();  // 게임모드 초기화

        setLayout(new GridLayout(0,3));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setOpaque(false);

        JPanel p1 = new JPanel(new GridBagLayout());
        JPanel p2 = new JPanel(new GridBagLayout());
        JPanel p3 = new JPanel(new GridBagLayout());

        p1.setOpaque(false);
        p2.setOpaque(false);
        p3.setOpaque(false);

        score1 = new JLabel("0");
        score2 = new JLabel("0");

        score1.setForeground(new Color(69, 167, 94));
        score2.setForeground(new Color(240, 69, 245));
        JLabel scoreText = new JLabel("SCORE");

        addScorePanel(p1, score1, JLabel.CENTER);
        addScorePanel(p2, scoreText, JLabel.CENTER);
        addScorePanel(p3, score2, JLabel.CENTER);

        add(p1);
        add(p2);
        add(p3);
    }

    public void setStrategy(ScoreStrategy strategy, int playerType) {
        if(playerType == PLAYER1) strategy1 = strategy;
        else strategy2 = strategy;
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

    // 스코어 추가, 소모
    public void addScore(int score, int playerType) {
        ScoreStrategy targetStrategy = (playerType == PLAYER1) ? strategy1 : strategy2;

        // 반영할 스코어 패널 지정
        JLabel targetLable = score1;
        if(playerType == PLAYER1) targetLable = score1;
        else if(playerType == PLAYER2) targetLable = score2;
        
        if(score >= 0) score *= targetStrategy.getScore(); // 추가 스코어가 양수인 경우에만 적용
        else score *= DefaultScoreStrategy.getInstance().getScore();
        
        int newScore = Integer.valueOf(targetLable.getText()) + score;
        if(newScore < 0) newScore = 0;
        targetLable.setText(String.valueOf(newScore));
    }

    // 카드 뒤집었을 때 스코어 추가
    public void addCardScore(int playerType){
        ScoreStrategy targetStrategy = (playerType == PLAYER1) ? strategy1 : strategy2;
        JLabel targetLable = score1;
        if(playerType == PLAYER1) targetLable = score1;
        else if(playerType == PLAYER2) targetLable = score2;

        int newScore = Integer.valueOf(targetLable.getText()) + targetStrategy.getScore();
        targetLable.setText(String.valueOf(newScore));
    }

    // 스코어 반환
    public String getScore(int playerType) {
        if(playerType == PLAYER1) return score1.getText();
        else if(playerType == PLAYER2) return score2.getText();
        else return "0";
    }
}




