package Client.RoomPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 버튼들이 있는 패널과 채팅 패널을 붙이는 클래스
 */
public class RoomBottomPanel extends JPanel {

    RoomBottomPanel(long playerId) {

        setLayout(new GridLayout(1, 2, 10, 0));
        setOpaque(false);
        RoomChatPanel roomChatPanel = new RoomChatPanel();
        add(roomChatPanel);
        add(new RoomControlPanel(playerId, roomChatPanel));
    }
}
