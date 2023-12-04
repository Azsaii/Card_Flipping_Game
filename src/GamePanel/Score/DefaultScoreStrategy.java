package GamePanel.Score;

import static GamePanel.Score.ScorePanel.DEFAULT_SCORE_ADD;

public class DefaultScoreStrategy implements ScoreStrategy {
    @Override
    public int getScore() {
        return DEFAULT_SCORE_ADD;
    }
}
