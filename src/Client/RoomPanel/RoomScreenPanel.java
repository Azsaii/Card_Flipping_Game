package Client.RoomPanel;

import javax.swing.*;
import java.awt.*;

public class RoomScreenPanel extends JPanel {

    public RoomScreenPanel(long playerId) {
        setLayout(new GridLayout(2, 1, 0, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new RoomPlayerListPanel());
        add(new RoomBottomPanel(playerId));
    }

}
