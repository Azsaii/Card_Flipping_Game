package Client.MainPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 메인 화면 패널입니다.
 * RoomListPanel: 게임 방 리스트가 보이는 패널
 * MainControlPanel: 방 생성, 나가기 버튼이 있는 패널과 채팅 패널을 붙이는 패널
 */
public class MainScreenPanel extends JPanel {
    public MainScreenPanel() {

        setLayout(new GridLayout(1, 2));

        add(new RoomListPanel());
        add(new MainControlPanel());
    }
}
