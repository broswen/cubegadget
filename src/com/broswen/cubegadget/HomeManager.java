package com.broswen.cubegadget;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
            return;
        }
        p.sendMessage("[] A new home has been added.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
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
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
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
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        p.teleport(playerHomes.get(p.getUniqueId()).get(index));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    public String serializeLocation(Location l){
        return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ","
                + l.getYaw() + "," + l.getPitch();
    }

    public Location deserializeLocation(String s){
        String[] parts = s.split(",");
        if(parts.length < 6) return null;
        World w = Bukkit.getWorld(parts[0]);
        if(w == null) return null;
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

    public void saveHomes(FileConfiguration config) {
        for(UUID uuid : playerHomes.keySet()){
            ArrayList<Location> homes = playerHomes.get(uuid);
            ArrayList<String> strHomes = new ArrayList<>();
            for(Location l : homes){
                strHomes.add(serializeLocation(l));
            }
            config.set(uuid.toString(), strHomes);
        }

    }

    public void loadHomes(FileConfiguration config) {
        for(String k : config.getKeys(false)){
            UUID uuid = UUID.fromString(k);
            List<String> homes = config.getStringList(k);
            ArrayList<Location> locHomes = new ArrayList<>();
            for(String h : homes){
                locHomes.add(deserializeLocation(h));
            }
            playerHomes.put(uuid, locHomes);
        }
    }
}
