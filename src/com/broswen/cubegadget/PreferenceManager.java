package com.broswen.cubegadget;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class PreferenceManager {

    private Map<UUID, Map<String, Boolean>> preferencesMap;

    public PreferenceManager(){
        preferencesMap = new HashMap<>();
    }

    public Map<String, Boolean> getPreferences(UUID uuid){
        if(!preferencesMap.containsKey(uuid)) preferencesMap.put(uuid, new HashMap<>());
        return preferencesMap.get(uuid);
    }


    public void loadPreferences(FileConfiguration config){
        for(String k : config.getConfigurationSection("preferences").getKeys(false)){
            UUID uuid = UUID.fromString(k);
            Map<String, Boolean> prefs = new HashMap<>();
            for(String k2 : config.getConfigurationSection("preferences." + uuid).getKeys(false)){
                boolean temp = config.getBoolean("preferences." + uuid + "." + k2, false);
                prefs.put(k2, temp);
            }
            preferencesMap.put(uuid, prefs);
        }
    }

    public void savePreferences(FileConfiguration config){
        for(UUID uuid : preferencesMap.keySet()){
            Map<String, Boolean> prefs = preferencesMap.get(uuid);
            for(String k : prefs.keySet()){
                //TODO save prefs as list
                config.getConfigurationSection("preferences." + uuid + "." + k + "." + prefs.get(k));
            }
        }
    }
}
