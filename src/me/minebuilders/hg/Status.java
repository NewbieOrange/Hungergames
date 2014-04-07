package me.minebuilders.hg;

import org.bukkit.ChatColor;

import me.minebuilders.hg.Config;

public enum Status
{
    RUNNING(ChatColor.GREEN + "" + ChatColor.BOLD + Config.l_Status_running), STOPPED(
            ChatColor.DARK_RED + "" + ChatColor.BOLD + Config.l_Status_stopped), WAITING(
            ChatColor.AQUA + "" + ChatColor.BOLD + Config.l_Status_waiting), BROKEN(
            ChatColor.DARK_RED + "" + ChatColor.BOLD + Config.l_Status_broken), ROLLBACK(
            ChatColor.RED + "" + ChatColor.BOLD + Config.l_Status_rollback), NOTREADY(
            ChatColor.DARK_BLUE + "" + ChatColor.BOLD + Config.l_Status_not_ready), BEGINNING(
            ChatColor.GREEN + "" + ChatColor.BOLD + Config.l_Status_beginning), COUNTDOWN(
            ChatColor.AQUA + "" + ChatColor.BOLD + Config.l_Status_count_down);

    private String name;

    Status(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
