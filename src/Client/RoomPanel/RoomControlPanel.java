package Client.RoomPanel;

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

public class RoomControlPanel extends JPanel {

    RoomControlPanel(long playerId) {

        setLayout(new GridLayout());

        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());

        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 3.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        ImageIcon exitIcon = new ImageIcon("images/exit-icon.png");
        JButton exitButton = new JButton(exitIcon);

        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 0, 0);
        panel2.add(exitButton, gbc);

        ImageIcon readyFillIcon = new ImageIcon("images/ready-icon-fill.png");
        ImageIcon readyOutlineIcon = new ImageIcon("images/ready-icon-outline.png");

        JButton readyButton = new JButton(readyOutlineIcon);

        readyButton.setOpaque(false);
        readyButton.setContentAreaFilled(false);
        readyButton.setBorderPainted(false);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 5);
        panel2.add(readyButton, gbc);

        ImageIcon startIcon = new ImageIcon("images/start-icon.png");

        JButton startButton = new JButton(startIcon);

        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel1.add(startButton, gbc);

        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();


                Map<String, Object> request = new HashMap<>();

                if(button.getIcon() == readyOutlineIcon) {
                    request.put("command", "게임 준비");
                    button.setIcon(readyFillIcon);
                } else if (button.getIcon() == readyFillIcon) {
                    request.put("command", "게임 준비 미완료");
                    button.setIcon(readyOutlineIcon);
                }

                request.put("playerId", MainFrame.playerId);
                request.put("roomId", MainFrame.roomId);

                MainFrame.dataTranslatorWrapper.broadcast(request);
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Map<String, Object> request = new HashMap<>();

                request.put("command", "게임 시작");
                request.put("playerId", playerId);
                request.put("roomId", MainFrame.roomId);
                System.out.println("roomid=" + MainFrame.roomId);

                MainFrame.dataTranslatorWrapper.broadcast(request);
            }
        });

        startButton.setEnabled(false);

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Map<String, Object> request = new HashMap<>();

                request.put("command", "방 나가기");
                request.put("playerId", MainFrame.playerId);
                request.put("roomId", MainFrame.roomId);
                MainFrame.dataTranslatorWrapper.broadcast(request);

                MainFrame.roomId = 0; //현재 플레이어의 roomId를 0으로 초기화
                readyButton.setEnabled(true); //준비 버튼 비활성화
                readyButton.setIcon(readyOutlineIcon);
                startButton.setEnabled(false);
            }
        });


        add(panel1);


        Thread updateRoomControlThread = new Thread(() -> {
            System.out.println("방 컨트롤 업데이트 스레드 작동");

            while (true) {
                DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.ROOM_CONTROL_UI_UPDATE_SERVER);

                Map<String, Object> response = dataTranslator.receiveData();
                String command = (String) response.get("command");

                if (command.equals("게임 시작 UI 업데이트")) {
                    String result = (String) response.get("result");
                    if(result.equals("OK")) {
                        startButton.setEnabled(true);
                    } else if (result.equals("FAIL")) {
                        startButton.setEnabled(false);
                    }
                }
            }
        });

        updateRoomControlThread.start();

    }
}
