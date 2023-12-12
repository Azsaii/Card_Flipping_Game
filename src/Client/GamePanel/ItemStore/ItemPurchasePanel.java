package Client.GamePanel.ItemStore;

import Client.GamePanel.Card.CardPanel;
import Server.Data.ItemData;
import Client.GamePanel.Score.DoubleScoreStrategy;
import Client.GamePanel.Score.ScorePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import static Client.GamePanel.ItemStore.ItemStorePanel.ITEM_COUNT;

/**
 * 아이템 구입 패널입니다.
 * 아이템을 클릭하면 가격에 맞게 스코어가 감소하고 아이템이 사용됨
 * 아이템 데이터를 세팅
 */
public class ItemPurchasePanel extends JPanel {

    ItemDescriptionPanel descriptionPanel; // 설명 패널
    ItemInUsePanel inUsePanel;                   // 보유 패널
    
    private static final int ITEM_PURCHASE_LIMIT = 2;   // 아이템 최대 구입 가능 수
    private int inUseItemDataCount = 0;   // 사용 중인 아이템 수

    // 아이템 데이터
    private static final ItemData RANDOM_FLIP = new ItemData(1, "랜덤 뒤집개", "images/RANDOM_FLIP.PNG", 100, "모든 카드를 랜덤 색으로 재배치합니다.\n상대의 카드가 뒤집힌 수만큼 스코어를 빼앗습니다.", 4.0);
    private static final ItemData BLACK_FOG = new ItemData(2, "검은 안개", "images/BLACK_FOG.PNG", 100, "상대가 랜덤한 절반의 카드 색을 보지 못하게 합니다.", 10.0);
    private static final ItemData GOLD_FLIP = new ItemData(3, "황금 뒤집개", "images/GOLD_FLIP.PNG", 200, "모든 카드를 플레이어의 색으로 뒤집습니다.", 7.0);
    private static final ItemData DOUBLE_EVENT = new ItemData(4, "더블 이벤트", "images/DOUBLE_EVENT.PNG", 300, "n초 동안 모든 방법으로 얻는 스코어 2배가 됩니다.", 10.0);
    private static final ItemData ABSORB = new ItemData(5, "흡혈", "images/ABSORB.PNG", 300, "n초 동안 상대방이 얻는 스코어를 빼앗습니다.\n상대방에게 사용 여부가 알려지지 않습니다.", 10.0);
    private static final ItemData ICE_AGE = new ItemData(6, "얼음", "images/ICE_AGE.PNG", 300, "n초 동안 모든 카드가 뒤집을 수 없는 상태로 변합니다.", 7.0);
    private ItemData[] itemDatas = new ItemData[ITEM_COUNT];

    // 아이템 구입에 따른 스코어 처리를 위해 주입받음
    private ScorePanel scorePanel;

    // 아이템 구입에 따른 카드 패널에게 메시지를 보내기 위해 주입받음
    private CardPanel cardPanel;

    public ItemPurchasePanel(ScorePanel scorePanel, CardPanel cardPanel, ItemDescriptionPanel descriptionPanel, ItemInUsePanel inUsePanel){

        this.scorePanel = scorePanel;
        this.cardPanel = cardPanel;
        this.descriptionPanel = descriptionPanel;
        this.inUsePanel = inUsePanel;

        setItemDatas(); // 아이템 데이터를 배열에 세팅
        setLayout(new GridLayout(2, 3)); // 2행 3열의 그리드 레이아웃

        // 아이템 패널 생성 및 추가
        for (int i = 0; i < ITEM_COUNT; i++) {
            JPanel itemPanel = createItemPanel(i); // 아이템 패널 생성 메소드 호출
            itemPanel.setBackground(Color.white);
            add(itemPanel);
        }
    }

    private void setItemDatas(){
        itemDatas[0] = RANDOM_FLIP;
        itemDatas[1] = BLACK_FOG;
        itemDatas[2] = GOLD_FLIP;
        itemDatas[3] = DOUBLE_EVENT;
        itemDatas[4] = ABSORB;
        itemDatas[5] = ICE_AGE;
    }
    
    public void setInUseItemDataCount(int inUseItemDataCount){
        this.inUseItemDataCount = inUseItemDataCount;
    }

    private JPanel createItemPanel(int i) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());

        // 테두리 설정
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 아이템 이름 표시
        setIiemPurchaseLable(itemPanel, itemDatas[i].getItemName(), BorderLayout.NORTH);

        // 아이템 이미지 표시
        ImageIcon imageIcon = new ImageIcon(itemDatas[i].getItemPath());
        ItemLabel imageLabel = new ItemLabel(itemDatas[i].getItemId());
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(70, 70)); // 이미지 크기 조절
        imageLabel.addMouseListener(new MouseAdapter(){

            // 마우스가 아이템 위에 올라가면 설명 패널에 아이템 설명을 표시
            @Override
            public void mouseEntered(MouseEvent e) {
                descriptionPanel.setItemDescription(itemDatas[i].getItemName(), itemDatas[i].getDescription());
            }

            // 마우스가 아이템을 나가면 설명 패널을 초기화
            @Override
            public void mouseExited(MouseEvent e) {
                descriptionPanel.clearItemName();
            }

            // 마우스를 클릭하면 아이템 보유 패널에 구입한 아이템 리스트가 전달되어 표시
            @Override
            public void mouseClicked(MouseEvent e) {
                if(inUseItemDataCount != ITEM_PURCHASE_LIMIT) inUseItemDataCount++;
                inUsePanel.setInUseItem(itemDatas[i]);

                ItemLabel source = (ItemLabel) e.getSource();
                int itemId = source.getItemId();
                System.out.println("아이템 클릭됨, itemid " + itemId);
                switch (itemId) {
                    case 1: { // 랜덤 뒤집개
                        boolean[] randomCardArray = new boolean[24];
                        Random rand = new Random();
                        for(int index = 0; index < randomCardArray.length; index++) {
                            randomCardArray[index] = rand.nextBoolean();
                        }
                        cardPanel.sendRandomFlipData(randomCardArray); // 서버로 카드 데이터 전송
                        break;
                    }
                    case 3: cardPanel.sendGoldFlipData(); break; // 황금 뒤집개
                    case 4: scorePanel.setStrategy(new DoubleScoreStrategy()); break; // 더블 이벤트
                }
            }
        });

        itemPanel.add(imageLabel, BorderLayout.CENTER);

        // 아이템 가격 표시
        setIiemPurchaseLable(itemPanel, String.valueOf(itemDatas[i].getItemPrice()), BorderLayout.SOUTH);

        return itemPanel;
    }

    // 이름, 가격 레이블을 추가하는 함수
    private void setIiemPurchaseLable(JPanel itemPanel, String text, String pos){
        JLabel itemLabel = new JLabel(text, SwingConstants.CENTER);
        Font labelFont = itemLabel.getFont();
        itemLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 20)); // 글꼴 크기를 20으로 설정
        itemPanel.add(itemLabel, pos);
    }
}
