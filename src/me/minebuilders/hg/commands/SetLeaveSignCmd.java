package me.minebuilders.hg.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.minebuilders.hg.Util;

public class SetLeaveSignCmd extends BaseCmd
{

    public SetLeaveSignCmd()
    {
        forcePlayer = true;
        cmdName = "setleavesign";
        forceInGame = false;
        argLength = 1;
        forceInRegion = true;
    }

    @Override
    public boolean run()
    {
        Block b = Util.getTargetBlock(player, 6);
        if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST)
        {
            Sign sign = (Sign) b.getState();
            sign.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames");
            sign.setLine(1, ChatColor.DARK_RED + "Leave Game");

            Util.msg(player, "&aThe leaveSign has been set!");
        }
        return true;
    }

}
