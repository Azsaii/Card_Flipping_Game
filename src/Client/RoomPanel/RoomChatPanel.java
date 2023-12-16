package Client.RoomPanel;

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
 * 방 채팅 클래스
 */
public class RoomChatPanel extends ChatPanel {
    public RoomSendChatAction roomSendChatAction; // 메시지 보내는 이벤트 클래스
    RoomChatPanel() {
        roomSendChatAction = new RoomSendChatAction(button, textField);
        roomSendChatAction.setCommand("room_chat"); // 메시지 커맨드 설정
        super.button.addActionListener(roomSendChatAction);
        super.textField.addActionListener(roomSendChatAction);
        
        // 방 채팅 문자를 받는 스레드 시작
        // 채팅을 받아 textPanem 에 추가한다.
        new ChatThread("room_chat_update", ServerName.ROOM_CHAT_UI_UPDATE_SERVER, super.textPane).start();
    }
}
