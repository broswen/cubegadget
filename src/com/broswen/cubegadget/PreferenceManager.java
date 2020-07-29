package com.broswen.cubegadget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class PreferenceManager {

    private static Logger logger = LogManager.getLogger(PreferenceManager.class);

    private Map<UUID, Map<String, Boolean>> preferencesMap;

    public PreferenceManager(){
        preferencesMap = new HashMap<>();
    }

    public Map<String, Boolean> getPreferences(UUID uuid){
        if(!preferencesMap.containsKey(uuid)) preferencesMap.put(uuid, new HashMap<>());
        return preferencesMap.get(uuid);
    }


    public void loadPreferences(FileConfiguration config){

        if(config.getConfigurationSection("preferences") == null) config.createSection("preferences");

        for(String k : config.getConfigurationSection("preferences").getKeys(false)){
            UUID uuid = UUID.fromString(k);
            logger.info("loading prefs for: {}", uuid);
            Map<String, Boolean> prefs = new HashMap<>();
            for(String k2 : config.getConfigurationSection("preferences." + uuid).getKeys(false)){
                boolean temp = config.getBoolean("preferences." + uuid + "." + k2, false);
                logger.info(k2, temp);
                prefs.put(k2, temp);
            }
            preferencesMap.put(uuid, prefs);
        }
    }

    public void savePreferences(FileConfiguration config){
        for(UUID uuid : preferencesMap.keySet()){
            logger.info("saving prefs for: {}", uuid);
            Map<String, Boolean> prefs = preferencesMap.get(uuid);
            for(String k : prefs.keySet()){
                logger.info(k, prefs.getOrDefault(k, false));
                if(config.getConfigurationSection("preferences." + uuid.toString()) == null) config.createSection("preferences." + uuid.toString());
                config.getConfigurationSection("preferences." + uuid).set(k, prefs.getOrDefault(k, false));
            }
        }
    }
}
