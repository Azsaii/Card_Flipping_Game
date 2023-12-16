package Client.Chat;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.util.Map;

/**
 * 서버로부터 메시지를 받아 채팅 패널에 내역을 추가하는 스레드
 */
public class ChatThread extends Thread {

    private String responseCommand;
    private JTextPane textPane;
    private ServerName serverName;
    private boolean isFirstMessage = true;

    public ChatThread(String responseCommand, ServerName serverName, JTextPane textPane){
        this.responseCommand = responseCommand;
        this.serverName = serverName;
        this.textPane = textPane;
    }

    @Override
    public void run(){
        DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(serverName);
        StyledDocument doc = textPane.getStyledDocument();

        while (true) {
            Map<String, Object> response = dataTranslator.receiveData();
            if(response == null) break;
            String command = (String) response.get("command");

            if (command.equals(responseCommand)) {
                String message = (String) response.get("message");

                int startIndex = message.indexOf("[플레이어 ") + "[플레이어 ".length();
                int endIndex = message.indexOf("]");
                long senderPlayerId = Long.parseLong(message.substring(startIndex, endIndex));  // String에서 long으로 변환

                SimpleAttributeSet attribs = new SimpleAttributeSet();  // 속성 집합 생성
                StyleConstants.setSpaceAbove(attribs, 5f);  // 메시지 위의 간격 설정

                if (MainFrame.playerId == senderPlayerId) {
                    message = message.replace("[플레이어 " + senderPlayerId + "]", "");
                    message = message.trim() + " [나]\n";  // 메시지의 끝에 "[나]" 추가
                    StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
                } else {
                    StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_LEFT);
                }

                // 첫 메시지를 보내기 전에 "채팅 서버가 연결되었습니다" 메시지를 이탤릭체로 삽입
                if (isFirstMessage) {
                    appendNoticeMessage(doc);
                }

                doc.setParagraphAttributes(doc.getLength(), 1, attribs, false);  // 새로운 문단에 정렬 속성 적용

                try {
                    doc.insertString(doc.getLength(), message + " ", attribs);  // 메시지 추가
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            } else if (command.equals("room_chat_delete")) {
                textPane.setText("");
            } else if(command.equals("game_exit")) {
                return; // 게임 종료 시 게임 채팅 스레드 종료
            }
        }
    }

    // 첫 메시지를 보내기 전에 "채팅 서버가 연결되었습니다" 메시지를 이탤릭체로 삽입
    private void appendNoticeMessage(StyledDocument doc){
        SimpleAttributeSet initialAttribs = new SimpleAttributeSet();
        StyleConstants.setBold(initialAttribs, true);  // 볼드체 설정
        StyleConstants.setAlignment(initialAttribs, StyleConstants.ALIGN_LEFT);  // 정렬 설정
        StyleConstants.setFontSize(initialAttribs, 15);  // 글씨 크기 설정
        try {
            doc.insertString(0, "채팅을 시작합니다.\n ", initialAttribs);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        isFirstMessage = false;
    }
}
