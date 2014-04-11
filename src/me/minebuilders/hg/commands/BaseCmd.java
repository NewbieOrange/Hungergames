package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import me.minebuilders.hg.Config;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCmd
{

    public CommandSender sender;
    public String[] args;
    public String cmdName;
    public int argLength = 0;
    public boolean forcePlayer = true;
    public boolean forceInGame = false;
    public boolean forceInRegion = false;
    public String usage = "";
    public Player player;

    public boolean processCmd(HG p, CommandSender s, String[] arg)
    {
        sender = s;
        args = arg;

        if (s instanceof Player)
        {
            player = (Player) s;
        }
        else if (forcePlayer)
        {
            Util.log("You can not use this command in console.");
            return false;
        }

        if (!s.hasPermission("hg." + cmdName))
            sender.sendMessage(ChatColor.RED + Config.l_dont_have_permission + ": "
                    + ChatColor.GOLD + "/hg " + cmdName);
        else if (forceInGame && !HG.plugin.players.containsKey(player.getName()))
            sender.sendMessage(ChatColor.RED + "Your not in a valid game!");
        else if (forceInRegion && !HG.manager.isInRegion(player.getLocation()))
            sender.sendMessage(ChatColor.RED + "Your not in a valid HungerGames region!");
        else if (argLength > arg.length)
            Util.scm(s, "&4Wrong usage: " + sendHelpLine());
        else
            return run();
        return true;
    }

    public abstract boolean run();

    public String sendHelpLine()
    {
        return "&c/&3hg " + cmdName + " &b" + usage;
    }
}
