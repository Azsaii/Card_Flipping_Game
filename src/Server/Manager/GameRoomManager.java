package Server.Manager;

import Server.Data.GameRoom;
import Server.Data.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


//싱글톤 패턴을 적용한 클래스, 여러 스레드에서 사용하려면 synchronized 키워드로 동기화 작업을 해야함.
public class GameRoomManager {

    private static GameRoomManager gameManager = new GameRoomManager();

    private List<GameRoom> gameRooms = new CopyOnWriteArrayList<>();

    private long roomId = 1;

    private GameRoomManager() { }

    public static GameRoomManager getInstance() {
        return gameManager;
    }

    public synchronized GameRoom createRoom(Player player) { //게임 방을 생성하는 메서드 (게임 방을 생성한 플레이어는 자동으로 해당 방의 방장이 됨)
        GameRoom gameRoom = new GameRoom(roomId++, player);
        gameRooms.add(gameRoom);
        return gameRoom;

    }

    public void removeRoom(GameRoom gameRoom) {
        gameRooms.remove(gameRoom);
    }


    public boolean isPlayerInRoom(long roomId, Player player) {
        GameRoom gameRoom = getGameRoom(roomId);
        return gameRoom.isPlayerInRoom(player);
    }


    public GameRoom getGameRoom(long roomId) { //해당 ID를 가진 게임 방을 구하는 메서드
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.getId() == roomId) {
                return gameRoom;
            }
        }

        return null;
    }

    public GameRoom getGameRoom(Player player) { //현재 플레이어가 속한 게임 방을 구하는 메서드
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.isPlayerInRoom(player)) {
                return gameRoom;
            }
        }

        return null;
    }

    public boolean enter(long roomId, Player player) { //해당 ID를 가진 게임 방에 해당 플레이어를 입장 시키는 메서드
        GameRoom gameRoom = getGameRoom(roomId);
        return gameRoom.enter(player);
    }

    public void leave(long roomId, Player player) { //해당 ID를 가진 게임 방에 해당 플레이어를 퇴장 시키는 메서드
        GameRoom gameRoom = getGameRoom(roomId);
        gameRoom.leave(player);
    }


    public String getRoomsList() {

        String[][] rooms = new String[gameRooms.size()][2];

        for (int i = 0; i < gameRooms.size(); i++) {
            rooms[i][0] = String.valueOf(gameRooms.get(i).getId());
            rooms[i][1] = gameRooms.get(i).getPlayerCount() + "/" + 2;
        }

        String roomsStr = Arrays.deepToString(rooms);

        return roomsStr;
    }


}
