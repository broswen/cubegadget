package com.broswen.cubegadget;

import org.bukkit.Location;
import org.bukkit.Material;

public class Home{
    public Material material;
    public Location location;
    public Home(Material material, Location location){
        this.material = material;
        this.location = location;
    }

    @Override
    public String toString() {
        return material + "," + location;
    }
}
