package Client;

import Client.GamePanel.GameScreenPanel;
import Client.MainPanel.MainScreenPanel;
import Client.RoomPanel.RoomScreenPanel;
import Network.DataTranslator;
import Network.DataTranslatorWrapper;
import Network.ServerName;
import Server.Data.GameRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 게임을 시작하는 메인 프레임입니다.
 * 메인 화면, 방 화면, 게임 화면 패널을 붙입니다.
 * 서버와 연결합니다.
 * 화면 전환 기능을 담당합니다.
 */
public class MainFrame extends JFrame {
    public static DataTranslatorWrapper dataTranslatorWrapper = new DataTranslatorWrapper();

    private final int MAINSCREEN = 0;
    private final int ROOMSCREEN = 1;
    private final int GAMESCREEN = 2;

    public static GameRoom gameRoom;
    public static long playerId;
    public static long roomId;
    private CardLayout cardLayout;
    private JPanel mainPanel;


    private MainScreenPanel mainScreenPanel;
    private GameScreenPanel gameScreenPanel;
    private RoomScreenPanel roomScreenPanel;

    public MainFrame() throws IOException {

        /* 서버 별 입출력 스트림을 다루는 객체 생성 */
        Map<String, Object> GameDataInitialServerResponse = new DataTranslator("localhost", 5000).receiveData(); //클라이언트는 GameDataInitialServer 에 먼저 연결
        playerId = (long) GameDataInitialServerResponse.get("playerId"); // GameDataInitialServer 에서 playerId를 얻습니다.
        
        dataTranslatorWrapper.add(ServerName.GAME_ROOM_DATA_SERVER, new DataTranslator("localhost", 5001));
        dataTranslatorWrapper.add(ServerName.SCREEN_UI_UPDATE_SERVER, new DataTranslator("localhost", 5002));
        dataTranslatorWrapper.add(ServerName.ROOM_LIST_UI_UPDATE_SERVER, new DataTranslator("localhost", 5003));
        dataTranslatorWrapper.add(ServerName.PLAYER_STATUS_UI_UPDATE_SERVER, new DataTranslator("localhost", 5004));
        dataTranslatorWrapper.add(ServerName.ROOM_CHAT_UI_UPDATE_SERVER, new DataTranslator("localhost", 5005));
        dataTranslatorWrapper.add(ServerName.ALL_CHAT_UI_UPDATE_SERVER, new DataTranslator("localhost", 5006));
        dataTranslatorWrapper.add(ServerName.ROOM_CONTROL_UI_UPDATE_SERVER, new DataTranslator("localhost", 5007));

        dataTranslatorWrapper.add(ServerName.CARD_UI_UPDATE_SERVER, new DataTranslator("localhost", 5010));
        dataTranslatorWrapper.add(ServerName.ITEM_UI_UPDATE_SERVER, new DataTranslator("localhost", 5011));
        dataTranslatorWrapper.add(ServerName.GAME_CHAT_UI_UPDATE_SERVER, new DataTranslator("localhost", 5012));

        /* 메인 화면 구성 */
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        // startPanel = new GameStartTimerPanel();  // 게임 시작 대기 패널

        // 메인 화면과 게임 화면을 생성
        mainScreenPanel = new MainScreenPanel();
        gameScreenPanel = new GameScreenPanel(playerId);
        roomScreenPanel = new RoomScreenPanel(playerId);

        /**
         * 찬형코드 메인화면-게임방 ui아직 연동 안함. 아직 디자인이 덜됨 1210
         * 최신코드 올라오면 추가 후 아래 임시로 만든 mainMenu, startPanel 찬형코드로 대체하기.
         * 대체 후 '게임시작' 버튼 눌렀을 때 5초 세고 게임화면 입장하도록 추가하기.
         */

        // 각 패널을 카드 레이아웃에 추가
        mainPanel.add(mainScreenPanel, "mainScreenPanel");
        mainPanel.add(gameScreenPanel, "gameScreenPanel");
        mainPanel.add(roomScreenPanel, "roomScreenPanel");
        cardLayout.show(mainPanel, "mainScreenPanel");

        add(mainPanel);
        
        // 서버로부터 화면 업데이트 메시지 받아 화면 전환하는 스레드
        Thread updateScreenThread = new Thread(() -> {

            DataTranslator dataTranslator = dataTranslatorWrapper.get(ServerName.SCREEN_UI_UPDATE_SERVER);

            while (true) {
                Map<String, Object> response = dataTranslator.receiveData();
                if(response == null) break;
                String command = (String) response.get("command");

                if(command.equals("screen_change_main")) {
                    updateScreen(MAINSCREEN);
                } else if (command.equals("screen_change_room")) {
                    updateScreen(ROOMSCREEN);
                } else if (command.equals("screen_change_game")) {
                    gameRoom = (GameRoom) response.get("gameRoom");
                    updateScreen(GAMESCREEN);
                }
            }
        });
        updateScreenThread.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });
    }

    public void updateScreen(int screenOption) {

        switch (screenOption) {
            case MAINSCREEN:
                cardLayout.show(mainPanel, "mainScreenPanel");
                break;
            case ROOMSCREEN:
                cardLayout.show(mainPanel, "roomScreenPanel");
                break;
            case GAMESCREEN:
                mainPanel.remove(gameScreenPanel);
                gameScreenPanel = new GameScreenPanel(playerId);
                gameScreenPanel.startGameRoom();
                mainPanel.add(gameScreenPanel, "gameScreenPanel");
                cardLayout.show(mainPanel, "gameScreenPanel");
                break;
        }
    }

    public static void exitGame(){
        // 프레임이 닫힐 때 모든 소켓 닫기
        Map<String, Object> request = new HashMap<>();
        request.put("command", "exit"); // 요청 종류
        request.put("playerId", playerId); // 플레이어 id
        dataTranslatorWrapper.broadcast(request);
        dataTranslatorWrapper.closeAllSocket();
    }

    public static void setRoomid(long roomId){
        MainFrame.roomId = roomId;
    }
    public static long getRoomid(){return MainFrame.roomId;}
    public static GameRoom getGameRoom(){return MainFrame.gameRoom;}

    public static void main(String[] args) throws IOException {
        MainFrame frame = new MainFrame();
        frame.setTitle("Card Flipping Game");
        frame.setSize(1500, 800);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
