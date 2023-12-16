package Client.RoomPanel;

import javax.swing.*;
import java.awt.*;

public class RoomBottomPanel extends JPanel {

    RoomBottomPanel(long playerId) {

        setLayout(new GridLayout(1, 2, 10, 0));
        setOpaque(false);
        RoomChatPanel roomChatPanel = new RoomChatPanel();
        add(roomChatPanel);
        add(new RoomControlPanel(playerId, roomChatPanel));
    }
}
