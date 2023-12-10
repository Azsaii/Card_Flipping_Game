package Network;

import java.io.*;
import java.net.Socket;
import java.util.Map;

//데이터 송수신을 위한 클래스
public class DataTranslator{

    private ObjectInputStream in;
    private ObjectOutputStream out;


    //클라이언트에서 서버로 연결을 하고자 할 때 사용하는 생성자
    public DataTranslator(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }


    //서버에서 클라이언트로 연결을 하고자 할 때 사용하는 생성자
    public DataTranslator(Socket clientSocket) throws IOException {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    //request 매핑 정보를 보내는 함수
    public synchronized void sendData(Map<String, Object> request) {
        try {
            out.writeObject(request);
            out.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //request 매핑 정보를 받는 함수
    public Map<String, Object> receiveData() {
        try {
            Object obj = in.readObject();

            if (obj instanceof Map) {
                return (Map<String, Object>) obj;
            } else {
                throw new IllegalArgumentException("Received object is not a map");
            }
        }catch (IOException |ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
