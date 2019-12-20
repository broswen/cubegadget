package com.broswen.cubegadget;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {

    private HashMap<UUID, TeleportRequest> lastRequests;
    public TeleportManager(){
        lastRequests = new HashMap<>();
    }

    public void sendRequest(Player from, Player to){
        //create new request for a player from a player with a time
        //notify sender/receiver
        this.lastRequests.put(to.getUniqueId(), new TeleportRequest(from, System.currentTimeMillis()));
        from.sendMessage("[] You sent a teleport request to " + ChatColor.YELLOW + to.getDisplayName() + ChatColor.RESET + ".");
        to.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " requested to teleport to you.");
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

        from.sendMessage("[] You accepted " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + "'s request.");
        req.from.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " accepted your request.");
        req.from.teleport(from.getLocation());
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
        from.sendMessage("[] You denied " + ChatColor.YELLOW + req.from.getDisplayName() + ChatColor.RESET + "'s request.");
        req.from.sendMessage("[] " + ChatColor.YELLOW + from.getDisplayName() + ChatColor.RESET + " denied your request.");
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
