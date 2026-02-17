package dev.playyy.systems;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import dev.playyy.models.DungeonKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryScannerSystem {

    public static List<DungeonKey> getAvailableKeys(Player player){
        List<DungeonKey> keys = new ArrayList<>();
        for(int i = 0; i < player.getInventory().getStorage().getCapacity(); i++){
            ItemStack item = player.getInventory().getStorage().getItemStack((short)i);
            if(item == null) continue;
            if(item.getItemId().contains("Key")){
                String dif = item.getItem().getData().getRawTags().get("Difficulty")[0];
                player.sendMessage(Message.raw("Item encontrado com a dificuldade: " + dif));
                keys.add(new DungeonKey(i, item));
            }

        }
        return keys;
    }

}
