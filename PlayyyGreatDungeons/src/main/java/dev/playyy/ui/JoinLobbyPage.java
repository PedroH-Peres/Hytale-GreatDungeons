package dev.playyy.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.DungeonLobby;
import dev.playyy.models.LobbyManager;
import io.sentry.MeasurementUnit;
import jdk.jfr.Event;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.UUID;

public class JoinLobbyPage extends InteractiveCustomUIPage<JoinLobbyPage.Data> {
    public JoinLobbyPage(@NonNullDecl PlayerRef playerRef, @NonNullDecl CustomPageLifetime lifetime) {
        super(playerRef, lifetime, Data.CODEC);
    }

    private String selectedUuid;


    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("UI/JoinLobby.ui");
        buildLobbyList(uiCommandBuilder, uiEventBuilder);
    }

    void buildLobbyList(@NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder){
        uiCommandBuilder.clear("#LobbyList");
        int index = 0;
        playerRef.sendMessage(Message.raw("Size: "+ LobbyManager.getActiveLobbies().size()));
        for(var lobby : LobbyManager.getActiveLobbies()){
            uiCommandBuilder.append("#LobbyList", "UI/LobbyListItem.ui");
            uiCommandBuilder.set("#LobbyList["+index+"] #Map.Text", lobby.getMapName());
            uiCommandBuilder.set("#LobbyList["+index+"] #Difficulty.Text", lobby.getMapDifficulty());
            uiCommandBuilder.set("#LobbyList["+index+"] #Host.Text", lobby.getHost().getDisplayName());
            uiCommandBuilder.set("#LobbyList["+index+"] #MaxPlayers.Text", "["+lobby.getPlayersQuantity()+"/"+ lobby.getMaxPlayers()+"]");
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#LobbyList["+index+"] #RoomButton", EventData.of("Select", lobby.getLobbyUuid().toString()));
            index++;
        }
    }

    void buildLobbyInfo(@NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, String selected){
        DungeonLobby selectedLobby = LobbyManager.getLobbyById((UUID.fromString(selected)));
        uiCommandBuilder.clear("#LobbyInfo");
        uiCommandBuilder.append("#LobbyInfo", "UI/JoinLobbyItem.ui");
        uiCommandBuilder.set("#LobbyInfo[0] #Map.Text", selectedLobby.getMapName());
        uiCommandBuilder.set("#LobbyInfo[0] #Difficulty.Text", selectedLobby.getMapDifficulty());
        uiCommandBuilder.set("#LobbyInfo[0] #MaxPlayers.Text", "["+selectedLobby.getPlayersQuantity()+"/"+ selectedLobby.getMaxPlayers()+"]");
        uiCommandBuilder.set("#LobbyInfo[0] #Host.Text", selectedLobby.getHost().getDisplayName());
        uiCommandBuilder.set("#LobbyInfo[0] #ItemIcon.ItemId", selectedLobby.getIcon());
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#LobbyInfo[0] #JoinButton", EventData.of("JoinButton","Join"));

        this.sendUpdate(uiCommandBuilder, uiEventBuilder, false);
    }

    public void updateLobbyList(){
        UICommandBuilder uiCommandBuilder = new UICommandBuilder();
        UIEventBuilder uiEventBuilder = new UIEventBuilder();
        buildLobbyList(uiCommandBuilder, uiEventBuilder);
        this.sendUpdate(uiCommandBuilder, uiEventBuilder, false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, JoinLobbyPage.Data data) {
        super.handleDataEvent(ref, store, data);

        if(data.selected_lobby != null){
            selectedUuid = data.selected_lobby;
            UICommandBuilder uiCommandBuilder = new UICommandBuilder();
            UIEventBuilder uiEventBuilder = new UIEventBuilder();
            buildLobbyInfo(uiCommandBuilder,uiEventBuilder, data.selected_lobby);
            updateLobbyList();
        }
        playerRef.sendMessage(Message.raw("join_value: "+ data.join_value + "selected: " + data.selected_lobby));
        if(selectedUuid != null && data.join_value.equals("Join")){
            playerRef.sendMessage(Message.raw("Entrei no lobby"));
            DungeonLobby lobby = LobbyManager.getLobbyById(UUID.fromString(selectedUuid));
            if(!lobby.isFull()){
                Player player = playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType());
                lobby.addMember(player);
                player.getPageManager().openCustomPage(playerRef.getReference(), playerRef.getReference().getStore(), new DungeonLobbyUI(playerRef, CustomPageLifetime.CanDismiss));

            }else{
                playerRef.sendMessage(Message.raw("Sala cheia!"));
            }
        }

        sendUpdate();
    }


    public static class Data {
        public static final BuilderCodec<JoinLobbyPage.Data> CODEC = BuilderCodec.builder(JoinLobbyPage.Data.class, JoinLobbyPage.Data::new)
                .append(new KeyedCodec<>("Select", Codec.STRING),
                        (data, s) -> data.selected_lobby = s,
                        data -> data.selected_lobby)
                .add()
                .append(new KeyedCodec<>("JoinButton", Codec.STRING),
                        (data, s) -> data.join_value = s,
                        data -> data.join_value)
                .add()
                .build();

        private String join_value = "";
        private String selected_lobby;
    }
}
