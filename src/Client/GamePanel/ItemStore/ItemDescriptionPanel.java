package Client.GamePanel.ItemStore;

import javax.swing.*;
import java.awt.*;

/**
 * 아이템 설명 패널입니다.
 * GameItemPurchasePanel 의 아이템 위에 마우스를 올리면 이 패널에 아이템 정보가 표시됨
 */
public class ItemDescriptionPanel extends JPanel {
    
    JLabel nameLabel; // 아이템 이름 레이블
    JTextArea descriptionArea; // 아이템 설명 텍스트 영역
    Font labelFont;

    final static String INIT_DESCRIPTION = "아이템 위에 커서를 올리면 설명이 표시됩니다.";

    public ItemDescriptionPanel(){

        setLayout(new BorderLayout());

        nameLabel = new JLabel("", SwingConstants.CENTER);
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false); // 편집 불가능으로 설정
        descriptionArea.setLineWrap(true); // 자동 줄바꿈 활성화
        descriptionArea.setWrapStyleWord(true); // 단어 단위로 줄바꿈
        descriptionArea.setFont(new Font("", Font.PLAIN, 15)); // 글꼴 설정
        add(nameLabel, BorderLayout.NORTH);
        add(descriptionArea, BorderLayout.CENTER);

        setItemDescription("",INIT_DESCRIPTION);
    }

    public void setItemDescription(String name, String description) {
        nameLabel.setText(name);
        labelFont = nameLabel.getFont();
        nameLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 20)); // 글꼴 크기를 20으로 설정
        descriptionArea.setText(description);
    }

    // 마우스가 아이템 바깥으로 나갔을 때 설명 패널 초기화
    public void clearItemName() {
        setItemDescription("",INIT_DESCRIPTION);
    }
}
