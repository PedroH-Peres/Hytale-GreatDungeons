package dev.playyy.models;

import com.hypixel.hytale.server.core.entity.entities.Player;
import dev.playyy.components.LobbyState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonLobby {

    private final UUID lobbyId;
    private final Player host;
    private final String dungeonPrefab;
    private final int maxPlayers;

    public enum PlayerLobbyState {
        READY,
        PENDING
    }

    private final Map<UUID, PlayerLobbyState> members = new ConcurrentHashMap<>();

    public DungeonLobby(Player host, String dungeonPrefab, int maxPlayers){
        this.lobbyId = UUID.randomUUID();
        this.host = host;
        this.dungeonPrefab = dungeonPrefab;
        this.maxPlayers = maxPlayers;
    }

    public Player getHost(){
        return host;
    }

    public UUID getLobbyUuid(){
        return lobbyId;
    }

    public int getMaxPlayers(){
        return maxPlayers;
    }

    public List<UUID> getMembersUUID(){
        return new ArrayList<>(members.keySet());
    }

    public PlayerLobbyState getPlayerState(UUID playerUuid){
        return members.get(playerUuid);
    }

    public boolean isEveryoneReady(){
        return members.values().stream()
                .allMatch(state -> state == PlayerLobbyState.READY);
    }

    public void togglePlayerReady(UUID playerUuid){
        if(members.get(playerUuid) == PlayerLobbyState.READY){
            members.put(playerUuid, PlayerLobbyState.PENDING);
        } else if (members.get(playerUuid) == PlayerLobbyState.PENDING) {
            members.put(playerUuid, PlayerLobbyState.READY);
        }
    }

}
