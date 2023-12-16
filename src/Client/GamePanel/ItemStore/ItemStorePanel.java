package Client.GamePanel.ItemStore;

import Client.GamePanel.Card.CardPanel;
import Client.GamePanel.Score.ScorePanel;

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
    private ItemStoreTitlePanel storeImagePanel = new ItemStoreTitlePanel();
    private ItemDescriptionPanel descriptionPanel = new ItemDescriptionPanel();
    private ItemInUsePanel inUsePanel = new ItemInUsePanel();
    private ItemPurchasePanel purchasePanel;

    private ScorePanel scorePanel;

    public ItemStorePanel(ScorePanel scorePanel, CardPanel cardPanel, int playerType){

        this.scorePanel = scorePanel;
        purchasePanel = new ItemPurchasePanel(scorePanel, cardPanel, descriptionPanel, inUsePanel, playerType);
        cardPanel.setItemPurchasePanel(purchasePanel); // 아이스에이지 효과 함수 호출을 위함

        // BoxLayout 생성하고, 이를 수직으로 설정
        setLayout(null);
        setOpaque(false);

        // 패널 붙이기
        setPanelProperties(storeImagePanel, 0,100);
        setPanelProperties(descriptionPanel, 100,110);
        setPanelProperties(purchasePanel, 210,390);
        setPanelProperties(inUsePanel, 600, 161);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                revalidate();
                repaint();
            }
        });
    }

    // 패널 붙이는 메서드
    private void setPanelProperties(JPanel panel, int y, int height) {
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBounds(0, y, WIDTH, height);
        add(panel);
    }
}
