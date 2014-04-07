package me.minebuilders.hg.tasks;

import org.bukkit.Bukkit;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class StartingTask implements Runnable
{

    private int timer;
    private int id;
    private Game game;

    public StartingTask(Game g)
    {
        this.timer = 30;
        this.game = g;
        Util.broadcast("&b&l Arena " + g.getName() + " will begin in 30 seconds!");
        Util.broadcast("&b&l Use:&3&l /hg join " + g.getName() + "&b&l to join!");

        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, 20L,
                20L);
    }

    @Override
    public void run()
    {
        timer--;

        int size = game.getPlayers().size();
        while (size > 0)
        {
            Bukkit.getPlayer(game.getPlayers().get(size - 1)).setLevel(timer);
            size--;
        }

        if (timer <= 0)
        {
            stop();
            game.startFreeRoam();
        }
        else if (timer % 5 == 0 || timer < 5)
        {
            game.msgAll("The game will start in " + timer + " seconds..");
        }
    }

    public void stop()
    {
        Bukkit.getScheduler().cancelTask(id);
    }
}
