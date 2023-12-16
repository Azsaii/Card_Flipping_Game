package Client.GamePanel.ItemStore;

import javax.swing.*;
import java.awt.*;

/**
 * 상점 이미지 패널입니다.
 */
public class ItemStoreTitlePanel extends JPanel {

    public ItemStoreTitlePanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        JLabel gameTitle = new JLabel("Card Flipping Game");
        Font gameTitleFont = gameTitle.getFont();

        gameTitle.setFont(gameTitleFont.deriveFont(Font.BOLD, 30));
        gameTitle.setHorizontalAlignment(JLabel.CENTER);
        add(gameTitle, BorderLayout.CENTER);
    }
}
