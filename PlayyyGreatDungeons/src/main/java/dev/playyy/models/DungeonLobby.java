package dev.playyy.models;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.components.LobbyState;
import dev.playyy.ui.DungeonLobbyUI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonLobby {

    private final UUID lobbyId;
    private final Player host;
    private final String mapName;
    private final String dungeonInstanceId;
    private final int maxPlayers;

    public enum PlayerLobbyState {
        READY,
        PENDING
    }

    public enum MapDifficulty{
        EASY,
        MEDIUM,
        HARD,
        EXTREME
    }

    private final String mapDifficulty;
    private final Map<UUID, PlayerData> membersData = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerLobbyState> members = new ConcurrentHashMap<>();

    public DungeonLobby(Player host, String mapName, int maxPlayers, String mapDifficulty){
        this.lobbyId = UUID.randomUUID();
        this.host = host;
        this.mapName = mapName;
        this.dungeonInstanceId = DungeonDatabase.getInstanceId(mapName, maxPlayers, mapDifficulty);
        this.maxPlayers = maxPlayers;
        this.mapDifficulty = mapDifficulty.toUpperCase();
        this.addMember(host);
    }

    public void addMember(Player player){
        Ref<EntityStore> playerRef = player.getReference();
        UUID playerId = playerRef.getStore().getComponent(playerRef, UUIDComponent.getComponentType()).getUuid();
        LobbyManager.addMemberinLobby(playerId, this);
        membersData.put(playerId, new PlayerData(playerId));
        members.put(playerId, PlayerLobbyState.PENDING);
        for (var memberData : this.getMembersData()){
            if (memberData.page == null) continue;
            memberData.page.updatePlayersList();
        }
    }

    public Player getHost(){
        return host;
    }
    public String getMapName(){return mapName;}
    public int getPlayersQuantity(){
        return members.size();
    }

    public String getDungeonInstanceId(){
        return dungeonInstanceId;
    }

    public UUID getLobbyUuid(){
        return lobbyId;
    }

    public int getMaxPlayers(){
        return maxPlayers;
    }

    public boolean isFull(){
        return members.size() == maxPlayers;
    }

    public List<UUID> getMembersUUID(){
        return new ArrayList<>(members.keySet());
    }

    public PlayerData getMemberData(UUID playerUuid){
        return membersData.get(playerUuid);
    }

    public Collection<PlayerData> getMembersData(){
        return membersData.values();
    }

    public PlayerLobbyState getPlayerState(UUID playerUuid){
        return members.get(playerUuid);
    }

    public String getMapDifficulty(){
        return mapDifficulty;
    }

    public boolean isEveryoneReady(){
        return members.values().stream()
                .allMatch(state -> state == PlayerLobbyState.READY);
    }

    public String getIcon(){
        switch(mapName){
            case "Crypt":
                return "Deco_Bone_Skulls";
            default:
                return "Deco_Bone_Skulls";
        }

    }

    public void togglePlayerReady(UUID playerUuid){
        if(members.get(playerUuid) == PlayerLobbyState.READY){
            members.put(playerUuid, PlayerLobbyState.PENDING);
        } else if (members.get(playerUuid) == PlayerLobbyState.PENDING) {
            members.put(playerUuid, PlayerLobbyState.READY);
        }
        for (var memberData : getMembersData()){
            memberData.page.updatePlayersList();
        }
    }

}
