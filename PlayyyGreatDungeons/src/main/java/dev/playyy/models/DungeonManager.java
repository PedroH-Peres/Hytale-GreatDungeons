package dev.playyy.models;

import com.hypixel.hytale.server.core.universe.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonManager {

    private static final Map<World, DungeonInstance> activeDungeons = new ConcurrentHashMap<>();

    public static void register(World world) {
        activeDungeons.put(world, new DungeonInstance(world));
    }

    public static void unregister(World world) {
        activeDungeons.remove(world);
    }

    public static DungeonInstance get(World world) {
        return activeDungeons.get(world);
    }

    public static boolean isDungeonWorld(World world) {
        return activeDungeons.containsKey(world);
    }

}
