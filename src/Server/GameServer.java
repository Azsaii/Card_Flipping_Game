package Server;

import Server.Data.GameDataInitialServer;
import Server.Data.GameRoomDataServer;
import Server.UI.*;

import java.util.concurrent.CyclicBarrier;

/**
 * 서버 시작용 클래스
 * 실행 시 모든 서버가 시작됩니다.
 */
public class GameServer {
    public static void main(String[] args) {


        GameDataInitialServer gameDataInitialServer = new GameDataInitialServer(5000);

        gameDataInitialServer.attachTemplateServer(new GameRoomDataServer(5001));
        gameDataInitialServer.attachTemplateServer(new ScreenUIUpdateServer(5002));
        gameDataInitialServer.attachTemplateServer(new RoomListUIUpdateServer(5003));
        gameDataInitialServer.attachTemplateServer(new PlayerStatusUIUpdateServer(5004));
        gameDataInitialServer.attachTemplateServer(new RoomChatServer(5005));
        gameDataInitialServer.attachTemplateServer(new MainChatServer(5006));
        gameDataInitialServer.attachTemplateServer(new RoomControlUIUpdateServer(5007));

        gameDataInitialServer.attachTemplateServer(new CardUIUpdateServer(5010));
        gameDataInitialServer.attachTemplateServer(new ItemUIUpdateServer(5011));
        gameDataInitialServer.attachTemplateServer(new GameChatServer(5012));

        //데이터 초기화 서버 스레드 동작
        new Thread(gameDataInitialServer).start();

        //모든 서버 스레드 동작
        gameDataInitialServer.startAllServer();

    }
}
