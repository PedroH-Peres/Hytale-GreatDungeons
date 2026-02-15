package dev.playyy.commands;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import dev.playyy.models.DungeonInstance;
import dev.playyy.models.DungeonManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class TestInstanceCommand extends CommandBase {
    public TestInstanceCommand(@NonNullDecl String name, @NonNullDecl String description) {
        super(name, description);
    }
    private World dungeonWorld;
    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {

        if(!commandContext.isPlayer()) return;

        Player player = commandContext.senderAs(Player.class);
        Ref<EntityStore> selfRef = commandContext.senderAsPlayerRef();

        InstancesPlugin plugin = InstancesPlugin.get();

        UUID playerUUID = commandContext.sender().getUuid();
        PlayerRef playerRef = Universe.get().getPlayer(playerUUID);
        World world = Universe.get().getWorld(playerRef.getWorldUuid());

        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        Transform returnPoint = spawnProvider != null ? spawnProvider.getSpawnPoint(world, playerRef.getUuid()) : new Transform();
        world.execute(() -> {
            CompletableFuture<World> worldFuture = InstancesPlugin.get().spawnInstance("Playyy_Dungeon", world, returnPoint);
            worldFuture.thenAccept(w -> {
               dungeonWorld = w;
               DungeonManager.register(dungeonWorld);
            });
            InstancesPlugin.teleportPlayerToLoadingInstance(playerRef.getReference(), playerRef.getReference().getStore(), worldFuture, null);

        });


    }
    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }

    public static void onPlayerReady(PlayerReadyEvent event){
        Ref<EntityStore> playerRef = event.getPlayerRef();
        Player player = event.getPlayer();
        UUID playerUUID = playerRef.getStore().getComponent(playerRef, UUIDComponent.getComponentType()).getUuid();
        PlayerRef player_Ref = Universe.get().getPlayer(playerUUID);
        World world = playerRef.getStore().getExternalData().getWorld();
        if(DungeonManager.isDungeonWorld(world)){
            DungeonInstance instance = DungeonManager.get(world);
            instance.setEntityCounted();
            instance.setPlayersHud();
        }

    }
}
