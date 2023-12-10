package Server.Manager;

import Server.Data.GameWaitingRoom;
import Server.Data.Player;

//싱글톤 패턴을 적용한 클래스, 여러 스레드에서 사용하려면 synchronized 키워드로 동기화 작업을 해야함.
public class GameWaitingRoomManager {
    private static GameWaitingRoomManager gameManager = new GameWaitingRoomManager();

    private GameWaitingRoom gameWaitingRoom = new GameWaitingRoom();

    private GameWaitingRoomManager() { }

    public static GameWaitingRoomManager getInstance() {
        return gameManager;
    }

    public synchronized void enter(Player player) { //해당 ID를 가진 게임 방에 해당 플레이어를 입장 시키는  메서드
        gameWaitingRoom.enter(player);
    }

    public synchronized void leave(Player player) { //해당 ID를 가진 게임 방에 해당 플레이어를 입장 시키는  메서드
        gameWaitingRoom.leave(player);
    }

    public synchronized GameWaitingRoom getGameWaitingRoom() {
        return gameWaitingRoom;
    }
}
