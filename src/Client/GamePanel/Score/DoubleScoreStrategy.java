package Client.GamePanel.Score;

import static Client.GamePanel.Score.ScorePanel.DEFAULT_SCORE_ADD;

/**
 * 더블 이벤트 아이템 사용 시 획득 스코어를 2배로 리턴하는 클래스
 */
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
