package Client.RoomPanel;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;
import Server.Data.GameRoom;
import Server.Data.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 게임 방에서 플레이어 목록을 업데이트한느 클래스
 */
public class RoomPlayerListPanel extends JPanel {

    RoomPlayerListPanel() {

        setLayout(new GridLayout(0, 2, 10, 0));
        setOpaque(false);


        Thread updatePlayerStatusThread = new Thread(() -> {
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.PLAYER_STATUS_UI_UPDATE_SERVER);

            while (true) {

                Map<String, Object> response = dataTranslator.receiveData();
                if(response == null) break;
                   String command = (String) response.get("command");

                    if (command.equals("player_info_update")) {

                        GameRoom gameRoom = (GameRoom) response.get("GameRoom");

                        RoomPlayerListPanel.this.removeAll();

                        for (Player player : gameRoom.getPlayers()) {

                            // 패널 생성
                            final JPanel mainPanel = new JPanel();
                            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
                            mainPanel.setBorder(border);
                            mainPanel.setOpaque(false);
                            mainPanel.setLayout(new BorderLayout(0, 0));

                            JPanel southPanel = new JPanel();
                            southPanel.setLayout(new BorderLayout(0, 0));
                            southPanel.setOpaque(false);

                            // 방장 표시 아이콘 추가
                            ImageIcon crownImageIcon = null;
                            if(gameRoom.getLeader() == player) {
                                crownImageIcon = new ImageIcon("images/crown-icon-fill.png");
                            }else {
                                crownImageIcon = new ImageIcon("images/crown-icon-outline.png");
                            }
                            JLabel crownImg = new JLabel(crownImageIcon);

                            // 프로필 아이콘 추가
                            ImageIcon profileImageIcon = new ImageIcon("images/profile/" + (player.getRandomProfileImage()) + ".png");
                            JLabel playerProfile = new JLabel(profileImageIcon);

                            // 레디 아이콘 추가
                            ImageIcon readyImageIcon = null;
                            if(player.isReady()) {
                                readyImageIcon = new ImageIcon("images/ready-italic-icon-fill.png");
                            }else {
                                readyImageIcon = new ImageIcon("images/ready-italic-icon-outline.png");
                            }
                            JLabel readyLabel = new JLabel(readyImageIcon);

                            // 패널에 붙이기
                            mainPanel.add(playerProfile, BorderLayout.CENTER);
                            mainPanel.add(crownImg, BorderLayout.NORTH);
                            mainPanel.add(southPanel, BorderLayout.SOUTH);

                            JLabel playerNameLabel = new JLabel();
                            playerNameLabel.setHorizontalAlignment(0);
                            playerNameLabel.setText(player.getName());

                            Font font = new Font(playerNameLabel.getFont().getName(), Font.BOLD, 20);
                            playerNameLabel.setFont(font);

                            southPanel.add(playerNameLabel, BorderLayout.NORTH);
                            southPanel.add(readyLabel, BorderLayout.CENTER);

                            RoomPlayerListPanel.this.add(mainPanel);
                            RoomPlayerListPanel.this.revalidate();
                            RoomPlayerListPanel.this.repaint();
                        }

                    }
            }
        });
        updatePlayerStatusThread.start();
    }
}