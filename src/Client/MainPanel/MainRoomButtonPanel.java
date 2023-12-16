package Client.MainPanel;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;
import Server.Data.Player;
import Server.Manager.GameRoomManager;
import Server.Manager.PlayerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 방 생성, 게임 종료(나가기) 버튼이 있는 패널
 */
public class MainRoomButtonPanel extends JPanel  {

    public MainRoomButtonPanel() {

        setOpaque(false);
        setLayout(new GridLayout(0, 2));

        ImageIcon createRoomIcon = new ImageIcon("images/createRoom-icon.png");
        ImageIcon exitIcon = new ImageIcon("images/exit-icon.png");

        JButton createRoomButton = setMainRoomButtons(createRoomIcon);
        JButton exitButton = setMainRoomButtons(exitIcon);

        // 방 생성 로직 처리
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<String, Object> request = new HashMap<>();

                request.put("command", "room_add");
                request.put("playerId", MainFrame.playerId);

                MainFrame.dataTranslatorWrapper.broadcast(request);

                DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.GAME_ROOM_DATA_SERVER);
                Map<String, Object> response = dataTranslator.receiveData();

                MainFrame.roomId = (long) response.get("roomId");
                System.out.println("MainFrame.rooId = " + MainFrame.roomId);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.exitGame();
                System.exit(0);
            }
        });
    }

    private JButton setMainRoomButtons(ImageIcon icon){
        JButton btn = new JButton(icon);

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);

        add(btn);
        return btn;
    }
}
