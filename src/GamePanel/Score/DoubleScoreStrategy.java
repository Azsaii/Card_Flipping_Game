package GamePanel.Score;

import static GamePanel.Score.ScorePanel.DEFAULT_SCORE_ADD;

public class DoubleScoreStrategy implements ScoreStrategy{
    @Override
    public int getScore() {
        return DEFAULT_SCORE_ADD * 2;
    }
}
