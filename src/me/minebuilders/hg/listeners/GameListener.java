package me.minebuilders.hg.listeners;

import me.minebuilders.hg.Config;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.PlayerData;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class GameListener implements Listener
{

    private HG plugin;
    private String tsn = ChatColor.GOLD + "TrackingStick " + ChatColor.GREEN + "Uses: ";
    private ItemStack trackingStick;

    public GameListener(HG plugin)
    {
        this.plugin = plugin;
        ItemStack it = new ItemStack(Material.STICK, 1);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(tsn + Config.trackingstickuses);
        it.setItemMeta(im);
        trackingStick = it;
    }

    public void dropInv(Player p)
    {
        PlayerInventory inv = p.getInventory();
        Location l = p.getLocation();
        for (ItemStack i : inv.getContents())
        {
            if (i != null && i.getType() != Material.AIR)
                l.getWorld().dropItemNaturally(l, i);
        }
        for (ItemStack i : inv.getArmorContents())
        {
            if (i != null && i.getType() != Material.AIR)
                l.getWorld().dropItemNaturally(l, i);
        }
    }

    public void checkStick(Game g)
    {
        if (Config.playersfortrackingstick == g.getPlayers().size())
        {
            for (String r : g.getPlayers())
            {
                Player p = Bukkit.getPlayer(r);
                if (p != null)
                {
                    Util.scm(p, "&a&l[]------------------------------------------[]");
                    Util.scm(p,
                            "&a&l |&3&l   You have been given a player-tracking stick! &a&l |");
                    Util.scm(p,
                            "&a&l |&3&l   Swing the stick to track players!                &a&l |");
                    Util.scm(p, "&a&l[]------------------------------------------[]");
                    p.getInventory().addItem(trackingStick);
                }
            }
        }
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event)
    {
        final Player p = event.getEntity();

        PlayerData pd = plugin.players.get(p.getName());

        if (pd != null)
        {
            final Game g = pd.getGame();

            p.setHealth(20);

            LivingEntity killer = p.getKiller();

            if (killer != null)
            {
                g.msgDef("&l&d" + HG.killmanager.getKillString(p.getName(), killer));
            }
            else
            {
                g.msgDef("&d"
                        + HG.killmanager.getDeathString(
                                p.getLastDamageCause().getCause(), p.getName()));
            }
            event.setDeathMessage(null);

            event.getDrops().clear();

            dropInv(p);
            
            g.exit(p);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
            {

                @Override
                public void run()
                {
                    g.leave(p);
                    checkStick(g);
                }
            }, 10L);
        }
    }

    @EventHandler
    public void onSprint(FoodLevelChangeEvent event)
    {
        Player p = (Player) event.getEntity();
        if (plugin.players.containsKey(p.getName()))
        {
            Status st = plugin.players.get(p.getName()).getGame().getStatus();
            if (st == Status.WAITING || st == Status.COUNTDOWN)
            {
                event.setFoodLevel(1);
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void useTrackStick(Player p)
    {
        ItemStack i = p.getItemInHand();
        ItemMeta im = i.getItemMeta();
        if (im.getDisplayName() != null && im.getDisplayName().startsWith(tsn))
        {
            int uses = 0;
            uses = Integer.parseInt(im.getDisplayName().replace(tsn, ""));
            if (uses == 0)
            {
                p.sendMessage(ChatColor.RED + "This trackingstick is out of uses!");
            }
            else
            {
                boolean foundno = true;
                for (Entity e : p.getNearbyEntities(120, 50, 120))
                {
                    if (e instanceof Player)
                    {
                        im.setDisplayName(tsn + (uses - 1));
                        foundno = false;
                        Location l = e.getLocation();
                        int range = (int) p.getLocation().distance(l);
                        Util.msg(
                                p,
                                ("&3" + ((Player) e).getName())
                                        + "&b is "
                                        + range
                                        + " blocks away from you:&3 "
                                        + getDirection(p.getLocation().getBlock(),
                                                l.getBlock()));
                        i.setItemMeta(im);
                        p.updateInventory();
                        return;
                    }
                }
                if (foundno)
                    Util.msg(p, ChatColor.RED + "Couldn't locate any nearby players!");

            }
        }
    }

    public String getDirection(Block block, Block block1)
    {
        Vector bv = block.getLocation().toVector();
        Vector bv2 = block1.getLocation().toVector();
        float y = (float) angle(bv.getX(), bv.getZ(), bv2.getX(), bv2.getZ());
        float cal = (y * 10);
        int c = (int) cal;
        if (c <= 1 && c >= -1)
        {
            return "South";
        }
        else if (c > -14 && c < -1)
        {
            return "SouthWest";
        }
        else if (c >= -17 && c <= -14)
        {
            return "West";
        }
        else if (c > -29 && c < -17)
        {
            return "NorthWest";
        }
        else if (c > 17 && c < 29)
        {
            return "NorthEast";
        }
        else if (c <= 17 && c >= 14)
        {
            return "East";
        }
        else if (c > 1 && c < 14)
        {
            return "SouthEast";
        }
        else if (c <= 29 && c >= -29)
        {
            return "North";
        }
        else
        {
            return "UnKnown";
        }
    }

    public double angle(double d, double e, double f, double g)
    {
        // Vector differences
        int x = (int) (f - d);
        int z = (int) (g - e);

        double yaw = Math.atan2(x, z);
        return yaw;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onAttack(EntityDamageByEntityEvent event)
    {
        Entity defender = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Arrow)
            damager = (Entity) ((Arrow) damager).getShooter();
        if (defender instanceof Player)
        {
            Player p = (Player) defender;
            PlayerData pd = plugin.players.get(p.getName());

            if (pd != null)
            {
                Game g = pd.getGame();

                if (g.getStatus() != Status.RUNNING)
                {
                    event.setCancelled(true);
                }
                else if (pd.isOnTeam(p.getName()) && damager instanceof Player
                        && pd.getTeam().isOnTeam(((Player) damager).getName()))
                {
                    Util.scm(((Player) damager), "&c" + p.getName() + " is on your team!");
                    event.setCancelled(true);
                }
                else if (event.isCancelled())
                    event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onItemUseAttempt(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        if (event.getAction() != Action.PHYSICAL
                && plugin.players.containsKey(p.getName()))
        {
            Status st = plugin.players.get(p.getName()).getGame().getStatus();
            if (st == Status.WAITING || st == Status.COUNTDOWN)
            {
                event.setCancelled(true);
                p.sendMessage(ChatColor.RED
                        + "You cannot interact until the game has started!");
            }
        }
    }

    @EventHandler
    public void onPlayerClickLobby(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        {
            Block b = event.getClickedBlock();
            if (b.getType().equals(Material.WALL_SIGN))
            {
                Sign sign = (Sign) b.getState();
                if (sign.getLine(0).equals(
                        ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames"))
                {
                    Game game = HG.manager.getGame(sign.getLine(1).substring(2));
                    if (game == null)
                    {
                        Util.msg(p, ChatColor.RED + "This arena does not exist!");
                        return;
                    }
                    else
                    {
                        if (p.getItemInHand().getType() == Material.AIR)
                        {
                            game.join(p);
                        }
                        else
                        {
                            Util.msg(p, ChatColor.RED + "Click the sign with your hand!");
                        }
                    }
                }
            }
        }
        else if (event.getAction().equals(Action.LEFT_CLICK_AIR))
        {
            if (p.getItemInHand().getType().equals(Material.STICK)
                    && plugin.players.containsKey(p.getName()))
            {
                useTrackStick(p);
            }
        }
    }

    @EventHandler
    public void onPlayerClickLeave(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        {
            Block b = event.getClickedBlock();
            if (b.getType().equals(Material.WALL_SIGN)
                    || b.getType().equals(Material.SIGN_POST))
            {
                Sign sign = (Sign) b.getState();
                if (sign.getLine(0).equals(
                        ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames")
                        && sign.getLine(1).equals(ChatColor.DARK_RED + "Leave Game"))
                {
                    if (HG.plugin.players.containsKey(p.getName()))
                    {
                        if (p.getItemInHand().getType() == Material.AIR)
                        {
                            HG.plugin.players.get(p.getName()).getGame().leave(p);
                            Util.msg(p, "&cYou left Hungergames!");
                        }
                        else
                        {
                            Util.msg(p, ChatColor.RED + "Click the sign with your hand!");
                        }

                    }
                    else
                    {
                        Util.msg(p, ChatColor.RED + "You're not in a vaild game!");
                        return;
                    }
                }
            }
        }
        else if (event.getAction().equals(Action.LEFT_CLICK_AIR))
        {
            if (p.getItemInHand().getType().equals(Material.STICK)
                    && plugin.players.containsKey(p.getName()))
            {
                useTrackStick(p);
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event)
    {
        Player p = event.getPlayer();
        Block b = event.getBlock();

        if (HG.manager.isInRegion(b.getLocation()))
        {

            if (Config.breakblocks && plugin.players.containsKey(p.getName()))
            {

                Game g = plugin.players.get(p.getName()).getGame();

                if (g.getStatus() == Status.RUNNING || g.getStatus() == Status.BEGINNING)
                {
                    if (!Config.blocks.contains(b.getType().toString()))
                    {
                        p.sendMessage(ChatColor.RED + "You cannot edit this block type!");
                        event.setCancelled(true);
                        return;
                    }
                    else
                    {
                        g.recordBlockPlace(event.getBlockReplacedState());
                        return;
                    }
                }
                else
                {
                    p.sendMessage(ChatColor.RED + "The game is not running!");
                    event.setCancelled(true);
                    return;
                }
            }
            else if (p.hasPermission("hg.create")
                    && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING)
            {
                if (b.getType() == Material.CHEST)
                {
                    HG.manager.getGame(b.getLocation()).addChests(b.getLocation());
                }
            }
            else
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event)
    {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if (HG.manager.isInRegion(b.getLocation()))
        {
            if (Config.breakblocks && plugin.players.containsKey(p.getName()))
            {
                Game g = plugin.players.get(p.getName()).getGame();
                if (g.getStatus() == Status.RUNNING)
                {
                    if (!Config.blocks.contains(b.getType().toString()))
                    {
                        p.sendMessage(ChatColor.RED + "You cannot edit this block type!");
                        event.setCancelled(true);
                        return;
                    }
                    else
                    {
                        g.recordBlockBreak(b);
                        return;
                    }
                }
                else
                {
                    p.sendMessage(ChatColor.RED + "The game is not running!");
                    event.setCancelled(true);
                    return;
                }
            }
            else if (p.hasPermission("hg.create")
                    && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING)
            {
                return;
            }
            else
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        Player p = event.getPlayer();
        if (plugin.players.containsKey(p.getName())
                && plugin.players.get(p.getName()).getGame().getStatus() == Status.WAITING)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onlogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (plugin.players.containsKey(player.getName()))
        {
            plugin.players.get(player.getName()).getGame().leave(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (Config.checkPlayerMoveEvent)
        {
            PlayerData pd = plugin.players.get(event.getPlayer().getName());
            if (pd != null)
            {
                Game game = plugin.players.get(event.getPlayer().getName()).getGame();
                if (game.getStatus() == Status.COUNTDOWN
                        || game.getStatus() == Status.WAITING)
                {
                    Location from = event.getFrom();
                    Location to = event.getTo();

                    if (Math.abs(from.getX() - to.getX()) > 0.001
                            || Math.abs(from.getZ() - to.getZ()) > 0.001)
                    {
                        event.setTo(new Location(from.getWorld(), from.getX(), to.getY(),
                                from.getZ(), to.getYaw(), to.getPitch()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event)
    {
        if (Config.preventAttackingBeforeStarted)
        {
            Entity damager = event.getDamager();
            if (damager instanceof Player)
            {
                PlayerData pd = plugin.players.get(((Player) damager).getName());
                if (pd != null)
                {
                    Status status = plugin.players.get(((Player) damager).getName())
                            .getGame().getStatus();
                    if (status == Status.COUNTDOWN || status == Status.WAITING)
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
