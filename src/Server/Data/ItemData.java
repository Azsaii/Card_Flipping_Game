package Server.Data;

/**
 * 아이템 모델 클래스
 */
public class ItemData {
    private int itemId; // 아이템 id
    private String itemName; // 아이템 이름
    private String itemPath; // 활성화 이미지 경로
    private String deactiveItemPath; // 비활성화 이미지 경로
    private int itemPrice; // 아이템 가격
    private String description; // 아이템 설명
    private int duration; // 지속시간
    private int coolTime; // 쿨타임

    public ItemData(int itemId, String itemName, String itemPath, String deactiveItemPath, int itemPrice, String description, int duration, int coolTime) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPath = itemPath;
        this.deactiveItemPath = deactiveItemPath;
        this.itemPrice = itemPrice;
        this.description = description;
        this.duration = duration;
        this.coolTime = coolTime;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPath() {return itemPath;}

    public String getItemDeactivePath() {return deactiveItemPath;}

    public int getItemPrice() {
        return itemPrice;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() { return duration; }

    public int getCoolTime() { return coolTime; }
}
