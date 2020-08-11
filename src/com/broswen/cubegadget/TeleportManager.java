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

import static com.broswen.cubegadget.CubeGadget.historyManager;
import static com.broswen.cubegadget.CubeGadget.preferenceManager;

public class TeleportManager implements Listener {

    private HashMap<UUID, TeleportRequest> lastRequests;
    private HashMap<UUID, Location> lastPositions;
    public TeleportManager(){
        lastRequests = new HashMap<>();
        lastPositions = new HashMap<>();
    }

    public void sendRequest(Player from, Player to, boolean pull){
        //create new request for a player from a player with a time
        //notify sender/receiver
        TeleportRequest req = new TeleportRequest(from, System.currentTimeMillis(), pull);
        this.lastRequests.put(to.getUniqueId(), req);
        from.sendMessage("[] You sent a teleport request to " + ChatColor.YELLOW + to.getDisplayName() + ChatColor.RESET + ".");
        if(req.pull) {
            to.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " invited you to teleport " + ChatColor.BOLD + "to them"+ChatColor.RESET+".");
        }else{
            to.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " requested to teleport to you.");
        }
        to.playSound(to.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
    }

    public void acceptRequest(Player accepter){
        //check if request exists
        //check if timeout (15 seconds)
        //check if sender online
        //notify sender/receiver
        if(!lastRequests.containsKey(accepter.getUniqueId())) return;
        TeleportRequest req = lastRequests.get(accepter.getUniqueId());
        if(System.currentTimeMillis() - req.sent > 15000){
            accepter.sendMessage("[] The last request has timed out.");
            return;
        }
        if(!lastRequests.get(accepter.getUniqueId()).from.isOnline()){
            accepter.sendMessage("[] " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + " is no longer online.");
            return;
        }
        lastRequests.remove(accepter.getUniqueId());
        accepter.sendMessage("[] You accepted " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + "'s request.");
        req.from.sendMessage("[] " + ChatColor.YELLOW + accepter.getDisplayName() + ChatColor.RESET + " accepted your request.");

        if(req.pull){
            historyManager.addToHistory(accepter, HomeManager.createFromLocation(accepter.getLocation()));
            lastPositions.put(accepter.getUniqueId(), accepter.getLocation());
            accepter.getWorld().playSound(accepter.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            accepter.teleport(req.from.getLocation());
            accepter.getWorld().playSound(accepter.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        }else{
            historyManager.addToHistory(req.from, HomeManager.createFromLocation(req.from.getLocation()));
            lastPositions.put(req.from.getUniqueId(), req.from.getLocation());
            req.from.getWorld().playSound(req.from.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            req.from.teleport(accepter.getLocation());
            req.from.getWorld().playSound(req.from.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        }
    }

    public void denyRequest(Player denier){
        //check if request exists
        //check if timeout (15 seconds)
        //check if sender online
        //notify sender/receiver
        if(!lastRequests.containsKey(denier.getUniqueId())) return;
        TeleportRequest req = lastRequests.get(denier.getUniqueId());
        if(System.currentTimeMillis() - req.sent > 15000){
            denier.sendMessage("[] The last request has timed out.");
            return;
        }
        lastRequests.remove(denier.getUniqueId());
        denier.sendMessage("[] You denied " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + "'s request.");
        req.from.sendMessage("[] " + ChatColor.YELLOW + denier.getDisplayName() + ChatColor.RESET + " denied your request.");
        req.from.playSound(req.from.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL,1, .5f);
    }

    public void teleport(Player p, Home home){
        Location temp = p.getLocation();

        if(!HomeManager.isSafe(home.location) && !preferenceManager.getPreferences(p.getUniqueId()).getOrDefault("IgnoreUnsafe", false)){
            p.sendMessage("[] That position is no longer safe (There are blocks in the way).");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
            return;
        }
        historyManager.addToHistory(p, HomeManager.createFromLocation(p.getLocation()));

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        Location teleport = home.location;
        teleport.getWorld().refreshChunk(teleport.getChunk().getX(), teleport.getChunk().getZ());
        p.teleport(teleport);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        lastPositions.put(p.getUniqueId(), temp);
    }

    public void back(Player p) {
        if(!lastPositions.containsKey(p.getUniqueId())){
            p.sendMessage("[] You don't have a previous location.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL,1, .5f);
            return;
        }



        Location temp = p.getLocation();

        if(!HomeManager.isSafe(lastPositions.get(p.getUniqueId())) && !preferenceManager.getPreferences(p.getUniqueId()).getOrDefault("IgnoreUnsafe", false)){
            p.sendMessage("[] Your last position is no longer safe (There are blocks in the way).");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1, .5f);
            return;
        }
        historyManager.addToHistory(p, HomeManager.createFromLocation(p.getLocation()));

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        Location teleport = lastPositions.get((p.getUniqueId()));
        teleport.getWorld().refreshChunk(teleport.getChunk().getX(), teleport.getChunk().getZ());
        p.teleport(teleport);
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
        public boolean pull;
        public Player from;
        public long sent;
        public TeleportRequest(Player from, long sent){
            this.from = from;
            this.sent = sent;
            this.pull = false;
        }


        //a request to pull the player TO the requester
        public TeleportRequest(Player from, long sent, boolean pull){
            this.from = from;
            this.sent = sent;
            this.pull = pull;
        }

    }
}
