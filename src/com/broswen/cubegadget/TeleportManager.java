package com.broswen.cubegadget;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager implements Listener {

    private HashMap<UUID, TeleportRequest> lastRequests;
    private HashMap<UUID, Location> lastPositions;
    public TeleportManager(){
        lastRequests = new HashMap<>();
        lastPositions = new HashMap<>();
    }

    public void sendRequest(Player from, Player to){
        //create new request for a player from a player with a time
        //notify sender/receiver
        this.lastRequests.put(to.getUniqueId(), new TeleportRequest(from, System.currentTimeMillis()));
        from.sendMessage("[] You sent a teleport request to " + ChatColor.YELLOW + to.getDisplayName() + ChatColor.RESET + ".");
        to.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " requested to teleport to you.");
        to.playSound(to.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
    }

    public void acceptRequest(Player from){
        //check if request exists
        //check if timeout (15 seconds)
        //check if sender online
        //notify sender/receiver
        if(!lastRequests.containsKey(from.getUniqueId())) return;
        TeleportRequest req = lastRequests.get(from.getUniqueId());
        if(System.currentTimeMillis() - req.sent > 15000){
            from.sendMessage("[] The last request has timed out.");
            return;
        }
        if(!lastRequests.get(from.getUniqueId()).from.isOnline()){
            from.sendMessage("[] " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + " is no longer online.");
            return;
        }
        lastRequests.remove(from.getUniqueId());
        from.sendMessage("[] You accepted " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + "'s request.");
        req.from.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " accepted your request.");
        lastPositions.put(req.from.getUniqueId(), req.from.getLocation());
        req.from.getWorld().playSound(req.from.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        req.from.teleport(from.getLocation());
        req.from.getWorld().playSound(req.from.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    public void denyRequest(Player from){
        //check if request exists
        //check if timeout (15 seconds)
        //check if sender online
        //notify sender/receiver
        if(!lastRequests.containsKey(from.getUniqueId())) return;
        TeleportRequest req = lastRequests.get(from.getUniqueId());
        if(System.currentTimeMillis() - req.sent > 15000){
            from.sendMessage("[] The last request has timed out.");
            return;
        }
        lastRequests.remove(from.getUniqueId());
        from.sendMessage("[] You denied " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + "'s request.");
        req.from.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " denied your request.");
        req.from.playSound(req.from.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL,1, .5f);
    }

    public void back(Player p) {
        if(!lastPositions.containsKey(p.getUniqueId())){
            p.sendMessage("[] You don't have a previous location.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL,1, .5f);
            return;
        }



        Location temp = p.getLocation();

        if(!HomeManager.isSafe(lastPositions.get(p.getUniqueId()))){
            p.sendMessage("[] Your last position is no longer safe (There are blocks in the way).");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
            return;
        }
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        p.teleport(lastPositions.get(p.getUniqueId()));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        lastPositions.put(p.getUniqueId(), temp);

    }

    public void updateLastPosition(Player p){
        lastPositions.put(p.getUniqueId(), p.getLocation());
    }

    public Location getLastPosition(Player p){
        return lastPositions.get(p.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        updateLastPosition(e.getEntity());
    }

    private class TeleportRequest{
        public Player from;
        public long sent;
        public TeleportRequest(Player from, long sent){
            this.from = from;
            this.sent = sent;
        }
    }
}
