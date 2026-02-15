package dev.playyy.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.DungeonLobby;
import dev.playyy.models.LobbyManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DungeonLobbyUI extends InteractiveCustomUIPage<DungeonLobbyUI.Data> {

    private UUID lobbyId;

    public DungeonLobbyUI(@NonNullDecl PlayerRef playerRef, @NonNullDecl CustomPageLifetime lifetime, UUID lobbyId) {
        super(playerRef, lifetime, Data.CODEC);
        this.lobbyId = lobbyId;
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("Lobby/DungeonLobbyPage.ui");
        buildPlayerList(uiCommandBuilder, uiEventBuilder, store);
    }

    void buildPlayerList(@NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store){
        DungeonLobby lobby = LobbyManager.getLobbyById(lobbyId);
        int index = 0;
        for(var memberId : lobby.getMembersUUID()){
            PlayerRef player = Universe.get().getPlayer(memberId);
            Ref<EntityStore> playerRef = player.getReference();
            uiCommandBuilder.append("#WorldPlayersList", "Pages/SeeEntityInfo/ListItem.ui");
            uiCommandBuilder.set("PlayerList[" + index + "] #Name.Text", player.getUsername());
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating,
                    "#PlayerList[" + index + "] #ReadyButton",
                    EventData.of("ReadyButton", "ReadyToggle")
            );
            index++;
        }
    }



    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, Data data) {
        super.handleDataEvent(ref, store, data);

        if(data.ready_value != null){
            if(data.ready_value.equals("ReadyToggle")){

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
                .build();

        private String ready_value;
    }
}
