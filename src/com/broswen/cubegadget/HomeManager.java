package com.broswen.cubegadget;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HomeManager {
    private final int MAX_HOMES = 9;
    private HashMap<UUID, ArrayList<Location>> playerHomes;
    public HomeManager(){
       playerHomes = new HashMap<>();
    }

    public void addHome(Player p, Location home){
        //check if exists
        if(!playerHomes.containsKey(p.getUniqueId())){
            playerHomes.put(p.getUniqueId(), new ArrayList<>());
        }
        ArrayList<Location> homes = playerHomes.get(p.getUniqueId());
        if(homes.size() >= MAX_HOMES){
            p.sendMessage("[] You already have the maximum number of homes (" + MAX_HOMES + ").");
            return;
        }
        p.sendMessage("[] A new home has been added.");
        p.closeInventory();
        playerHomes.get(p.getUniqueId()).add(home);
    }

    public void removeHome(Player p, int index){
        //check if exists
        if(!playerHomes.containsKey(p.getUniqueId())){
            playerHomes.put(p.getUniqueId(), new ArrayList<>());
        }
        ArrayList<Location> homes = playerHomes.get(p.getUniqueId());
        p.sendMessage("[] A home has been removed.");
        p.closeInventory();
        playerHomes.get(p.getUniqueId()).remove(index);
    }

    public ArrayList<Location> getHomes(Player p){
        if(!playerHomes.containsKey(p.getUniqueId())){
            playerHomes.put(p.getUniqueId(), new ArrayList<>());
        }
        return playerHomes.get(p.getUniqueId());
    }

    public void goToHome(Player p, int index){
        if(!playerHomes.containsKey(p.getUniqueId())){
            playerHomes.put(p.getUniqueId(), new ArrayList<>());
        }
        if(playerHomes.get(p.getUniqueId()).get(index) == null){
            p.sendMessage("[] Error finding that home.");
            return;
        }
        p.teleport(playerHomes.get(p.getUniqueId()).get(index));
    }
}
