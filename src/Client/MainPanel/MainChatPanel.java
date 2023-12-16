package Client.MainPanel;

import Client.Chat.ChatPanel;
import Client.Chat.ChatThread;
import Client.Chat.ChatSendAction;
import Network.ServerName;

/**
 * 메인 화면 채팅 패널
 */
public class MainChatPanel extends ChatPanel {
    public ChatSendAction chatSendAction; // 메시지 보내는 이벤트 클래스
    public MainChatPanel() {

        chatSendAction = new ChatSendAction(button, textField);
        chatSendAction.setCommand("all_chat"); // 메시지 커맨드 설정
        super.button.addActionListener(chatSendAction);
        super.textField.addActionListener(chatSendAction);

        // 메인 화면 채팅 문자를 받는 스레드 시작
        new ChatThread("all_chat_update", ServerName.ALL_CHAT_UI_UPDATE_SERVER, super.textPane).start();
    }
}
