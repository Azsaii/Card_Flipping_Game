package Network;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//여러 DataTranslator 클래스를 다루기 위한 클래스
public class DataTranslatorWrapper {


    private final Map<ServerName, DataTranslator> dataTranslators = new ConcurrentHashMap<>();


    public synchronized void add(ServerName serverName, DataTranslator dataTranslator) {
        dataTranslators.put(serverName, dataTranslator);
    }

    public synchronized DataTranslator get(ServerName serverName) {
        return dataTranslators.get(serverName);
    }
    public synchronized void broadcast(Map<String, Object> request) {
        for (DataTranslator dataTranslator : dataTranslators.values()) {
            dataTranslator.sendData(request);
        }
    }

    public void closeAllSocket(){
        for (DataTranslator dataTranslator : dataTranslators.values()) {
            dataTranslator.closeSocket();
        }
    }
}
