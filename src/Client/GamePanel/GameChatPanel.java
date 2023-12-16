package Client.GamePanel;

import Client.Chat.ChatThread;
import Client.Chat.RoomSendChatAction;
import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.*;
import java.awt.*;

public class GameChatPanel extends JPanel {

    private JTextPane textPane;  // JTextArea 대신 JTextPane 사용
    private JTextField textField;
    private JScrollPane scrollPane;
    private ChatThread chatThread;

    public RoomSendChatAction roomSendChatAction;
    public GameChatPanel(){

        setLayout(null);  // 배치 관리자 제거
        setOpaque(false);

        textPane = new JTextPane();  // JTextPane 생성
        scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 5, 0), // 컴포넌트에 여백 추가
                BorderFactory.createLineBorder(Color.BLACK) // 테두리 추가
        ));

        textField = new JTextField();
        textField.setMargin(new Insets(5, 5, 5, 5));  // 텍스트 필드의 내부 여백 설정
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0), // 컴포넌트에 여백 추가
                BorderFactory.createLineBorder(Color.BLACK) // 테두리 추가
        ));

        // 폰트 설정
        Font font = new Font("돋움", Font.PLAIN, 15);
        textPane.setFont(font);
        textField.setFont(font);
        textPane.setMargin(new Insets(5, 5, 5, 5));

        add(scrollPane);
        add(textField);

        // 메시지 전송 이벤트 클래스
        roomSendChatAction = new RoomSendChatAction(textField);
        roomSendChatAction.setCommand("game_chat");
        textField.addActionListener(roomSendChatAction);

        //방 채팅 문자를 받는 스레드 생성
        chatThread = new ChatThread("game_chat", ServerName.GAME_CHAT_UI_UPDATE_SERVER, textPane);
    }

    // 컴포넌트 그리기 관련 메서드
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int margin = 5;  // 마진 설정
        int gap = 5;  // 컴포넌트 사이의 간격 설정
        int width = 325;
        int height1 = 700;
        int height2 = 50;

        scrollPane.setBounds(margin, margin, width, height1);
        textField.setBounds(margin, height1 + margin + gap, width, height2);
    }

    public void startGameChatThread(){chatThread.start();} // 게임 채팅 스레드 작동
}
