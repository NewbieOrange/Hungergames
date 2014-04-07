package me.minebuilders.hg.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RandomItems
{

    private FileConfiguration item = null;
    private File customConfigFile = null;
    private final HG plugin;

    public RandomItems(HG plugin)
    {
        this.plugin = plugin;
        reloadCustomConfig();
        load();
    }

    public void reloadCustomConfig()
    {
        if (customConfigFile == null)
        {
            customConfigFile = new File(plugin.getDataFolder(), "items.yml");
        }
        item = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource("items.yml");
        if (defConfigStream != null)
        {
            YamlConfiguration defConfig = YamlConfiguration
                    .loadConfiguration(defConfigStream);

            item.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCustomConfig()
    {
        if (item == null)
        {
            this.reloadCustomConfig();
        }
        return item;
    }

    public void saveCustomConfig()
    {
        if (item == null || customConfigFile == null)
        {
            return;
        }
        try
        {
            getCustomConfig().save(customConfigFile);
        }
        catch (IOException ex)
        {
            Util.log("Could not save config to " + customConfigFile);
        }
    }

    public void load()
    {
        if (item.getStringList("items").isEmpty())
        {
            setDefaultss();
            saveCustomConfig();
            reloadCustomConfig();
            Util.log("generating defaults for random items!");
        }
        for (String s : item.getStringList("items"))
        {
            String[] amount = s.split(" ");
            for (String p : amount)
            {
                if (p.startsWith("x:"))
                {
                    int c = Integer.parseInt(p.replace("x:", ""));
                    while (c != 0)
                    {
                        c--;
                        plugin.items.put(plugin.items.size() + 1,
                                plugin.ism.getItem(s.replace("x:", ""), true));
                    }
                }
                else
                {
                    plugin.items
                            .put(plugin.items.size() + 1, plugin.ism.getItem(s, true));
                }
            }
        }
        Util.log(plugin.items.size() + " Random items have been loaded!");
    }

    public void setDefaultss()
    {
        ArrayList<String> s = new ArrayList<String>();
        s.add("STONE_SWORD 1 x:5");
        s.add("GOLD_SWORD 1");
        s.add("MUSHROOM_SOUP 1 x:2");
        s.add("STONE_HOE 1");
        s.add("LEATHER_HELMET 1 x:2");
        s.add("LEATHER_CHESTPLATE 1 x:2");
        s.add("LEATHER_LEGGINGS 1 x:2");
        s.add("LEATHER_BOOTS 1 x:2");
        s.add("IRON_HELMET 1 x:2");
        s.add("IRON_CHESTPLATE 1 x:2");
        s.add("IRON_LEGGINGS 1 x:2");
        s.add("IRON_BOOTS 1 x:2");// 280
        s.add("BOW 1 x:3");
        s.add("ARROW 20 x:2");
        s.add("MILK_BUCKET 1 x:2");
        s.add("FISHING_ROD 1");
        s.add("COMPASS 1");
        s.add("STICK 1 name:&6TrackingStick_&aUses:_5");
        s.add("GOLD_HELMET 1");
        s.add("GOLD_CHESTPLATE 1");
        s.add("GOLD_LEGGINGS 1");
        s.add("GOLD_BOOTS 1");
        s.add("BONE 1 x:2");
        s.add("DIAMOND_SWORD 1 name:&6Death_Dealer");
        s.add("GOLDEN_APPLE 1");
        s.add("CHAINMAIL_CHESTPLATE 1 x:1");
        s.add("CHAINMAIL_LEGGINGS 1 x:1");
        s.add("COOKIE 2 x:3");
        s.add("MELON 1 x:4");
        s.add("COOKED_BEEF 1 x:2");
        s.add("ENDER_PEARL 1 x:2");
        s.add("POTION:8194 1 x:2");
        s.add("POTION:8197 1 x:2");
        s.add("POTION:16420 1");
        s.add("POTION:16385 1 x:2");
        s.add("APPLE 2 x:5");
        item.set("items", s);
    }
}
