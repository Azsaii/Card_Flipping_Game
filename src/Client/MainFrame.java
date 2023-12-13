package Client;

import Client.GamePanel.GameScreenPanel;
import Client.GamePanel.Timer.GameStartTimerPanel;
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
 * 메인 프레임입니다.
 * 메인 화면, 방 화면, 게임 화면 패널을 붙입니다.
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
    private GameStartTimerPanel startPanel;

    private JPanel mainScreenPanel;
    private GameScreenPanel gameScreenPanel;
    private JPanel roomScreenPanel;

    public MainFrame() throws IOException {

        /* 서버 별 입출력 스트림을 다루는 객체 생성 */
        Map<String, Object> GameDataInitialServerResponse = new DataTranslator("localhost", 5000).receiveData(); //클라이언트는 GameDataInitialServer에 먼저 연결
        playerId = (long) GameDataInitialServerResponse.get("playerId"); // GameDataInitialServer에서 playerId를 얻습니다.

        System.out.println("mainframe - playerId id = " + playerId);
        
        dataTranslatorWrapper.add(ServerName.GAME_ROOM_DATA_SERVER, new DataTranslator("localhost", 5001));
        dataTranslatorWrapper.add(ServerName.SCREEN_UI_UPDATE_SERVER, new DataTranslator("localhost", 5002));
        dataTranslatorWrapper.add(ServerName.ROOM_LIST_UI_UPDATE_SERVER, new DataTranslator("localhost", 5003));
        dataTranslatorWrapper.add(ServerName.PLAYER_STATUS_UI_UPDATE_SERVER, new DataTranslator("localhost", 5004));
        dataTranslatorWrapper.add(ServerName.ROOM_CHAT_UI_UPDATE_SERVER, new DataTranslator("localhost", 5005));
        dataTranslatorWrapper.add(ServerName.CHAT_UI_UPDATE_SERVER, new DataTranslator("localhost", 5006));
        dataTranslatorWrapper.add(ServerName.ROOM_CONTROL_UI_UPDATE_SERVER, new DataTranslator("localhost", 5007));

        dataTranslatorWrapper.add(ServerName.CARD_UI_UPDATE_SERVER, new DataTranslator("localhost", 5010));
        dataTranslatorWrapper.add(ServerName.ITEM_UI_UPDATE_SERVER, new DataTranslator("localhost", 5011));

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
        
//        // startPanel 에서 5초를 센 후 이벤트가 발생하면 GameScreen 으로 넘어간다.
//        startPanel.setActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                cardLayout.show(mainPanel, "GameScreen");
//            }
//        });
//
//        /**
//         * 이 부분 찬형코드에서 방에 두명 모이고 게임 시작할 때 작동하도록 옮겨야함.
//         */
//        // 메인 화면에 play 버튼을 추가하고, 클릭 시 게임 화면으로 전환하도록 설정
//        JButton playButton = new JButton("Play");
//        playButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                /**
//                 * 개발 중이므로 타이머 임시 제거함. 작업 끝나고 살려야한다.
//                 */
////                cardLayout.show(mainPanel,  "StartPanel");
////                startPanel.startTimer(); // 게임 시작 패널의 타이머 시작
//
//                cardLayout.show(mainPanel,  "GameScreen");
//            }
//        });

        
        // 서버로부터 화면 업데이트 메시지 받아 화면 전환하는 스레드
        Thread updateScreenThread = new Thread(() -> {
            System.out.println("화면 전환 스레드 작동");

            DataTranslator dataTranslator = dataTranslatorWrapper.get(ServerName.SCREEN_UI_UPDATE_SERVER);

            while (true) {
                Map<String, Object> response = dataTranslator.receiveData();
                if(response == null) break;
                String command = (String) response.get("command");

                if(command.equals("메인 화면 전환")) {
                    updateScreen(MAINSCREEN);
                } else if (command.equals("방 화면 전환")) {
                    updateScreen(ROOMSCREEN);
                } else if (command.equals("게임 화면 전환")) {
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
