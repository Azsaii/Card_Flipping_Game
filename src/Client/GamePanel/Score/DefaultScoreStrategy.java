package Client.GamePanel.Score;

import static Client.GamePanel.Score.ScorePanel.DEFAULT_SCORE_ADD;

/**
 * 기본 스코어를 리턴하는 클래스
 */
public class DefaultScoreStrategy implements ScoreStrategy {

    private static DefaultScoreStrategy instance;

    public static DefaultScoreStrategy getInstance(){
        if(instance == null) instance = new DefaultScoreStrategy();
        return instance;
    }
    @Override
    public int getScore() {
        return DEFAULT_SCORE_ADD;
    }
}
