package dev.playyy.ui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonHUD extends CustomUIHud {

    public static Map<Player, DungeonHUD> activeHuds = new ConcurrentHashMap<>();

    private int totalMobs, currentKills, bosses;
    public DungeonHUD(@NonNullDecl PlayerRef playerRef, int totalMobs, int currentKills, int bosses) {
        super(playerRef);
        this.totalMobs = totalMobs;
        this.currentKills = currentKills;
        this.bosses = bosses;
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Hud/DungeonHud.ui");
        uiCommandBuilder.set("#Kills.Text", "Kills: ["+currentKills+"/"+totalMobs+"]");
        uiCommandBuilder.set("#Boss.Text", "Bosses: ["+bosses+"]");
    }

    public void updateCurrentKills(int currentKills, int bosses){
        this.currentKills = currentKills;
        this.bosses = bosses;
        UICommandBuilder builder = new UICommandBuilder();
        if(currentKills >= totalMobs){
            builder.set("#Kills.Text", "Kills: ["+ this.totalMobs +"/"+totalMobs+"]");
            builder.set("#Kills.Style.TextColor", "#0BA300");
        }else{
            builder.set("#Kills.Text", "Kills: ["+ this.currentKills +"/"+totalMobs+"]");

        }

        if(bosses <= 0){
            builder.set("#Boss.Text", "Bosses: [0]");
            builder.set("#Boss.Style.TextColor", "#0BA300");
        }else{
            builder.set("#Boss.Text", "Bosses: ["+bosses+"]");
        }


        update(false, builder);
    }




}
