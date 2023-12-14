package Client.MainPanel;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 메인 화면 채팅 패널
 */
public class MainChatPanel extends JPanel {
    JButton button;
    JTextField textField;

    public MainChatPanel() {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JTextArea textArea = new JTextArea(10, 30);
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

        Thread RoomChatThread = new Thread(() -> {
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.CHAT_UI_UPDATE_SERVER);

            while (true) {
                Map<String, Object> response = dataTranslator.receiveData();
                if(response == null) break;
                String command = (String) response.get("command");

                if (command.equals("전체 채팅 업데이트")) {
                    String message = (String) response.get("message");

                    textArea.append(message);
                    textArea.setCaretPosition(textArea.getText().length());
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
                String message =  String.format("[%s] %s\n", "플레이어 " + MainFrame.playerId, textField.getText());

                Map<String, Object> request = new HashMap<>();

                request.put("command", "전체 채팅");
                request.put("message", message);

                MainFrame.dataTranslatorWrapper.broadcast(request);

                textField.setText(""); // 채팅 메세지를 보내고 나면 채팅 메세지 쓰는 textField은 비운다.
                textField.requestFocus(); //채팅 메세지 보내고 커서를 다시 textField 필드로 위치시킨다
            }
        }
    }
}
