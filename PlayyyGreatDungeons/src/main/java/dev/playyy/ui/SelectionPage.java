package dev.playyy.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.LobbyManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class SelectionPage extends InteractiveCustomUIPage<DungeonLobbyUI.Data> {
    public SelectionPage(@NonNullDecl PlayerRef playerRef, @NonNullDecl CustomPageLifetime lifetime, @NonNullDecl BuilderCodec<DungeonLobbyUI.Data> eventDataCodec) {
        super(playerRef, lifetime, eventDataCodec);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {

    }

    public static class Data {
        public static final BuilderCodec<DungeonLobbyUI.Data> CODEC = BuilderCodec.builder(DungeonLobbyUI.Data.class, DungeonLobbyUI.Data::new)
                .build();

        private String ready_value;
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, DungeonLobbyUI.Data data) {
        super.handleDataEvent(ref, store, data);

        sendUpdate();
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }
}
