package Client.GamePanel.ItemStore;

import Server.Data.ItemData;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용 중인 아이템 표시 패널입니다.
 * GameItemPurchasePanel 에서 구매하여 사용 중인 아이템이 이곳에 표시됨
 * 아이템 사용이 종료되면 아이템이 사라짐
 */
public class ItemInUsePanel extends JPanel {

    private JPanel itemPanel1;  // 사용 중인 아이템 표시 패널 1
    private JPanel itemPanel2;  // 사용 중인 아이템 표시 패널 2

    private JLabel coolTimeLabel1;    // 쿨타임1
    private JLabel coolTimeLabel2;    // 쿨타임2

    private ItemData inUseItem1;    // 사용 중인 아이템1
    private ItemData inUseItem2;    // 사용 중인 아이템2

    private Map<Integer, JPanel> usingPanels = new HashMap<>();

    public ItemInUsePanel(){

        // int vGap = (int)((getParent().getHeight() - (2 * itemPanel1.getHeight())) / 2.0);
        setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
        setBackground(Color.white);

        coolTimeLabel1 = new JLabel();
        coolTimeLabel2 = new JLabel();
        coolTimeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        coolTimeLabel2.setHorizontalAlignment(SwingConstants.CENTER);

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
    private void addItemTimerLable(JPanel panel, JLabel label, String coolTime){
        label.setText(coolTime);
        Font labelFont = label.getFont();
        label.setFont(new Font(labelFont.getName(), Font.PLAIN, 20)); // 글꼴 크기를 20으로 설정
        panel.add(label, BorderLayout.NORTH);
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

    private void attachInUseItemPanel(JPanel panel, JLabel label, ItemData item){
        addItemTimerLable(panel, label, String.valueOf(item.getCoolTime()));
        addItemImageLable(panel, item.getItemPath());
        usingPanels.put(item.getItemId(), panel);
    }

    // 사용 아이템을 패널에 추가하는 메서드
    public void attachInUseItem(ItemData inUseItemData) {

        if(inUseItem1 == null){
            inUseItem1 = inUseItemData;
            attachInUseItemPanel(itemPanel1, coolTimeLabel1, inUseItem1);

        } else if(inUseItem2 == null){
            inUseItem2 = inUseItemData;
            attachInUseItemPanel(itemPanel2, coolTimeLabel2, inUseItem2);
        }
        revalidate();
        repaint();
    }

    // 아이템 id를 받아 사용 중인 아이템 패널에서 제거하는 메서드
    public void detachInUseItem(int itemId){
        JPanel targetPanel = usingPanels.get(itemId);
        targetPanel.removeAll();

        if(inUseItem1 != null && itemId == inUseItem1.getItemId()){
            inUseItem1 = null;
            coolTimeLabel1.setText("");
        } else if(inUseItem2 != null && itemId == inUseItem2.getItemId()){
            inUseItem2 = null;
            coolTimeLabel2.setText("");
        }
        revalidate();
        repaint();
    }

    // 아이템 id를 받아서 해당하는 아이템의 쿨타임 업데이트하는 메서드
    public void updateCoolTime(int itemId){

        if(inUseItem1 != null && itemId == inUseItem1.getItemId()){
            Double coolTime = Double.valueOf(coolTimeLabel1.getText());
            coolTimeLabel1.setText(String.valueOf(coolTime - 1));
        } else if(inUseItem2 != null && itemId == inUseItem2.getItemId()){
            Double coolTime = Double.valueOf(coolTimeLabel2.getText());
            coolTimeLabel2.setText(String.valueOf(coolTime - 1));
        }

        revalidate();
        repaint();
    }
}
