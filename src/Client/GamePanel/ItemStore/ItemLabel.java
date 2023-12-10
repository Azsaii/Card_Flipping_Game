package Client.GamePanel.ItemStore;

import javax.swing.*;

public class ItemLabel extends JLabel {
    private int itemId;

    public ItemLabel(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }
}
