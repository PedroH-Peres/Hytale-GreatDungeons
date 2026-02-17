package dev.playyy.models;

import java.util.HashMap;
import java.util.Map;

public class DungeonDatabase {

    private static final Map<String, String> instanceMap = new HashMap<>();

    static{

        registerInstance("Crypt", 1, "EASY", "Crypt_Solo_Easy");
        registerInstance("Crypt", 4, "EASY", "Crypt_Group_Easy");
        registerInstance("Crypt", 1, "MEDIUM", "Crypt_Solo_Medium");

    }


    private static void registerInstance(String mapName, int size, String diff, String instanceId) {
        instanceMap.put(mapName + ":" + size + ":" + diff, instanceId);
    }

    public static String getInstanceId(String mapName, int size, String difficulty) {
        return instanceMap.get(mapName + ":" + size + ":" + difficulty);
    }

}
