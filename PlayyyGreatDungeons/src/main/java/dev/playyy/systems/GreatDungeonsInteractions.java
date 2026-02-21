package dev.playyy.systems;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.DungeonInstance;
import dev.playyy.models.DungeonLobby;
import dev.playyy.models.DungeonManager;
import dev.playyy.models.LobbyManager;
import dev.playyy.ui.DungeonLobbyUI;
import dev.playyy.ui.SelectionPage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;

import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class GreatDungeonsInteractions extends EntityEventSystem<EntityStore, UseBlockEvent.Pre> {


    public GreatDungeonsInteractions() {
        super(UseBlockEvent.Pre.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl UseBlockEvent.Pre event) {
        logInfo("Block ID: "+ event.getBlockType().getId());
        logInfo("Interaction: " + event.getInteractionType().toString());
        Ref<EntityStore> playerRef = event.getContext().getEntity();
        PlayerRef pRef = playerRef.getStore().getComponent(playerRef, PlayerRef.getComponentType());
        Player player = playerRef.getStore().getComponent(playerRef, Player.getComponentType());
        if(player==null) return;
        World world = playerRef.getStore().getExternalData().getWorld();
        if(DungeonManager.isDungeonWorld(world)){
            DungeonInstance instance = DungeonManager.get(world);
            if(event.getBlockType().getId().equals("Furniture_Lumberjack_Chest_Small")){
                if(!instance.getVictoryCondition()){
                    player.sendMessage(Message.raw("Baú bloqueado!"));
                    event.setCancelled(true);
                }
            }
        }

        if(event.getBlockType().getId().equals("GreatDungeons_Portal")){
            if(LobbyManager.getPlayerLobby(player) == null){
                logInfo("To aqui!");
                player.getPageManager().openCustomPage(playerRef, playerRef.getStore(), new SelectionPage(pRef, CustomPageLifetime.CanDismiss));

                pRef.sendMessage(Message.raw("Select Page Shown"));
            }else{
                if(LobbyManager.getPlayerLobby(player).getMemberData(pRef.getUuid()) == null) return;
                if(LobbyManager.getPlayerLobby(player).getMemberData(pRef.getUuid()).page == null) return;

                player.getPageManager().openCustomPage(playerRef, playerRef.getStore(), LobbyManager.getPlayerLobby(player).getMemberData(pRef.getUuid()).page );
            }


        }


    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }
}
