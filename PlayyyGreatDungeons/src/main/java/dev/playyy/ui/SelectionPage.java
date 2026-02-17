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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.LobbyManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class SelectionPage extends InteractiveCustomUIPage<SelectionPage.Data> {
    public SelectionPage(@NonNullDecl PlayerRef playerRef, @NonNullDecl CustomPageLifetime lifetime) {
        super(playerRef, lifetime, Data.CODEC);
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("UI/SelectionPage.ui");
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CreateButton", EventData.of("Selection", "Create"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#JoinButton", EventData.of("Selection", "Join"));
    }

    public static class Data {
        public static final BuilderCodec<SelectionPage.Data> CODEC = BuilderCodec.builder(SelectionPage.Data.class, SelectionPage.Data::new)
                .append(new KeyedCodec<>("Selection", Codec.STRING),
                        (data, s) -> data.selection_value = s,
                        data -> data.selection_value)
                .add()
                .build();

        private String selection_value = "";
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, SelectionPage.Data data) {
        super.handleDataEvent(ref, store, data);
        Player player = ref.getStore().getComponent(ref, Player.getComponentType());
        if(data.selection_value.equals("Create")){
            player.getPageManager().openCustomPage(ref, store, new CreateLobbyPage(playerRef, CustomPageLifetime.CanDismiss));
        }else if(data.selection_value.equals("Join")){
            player.getPageManager().openCustomPage(ref, store, new JoinLobbyPage(playerRef, CustomPageLifetime.CanDismiss));
        }
        data.selection_value = "";
        sendUpdate();
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }
}
