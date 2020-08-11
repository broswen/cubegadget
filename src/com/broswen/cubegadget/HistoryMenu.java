package com.broswen.cubegadget;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

import static com.broswen.cubegadget.CubeGadget.*;

public class HistoryMenu implements Listener {

    private Inventory inventory;
    private Player owner;

    public HistoryMenu(Player owner) {
        inventory = Bukkit.createInventory(null, 9, "Location History");
        this.owner = owner;


        populate();
    }

    private void populate() {
        LinkedList<Home> history = historyManager.getHistory(this.owner);
        int i = 0;
        for (Home h : history) {
            GadgetMenu.setIcon(inventory, i, h.material, h.location.getBlockX() + "," + h.location.getBlockY() + "," + h.location.getBlockZ(), h.getInfo());
            i++;
        }
    }

    public void show(Player p){
        p.openInventory(this.inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        //ignore if not this menu
        if (e.getInventory() != this.inventory) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();


        if (i == null || e.getRawSlot() > 8) {
            e.setCancelled(true);
            return;
        }

        if(!cooldownManager.canAction(p)){
            p.sendMessage("[] Please wait " + cooldownManager.DELAY/1000 + " seconds between teleports.");
            return;
        }

        cooldownManager.setLastAction(p);

        Home h = historyManager.getHistory(p).get(e.getRawSlot());

        teleportManager.teleport(p, h);

    }
}
