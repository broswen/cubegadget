package com.broswen.cubegadget;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GadgetMenu implements Listener {
    private Inventory inventory;
    private Material fillerMaterial;
    private Sound good, bad;
    private TeleportManager teleportManager;
    private HomeManager homeManager;
    private Player owner;
    public GadgetMenu(Player owner, TeleportManager teleportManager, HomeManager homeManager, Material filler, Sound good, Sound bad){
        inventory = Bukkit.createInventory(null, 54, "CubeGadget");
        this.owner = owner;
        this.fillerMaterial = filler;
        this.good = good;
        this.bad = bad;
        this.teleportManager = teleportManager;
        this.homeManager = homeManager;

        setIcon(1, Material.GREEN_CONCRETE, "ACCEPT REQUEST");
        setIcon(3, Material.RED_CONCRETE, "DENY REQUEST");
        setIcon(5, Material.GREEN_BED, "ADD HOME", "Shift + Click to remove homes");

        populateWithHomes(9);
        populateWithHeads(18);
    }

    public void show(Player p){
        p.openInventory(this.inventory);
    }

    private void fillWithFiller(int start, int stop){
        for(int i = start; i < stop; i++){
            if(this.inventory.getItem(i) != null) continue;
            this.inventory.setItem(i, new ItemStack(this.fillerMaterial));
        }
    }

    private void populateWithHeads(int start){
        for(Player p : Bukkit.getOnlinePlayers()){
            ItemStack i = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) i.getItemMeta();
            sm.setOwningPlayer(p);
            sm.setDisplayName(p.getDisplayName());
            i.setItemMeta(sm);
            this.inventory.setItem(start++, i);
            if(start > this.inventory.getSize()) break;
        }
    }

    private void populateWithHomes(int start){
        ArrayList<Location> homes = homeManager.getHomes(owner);
        for(int i = 0; i < homes.size(); i++){
            setIcon(i + start, Material.WHITE_BED,
                    homes.get(i).getBlockX() + "," + homes.get(i).getBlockY() + "," + homes.get(i).getBlockZ(),
                    String.valueOf((int) homes.get(i).distance(owner.getLocation())));
        }
    }

    private void setIcon(int position, Material material, String title){
        setIcon(position,material,title, "");
    }
    private void setIcon(int position, Material material, String title, String desc){
        ItemStack i = new ItemStack(material);
        ItemMeta im = i.getItemMeta();
        im.setLore(Arrays.asList(desc));
        im.setDisplayName(title);
        i.setItemMeta(im);
        this.inventory.setItem(position, i);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        //ignore if not this menu
        if(e.getInventory() != this.inventory) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();

        if(i == null || i.getType().equals(this.fillerMaterial)){
            p.playSound(p.getLocation(), this.bad, 1 ,1);
            return;
        }

        if(i.getType().equals(Material.GREEN_CONCRETE)){

            p.playSound(p.getLocation(), good, 1 ,1);
            teleportManager.acceptRequest(p);

        }else if(i.getType().equals(Material.RED_CONCRETE)){

            p.playSound(p.getLocation(), good, 1 ,1);
            teleportManager.denyRequest(p);

        }else if(i.getType().equals(Material.PLAYER_HEAD)){

            p.playSound(p.getLocation(), good, 1 ,1);
            teleportManager.sendRequest(p, Bukkit.getPlayer(i.getItemMeta().getDisplayName()));

        }else if(i.getType().equals(Material.WHITE_BED)){
            int index = e.getRawSlot() - 9;
            if(e.isShiftClick()){
                homeManager.removeHome(p, index);
            }else{
                homeManager.goToHome(p, index);
            }

        }else if(i.getType().equals(Material.GREEN_BED)){
            p.playSound(p.getLocation(), good, 1, 1);
            homeManager.addHome(p, p.getLocation());
        }
    }
}
