package Client.MainPanel;

import Client.Chat.ChatPanel;
import Client.Chat.ChatThread;
import Client.Chat.RoomSendChatAction;
import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * 메인 화면 채팅 패널
 */
public class MainChatPanel extends ChatPanel {
    public RoomSendChatAction roomSendChatAction; // 메시지 보내는 이벤트 클래스
    public MainChatPanel() {

        roomSendChatAction = new RoomSendChatAction(button, textField);
        roomSendChatAction.setCommand("all_chat"); // 메시지 커맨드 설정
        super.button.addActionListener(roomSendChatAction);
        super.textField.addActionListener(roomSendChatAction);

        // 메인 화면 채팅 문자를 받는 스레드 시작
        new ChatThread("all_chat_update", ServerName.CHAT_UI_UPDATE_SERVER, super.textPane).start();
    }
}
