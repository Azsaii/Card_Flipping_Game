package GameSystem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

// 원본에서 socket -> roomSocket / getSocket -> getRoomSocket으로 변경
public class Player {
    private long playerId;
    private long roomId;
    private boolean ready;
    private Socket roomSocket;
    private Socket dataSocket;
    private BufferedWriter out;

    public Player(long playerId, long roomId) {
        this.playerId = playerId;
        this.roomId = roomId;
    }
    public void setRoomSocket(Socket roomSocket){
        this.roomSocket = roomSocket;
    }
    public void setDataSocket(Socket dataSocket){
        this.dataSocket = dataSocket;
        try {
            this.out = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public long getId(){
        return playerId;
    }
    public Socket getRoomSocket() {
        return roomSocket;
    }
    public Socket getDataSocket() {
        return dataSocket;
    }
    public void setReady(boolean ready) {
        this.ready = true;
    }
    public boolean isReady() {
        return ready;
    }
}
