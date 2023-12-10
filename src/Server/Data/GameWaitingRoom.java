package Server.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameWaitingRoom {

    private List<Player> players = new CopyOnWriteArrayList<>();

    public void enter(Player player) {
        players.add(player);
    }

    public void leave(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
    }
}
