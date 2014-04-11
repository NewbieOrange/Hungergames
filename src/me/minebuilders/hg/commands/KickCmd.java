package me.minebuilders.hg.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class KickCmd extends BaseCmd
{

    public KickCmd()
    {
        forcePlayer = false;
        cmdName = "kick";
        argLength = 2;
        usage = "<&cplayer&b>";
    }

    @Override
    public boolean run()
    {
        Player player = Bukkit.getPlayer(args[1]);
        if (player != null)
        {
            Game game = HG.manager.getGame(player.getLocation());
            if (game != null)
            {
                game.leave(player);

                Util.msg(player,
                        ChatColor.RED + "You have been kicked by " + sender.getName());
                Util.msg(sender, ChatColor.RED + "The player " + args[1]
                        + ChatColor.DARK_AQUA + " has been kicked from arena "
                        + ChatColor.AQUA + game.getName());
            }
            else
            {
                Util.msg(sender, ChatColor.RED + "The player " + args[1]
                        + " is not available!");
            }
        }
        else
        {
            Util.msg(sender, ChatColor.RED + "The player " + args[1] + " does not exist!");
        }
        return true;
    }

}
