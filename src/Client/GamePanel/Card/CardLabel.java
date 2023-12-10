package Client.GamePanel.Card;

import javax.swing.*;

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

