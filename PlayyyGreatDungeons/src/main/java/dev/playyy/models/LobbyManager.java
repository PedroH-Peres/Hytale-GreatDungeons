package dev.playyy.models;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager {

    private static final Map<UUID, DungeonLobby> activeLobbies = new ConcurrentHashMap<>();
    private static final Map<UUID, DungeonLobby> playerLobbyLookup = new ConcurrentHashMap<>();

    public static DungeonLobby createLobby(Player host, String prefab, int max) {
        Ref<EntityStore> hostRef = host.getReference();
        UUID hostUuid = hostRef.getStore().getComponent(hostRef, UUIDComponent.getComponentType()).getUuid();

        if (activeLobbies.containsKey(hostUuid)) {
            return activeLobbies.get(hostUuid);
        }

        DungeonLobby lobby = new DungeonLobby(host, prefab, max);
        activeLobbies.put(hostUuid, lobby);
        playerLobbyLookup.put(hostUuid, lobby);
        return lobby;
    }

    public static DungeonLobby getLobbyById(UUID lobbyId){
        return activeLobbies.get(lobbyId);
    }

    public static DungeonLobby getPlayerLobby(Player player) {
        Ref<EntityStore> playerRef = player.getReference();
        UUID playerUuid = playerRef.getStore().getComponent(playerRef, UUIDComponent.getComponentType()).getUuid();
        return playerLobbyLookup.get(playerUuid);
    }

    public static DungeonLobby getPlayerLobbybyUuid(UUID playerUuid) {
        return playerLobbyLookup.get(playerUuid);
    }

    public static void removeLobby(Player host) {
        Ref<EntityStore> hostRef = host.getReference();
        UUID hostUuid = hostRef.getStore().getComponent(hostRef, UUIDComponent.getComponentType()).getUuid();

        DungeonLobby lobby = activeLobbies.remove(hostUuid);
        if (lobby != null) {
            for (UUID memberId : lobby.getMembersUUID()) {
                playerLobbyLookup.remove(memberId);
            }
        }
    }

    public static void registerPlayerInLobby(Player p, DungeonLobby lobby) {
        Ref<EntityStore> pRef = p.getReference();
        UUID pUuid = pRef.getStore().getComponent(pRef, UUIDComponent.getComponentType()).getUuid();

        playerLobbyLookup.put(pUuid, lobby);
    }



}
