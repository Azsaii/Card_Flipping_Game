package Client.MainPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 메인화면 채팅 패널, 방생성/나가기 버튼이 있는 패널을 붙이는 패널
 */
public class  MainControlPanel extends JPanel {


    private MainRoomButtonPanel roomControlPanel;
    private MainChatPanel mainChatPanel;

    public MainControlPanel() {

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel gameTitle = new JLabel("Card Flipping Game");
        Font gameTitleFont = gameTitle.getFont();

        gameTitle.setFont(gameTitleFont.deriveFont(Font.BOLD, 50));
        gameTitle.setHorizontalAlignment(0);

        JLabel mainChatTitle = new JLabel("전체 채팅방");
        Font mainChatTitleFont = mainChatTitle.getFont();

        mainChatTitle.setFont(mainChatTitleFont.deriveFont(Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.fill = GridBagConstraints.BOTH;

        add(gameTitle, gbc);

        gbc.gridy = 1;
        gbc.weighty = 2;
        roomControlPanel = new MainRoomButtonPanel();
        add(roomControlPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        add(mainChatTitle, gbc);

        gbc.gridy = 3;
        gbc.weighty = 4;
        mainChatPanel = new MainChatPanel();
        add(mainChatPanel, gbc);

    }
}
