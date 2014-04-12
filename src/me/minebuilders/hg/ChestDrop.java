package me.minebuilders.hg;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestDrop implements Listener
{

    private FallingBlock fb;
    private BlockState beforeBlock;
    private Player invopener;
    private Chunk c;

    public ChestDrop(FallingBlock fb)
    {
        this.fb = fb;
        this.c = fb.getLocation().getChunk();
        c.load();
        Bukkit.getPluginManager().registerEvents(this, HG.plugin);
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent event)
    {
        if (event.getChunk().equals(c))
        {
            event.setCancelled(true);
        }
    }

    public void remove()
    {
        if (fb != null && !fb.isDead())
            fb.remove();
        if (beforeBlock != null)
        {
            beforeBlock.update(true);
            Block b = beforeBlock.getBlock();
            if (b.getType() == Material.PISTON_BASE)
            {
                b.setType(Material.AIR);
            }
            else if (b.getType() != Material.AIR)
            {
                Util.warning("Warning! Block at " + beforeBlock.getWorld()
                        + ": " + beforeBlock.getX() + ", " + beforeBlock.getY() + ", "
                        + beforeBlock.getZ() + " is " + b.getType()
                        + ", instead of PISTOL_BASE, ignoring it");
                Util.warning("This message can be ingored safely");
            }
        }

        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onEntityModifyBlock(EntityChangeBlockEvent event)
    {
        Entity en = event.getEntity();

        if (!(en instanceof FallingBlock))
            return;

        FallingBlock fb2 = (FallingBlock) en;

        if (fb2.equals(fb))
        {
            beforeBlock = event.getBlock().getState();

            Location l = beforeBlock.getLocation();
            Util.shootFirework(new Location(l.getWorld(), l.getX() + 0.5, l.getY(), l
                    .getZ() + 0.5));
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        for (HumanEntity p : event.getViewers())
        {
            if (((Player) p).equals(invopener))
            {
                Location l = beforeBlock.getLocation();
                l.getWorld().createExplosion(l.getBlockX(), l.getBlockY(), l.getBlockZ(),
                        1, false, false);
                remove();
                return;
            }
        }
    }

    @EventHandler
    public void onBla(PlayerInteractEvent event)
    {

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && beforeBlock != null
                && event.getClickedBlock().getLocation()
                        .equals(beforeBlock.getLocation()))
        {
            Player p = event.getPlayer();
            Random rg = new Random();
            invopener = p;

            int c = rg.nextInt(Config.randomChestMaxContent) + 1;
            Inventory i;
            if (c <= 9)
            {
                i = Bukkit.getServer().createInventory(p, 9);
            }
            else
            {
                i = Bukkit.getServer().createInventory(p, ((int) (c / 9)) * 9);
            }
            i.clear();
            while (c != 0)
            {
                ItemStack it = HG.manager.randomitem();
                if (it != null)
                {
                    if (Config.randomItemDurability)
                    {
                        int dura = (int) it.getType().getMaxDurability();
                        if (dura > 0)
                        {
                            it.setDurability((short) (rg.nextInt(dura) + 1));
                        }
                    }

                    i.addItem(it);
                }
                c--;
            }
            p.openInventory(i);
        }
    }
}
