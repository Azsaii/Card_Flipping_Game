package Client.GamePanel.ItemStore;

import Server.Data.ItemData;

import javax.swing.*;
import java.awt.*;

/**
 * 사용 중인 아이템 표시 패널입니다.
 * GameItemPurchasePanel 에서 구매하여 사용 중인 아이템이 이곳에 표시됨
 * 아이템 사용이 종료되면 아이템이 사라짐
 */
public class ItemInUsePanel extends JPanel {

    // 싱글톤
    private static ItemInUsePanel instance;
    public static synchronized ItemInUsePanel getInstance(){
        if(instance == null) instance = new ItemInUsePanel();
        return instance;
    }

    private JPanel itemPanel1;  // 사용 중인 아이템 표시 패널 1
    private JPanel itemPanel2;  // 사용 중인 아이템 표시 패널 2

    private ItemData inUseItem1;    // 사용 중인 아이템1
    private ItemData inUseItem2;    // 사용 중인 아이템2

    public ItemInUsePanel(){

        // int vGap = (int)((getParent().getHeight() - (2 * itemPanel1.getHeight())) / 2.0);
        setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
        setBackground(Color.white);

        itemPanel1 = new JPanel();
        itemPanel2 = new JPanel();

        addItemPanel(itemPanel1);
        addItemPanel(itemPanel2);
    }

    private void addItemPanel(JPanel panel){
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);
        add(panel);
    }

    // 아이템 쿨타임 레이블 추가하는 함수
    private void addItemTimerLable(JPanel panel, String time){
        JLabel timeLabel = new JLabel(time, SwingConstants.CENTER);
        Font labelFont = timeLabel.getFont();
        timeLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 20)); // 글꼴 크기를 20으로 설정
        panel.add(timeLabel, BorderLayout.NORTH);
    }

    // 아이템 이미지 추가하는 함수
    private void addItemImageLable(JPanel panel, String path){
        ImageIcon imageIcon = new ImageIcon(path);
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(100, 100)); // 이미지 크기 조절
        panel.add(imageLabel, BorderLayout.CENTER);
    }

    private void setInUseItemPanel(JPanel panel, ItemData item){
        addItemTimerLable(panel, String.valueOf(item.getCoolTime()));
        addItemImageLable(panel, item.getItemPath());
    }

    public void setInUseItem(ItemData inUseItemData) {

        if(inUseItem1 == null){
            inUseItem1 = inUseItemData;
            setInUseItemPanel(itemPanel1, inUseItem1);

        } else if(inUseItem2 == null){
            inUseItem2 = inUseItemData;
            setInUseItemPanel(itemPanel2, inUseItem2);
        }
        revalidate();
        repaint();
    }
}
