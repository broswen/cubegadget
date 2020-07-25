package com.broswen.cubegadget;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GadgetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return false;
        Player sender = (Player) commandSender;
        ItemStack i = new ItemStack(Material.COMPASS);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName("Gadget");
        im.setLore(Arrays.asList("Sneak + Click for Back"));
        i.setItemMeta(im);
        sender.getWorld().dropItemNaturally(sender.getLocation(), i);
        sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        sender.sendMessage("[] Here is your Gadget!");
        return true;
    }
}
