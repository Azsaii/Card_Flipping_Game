import GamePanel.GameScreenPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    public MainMenu(JPanel mainPanel){
        JButton btn = new JButton("MainMenu");
        add(btn);

//        JButton btn1 = new JButton("B1");
//        JButton btn2 = new JButton("B2");
//
//        btn1.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JPanel gameScreen1 = new GameScreenPanel(1);      // 게임 화면 패널
//                mainPanel.add(gameScreen1, "GameScreen");
//            }
//        });
//
//        btn2.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JPanel gameScreen2 = new GameScreenPanel(2);      // 게임 화면 패널
//                mainPanel.add(gameScreen2, "GameScreen");
//            }
//        });
//
//        add(btn1);
//        add(btn2);
    }
}
