package GameSystem;

import java.io.IOException;

public class GameServer {
    public static void main(String[] args) throws IOException {
        new GameDataServer().start();
    }
}
