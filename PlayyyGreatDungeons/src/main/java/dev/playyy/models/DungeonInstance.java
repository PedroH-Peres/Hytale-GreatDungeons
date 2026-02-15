package dev.playyy.models;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.playyy.ui.DungeonHUD;

import java.util.UUID;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class DungeonInstance {

    private final UUID worldUuid;
    private final World world;

    private DungeonHUD dungeonUi;

    private int totalMobs = 0;
    private int totalMobsToKill = 0;
    private int currentKills = 0;
    private int currentBosses = 0;

    private boolean victoryCondition = false;

    public boolean entityCounted = false;

    public DungeonInstance(World world) {
        this.world = world;
        this.worldUuid = world.getWorldConfig().getUuid();
    }

    public void tryRegisterMob(NPCEntity npc){
        if(!entityCounted){

            if (npc.getNPCTypeId().contains("_Minion")){
                totalMobs++;
                logInfo("Minion contabilizado! Total: "+ totalMobs);
            }else if(npc.getNPCTypeId().contains("_Boss")){
                currentBosses++;
                logInfo("Boss contabilizado! Total: "+ currentBosses);
            }
        }
    }

    public void tryMobDeath(NPCEntity npc){
        if (npc.getNPCTypeId().contains("_Minion")){
            currentKills++;
            updatePlayersHud();
        }else if(npc.getNPCTypeId().contains("_Boss")){
            currentBosses--;
        }

        logInfo("Mobs: [" + currentKills + "/" + totalMobsToKill + "]");
        logInfo("Bosses: ["+currentBosses+"]");


        if(currentKills >= totalMobsToKill && currentBosses <= 0){
            victoryCondition = true;
            victoryEvent();
        }

    }

    public void setPlayersHud(){
        for(var playerRef : world.getPlayerRefs()){
            if(playerRef == null) return;
            Player player = getPlayer(playerRef);
            if(player.getHudManager().getCustomHud() == null){
                dungeonUi = new DungeonHUD(playerRef, totalMobs, currentKills, currentBosses);
                player.getHudManager().setCustomHud(playerRef, dungeonUi);
            }else {
                player.getHudManager().resetHud(playerRef);
            }
        }
    }

    public void updatePlayersHud(){
        for(var playerRef : world.getPlayerRefs()){
            if(playerRef == null) return;
            Player player = getPlayer(playerRef);
            if(player.getHudManager().getCustomHud() == null){
                dungeonUi = new DungeonHUD(playerRef, totalMobs, currentKills, currentBosses);
                player.getHudManager().setCustomHud(playerRef, dungeonUi);
            }else {
                dungeonUi.updateCurrentKills(currentKills, currentBosses);
            }
        }
    }

    public Player getPlayer(PlayerRef playerRef) {
        if (playerRef == null) return null;

        var ref = playerRef.getReference();
        if (ref == null) return null;

        return ref.getStore().getComponent(ref, Player.getComponentType());
    }

    public int getTotalMobsToKill(){
        return totalMobsToKill;
    }

    public int getTotalMobs(){
        return totalMobs;
    }

    public int getCurrentKills(){return currentKills;}

    public boolean getVictoryCondition(){
        return victoryCondition;
    }

    public void setEntityCounted(){
        totalMobsToKill = totalMobs/2;
        entityCounted = true;
        logInfo("Total mobs to Kill: " + totalMobsToKill);
    }

    public void victoryEvent(){
        logInfo("You Win!");
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }






}

