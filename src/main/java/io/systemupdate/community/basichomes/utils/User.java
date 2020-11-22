package io.systemupdate.community.basichomes.utils;

import io.systemupdate.community.basichomes.BasicHomes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Created by SystemUpdate (http://systemupdate.io) on 16/06/15.
 */
public class User{
    private UUID playerUUID;
    private HashMap<String, Location> homes = new HashMap<>();
    private File userFile;
    private YamlConfiguration userConfig;
    private ConfigurationSection homeNode;
    private int maxHomes = 0;

    public User(final UUID uuid){
        this.playerUUID = uuid;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(BasicHomes.instance, new Runnable() {
            @Override
            public void run() {
                userFile = new File(BasicHomes.instance.getDataFolder() + File.separator + "userdata" + File.separator + playerUUID.toString() + ".yml");
                if(!userFile.exists()){
                    try{
                        userFile.createNewFile();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                userConfig = YamlConfiguration.loadConfiguration(userFile);
                homeNode = userConfig.getConfigurationSection("Home");
                if(homeNode != null){
                    for(String i : homeNode.getKeys(false)){
                        ConfigurationSection node = homeNode.getConfigurationSection(i);
                        homes.put(i, new Location(
                                Bukkit.getServer().getWorld(node.getString("world")),
                                node.getDouble("x"),
                                node.getDouble("y"),
                                node.getDouble("z"),
                                (float)node.getDouble("yaw"),
                                (float)node.getDouble("pitch") )
                        );
                    }
                }
            }
        });
        Player player = Bukkit.getServer().getPlayer(uuid);
        if(player != null){
            for(PermissionAttachmentInfo i : player.getEffectivePermissions()){
                if(i.getPermission().equalsIgnoreCase("basichomes.max.unlimited")){
                    maxHomes = Integer.MAX_VALUE;
                    break;
                }
                if(i.getPermission().startsWith("basichomes.max.")){
                    int value = Integer.valueOf(i.getPermission().replace("basichomes.max.", ""));
                    if(value > maxHomes){
                        maxHomes = value;
                    }
                }
            }
        }
    }

    public void addHome(final String name, final Location location){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(BasicHomes.instance, new Runnable() {
            @Override
            public void run() {
                homes.put(name, location);
                //homeNode.set(name, location);
                ConfigurationSection node = homeNode.createSection(name);
                node.set("world", location.getWorld().getName());
                node.set("x", location.getX());
                node.set("y", location.getY());
                node.set("z", location.getZ());
                node.set("yaw", location.getYaw());
                node.set("pitch", location.getPitch());
                try{
                    userConfig.save(userFile);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void delHome(final String name){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(BasicHomes.instance, new Runnable() {
            @Override
            public void run() {
                for(String i : homes.keySet()){
                    if(i.equalsIgnoreCase(name)){
                        homes.remove(i);
                        homeNode.set(i, null);
                        break;
                    }
                }
                try{
                    userConfig.save(userFile);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public Location getHome(String name){
        for(String i : homes.keySet()){
            if(i.equalsIgnoreCase(name)){
                return homes.get(i);
            }
        }
        return null;
    }

    public Set<String> getHomes(){
        return homes.keySet();
    }

    public int getMaxHomes(){
        return maxHomes;
    }
}
