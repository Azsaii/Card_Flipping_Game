package Client.GamePanel.Score;

import static Client.GamePanel.Score.ScorePanel.DEFAULT_SCORE_ADD;

public class DoubleScoreStrategy implements ScoreStrategy{

    private static DoubleScoreStrategy instance;

    public static DoubleScoreStrategy getInstance(){
        if(instance == null) instance = new DoubleScoreStrategy();
        return instance;
    }
    @Override
    public int getScore() {
        return DEFAULT_SCORE_ADD * 2;
    }
}
