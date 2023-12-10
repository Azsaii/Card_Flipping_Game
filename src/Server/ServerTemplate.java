package Server;

import Network.DataTranslator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CyclicBarrier;

public abstract class ServerTemplate implements Runnable {
    private int port;


    protected long playerId;   //각 서버에서 연결된 플레이어의 Id 값
    protected CyclicBarrier cyclicBarrier;

    protected DataTranslator dataTranslator;

    public ServerTemplate(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    dataTranslator = new DataTranslator(clientSocket); //클라이언트에 연결 되었으면 데이터를 송수신 할 수 있는 DataTranslator 객체 생성
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                handleClient();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void handleClient();

    public void update(long playerId, CyclicBarrier cyclicBarrier) { //GameDataInitialServer에서 나머지 서버들의 플레이어의 Id 값을 설정 할 수 있는 메서드
        this.playerId = playerId;
        this.cyclicBarrier = cyclicBarrier;
    }
}