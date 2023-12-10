package Client.MainPanel;

import javax.swing.*;
import java.awt.*;


public class MainScreenPanel extends JPanel {
    public MainScreenPanel() {

        setLayout(new GridLayout(1, 2));

        add(new RoomListPanel());
        add(new MainControlPanel());
    }
}
