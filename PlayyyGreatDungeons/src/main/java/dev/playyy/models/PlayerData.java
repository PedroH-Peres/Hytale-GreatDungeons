package dev.playyy.models;

import dev.playyy.ui.DungeonLobbyUI;

import java.util.UUID;

public class PlayerData {
    public final UUID UUID;
    DungeonLobbyUI page;

    PlayerData(UUID uuid){
        this.UUID = uuid;
    }

    public void rebuildPage(){
        page.updatePlayersList();
    }

    public void setPage(DungeonLobbyUI lobbyPage){
        this.page = lobbyPage;
    }

}
