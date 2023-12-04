package GamePanel.ItemStore;

import GamePanel.Score.ScorePanel;

import javax.swing.*;
import java.awt.*;

/**
 * 아이템 상점 패널입니다.
 * 상점 이미지 패널, 아이템 설명 패널, 아이템 구입 패널, 보유 아이템 패널을 가져와서 붙입니다.
 */
public class ItemStorePanel extends JPanel {

    // 패널 너비
    static final int WIDTH = 350;
    static final int ITEM_COUNT = 6;

    // 패널 생성
    JPanel storeImagePanel = ItemStoreImagePanel.getInstance();
    JPanel descriptionPanel = ItemDescriptionPanel.getInstance();
    JPanel inUsePanel = ItemInUsePanel.getInstance();
    JPanel purchasePanel;

    private ScorePanel scorePanel;

    public ItemStorePanel(ScorePanel scorePanel){

        this.scorePanel = scorePanel;
        purchasePanel = new ItemPurchasePanel(scorePanel);

        // BoxLayout을 생성하고, 이를 수직으로 설정
        setLayout(null);

        // 패널 붙이기
        setPanelProperties(storeImagePanel, 0,100);
        setPanelProperties(descriptionPanel, 100,100);
        setPanelProperties(purchasePanel, 200,400);
        setPanelProperties(inUsePanel, 600, 161);
    }

    // 패널 붙이는 메서드
    private void setPanelProperties(JPanel panel, int y, int height) {
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBounds(0, y, WIDTH, height);
        add(panel);
    }
}
