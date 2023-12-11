package Network;


// 서버 이름
public enum ServerName {
    GAME_ROOM_DATA_SERVER, //게임 방 관련 데이터 업데이트 하는 서버
    SCREEN_UI_UPDATE_SERVER, //화면 전환을 위한 서버
    ROOM_LIST_UI_UPDATE_SERVER, //방 목록을 업데이트하기 위한 서버
    PLAYER_STATUS_UI_UPDATE_SERVER, //게임 방의 플레이어 상태를 업데이트하기 위한 서버
    ROOM_CHAT_UI_UPDATE_SERVER, //게임 방 채팅을 업데이트 하기 위한 서버
    CHAT_UI_UPDATE_SERVER, //전체 채팅을 업데이트 하기 위한 서버
    ROOM_CONTROL_UI_UPDATE_SERVER, //방 컨트롤 UI를 업데이트 하기 위한 서버

    CARD_UI_UPDATE_SERVER, // 카드 UI 업데이트 서버
    ITEM_UI_UPDATE_SERVER, // 아이템 UI 업데이트 서버
}

