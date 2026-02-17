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
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.DungeonKey;
import dev.playyy.models.DungeonLobby;
import dev.playyy.models.LobbyManager;
import dev.playyy.systems.InventoryScannerSystem;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class CreateLobbyPage extends InteractiveCustomUIPage<CreateLobbyPage.Data> {
    public CreateLobbyPage(@NonNullDecl PlayerRef playerRef, @NonNullDecl CustomPageLifetime lifetime) {
        super(playerRef, lifetime, Data.CODEC);
    }

    private int selected;

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("UI/CreateLobby.ui");
        buildMapList(uiCommandBuilder, uiEventBuilder);
        buildRightPanel(uiCommandBuilder, uiEventBuilder);
    }

    void buildRightPanel(@NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder){
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CreateLobbyButton", EventData.of("CreateButton", "create"));
        playerRef.sendMessage(Message.raw("Create Button created"));
    }

    void buildMapList(@NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder){
        List<DungeonKey> keys = InventoryScannerSystem.getAvailableKeys(playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType()));
        uiCommandBuilder.clear("#KeyList");
        for(int i = 0; i < keys.size(); i++){
            uiCommandBuilder.append("#KeyList", "UI/MapKeyItem.ui");
            uiCommandBuilder.set("#KeyList[" + i + "] #MapName.Text", keys.get(i).getDungeonMap());
            uiCommandBuilder.set("#KeyList[" + i + "] #MapDifficulty.Text", keys.get(i).getDifficulty());
            uiCommandBuilder.set("#KeyList[" + i + "] #MapMaxPlayers.Text", ""+keys.get(i).getPlayerLimit());
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#KeyList[" + i + "] #MapButton", EventData.of("KeySelect", ""+i));
        }
    }

    void buildMiddlePanel(@NonNullDecl UICommandBuilder uiCommandBuilder, DungeonKey key){

    }

    void buildMapInfo(@NonNullDecl UICommandBuilder uiCommandBuilder, DungeonKey key){

    }



    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, Data data) {
        super.handleDataEvent(ref, store, data);
        List<DungeonKey> keys = InventoryScannerSystem.getAvailableKeys(playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType()));

        if(data.selected_value != null && !data.selected_value.equals("")) {
            selected = Integer.parseInt(data.selected_value);

        }
        playerRef.sendMessage(Message.raw("Selected: " + selected));
        playerRef.sendMessage(Message.raw("Value: " + data.create_value));
        DungeonKey key = keys.get(selected);
        buildMiddlePanel(new UICommandBuilder(), key);

        if(data.create_value.equals("create")){
            playerRef.sendMessage(Message.raw("Criando..."));
            Player host = playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType());
            DungeonLobby lobby = LobbyManager.createLobby(host, key.getDungeonMap(), key.getPlayerLimit(), key.getMapDifficulty().toUpperCase());
            host.getPageManager().openCustomPage(ref, store, new DungeonLobbyUI(playerRef, CustomPageLifetime.CanDismiss));
            data.create_value = "";
        }

        sendUpdate();
    }


    public static class Data {
        public static final BuilderCodec<CreateLobbyPage.Data> CODEC = BuilderCodec.builder(CreateLobbyPage.Data.class, CreateLobbyPage.Data::new)
                .append(new KeyedCodec<>("KeySelect", Codec.STRING),
                        (data, s) -> data.selected_value = s,
                        data -> data.selected_value)
                .add()
                .append(new KeyedCodec<>("CreateButton", Codec.STRING),
                        (data, s) -> data.create_value = s,
                        data -> data.create_value)
                .add()
                .build();

        private String selected_value = "";
        private String create_value = "";
    }
}
