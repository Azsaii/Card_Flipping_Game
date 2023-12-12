package Client.RoomPanel;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class RoomChatPanel extends JPanel {
    JTextArea textArea;
    JTextField textField;
    JButton button;
    RoomChatPanel() {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel chatBottomPanel = new JPanel();
        chatBottomPanel.setLayout(new BorderLayout());

        textField = new JTextField(30);
        textArea.setEditable(false);

        ImageIcon imageIcon = new ImageIcon("images/send-icon.png");

        button = new JButton(imageIcon);

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);


        chatBottomPanel.add("Center", textField);
        chatBottomPanel.add("East", button);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 3;
        gbc.fill = GridBagConstraints.BOTH;

        add(scrollPane, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1;

        add(chatBottomPanel, gbc);


        button.addActionListener(new RoomSendChatAction());
        textField.addActionListener(new RoomSendChatAction());

        //방 채팅 문자를 받는 스레드
        Thread RoomChatThread = new Thread(() -> {
            System.out.println("방 채팅 업데이트 스레드 작동");
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.ROOM_CHAT_UI_UPDATE_SERVER);

            while (true) {
                Map<String, Object> response = dataTranslator.receiveData();
                if(response == null) break;
                String command = (String) response.get("command");

                if (command.equals("방 채팅 업데이트")) {
                    String message = (String) response.get("message");

                    textArea.append(message);
                    textArea.setCaretPosition(textArea.getText().length());
                } else if (command.equals("방 채팅 지우기")) {
                    textArea.setText("");
                }
            }
        });

        RoomChatThread.start();
    }


    class RoomSendChatAction implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == button || e.getSource() == textField) {
                System.out.println("방 채팅 작동");
                String message =  String.format("[%s] %s\n", "플레이어 " + MainFrame.playerId, textField.getText());

                Map<String, Object> request = new HashMap<>();

                request.put("command", "방 채팅");
                request.put("roomId", MainFrame.roomId);
                request.put("message", message);

                MainFrame.dataTranslatorWrapper.broadcast(request);

                textField.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
                textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
            }
        }
    }
}
