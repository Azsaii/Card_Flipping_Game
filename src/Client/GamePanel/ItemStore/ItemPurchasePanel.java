package Client.GamePanel.ItemStore;

import Client.GamePanel.Card.CardPanel;
import Client.GamePanel.Score.DefaultScoreStrategy;
import Client.MainFrame;
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
import java.util.Map;
import java.util.Random;

import static Client.GamePanel.Card.CardPanel.*;
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

    private static final String[] itemDescriptoin = new String[]{
            "모든 카드를 랜덤 색으로 재배치한다.\n나의 색으로 뒤집힌만큼 스코어를 빼앗는다.\n쿨타임: 6초",
            "상대가 랜덤한 절반의 카드 색 변화를 못 보게 한다.\n지속시간: 7초, 쿨타임: 10초",
            "모든 카드를 나의 색으로 뒤집는다.\n쿨타임: 8초",
            "모든 방법으로 얻는 스코어 2배.\n지속시간: 7초, 쿨타임: 10초",
            "클릭한 카드의 주변 상하좌우 카드를 같이 뒤집는다.\n지속시간: 7초, 쿨타임: 10초",
            "모든 카드와 아이템 상호작용 불가.\n지속시간: 7초, 쿨타임: 10초",
    };

    // 아이템 데이터
    public static final ItemData RANDOM_FLIP = new ItemData(1, "랜덤 뒤집개", pathDefault + "RANDOM_FLIP.PNG", pathDefault + "RANDOM_FLIP_DE.PNG", 60, itemDescriptoin[0], 0, 6);
    public static final ItemData BLACK_FOG = new ItemData(2, "검은 안개", pathDefault + "BLACK_FOG.PNG", pathDefault + "BLACK_FOG_DE.PNG", 50, itemDescriptoin[1], 7, 10);
    public static final ItemData GOLD_FLIP = new ItemData(3, "황금 뒤집개", pathDefault + "GOLD_FLIP.PNG", pathDefault + "GOLD_FLIP_DE.PNG", 70, itemDescriptoin[2], 0, 8);
    public static final ItemData DOUBLE_EVENT = new ItemData(4, "더블 이벤트", pathDefault + "DOUBLE_EVENT.PNG", pathDefault + "DOUBLE_EVENT_DE.PNG", 100, itemDescriptoin[3], 7, 10);
    public static final ItemData CROSS = new ItemData(5, "크로스", pathDefault + "CROSS.PNG", pathDefault + "CROSS_DE.PNG", 100, itemDescriptoin[4], 7, 10);
    public static final ItemData ICE_AGE = new ItemData(6, "얼음", pathDefault + "ICE_AGE.PNG", pathDefault + "ICE_AGE_DE.PNG", 70, itemDescriptoin[5], 7, 10);
    private ItemData[] itemDatas = new ItemData[ITEM_COUNT];
    private ItemLabel[] itemLabels = new ItemLabel[ITEM_COUNT];

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
        setOpaque(false);

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
        itemDatas[4] = CROSS;
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
        ItemLabel itemLabel = new ItemLabel(itemDatas[i].getItemId());
        setImage(itemLabel, i, itemDatas[i].getItemPath());

        // 아이템 이벤트 설정
        itemLabel.addMouseListener(new MouseAdapter(){

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
                int coolTime = itemDatas[i].getCoolTime(); // 쿨타임
                int duration = itemDatas[i].getDuration(); // 쿨타임
                String command = itemDatas[i].getItemPath().split("/")[2].split("\\.")[0]; // 커맨드 알아내기

                // 아이템 사용 패널에 클릭한 아이템 추가되도록 업데이트
                inUseItemDataCount++;
                if(itemDatas[i].getDuration() == 0) { // 비지속형 아이템
                    inUsePanel.attachInUseItem(itemDatas[i], String.valueOf(itemDatas[i].getCoolTime()), false);
                } else {
                    inUsePanel.attachInUseItem(itemDatas[i], String.valueOf(itemDatas[i].getDuration()), true);
                }


                // 아이템 사용을 서버에 알림
                sendItemUseNotice(command);

                // 클릭된 아이템 비활성화
                deActiveItemPanel(i, source, duration, coolTime, itemId); // 클릭된 아이템 비활성화, 쿨타임 처리
            }
        });

        itemLabels[i] = itemLabel;
        itemsPanel.add(itemLabel, BorderLayout.CENTER);

        // 아이템 가격 표시
        setIiemPurchaseLable(itemsPanel, String.valueOf(itemDatas[i].getItemPrice()), BorderLayout.SOUTH);

        return itemsPanel;
    }

    // 이름, 가격 레이블을 추가하는 함수
    private void setIiemPurchaseLable(JPanel itemPanel, String text, String pos){
        JLabel itemLabel = new JLabel(text, SwingConstants.CENTER);
        Font labelFont = itemLabel.getFont();
        itemLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20)); // 글꼴 크기를 20으로 설정
        itemPanel.add(itemLabel, pos);
    }

    // 아이탬 패널 비활성화
    // 비지속형 아이템: 바로 쿨타임 시작
    // 지속형 아이템: 아이템 효과 지속시간(duation)이 종료된 후 쿨타임 시작
    private void deActiveItemPanel(int i, ItemLabel[] source, int duration, int coolTime, int itemId){
        itemActivated[i] = false;
        setImage(source[0], i, itemDatas[i].getItemDeactivePath()); // 비활성화 이미지 설정

        ActionListener actionListener = new ActionListener() {
            int remainingDelay = coolTime;

            @Override
            public void actionPerformed(ActionEvent e) {
                // delay 감소 및 쿨타임 업데이트
                remainingDelay--;
                inUsePanel.updateCoolTime(itemId);

                // 쿨타임이 끝났을 때 종료 처리
                if (remainingDelay <= 0) {
                    if(!cardPanel.isUnClickable) { // 아이스 에이지 상태면 활성화 지연
                        itemActivated[i] = true; // 아이템 활성화
                        setImage(source[0], i, itemDatas[i].getItemPath()); // 아이템 활성화 이미지로 세팅
                    }
                    inUseItemDataCount--; // 아이템 사용 수 감소
                    inUsePanel.detachInUseItem(itemId); // 아이템 사용 패널에서 제거
                    ((Timer)e.getSource()).stop();
                }
            }
        };

        if (duration == 0) { // 비지속형 아이템이면 바로 쿨타임 시작
            Timer timer = new Timer(1000, actionListener);
            timer.start();
        } else { // 지속형 아이템은 지속시간이 끝난 후 쿨타임 시작
            Timer delayTimer = new Timer(duration * 1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 지속시간 제거, 쿨타임 시작
                    inUsePanel.detachInUseItem(itemId);
                    inUsePanel.attachInUseItem(itemDatas[i], String.valueOf(itemDatas[i].getCoolTime()), false);

                    Timer timer = new Timer(1000, actionListener);
                    timer.start();
                    ((Timer)e.getSource()).stop();
                }
            });
            Timer updateCoolTimeTimer = new Timer(1000, new ActionListener() {
                int remainingDelay = duration;
                @Override
                public void actionPerformed(ActionEvent e) {
                    remainingDelay--;
                    inUsePanel.updateCoolTime(itemId); // 지속시간 레이블 표시 1초씩 감소
                    if (remainingDelay <= 0) {
                        ((Timer)e.getSource()).stop();
                    }
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
            updateCoolTimeTimer.start();
        }
    }


    // 아이템 사용 시 서버에 아이템 사용 알림
    public void sendItemUseNotice(String command){
        Map<String, Object> request = cardPanel.setDefaultRequest(command);

        if(command.equals(COMMAND_RANDOM_FLIP)) {
            boolean[] randomCardArray = new boolean[24];
            Random rand = new Random();
            for(int index = 0; index < randomCardArray.length; index++) {
                randomCardArray[index] = rand.nextBoolean();
            }
            request.put("randomCardArray", randomCardArray); // 전송 데이터에 랜덤 카드 좌표 추가
        }

        MainFrame.dataTranslatorWrapper.broadcast(request);
    }

    // 아이스 에이지 사용 시 모든 아이탬 패널 비활성화
    // 아이스 에이지 쿨타임이 끝나면 다시 활성화된다.
    public void deActiveItemPanel(double delay){
        for(int i = 0; i < 6; i++){
            itemActivated[i] = false;
            setImage(itemLabels[i], i, itemDatas[i].getItemDeactivePath()); // 비활성화 이미지 설정

            // 지정된 시간 후에 아이템 패널을 다시 활성화
            Timer timer = new Timer(1000, null);
            int finalI = i;
            int finalI1 = i;
            timer.addActionListener(new ActionListener() {
                double remainingDelay = delay;
                @Override
                public void actionPerformed(ActionEvent e) {
                    // delay 감소 및 쿨타임 업데이트
                    remainingDelay--;

                    // 쿨타임이 끝났을 때 종료 처리
                    if (remainingDelay <= 0) {
                        itemActivated[finalI] = true; // 아이템 활성화
                        setImage(itemLabels[finalI], finalI, itemDatas[finalI1].getItemPath()); // 아이템 활성화 이미지로 세팅
                        timer.stop();
                    }
                }
            });
            timer.start();
        }
    }
}
