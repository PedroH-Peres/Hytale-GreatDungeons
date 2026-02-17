package dev.playyy.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.playyy.models.DungeonLobby;
import dev.playyy.models.LobbyManager;
import dev.playyy.ui.DungeonLobbyUI;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class TestDungeonPageCommand extends AbstractPlayerCommand {

    public TestDungeonPageCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = commandContext.senderAs(Player.class);

        CompletableFuture.runAsync(() -> {
            logInfo("Comando executado");
            DungeonLobby lobby = LobbyManager.createLobby(player, "a", 2, "EASY");
            logInfo("Dungeon lobby criado: " + lobby.toString());
            player.getPageManager().openCustomPage(ref, store, new DungeonLobbyUI(playerRef, CustomPageLifetime.CanDismiss));
            logInfo("Teoricamente deu tudo certo");

            playerRef.sendMessage(Message.raw("UI Page Shown"));
        }, world);
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }
}
