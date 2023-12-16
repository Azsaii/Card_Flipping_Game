package Client.GamePanel;

import Client.Chat.ChatThread;
import Client.Chat.RoomSendChatAction;
import Client.Chat.RoundBorder;
import Network.ServerName;

import javax.swing.*;
import java.awt.*;

/**
 * 게임 내 채팅 담당 클래스
 */
public class GameChatPanel extends JPanel {

    private JLabel gameTitle;
    private JTextPane textPane;  // JTextArea 대신 JTextPane 사용
    private JTextField textField;
    private JScrollPane scrollPane;
    private ChatThread chatThread;

    public RoomSendChatAction roomSendChatAction;
    public GameChatPanel(){

        setLayout(null);  // 배치 관리자 제거
        setOpaque(false);

        gameTitle = new JLabel("Chatting");
        Font gameTitleFont = gameTitle.getFont();

        gameTitle.setFont(gameTitleFont.deriveFont(Font.BOLD, 30));
        gameTitle.setHorizontalAlignment(JLabel.CENTER);
        add(gameTitle);

        textPane = new JTextPane();
        textField = new JTextField();

        textPane.setMargin(new Insets(5, 5, 5, 5));
        scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);

        // 폰트 설정
        Font font = new Font("돋움", Font.PLAIN, 15);
        textPane.setFont(font);
        textField.setFont(font);

        // 둥근 테두리 설정
        textPane.setBorder(new RoundBorder(5));
        textField.setBorder(new RoundBorder(5));

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
        int height0 = 50;
        int height1 = 650;
        int height2 = 45;

        gameTitle.setBounds(margin, margin, width, height0);
        scrollPane.setBounds(margin, margin + height0, width, height1);
        textField.setBounds(margin, height0 + height1 + margin + gap, width, height2);
    }

    public void startGameChatThread(){chatThread.start();} // 게임 채팅 스레드 작동
}
