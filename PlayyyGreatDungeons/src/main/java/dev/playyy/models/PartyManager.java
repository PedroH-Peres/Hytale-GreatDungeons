package dev.playyy.models;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {

    private static final Map<UUID, Party> playerPartyMap = new ConcurrentHashMap<>();

    public static Party createParty(Player leader, int max_players) {
        Ref<EntityStore> leaderRef = leader.getReference();
        UUID leaderUUID = leaderRef.getStore().getComponent(leaderRef, UUIDComponent.getComponentType()).getUuid();
        if (playerPartyMap.containsKey(leaderUUID)) return null; // Já tem party

        Party party = new Party(leader, max_players);
        playerPartyMap.put(leaderUUID, party);
        return party;
    }

    public static Party getParty(Player p) {
        Ref<EntityStore> playerRef = p.getReference();
        UUID playerUUID = playerRef.getStore().getComponent(playerRef, UUIDComponent.getComponentType()).getUuid();
        return playerPartyMap.get(playerUUID);
    }

    public static void disband(Party party) {
        for (Player member : party.getMembers()) {
            Ref<EntityStore> playerRef = member.getReference();
            UUID playerUUID = playerRef.getStore().getComponent(playerRef, UUIDComponent.getComponentType()).getUuid();
            playerPartyMap.remove(playerUUID);
            member.sendMessage(Message.raw("Grupo desfeito"));
        }
    }

    public static void joinPlayerToLeader(Player guest, Player leader) {
        Party party = getParty(leader);
        if (party != null && !party.isFull()) {
            Ref<EntityStore> guestRef = guest.getReference();
            UUID guestUUID = guestRef.getStore().getComponent(guestRef, UUIDComponent.getComponentType()).getUuid();
            party.addMember(guest);
            playerPartyMap.put(guestUUID, party);
        }
    }


}
