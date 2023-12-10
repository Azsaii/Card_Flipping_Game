package Client.GamePanel.Score;

import static Client.GamePanel.Score.ScorePanel.DEFAULT_SCORE_ADD;

public class DefaultScoreStrategy implements ScoreStrategy {
    @Override
    public int getScore() {
        return DEFAULT_SCORE_ADD;
    }
}
