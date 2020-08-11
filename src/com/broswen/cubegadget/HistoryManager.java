package com.broswen.cubegadget;

import org.bukkit.craftbukkit.libs.jline.console.history.History;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class HistoryManager {
    Map<UUID, LinkedList<Home>> history;

    public HistoryManager(){
        history = new HashMap<>();
    }

    public void addToHistory(Player p, Home loc){
        if(!history.containsKey(p.getUniqueId())) history.put(p.getUniqueId(), new LinkedList<>());
        history.get(p.getUniqueId()).push(loc);
        if(history.get(p.getUniqueId()).size() > 9) history.get(p.getUniqueId()).removeLast();
    }

    public LinkedList<Home> getHistory(Player p){
        return history.getOrDefault(p.getUniqueId(), new LinkedList<>());
    }
}
