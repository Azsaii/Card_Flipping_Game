package Client.MainPanel;

import Client.MainFrame;
import Network.DataTranslator;
import Network.ServerName;
import Server.Data.Player;
import Server.Manager.GameRoomManager;
import Server.Manager.PlayerManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;
import java.util.*;

import static Client.MainFrame.playerId;


public class RoomListPanel extends JPanel {

    private TableModel tableModel;

    public RoomListPanel() {

        String[] columnNames = {"방 번호", "참여 인원"};

        tableModel = new ReadOnlyTableModel(columnNames, 0);

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        JTable table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        long roomId = Long.parseLong((String) table.getValueAt(selectedRow, 0));
                        showDialog(roomId);
                        table.clearSelection(); // 선택된 행 초기화
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

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
            request.put("command", "게임 입장");
            request.put("playerId", MainFrame.playerId);
            MainFrame.dataTranslatorWrapper.broadcast(request);

            while (true) {
                DataTranslator dataTranslator = MainFrame.dataTranslatorWrapper.get(ServerName.ROOM_LIST_UI_UPDATE_SERVER);

                Map<String, Object> response = dataTranslator.receiveData();
                String command = (String) response.get("command");

                if (command.equals("방 리스트 업데이트")) {
                    System.out.println("방 리스트 업데이트");
                    String[][] rooms = parseStringToArray((String) response.get("roomList"));
                    System.out.println("rooms = " + Arrays.deepToString(rooms));
                    ((ReadOnlyTableModel) tableModel).updateRoomList(rooms);
                }
            }
        });

        updateRoomListThread.start();
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
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false; // 모든 셀을 수정 불가능 하게 설정
        }
    }

    private void showDialog(long roomId) {
        // 다이얼로그를 띄우는 로직
        int input = JOptionPane.showConfirmDialog(this, "해당 방에 입장하시겠습니까? 선택한 방 번호: " + roomId, "방 입장", JOptionPane.YES_NO_OPTION);


        if(input == JOptionPane.YES_OPTION) { //만약 현재 플레이어가 방 입장을 한다면


            Map<String, Object> request = new HashMap<>();
            request.put("command", "방 입장");
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

