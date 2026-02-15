package dev.playyy;

import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.playyy.commands.TestDungeonPageCommand;
import dev.playyy.commands.TestInstanceCommand;
import dev.playyy.events.ExampleEvent;
import dev.playyy.systems.DungeonLootInteraction;
import dev.playyy.systems.EntityDungeonListenSystem;
import dev.playyy.systems.NPCKillSystem;

import javax.annotation.Nonnull;

public class GreatDungeons extends JavaPlugin {

    public GreatDungeons(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new TestInstanceCommand("dungeon", "Spawns an Instance Dungeon"));
        logInfo("Registered Dungeon Command");
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
        logInfo("Registered Welcome System");
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, TestInstanceCommand::onPlayerReady);
        logInfo("Registered Dungeon Ready System");
        this.getEntityStoreRegistry().registerSystem(new NPCKillSystem());
        logInfo("Registered NPC Kill System");
        this.getEntityStoreRegistry().registerSystem(new EntityDungeonListenSystem());
        logInfo("Registered Entity Listen System");
        this.getEntityStoreRegistry().registerSystem(new DungeonLootInteraction());
        logInfo("Registered Dungeon Loot Interaction");
        this.getCommandRegistry().registerCommand(new TestDungeonPageCommand("dungeoninfo", "Spawns an Instance Dungeon"));
        logInfo("Registered Dungeon Info Command");
    }

    public void logInfo(String msg) {
        getLogger().atInfo().log("[Playyy] " + msg);
    }
}