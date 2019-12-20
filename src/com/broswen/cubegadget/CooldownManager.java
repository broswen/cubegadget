package com.broswen.cubegadget;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private HashMap<UUID, Long> lastAction;
    public final long DELAY = 3000;
    public CooldownManager(){
       lastAction = new HashMap<>();
    }

    public long getLastAction(Player p){
        return lastAction.getOrDefault(p.getUniqueId(), 0L);
    }

    public void setLastAction(Player p){
        lastAction.put(p.getUniqueId(), System.currentTimeMillis());
    }

    public boolean canAction(Player p){
        return (System.currentTimeMillis() - getLastAction(p)) > DELAY;
    }
}
