package Server;

import Network.DataTranslator;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 채팅 담당 서버 템플릿
 */
public abstract class ChatServerThreadTemplate extends ServerThread {

    public ChatServerThreadTemplate(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        super(dataTranslator, cyclicBarrier);
    }

    @Override
    public void run() {

        while (true) {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            /* 요청을 받아 처리 */
            Map<String, Object> request = checkexit();
            if (request == null) break; // 클라이언트가 게임 종료한 경우 루프 빠져나간다.

            String command = (String) request.get("command");
            sendResponse(request, command);
        }
    }

    public abstract void sendResponse(Map<String, Object> request, String command);
}
