package Server.Data;

import Server.Manager.GameRoomManager;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 찬형 코드 옮길 때 id -> roomId로 변경 필요함
 */
public class GameRoom implements Serializable {

    private long id; // 룸 ID

    private Player leader; //방장

    private int playerCount = 0; //현재 참여중인 플레이어의 수

    private List<Player> players = new CopyOnWriteArrayList<>();
    public Player getLeader() {
        return leader;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public long getId() {
        return id;
    }

    public GameRoom(long id, Player player) {
        this.id = id;
        this.leader = player;
        players.add(player);
        playerCount++;
    }

    public boolean isPlayerInRoom(Player player) {
        return players.contains(player);
    }

    public boolean enter(Player player) {

        if (playerCount >= 2) {
            return false;
        }

        players.add(player);
        playerCount++;

        return true;
    }

    public void leave(Player player) {
        players.remove(player);
        playerCount--;

        if(playerCount == 0) { //모든 인원이 나갔다면
            GameRoomManager.getInstance().removeRoom(this); //GameRoomManager에서 현재 GameRoom 객체를 제거한다.
        }
        if(playerCount == 1) {
            leader = players.get(0);
        }
    }
    public boolean isAllReady() {

        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return playerCount >= 2;
    }
}
