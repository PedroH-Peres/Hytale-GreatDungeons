package dev.playyy.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;


public class NPCKillSystem extends DeathSystems.OnDeathSystem {

    private final ComponentType<EntityStore, NPCEntity> npcType;

    public NPCKillSystem() {
        this.npcType = NPCEntity.getComponentType();
    }


    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl DeathComponent deathComponent, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        NPCEntity npc = commandBuffer.getComponent(ref, npcType);
        if (npc == null) {
            return;
        }

        logInfo("NPC death detected: " + npc.getNPCTypeId());

        Ref<EntityStore> attackerRef = settingAtackRef(deathComponent, npc);
        if (attackerRef == null || !attackerRef.isValid()) {
            logInfo("No valid attacker ref for NPC death: ref=" + ref.getIndex());
            return;
        }
        PlayerRef killerRef = commandBuffer.getComponent(attackerRef, PlayerRef.getComponentType());
        UUID killerUuid = killerRef == null ? null : killerRef.getUuid();
        if(killerRef == null){return;}
        var packetHandler = killerRef.getPacketHandler();

        var primaryMessage = Message.raw("KILL!").color("#00FF00");
        var secondaryMessage = Message.raw("You have killed a " + npc.getNPCTypeId()).color("#228B22");
        var icon = new ItemStack("Weapon_Sword_Mithril", 1).toPacket();
        NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage,
                secondaryMessage,
                (ItemWithAllMetadata) icon);

        Player targetPlayer = commandBuffer.getComponent(attackerRef, Player.getComponentType());
        if(targetPlayer != null){
            targetPlayer.sendMessage(Message.raw("You killed a mob!"));
        }

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return npcType;
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }

    private Ref<EntityStore> settingAtackRef(DeathComponent death, NPCEntity npc){
        Damage damage = death.getDeathInfo();
        if(damage != null){
            Damage.Source source = damage.getSource();
            if(source instanceof Damage.EntitySource){
                Ref<EntityStore> ref = ((Damage.EntitySource) source).getRef();
                if (ref != null && ref.isValid()) {
                    logInfo("Attacker resolved from death info: ref=" + ref.getIndex());
                    return ref;
                }
            }
        }

        var damageData = npc.getDamageData();
        if (damageData != null) {
            Ref<EntityStore> ref = damageData.getMostDamagingAttacker();
            if (ref != null && ref.isValid()) {
                logInfo("Attacker resolved from damage data: ref=" + ref.getIndex());
                return ref;
            }
            Ref<EntityStore> any = damageData.getAnyAttacker();
            if (any != null && any.isValid()) {
                logInfo("Fallback attacker resolved from damage data: ref=" + any.getIndex());
                return any;
            }
        }
        return null;
    }
}
