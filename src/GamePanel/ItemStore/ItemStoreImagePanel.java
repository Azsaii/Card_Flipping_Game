package GamePanel.ItemStore;

import GamePanel.GameScreenPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * 상점 이미지 패널입니다.
 */
public class ItemStoreImagePanel extends JPanel {

    // 싱글톤
    private static ItemStoreImagePanel instance;
    public static synchronized ItemStoreImagePanel getInstance(){
        if(instance == null) instance = new ItemStoreImagePanel();
        return instance;
    }

    private Socket clientDataSocket;
    private BufferedReader din;
    private BufferedWriter dout;

    public ItemStoreImagePanel() {
        JLabel label = new JLabel("이곳에 스토어 이미지 보임");
        add(label);

//        JButton btn1 = new JButton("B1");
//        JButton btn2 = new JButton("B2");
//
//        btn1.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // 카드, 아이템 정보 주고받는 연결 시작
//                try {
//                    new ClientDataThread(1).start();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });
//
//        btn2.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // 카드, 아이템 정보 주고받는 연결 시작
//                try {
//                    new ClientDataThread(2).start();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });
//
//        add(btn1);
//        add(btn2);
    }
}
