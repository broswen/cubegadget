package com.broswen.cubegadget;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.console.history.History;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CubeGadget extends JavaPlugin implements Listener {
    public static TeleportManager teleportManager;
    public static HomeManager homeManager;
    public static PreferenceManager preferenceManager;
    public static CooldownManager cooldownManager;
    public static HistoryManager historyManager;
    public static Inventory icons;

    @Override
    public void onDisable() {
        System.out.println("Disabling CubeGadget");
        super.onDisable();

        HandlerList.unregisterAll((Listener) this);

        homeManager.saveHomes(getConfig());
        preferenceManager.savePreferences(getConfig());
        saveConfig();

    }

    @Override
    public void onEnable() {
        System.out.println("Enabling CubeGadget");
        super.onEnable();

        this.getCommand("gadget").setExecutor(new GadgetCommand());

        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);
        historyManager = new HistoryManager();
        teleportManager = new TeleportManager();
        homeManager = new HomeManager();
        preferenceManager = new PreferenceManager();
        cooldownManager = new CooldownManager();


        getServer().getPluginManager().registerEvents(teleportManager, this);
        homeManager.loadHomes(getConfig());
        preferenceManager.loadPreferences(getConfig());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        if(!i.getType().equals(Material.COMPASS) || !i.getItemMeta().getDisplayName().equals("Gadget")) return;
        e.setCancelled(true);
        if(p.isSneaking()){
            if(!cooldownManager.canAction(p)){
                p.sendMessage("[] Please wait " + cooldownManager.DELAY/1000 + " seconds between teleports.");
                return;
            }

            cooldownManager.setLastAction(p);

            teleportManager.back(p);
            return;
        }
        GadgetMenu menu = new GadgetMenu(p, Material.GRAY_STAINED_GLASS_PANE, this);
        menu.show(p);
        getServer().getPluginManager().registerEvents(menu, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        System.out.println(preferenceManager.getPreferences(e.getPlayer().getUniqueId()).getOrDefault("IgnoreUnsafe", false));
    }
}
