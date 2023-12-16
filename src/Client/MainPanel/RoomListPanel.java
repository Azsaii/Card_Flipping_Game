package Client.MainPanel;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static Client.MainFrame.playerId;

/**
 * 메인 화면에서 게임 방 리스트를 보이는 패널
 */
public class RoomListPanel extends JPanel {

    private ReadOnlyTableModel  tableModel;
    private Image backgroundImage;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }
    public RoomListPanel() {

        try {
            backgroundImage = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnNames = {"방 번호", "참여 인원"};
        tableModel = new ReadOnlyTableModel(columnNames, 0);

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(tableModel);
        table.setRowHeight(table.getRowHeight() * 3); // 리스트 높이 설정
        setTableHeaderFont(table, new Font("SansSerif", Font.BOLD, 20));
        table.setIntercellSpacing(new Dimension(0, 12));  // 수직 간격 설정
        table.setBorder(null); // 테이블 테두리 제거
        table.setShowGrid(false); // 셀 사이 테두리 제거
        table.setOpaque(false);
        table.setBackground(new Color(0,0,0,0));
        setColumnHeight(table, table.getRowHeight());
        table.setTableHeader(null); // 헤더 제거

        // 색상 배열 생성
        Color[] colors = new Color[] {
                new Color(255, 179, 186),  // 파스텔 핑크
                new Color(255, 223, 186),  // 파스텔 오렌지
                new Color(255, 255, 186),  // 파스텔 노랑
                new Color(186, 255, 201),  // 파스텔 민트
                new Color(186, 225, 255),  // 파스텔 블루
                new Color(203, 186, 255),  // 파스텔 보라
                new Color(255, 186, 243)   // 파스텔 핑크 (라벤다)
        };

        // 테이블의 각 행에 랜덤 색상 적용
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 행 번호에 따라 색상 설정
                if (row == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    Color color = colors[(row-1) % colors.length];
                    c.setBackground(color);
                }

                // 텍스트 크기, 색상, 배치 설정
                c.setFont(new Font("SansSerif", Font.BOLD, 20));
                ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);

                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        String value = (String) table.getValueAt(selectedRow, 0);
                        try {
                            long roomId = Long.parseLong(value);
                            showDialog(roomId);
                        } catch (NumberFormatException n) {
                            return;
                        }
                        table.clearSelection(); // 선택된 행 초기화
                    }
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JLabel roomListLabel = new JLabel("방 목록");
        Font gameTitleFont = roomListLabel.getFont();
        roomListLabel.setFont(gameTitleFont.deriveFont(Font.BOLD, 20));
        roomListLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(roomListLabel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 3;
        add(scrollPane, gbc);

        Thread updateRoomListThread = new Thread(() -> {
            System.out.println("방 리스트 업데이트 스레드 작동");

            Map<String, Object> request = new HashMap<>();
            request.put("command", "game_enter");
            request.put("playerId", MainFrame.playerId);
            MainFrame.dataTranslatorWrapper.broadcast(request);
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.ROOM_LIST_UI_UPDATE_SERVER);

            while (true) {
                Map<String, Object> response = dataTranslator.receiveData();
                if (response == null) break;
                String command = (String) response.get("command");

                if (command.equals("room_list_update")) {
                    String[][] rooms = parseStringToArray((String) response.get("roomList"));
                    tableModel.updateRoomList(rooms);
                }
            }
        });

        updateRoomListThread.start();
    }

    // 컬럼 높이를 변경하는 메소드
    private void setColumnHeight(JTable table, int height) {
        table.setRowHeight(height);
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)tableColumn.getHeaderRenderer();
            if (renderer == null) {
                renderer = new DefaultTableCellRenderer();
                tableColumn.setHeaderRenderer(renderer);
            }
            renderer.setPreferredSize(new Dimension(tableColumn.getWidth(), height));
        }
    }
    private void setTableHeaderFont(JTable table, Font font) {
        JTableHeader header = table.getTableHeader();
        header.setFont(font);
    }

    private String[][] parseStringToArray(String str) {
        if (str.equals("[]")) {
            return new String[0][2];
        }

        str = str.substring(2, str.length() - 2);

        String[] rows = str.split("\\], \\[");

        String[][] array = new String[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            array[i] = rows[i].split(", ");
        }

        return array;
    }

    private static class ReadOnlyTableModel extends AbstractTableModel {
        private List<String[]> data;
        private String[] columnNames;

        public ReadOnlyTableModel(String[] columnNames, int initialCapacity) {
            this.columnNames = columnNames;
            this.data = new ArrayList<>(initialCapacity);
        }

        public void updateRoomList(String[][] newData) {
            data.clear();
            data.add(new String[]{"방 번호", "참여 인원"});
            for (String[] row : newData) {
                data.add(row);
            }
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex)[columnIndex];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) { // 모든 셀을 수정 불가능 하게 설정
            return false;
        }
    }

    private void showDialog(long roomId) {
        // 다이얼로그를 띄우는 로직
        int input = JOptionPane.showConfirmDialog(this, "해당 방에 입장하시겠습니까? 선택한 방 번호: " + roomId, "room_enter", JOptionPane.YES_NO_OPTION);

        if(input == JOptionPane.YES_OPTION) { //만약 현재 플레이어가 방 입장을 한다면

            Map<String, Object> request = new HashMap<>();
            request.put("command", "room_enter");
            request.put("playerId", playerId);
            request.put("roomId", roomId);

            MainFrame.dataTranslatorWrapper.broadcast(request);
            DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.GAME_ROOM_DATA_SERVER);
            Map<String, Object> response = dataTranslator.receiveData();

            String result = (String) response.get("result");

            if (result.equals("OK")) { //게임 방 입장이 성공적으로 되었다면
                MainFrame.setRoomid(roomId); // 메인 프레임의 roomId 세팅
            } else if (result.equals("FAIL")) {
                JOptionPane.showMessageDialog(this, "해당 방의 인원이 꽉 차여져 있어 입장할 수 없습니다!", "방 입장", JOptionPane.WARNING_MESSAGE);
            }

            System.out.println("MainFrame.rooId = " + MainFrame.roomId);
        }


    }
}