package Client.RoomPanel;

import Client.Chat.ChatPanel;
import Client.Chat.ChatThread;
import Client.Chat.ChatSendAction;
import Network.ServerName;

/**
 * 방 채팅 담당 클래스
 */
public class RoomChatPanel extends ChatPanel {
    public ChatSendAction chatSendAction; // 메시지 보내는 이벤트 클래스
    RoomChatPanel() {
        chatSendAction = new ChatSendAction(button, textField);
        chatSendAction.setCommand("room_chat"); // 메시지 커맨드 설정
        super.button.addActionListener(chatSendAction);
        super.textField.addActionListener(chatSendAction);
        
        // 방 채팅 문자를 받는 스레드 시작
        // 채팅을 받아 textPanem 에 추가한다.
        new ChatThread("room_chat_update", ServerName.ROOM_CHAT_UI_UPDATE_SERVER, super.textPane).start();
    }
}
