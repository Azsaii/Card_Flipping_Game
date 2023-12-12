package Client.RoomPanel;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;
import Server.Data.GameRoom;
import Server.Data.Player;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Map;

public class RoomPlayerListPanel extends JPanel {


    RoomPlayerListPanel() {

        setLayout(new GridLayout(0, 2, 10, 0));

        Thread updatePlayerStatusThread = new Thread(() -> {
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.PLAYER_STATUS_UI_UPDATE_SERVER);

            while (true) {

                Map<String, Object> response = dataTranslator.receiveData();
                if(response == null) break;
                   String command = (String) response.get("command");

                    if (command.equals("플레이어 정보 업데이트")) {

                        GameRoom gameRoom = (GameRoom) response.get("GameRoom");

                        RoomPlayerListPanel.this.removeAll();

                        for (Player player : gameRoom.getPlayers()) {

                            final JPanel panel = new JPanel();
                            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
                            panel.setBorder(border);

                            panel.setLayout(new BorderLayout(0, 0));

                            ImageIcon imageIcon = null;

                            if(gameRoom.getLeader() == player) {
                                imageIcon = new ImageIcon("images/crown-icon-fill.png");
                            }else {
                                imageIcon = new ImageIcon("images/crown-icon-outline.png");
                            }

                            JLabel crownImg = new JLabel(imageIcon);
                            panel.add(crownImg, BorderLayout.NORTH);

                            imageIcon = new ImageIcon("images/profile/" + (player.getRandomProfileImage()) + ".png");

                            JLabel playerProfile = new JLabel(imageIcon);

                            panel.add(playerProfile, BorderLayout.CENTER);

                            JPanel panel1 = new JPanel();
                            panel1.setLayout(new BorderLayout(0, 0));

                            panel.add(panel1, BorderLayout.SOUTH);


                            JLabel label = new JLabel();
                            label.setHorizontalAlignment(0);
                            label.setText(player.getName());

                            Font font = new Font(label.getFont().getName(), Font.BOLD, 20);
                            label.setFont(font);

                            panel1.add(label, BorderLayout.NORTH);


                            if(player.isReady()) {
                                imageIcon = new ImageIcon("images/ready-italic-icon-fill.png");
                            }else {
                                imageIcon = new ImageIcon("images/ready-italic-icon-outline.png");
                            }

                            JLabel label2 = new JLabel(imageIcon);

                            panel1.add(label2, BorderLayout.CENTER);

                            RoomPlayerListPanel.this.add(panel);
                            RoomPlayerListPanel.this.revalidate();
                            RoomPlayerListPanel.this.repaint();
                        }

                    }
            }
        });
        updatePlayerStatusThread.start();
    }
}