package Server.Data;

import Network.DataTranslator;
import Network.DataTranslatorWrapper;
import Network.ServerName;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;

// 원본에서 socket -> roomSocket / getSocket -> getRoomSocket으로 변경
public class Player implements Serializable {
    private long id;
    private String name;
    private boolean ready = false;
    private int randomProfileImage = (int)(Math.random() * 5) + 1;
    private GameRoom gameRoom;
    private final transient DataTranslatorWrapper dataTranslatorWrapper = new DataTranslatorWrapper();

    public Player(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getRandomProfileImage() {
        return randomProfileImage;
    }
    public DataTranslatorWrapper getDataTranslatorWrapper() {
        return dataTranslatorWrapper;
    }
    public long getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // 서버 이름과 함께 BufferedReader와 BufferedWriter를 추가하는 메소드
    public void addServer(ServerName serverName, DataTranslator dataTranslator) {
        dataTranslatorWrapper.add(serverName, dataTranslator);
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    public boolean isReady() {
        return ready;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }
    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }
}
