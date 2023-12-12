package Server.Manager;

import Server.Data.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//싱글톤 패턴을 적용한 클래스, 여러 스레드에서 사용하려면 synchronized 키워드로 동기화 작업을 해야함.
public class PlayerManager {
    private static PlayerManager instance = new PlayerManager();

    private Map<Long, Player> playerMap = new ConcurrentHashMap<>();

    private long playerId = 0;

    private PlayerManager() {}

    public static PlayerManager getInstance() {
        return instance;
    }


    public Player getPlayer(Long playerId) {
        return playerMap.get(playerId);
    }

    public void addPlayer(Long playerId, Player player) {
        playerMap.put(playerId, player);
    }

    public void removePlayer(Long playerId){
        playerMap.remove(playerId);
    }

    //현재 플레이어 ID값을 리턴하고 플레이어 ID값 +1 증가시키는 메서드
    public synchronized long getNextPlayerId() {
        return ++playerId;
    }
}
