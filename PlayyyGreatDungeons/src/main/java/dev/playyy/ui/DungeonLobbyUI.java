package dev.playyy.ui;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.GreatDungeons;
import dev.playyy.models.DungeonLobby;
import dev.playyy.models.DungeonManager;
import dev.playyy.models.LobbyManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class DungeonLobbyUI extends InteractiveCustomUIPage<DungeonLobbyUI.Data> {

    private UUID lobbyId;

    public DungeonLobbyUI(@NonNullDecl PlayerRef playerRef, @NonNullDecl CustomPageLifetime lifetime) {
        super(playerRef, lifetime, Data.CODEC);
        LobbyManager.getPlayerLobbybyUuid(playerRef.getUuid()).getMemberData(playerRef.getUuid()).setPage(this);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("Lobby/DungeonLobbyPage.ui");
        buildMiddlePanel(uiCommandBuilder, uiEventBuilder);
        buildLeftPanel(uiCommandBuilder, uiEventBuilder);
    }

    public void updatePlayersList(){
        UICommandBuilder uiCommandBuilder = new UICommandBuilder();
        buildPlayerList(uiCommandBuilder);
        this.sendUpdate(uiCommandBuilder, false);
    }

    void buildMiddlePanel(@NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder){
        buildPlayerList(uiCommandBuilder);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ReadyButton", EventData.of("ReadyButton", "ReadyToggle"));
    }

    void buildPlayerList(@NonNullDecl UICommandBuilder uiCommandBuilder){
        UUID playerUuid = playerRef.getUuid();
        DungeonLobby lobby = LobbyManager.getPlayerLobbybyUuid(playerUuid);
        this.lobbyId = lobby.getLobbyUuid();
        List<UUID> membersId = lobby.getMembersUUID();
        uiCommandBuilder.clear("#PlayerList");
        for(int i = 0; i < membersId.size(); i++){
            PlayerRef player = Universe.get().getPlayer(membersId.get(i));
            uiCommandBuilder.append("#PlayerList", "Lobby/PlayerListItem.ui");
            uiCommandBuilder.set("#PlayerList[" + i + "] #Name.Text", player.getUsername());
            uiCommandBuilder.set("#PlayerList[" + i + "] #Level.Text", "Lvl.40");
            if(lobby.getPlayerState(membersId.get(i)) == DungeonLobby.PlayerLobbyState.READY){
                uiCommandBuilder.set("#PlayerList[" + i + "] #Ready.Text", "Ready");
                uiCommandBuilder.set("#ReadyButton.Text", "Unready");
            }else{
                uiCommandBuilder.set("#PlayerList[" + i + "] #Ready.Text", "Pending");
                uiCommandBuilder.set("#ReadyButton.Text", "Ready");
            }

        }

    }

    void buildLeftPanel(@NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder){
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#StartButton", EventData.of("StartButton", "Start"));
    }



    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, Data data) {
        super.handleDataEvent(ref, store, data);
        DungeonLobby lobby = LobbyManager.getPlayerLobbybyUuid(playerRef.getUuid());
        Player host = lobby.getHost();
        UUID hostUuid = host.getReference().getStore().getComponent(host.getReference(), UUIDComponent.getComponentType()).getUuid();
        if(data.ready_value.equals("ReadyToggle")){
            LobbyManager.getPlayerLobbybyUuid(playerRef.getUuid()).togglePlayerReady(playerRef.getUuid());
            data.ready_value = "";
        }
        if(data.start_value.equals("Start")){
            data.start_value = "";
            if(playerRef.getUuid() == hostUuid){
                if(lobby.isEveryoneReady()){
                    InstancesPlugin plugin = InstancesPlugin.get();

                    UUID playerUUID = playerRef.getUuid();
                    PlayerRef playerRef = Universe.get().getPlayer(playerUUID);
                    World world = Universe.get().getWorld(playerRef.getWorldUuid());

                    ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
                    Transform returnPoint = spawnProvider != null ? spawnProvider.getSpawnPoint(world, playerRef.getUuid()) : new Transform();
                    world.execute(() -> {
                        CompletableFuture<World> worldFuture = InstancesPlugin.get().spawnInstance(lobby.getDungeonInstanceId(), world, returnPoint);
                        worldFuture.thenAccept(w -> {
                            DungeonManager.register(w);
                        });
                        InstancesPlugin.teleportPlayerToLoadingInstance(playerRef.getReference(), playerRef.getReference().getStore(), worldFuture, null);

                    });
                }else{
                    playerRef.sendMessage(Message.raw("Todos os jogadores devem estar prontos!"));
                }
            }else{
                playerRef.sendMessage(Message.raw("Apenas o Host pode iniciar a expedição!"));
            }

        }
        sendUpdate();
    }


    public static class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("ReadyButton", Codec.STRING),
                        (data, s) -> data.ready_value = s,
                        data -> data.ready_value)
                .add()
                .append(new KeyedCodec<>("StartButton", Codec.STRING),
                        (data, s) -> data.start_value = s,
                        data -> data.start_value)
                .add()
                .build();

        private String ready_value = "";
        private String start_value = "";
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }

}
