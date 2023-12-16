package Client.GamePanel.Card;

import javax.swing.*;

/**
 * 카드의 JLabel 에 부가정보를 더하기 위한 클래스
 */
public class CardLabel extends JLabel {
    private int colorState;

    public CardLabel(ImageIcon icon, int colorState) {
        super(icon);
        this.colorState = colorState;
    }

    public int getColorState() {
        return colorState;
    }

    public void setColorState(int colorState) {
        this.colorState = colorState;
    }
}

