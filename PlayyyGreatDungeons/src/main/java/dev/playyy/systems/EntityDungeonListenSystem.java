package dev.playyy.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.playyy.models.DungeonInstance;
import dev.playyy.models.DungeonManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class EntityDungeonListenSystem extends RefSystem<EntityStore> {
    @Override
    public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        var entityId = ref.getIndex();
        var entityStore = ref.getStore();
        NPCEntity npc = entityStore.getComponent(ref, NPCEntity.getComponentType());
        if(npc == null) return;

        World entityWorld = entityStore.getExternalData().getWorld();
        DungeonInstance instance = DungeonManager.get(entityWorld);
        if(instance == null) return;

        instance.tryRegisterMob(npc);

        logInfo("[Dungeon System]: Instance detected: Trying to register mob");
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        var entityId = ref.getIndex();
        var entityStore = ref.getStore();
        NPCEntity npc = entityStore.getComponent(ref, NPCEntity.getComponentType());
        if(npc == null) return;

        World entityWorld = entityStore.getExternalData().getWorld();
        DungeonInstance instance = DungeonManager.get(entityWorld);
        if(instance == null) return;

        instance.tryMobDeath(npc);

        logInfo("[Dungeon System]: Instance detected: Trying to unregister mob");
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return NPCEntity.getComponentType();
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }
}
