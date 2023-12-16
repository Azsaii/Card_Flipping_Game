package Client.Chat;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * 메인 화면, 룸 화면에서 사용되는 채팅 패널 템플릿
 */
public abstract class ChatPanel extends JPanel {
    protected JTextPane textPane;
    protected JTextField textField;
    protected JButton button;

    public GridBagConstraints gbc;
    public JScrollPane scrollPane;
    public JPanel chatBottomPanel;

    public ChatPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        gbc = new GridBagConstraints();

        textPane = new JTextPane();
        scrollPane = new JScrollPane(textPane);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);

        chatBottomPanel = new JPanel();
        chatBottomPanel.setLayout(new BorderLayout());
        chatBottomPanel.setBackground(Color.white);

        textField = new JTextField(30);
        textPane.setEditable(false);
        textPane.setOpaque(false);

        Image scaledImage = new ImageIcon("images/SEND.png").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT);
        ImageIcon imageIcon = new ImageIcon(scaledImage);

        button = new JButton(imageIcon);

        button.setOpaque(false);
        button.setContentAreaFilled(true);
        button.setBackground(new Color(0,0,0,0)); // 투명한 배경색 설정
        button.setBorder(new RoundBorder(20));

        textField.setBorder(new RoundBorder(20));
        textPane.setBorder(new RoundBorder(20));

        chatBottomPanel.add("Center", textField);
        chatBottomPanel.add("East", button);
        chatBottomPanel.setOpaque(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 3.5;
        gbc.fill = GridBagConstraints.BOTH;

        add(scrollPane, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(10, 0, 0, 0); // 상단에 10픽셀의 여백 추가
        add(chatBottomPanel, gbc);
    }

    class RoundBorder implements Border {
        private int radius;

        RoundBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }
}

