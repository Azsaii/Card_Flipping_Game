package Client.GamePanel.ItemStore;

import javax.swing.*;

/**
 * 아이템의 JLabel 에 부가정보를 더하기 위한 클래스입니다.
 */
public class ItemLabel extends JLabel {
    private int itemId;

    public ItemLabel(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }
}
