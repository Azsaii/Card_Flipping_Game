package GameObject;

public class ItemData {
    private int id;
    private String itemName;
    private String itemPath;
    private int itemPrice;
    private String description;
    private Double coolTime;

    public ItemData(int id, String itemName, String itemPath, int itemPrice, String description, Double coolTime) {
        this.id = id;
        this.itemName = itemName;
        this.itemPath = itemPath;
        this.itemPrice = itemPrice;
        this.description = description;
        this.coolTime = coolTime;
    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPath() {
        return itemPath;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public String getDescription() {
        return description;
    }

    public Double getCoolTime() {
        return coolTime;
    }
}
