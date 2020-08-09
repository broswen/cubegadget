package com.broswen.cubegadget;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public class Home{
    public Material material;
    public Location location;
    private List<String> info;
    public Home(Material material, Location location){
        this.material = material;
        this.location = location;
    }

    @Override
    public String toString() {
        return material + "," + location;
    }

    public void setInfo(List<String> info){
        this.info = info;
    }

    public List<String> getInfo(){
        return this.info;
    }
}
