package Client.GamePanel;

import Client.MainFrame;
import Client.MainPanel.MainChatPanel;
import Client.RoomPanel.RoomChatPanel;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class GameChatPanel extends JPanel {

    JButton button;
    JTextField textField;
    Thread gameChatThread;

    public GameChatPanel(){
        setLayout(new BorderLayout());

        // 채팅 내역이 표시될 JTextArea
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        // 채팅 내역 영역을 스크롤 가능하게 함
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(340, 700)); // 패널 전체 크기의 90% 할당
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 채팅 입력 영역
        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.setPreferredSize(new Dimension(340, 80)); // 패널 전체 크기의 10% 할당
        chatInputPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // 테두리

        // 채팅 입력 필드
        textField = new JTextField();
        textField.addActionListener(new RoomSendChatAction());
        chatInputPanel.add(textField, BorderLayout.CENTER);

        // 채팅 전송 버튼 이미지
        Image scaledImage1 = new ImageIcon("images/send-icon.png").getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        ImageIcon imageIcon = new ImageIcon(scaledImage1);

        // 채팅 전송 버튼
        button = new JButton(imageIcon);
        button.addActionListener(new RoomSendChatAction());
        chatInputPanel.add(button, BorderLayout.EAST);

        // 채팅 영역과 채팅 입력 영역을 프레임에 추가
        add(scrollPane, BorderLayout.NORTH);
        add(chatInputPanel, BorderLayout.SOUTH);

        setVisible(true);

        //방 채팅 문자를 받는 스레드
        gameChatThread = new Thread(() -> {

            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.GAME_CHAT_UI_UPDATE_SERVER);

            while (true) {
                Map<String, Object> response = dataTranslator.receiveData();
                if(response == null) break;
                String command = (String) response.get("command");

                if (command.equals("game_chat")) {
                    String message = (String) response.get("message");

                    textArea.append(message);
                    textArea.setCaretPosition(textArea.getText().length());
                } else if(command.equals("game_exit")) return; // 게임 종료 시 스레드 종료
            }
        });
    }

    public void startGameChatThread(){gameChatThread.start();}

    class RoomSendChatAction implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == button || e.getSource() == textField) {
                String message =  String.format("[%s] %s\n", "플레이어 " + MainFrame.playerId, textField.getText());

                Map<String, Object> request = new HashMap<>();

                request.put("command", "game_chat");
                request.put("roomId", MainFrame.roomId);
                request.put("message", message);

                MainFrame.dataTranslatorWrapper.broadcast(request);

                textField.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
                textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
            }
        }
    }
}
