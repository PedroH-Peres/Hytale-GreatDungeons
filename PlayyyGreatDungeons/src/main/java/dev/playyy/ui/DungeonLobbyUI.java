package dev.playyy.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.GreatDungeons;
import dev.playyy.models.DungeonLobby;
import dev.playyy.models.LobbyManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

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
        logInfo("To indo buildar a lista");
        buildMiddlePanel(uiCommandBuilder, uiEventBuilder);

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
        logInfo("Iniciando build de " + playerUuid.toString());
        DungeonLobby lobby = LobbyManager.getPlayerLobbybyUuid(playerUuid);
        this.lobbyId = lobby.getLobbyUuid();
        logInfo(lobby.toString());
        List<UUID> membersId = lobby.getMembersUUID();
        logInfo("Size: " + membersId.size());
        logInfo(membersId.toString());
        uiCommandBuilder.clear("#PlayerList");
        for(int i = 0; i < membersId.size(); i++){
            PlayerRef player = Universe.get().getPlayer(membersId.get(i));
            logInfo("Player [" + i + "]");
            uiCommandBuilder.append("#PlayerList", "Lobby/PlayerListItem.ui");
            uiCommandBuilder.set("#PlayerList[" + i + "] #Name.Text", player.getUsername());
            uiCommandBuilder.set("#PlayerList[" + i + "] #Level.Text", "Lvl.40");
            if(lobby.getPlayerState(membersId.get(i)) == DungeonLobby.PlayerLobbyState.READY){
                uiCommandBuilder.set("#PlayerList[" + i + "] #Ready.Text", "Ready");
            }else{
                uiCommandBuilder.set("#PlayerList[" + i + "] #Ready.Text", "Pending");
            }

        }

    }



    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, Data data) {
        super.handleDataEvent(ref, store, data);
        if(data.ready_value.equals("ReadyToggle")){
            LobbyManager.getPlayerLobbybyUuid(playerRef.getUuid()).togglePlayerReady(playerRef.getUuid());
        }
        sendUpdate();
    }


    public static class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("ReadyButton", Codec.STRING),
                        (data, s) -> data.ready_value = s,
                        data -> data.ready_value)
                .add()
                .build();

        private String ready_value;
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }

}
