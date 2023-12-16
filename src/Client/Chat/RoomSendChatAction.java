package Client.Chat;

import Client.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 채팅 패널에서 텍스트 입력 후 엔터를 눌렀을 때 서버로 메시지 전송 이벤트 클래스
 */
public class RoomSendChatAction implements ActionListener
{
    private JButton button;
    private JTextField textField;
    private String command;

    public RoomSendChatAction(JButton button, JTextField textField){
        this.button = button;
        this.textField = textField;
    }

    public RoomSendChatAction(JTextField textField){
        this.button = new JButton();
        this.textField = textField;
    }

    public void setCommand(String command){
        this.command = command;
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == button || e.getSource() == textField) {
            String inputText = textField.getText().trim();  // 입력받은 텍스트에서 앞뒤 공백 제거

            // 텍스트 필드가 비어있다면 메시지를 보내지 않고 리턴
            if (inputText.isEmpty()) return;

            String message =  String.format("[%s] %s\n", "플레이어 " + MainFrame.playerId, textField.getText());
            Map<String, Object> request = new HashMap<>();

            request.put("command", command);
            request.put("roomId", MainFrame.roomId);
            request.put("message", message);

            MainFrame.dataTranslatorWrapper.broadcast(request);

            textField.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
            textField.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
        }
    }
}
