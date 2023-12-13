package Client.GamePanel.ItemStore;

import Client.GamePanel.Card.CardPanel;
import Server.Data.ItemData;
import Client.GamePanel.Score.DoubleScoreStrategy;
import Client.GamePanel.Score.ScorePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;

import static Client.GamePanel.ItemStore.ItemStorePanel.ITEM_COUNT;

/**
 * 아이템 구입 패널입니다.
 * 아이템을 클릭하면 가격에 맞게 스코어가 감소하고 아이템이 사용됨
 * 아이템 데이터를 세팅
 */
public class ItemPurchasePanel extends JPanel {

    private ItemDescriptionPanel descriptionPanel; // 설명 패널
    private ItemInUsePanel inUsePanel; // 사용 중인 아이템 패널
    
    private static final int ITEM_PURCHASE_LIMIT = 2;   // 아이템 최대 구입 가능 수
    private int inUseItemDataCount = 0;   // 사용 중인 아이템 수
    private int playerType; // 플레이어 타입

    private static final String pathDefault = "images/items/";

    // 아이템 데이터
    private static final ItemData RANDOM_FLIP = new ItemData(1, "랜덤 뒤집개", pathDefault + "RANDOM_FLIP.PNG", pathDefault + "RANDOM_FLIP_DE.PNG", 50, "모든 카드를 랜덤 색으로 재배치합니다.\n상대의 카드가 뒤집힌 수만큼 스코어를 빼앗습니다.", 4.0);
    private static final ItemData BLACK_FOG = new ItemData(2, "검은 안개", pathDefault + "BLACK_FOG.PNG", pathDefault + "BLACK_FOG_DE.PNG", 50, "상대가 랜덤한 절반의 카드 색을 보지 못하게 합니다.", 10.0);
    private static final ItemData GOLD_FLIP = new ItemData(3, "황금 뒤집개", pathDefault + "GOLD_FLIP.PNG", pathDefault + "GOLD_FLIP_DE.PNG", 100, "모든 카드를 플레이어의 색으로 뒤집습니다.", 7.0);
    private static final ItemData DOUBLE_EVENT = new ItemData(4, "더블 이벤트", pathDefault + "DOUBLE_EVENT.PNG", pathDefault + "DOUBLE_EVENT_DE.PNG", 100, "n초 동안 모든 방법으로 얻는 스코어 2배가 됩니다.", 10.0);
    private static final ItemData ABSORB = new ItemData(5, "흡혈", pathDefault + "ABSORB.PNG", pathDefault + "ABSORB_DE.PNG", 150, "n초 동안 상대방이 얻는 스코어를 빼앗습니다.\n상대방에게 사용 여부가 알려지지 않습니다.", 10.0);
    private static final ItemData ICE_AGE = new ItemData(6, "얼음", pathDefault + "ICE_AGE.PNG", pathDefault + "ICE_AGE_DE.PNG", 70, "n초 동안 모든 카드가 뒤집을 수 없는 상태로 변합니다.", 7.0);
    private ItemData[] itemDatas = new ItemData[ITEM_COUNT];

    // 아이템 구입에 따른 스코어 처리를 위해 주입받음
    private ScorePanel scorePanel;

    // 아이템 구입에 따른 카드 패널에게 메시지를 보내기 위해 주입받음
    private CardPanel cardPanel;

    // 아이템 활성화 / 비활성화 상태 저장
    private boolean[] itemActivated = new boolean[ITEM_COUNT];

    public ItemPurchasePanel(ScorePanel scorePanel, CardPanel cardPanel, ItemDescriptionPanel descriptionPanel, ItemInUsePanel inUsePanel, int playerType){

        this.scorePanel = scorePanel;
        this.cardPanel = cardPanel;
        this.descriptionPanel = descriptionPanel;
        this.inUsePanel = inUsePanel;
        this.playerType = playerType;

        setItemDatas(); // 아이템 데이터를 배열에 세팅
        setLayout(new GridLayout(2, 3)); // 2행 3열의 그리드 레이아웃

        // 아이템 패널 생성 및 추가
        for (int i = 0; i < ITEM_COUNT; i++) {
            JPanel itemPanel = createItemPanel(i); // 아이템 패널 생성 메소드 호출
            itemPanel.setBackground(Color.white);
            add(itemPanel);
        }

        Arrays.fill(itemActivated, true); // 모든 아이템을 활성화 상태로 초기화
    }

    private void setItemDatas(){
        itemDatas[0] = RANDOM_FLIP;
        itemDatas[1] = BLACK_FOG;
        itemDatas[2] = GOLD_FLIP;
        itemDatas[3] = DOUBLE_EVENT;
        itemDatas[4] = ABSORB;
        itemDatas[5] = ICE_AGE;
    }

    // 아이템 번호와 경로를 받아 이미지 설정하는 함수
    private void setImage(ItemLabel imageLabel, int i, String path){
        ImageIcon imageIcon = new ImageIcon(path);
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(70, 70)); // 이미지 크기 조절
    }

    private JPanel createItemPanel(int i) {
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BorderLayout());

        // 테두리 설정
        itemsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 아이템 이름 표시
        setIiemPurchaseLable(itemsPanel, itemDatas[i].getItemName(), BorderLayout.NORTH);

        // 아이템 이미지 설정
        ItemLabel imageLabel = new ItemLabel(itemDatas[i].getItemId());
        setImage(imageLabel, i, itemDatas[i].getItemPath());

        // 아이템 이벤트 설정
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

                // 아이템이 비활성화 상태이거나 이미 2개의 아이템의 쿨타임이 끝나지 않았으면 리턴
                if (!itemActivated[i] || inUseItemDataCount == ITEM_PURCHASE_LIMIT) {
                    return;
                }

                final ItemLabel[] source = {(ItemLabel) e.getSource()}; // 클릭된 레이블. 타이머 내부에서 사용하기 위해 final 선언됨
                int itemId = source[0].getItemId(); // 아이템 id
                double delay = itemDatas[i].getCoolTime(); // 쿨타임

                // 아이템 사용 패널에 클릭한 아이템 추가되도록 업데이트
                inUseItemDataCount++;
                inUsePanel.attachInUseItem(itemDatas[i]);

                // 아이템 구입으로 인한 스코어 소모
                scorePanel.addScore(itemDatas[i].getItemPrice() * -1, playerType);

                // 아이템 효과 로직 호출
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
                    case 3: { // 황금 뒤집개
                        cardPanel.sendGoldFlipData();
                        break;
                    }
                    case 4: { // 더블 이벤트
                        scorePanel.setStrategy(new DoubleScoreStrategy());
                        break;
                    }
                }

                // 아이템 패널 비활성화
                itemActivated[i] = false;
                setImage(source[0], i, itemDatas[i].getItemDeactivePath()); // 비활성화 이미지 설정

                System.out.println("sourceID = " + itemId + ", depath = " + itemDatas[i].getItemDeactivePath());

                // 지정된 시간 후에 아이템 패널을 다시 활성화
                Timer timer = new Timer(1000, null);
                timer.addActionListener(new ActionListener() {
                    double remainingDelay = delay;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // delay 감소 및 cool time 업데이트
                        remainingDelay--;
                        inUsePanel.updateCoolTime(itemId);

                        // delay가 0이 되었을 때 종료 처리
                        if (remainingDelay <= 0) {
                            itemActivated[i] = true;
                            setImage(source[0], i, itemDatas[i].getItemPath());
                            inUseItemDataCount--;
                            inUsePanel.detachInUseItem(itemDatas[i].getItemId());
                            timer.stop();
                        }
                    }
                });
                timer.start();
            }
        });

        itemsPanel.add(imageLabel, BorderLayout.CENTER);

        // 아이템 가격 표시
        setIiemPurchaseLable(itemsPanel, String.valueOf(itemDatas[i].getItemPrice()), BorderLayout.SOUTH);

        return itemsPanel;
    }

    // 이름, 가격 레이블을 추가하는 함수
    private void setIiemPurchaseLable(JPanel itemPanel, String text, String pos){
        JLabel itemLabel = new JLabel(text, SwingConstants.CENTER);
        Font labelFont = itemLabel.getFont();
        itemLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 20)); // 글꼴 크기를 20으로 설정
        itemPanel.add(itemLabel, pos);
    }
}
