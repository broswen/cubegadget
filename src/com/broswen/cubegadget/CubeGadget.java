package com.broswen.cubegadget;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CubeGadget extends JavaPlugin implements Listener {
    public static TeleportManager teleportManager;
    public static HomeManager homeManager;

    @Override
    public void onDisable() {
        System.out.println("Disabling CubeGadget");
        super.onDisable();

    }

    @Override
    public void onEnable() {
        System.out.println("Enabling CubeGadget");
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        teleportManager = new TeleportManager();
        homeManager = new HomeManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        if(!i.getType().equals(Material.COMPASS) || !i.getItemMeta().getDisplayName().equals("Gadget")) return;
        //player is clicking compass with name "Gadget"
        //generate gadget and show
        GadgetMenu menu = new GadgetMenu(p, teleportManager, homeManager, Material.GRAY_STAINED_GLASS_PANE, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, Sound.BLOCK_NOTE_BLOCK_BASS);
        menu.show(p);
        getServer().getPluginManager().registerEvents(menu, this);
    }
}
