package dev.playyy.models;

import com.hypixel.hytale.server.core.entity.entities.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    private final Player leader;
    private final List<Player> members;
    private final int MAX_PLAYERS;

    public Party(Player leader, int max_players){
        this.leader = leader;
        this.MAX_PLAYERS = max_players;
        this.members = new ArrayList<Player>();
        members.add(leader);
    }

    public void addMember(Player player){
        if(members.contains(player)) return;
        if(members.size() >= MAX_PLAYERS) return;
        members.add(player);

    }

    public boolean isFull(){
        return getPlayerQuantity() == MAX_PLAYERS;
    }

    public void removeMember(Player player){
        members.remove(player);
    }

    public int getPlayerQuantity(){
        return members.size();
    }

    public List<Player> getMembers(){
        return members;
    }

    public boolean isOnParty(Player player){
        return members.contains(player);
    }

    public Player getLeader(){
        return leader;
    }


}
