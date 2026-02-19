package dev.playyy.models;

import com.hypixel.hytale.server.core.inventory.ItemStack;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class DungeonKey {

    private final int slotId;
    private final String dungeonMap;
    private final String displayName;
    private final String difficulty;
    private final int playerLimit;

    public DungeonKey(int slot, ItemStack item){
        logInfo("=================");
        logInfo(item.getItem().getData().getRawTags().get("Map")[0].toString());
        logInfo(item.getItem().getData().getRawTags().get("Difficulty")[0].toString());
        logInfo(item.getItem().getData().getRawTags().get("MaxPlayers")[0].toString());
        logInfo("=================");
        this.slotId = slot;
        this.dungeonMap = item.getItem().getData().getRawTags().get("Map")[0].toString();
        this.displayName = item.getItemId();
        this.difficulty = item.getItem().getData().getRawTags().get("Difficulty")[0].toString();
        this.playerLimit = Integer.parseInt(item.getItem().getData().getRawTags().get("MaxPlayers")[0].toString()) ;
        logInfo("DungeonKey Created");

    }

    public String getMapDifficulty(){
        return difficulty;
    }

    public int getSlotId(){return slotId;}
    public String getDungeonMap(){return dungeonMap;}
    public String getDisplayName(){return displayName;}
    public String getDifficulty(){return difficulty;}
    public int getPlayerLimit(){return playerLimit;}
    public String getIcon(){
        switch(dungeonMap){
            case "Crypt":
                return "Deco_Bone_Skulls";
            default:
                return "Deco_Bone_Skulls";
        }

    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }
}
