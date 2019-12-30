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

public class GadgetMenu implements Listener {
    private Inventory inventory, icons;
    private Material fillerMaterial;
    private TeleportManager teleportManager;
    private HomeManager homeManager;
    private CooldownManager cooldownManager;
    private Player owner;
    public GadgetMenu(Player owner, TeleportManager teleportManager, HomeManager homeManager, CooldownManager cooldownManager, Material filler){
        inventory = Bukkit.createInventory(null, 54, "CubeGadget");
        icons = Bukkit.createInventory(null, 9, "Icons");
        this.owner = owner;
        this.fillerMaterial = filler;
        this.teleportManager = teleportManager;
        this.homeManager = homeManager;
        this.cooldownManager = cooldownManager;

        //setting icon chooser inventory
        setIcon(icons, 0, Material.GRASS_BLOCK, "Grass");
        setIcon(icons, 1, Material.SAND, "Sand");
        setIcon(icons, 2, Material.STONE, "Stone");
        setIcon(icons, 3, Material.OAK_LOG, "Log");
        setIcon(icons, 4, Material.BRICKS, "Bricks");
        setIcon(icons, 5, Material.STONE_BRICKS, "Stone Bricks");
        setIcon(icons, 6, Material.END_STONE, "End Stone");
        setIcon(icons, 7, Material.NETHER_BRICKS, "Nether Bricks");
        setIcon(icons, 8, Material.PRISMARINE, "Prismarine");

        setIcon(inventory,1, Material.GREEN_CONCRETE, "ACCEPT REQUEST", "Accept teleport request");
        setIcon(inventory,3, Material.RED_CONCRETE, "DENY REQUEST", "Deny teleport request");
        setIcon(inventory, 5, Material.GREEN_BED, "ADD HOME", "Shift + Click to remove homes");

        Location lastPosition = teleportManager.getLastPosition(owner);
        if(lastPosition == null){
            setIcon(inventory, 7, Material.END_PORTAL_FRAME, "BACK");
        }else{
            setIcon(inventory, 7, Material.END_PORTAL_FRAME, "BACK", lastPosition.getBlockX() + "," + lastPosition.getBlockY() + "," + lastPosition.getBlockZ());
        }
        setIcon(inventory, 8, Material.CRAFTING_TABLE, "WORKBENCH", "Click for crafting table");

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
        ArrayList<Home> homes = homeManager.getHomes(owner);
        for(int i = 0; i < homes.size(); i++){
            setIcon(inventory, i + start, homes.get(i).material,
                    homes.get(i).location.getBlockX() + "," + homes.get(i).location.getBlockY() + "," + homes.get(i).location.getBlockZ());
        }
    }

    public static void setIcon(Inventory inv, int position, Material material, String title){
        setIcon(inv, position,material,title, "");
    }
    public static void setIcon(Inventory inv, int position, Material material, String title, String desc){
        ItemStack i = new ItemStack(material);
        ItemMeta im = i.getItemMeta();
        im.setLore(Arrays.asList(desc));
        im.setDisplayName(title);
        i.setItemMeta(im);
        inv.setItem(position, i);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        //ignore if not this menu
        if(e.getInventory() != this.inventory && e.getInventory() != icons) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();

        if(i == null || i.getType().equals(this.fillerMaterial) || e.getRawSlot() > 53){
            return;
        }

        if(e.getInventory() == icons){
            homeManager.addHome(p, p.getLocation(), i.getType());
            return;
        }

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1 ,1);

        if(i.getType().equals(Material.GREEN_CONCRETE)){
            teleportManager.acceptRequest(p);
            return;
        }else if(i.getType().equals(Material.RED_CONCRETE)){
            teleportManager.denyRequest(p);
            return;
        }else if(i.getType().equals(Material.GREEN_BED)) {
            p.openInventory(icons);
            return;
        }else if(i.getType().equals(Material.CRAFTING_TABLE)){
            p.openWorkbench(p.getLocation(),true);
            return;
        }

        if(!cooldownManager.canAction(p)){
            p.sendMessage("[] Please wait " + cooldownManager.DELAY/1000 + " seconds between teleports.");
            return;
        }

        cooldownManager.setLastAction(p);


        if(i.getType().equals(Material.PLAYER_HEAD)){

            teleportManager.sendRequest(p, Bukkit.getPlayer(i.getItemMeta().getDisplayName()));

        }else if(e.getRawSlot() > 8 && e.getRawSlot() < 18){
            int index = e.getRawSlot() - 9;
            if(e.isShiftClick()){
                homeManager.removeHome(p, index);
            }else{
                homeManager.goToHome(p, index);
            }
        }else if(i.getType().equals(Material.END_PORTAL_FRAME)){
            teleportManager.back(p);
        }
    }
}
