package com.broswen.cubegadget;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeManager {
    private final int MAX_HOMES = 9;
    private HashMap<UUID, ArrayList<Home>> playerHomes;
    private TeleportManager teleportManager;
    public HomeManager(TeleportManager teleportManager){
       playerHomes = new HashMap<>();
       this.teleportManager = teleportManager;
    }

    public void addHome(Player p, Location location, Material material){
        //check if exists
        if(!playerHomes.containsKey(p.getUniqueId())){
            playerHomes.put(p.getUniqueId(), new ArrayList<>());
        }
        ArrayList<Home> homes = playerHomes.get(p.getUniqueId());
        if(homes.size() >= MAX_HOMES){
            p.sendMessage("[] You already have the maximum number of homes (" + MAX_HOMES + ").");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
            return;
        }

        if(!isSafe(location)){
            p.sendMessage("[] The specified location is unsafe.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
            return;
        }
        p.sendMessage("[] A new home has been added.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        p.closeInventory();
        playerHomes.get(p.getUniqueId()).add(new Home(material, location));
    }

    public void removeHome(Player p, int index){
        //check if exists
        if(!playerHomes.containsKey(p.getUniqueId())){
            playerHomes.put(p.getUniqueId(), new ArrayList<>());
        }
        ArrayList<Home> homes = playerHomes.get(p.getUniqueId());
        p.sendMessage("[] A home has been removed.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
        p.closeInventory();
        playerHomes.get(p.getUniqueId()).remove(index);
    }

    public ArrayList<Home> getHomes(Player p){
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
        Home home = playerHomes.get(p.getUniqueId()).get(index);
        if(!isSafe(home.location)){
            p.sendMessage("[] That home is no longer safe (There are blocks in the way).");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
            return;
        }
        teleportManager.updateLastPosition(p);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        home.location.getWorld().refreshChunk(home.location.getChunk().getX(), home.location.getChunk().getZ());
        p.teleport(home.location.add(0,.05,0));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    public String serializeLocation(Location l){
        return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + ","
                + l.getYaw() + "," + l.getPitch();
    }

    public String serializeHome(Home h){
        return h.material.toString() + "," + h.location.getWorld().getName() + "," + h.location.getBlockX() + "," + h.location.getBlockY() + "," + h.location.getBlockZ() + ","
                + h.location.getYaw() + "," + h.location.getPitch();
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

    public Home deserializeHome(String s){
        String[] parts = s.split(",");
        if(parts.length < 6) return null;
        World w = Bukkit.getWorld(parts[1]);
        if(w == null) return null;
        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        double z = Double.parseDouble(parts[4]);
        float yaw = Float.parseFloat(parts[5]);
        float pitch = Float.parseFloat(parts[6]);
        return new Home(Material.getMaterial(parts[0]), new Location(w, x, y, z, yaw, pitch));
    }

    public static boolean isSafe(Location l){
        return l.getBlock().isPassable() || l.getBlock().getRelative(BlockFace.UP).isPassable();
    }

    public void saveHomes(FileConfiguration config) {
        for(UUID uuid : playerHomes.keySet()){
            ArrayList<Home> homes = playerHomes.get(uuid);
            ArrayList<String> strHomes = new ArrayList<>();
            for(Home h : homes){
                strHomes.add(serializeHome(h));
            }
            config.set(uuid.toString(), strHomes);
        }

    }

    public void loadHomes(FileConfiguration config) {
        for(String k : config.getKeys(false)){
            UUID uuid = UUID.fromString(k);
            List<String> homes = config.getStringList(k);
            ArrayList<Home> locHomes = new ArrayList<>();
            for(String h : homes){
                //locHomes.add(deserializeLocation(h));
                locHomes.add(deserializeHome(h));
            }
            playerHomes.put(uuid, locHomes);
        }
    }
}


