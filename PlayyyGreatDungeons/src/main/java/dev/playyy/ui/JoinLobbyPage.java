package dev.playyy.ui;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.LobbyManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

public class JoinLobbyPage extends InteractiveCustomUIPage<JoinLobbyPage.Data> {
    public JoinLobbyPage(@NonNullDecl PlayerRef playerRef, @NonNullDecl CustomPageLifetime lifetime) {
        super(playerRef, lifetime, Data.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("UI/JoinLobby.ui");
        uiCommandBuilder.clear("#LobbyList");
        int index = 0;
        playerRef.sendMessage(Message.raw("Size: "+ LobbyManager.getActiveLobbies().size()));
        for(var lobby : LobbyManager.getActiveLobbies()){
            uiCommandBuilder.append("#LobbyList", "UI/LobbyListItem.ui");
            uiCommandBuilder.set("#LobbyList["+index+"] #Map.Text", lobby.getMapName());
            uiCommandBuilder.set("#LobbyList["+index+"] #Difficulty.Text", lobby.getMapDifficulty());
            uiCommandBuilder.set("#LobbyList["+index+"] #Host.Text", lobby.getHost().getDisplayName());
            uiCommandBuilder.set("#LobbyList["+index+"] #MaxPlayers.Text", "["+lobby.getPlayersQuantity()+"/"+ lobby.getMaxPlayers()+"]");
            index++;
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, JoinLobbyPage.Data data) {
        super.handleDataEvent(ref, store, data);
        sendUpdate();
    }


    public static class Data {
        public static final BuilderCodec<JoinLobbyPage.Data> CODEC = BuilderCodec.builder(JoinLobbyPage.Data.class, JoinLobbyPage.Data::new)
                .build();

        private String ready_value;
    }
}
