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

public class MainRoomButtonPanel extends JPanel  {

    public MainRoomButtonPanel() {

        setLayout(new GridLayout(0, 2));

        ImageIcon createRoomIcon = new ImageIcon("images/createRoom-icon.png");

        JButton createRoomButton = new JButton(createRoomIcon);

        createRoomButton.setOpaque(false);
        createRoomButton.setContentAreaFilled(false);
        createRoomButton.setBorderPainted(false);

        // 방 생성 로직 처리
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<String, Object> request = new HashMap<>();

                request.put("command", "방 생성");
                request.put("playerId", MainFrame.playerId);

                MainFrame.dataTranslatorWrapper.broadcast(request);

                DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.GAME_ROOM_DATA_SERVER);
                Map<String, Object> response = dataTranslator.receiveData();

                MainFrame.roomId = (long) response.get("roomId");
                System.out.println("MainFrame.rooId = " + MainFrame.roomId);
            }
        });

        add(createRoomButton);

        ImageIcon exitIcon = new ImageIcon("images/exit-icon.png");

        JButton exitButton = new JButton(exitIcon);

        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(exitButton);
    }
}
