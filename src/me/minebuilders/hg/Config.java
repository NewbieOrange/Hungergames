package me.minebuilders.hg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class Config
{

    // Basic settings
    public static boolean spawnmobs;
    public static int spawnmobsinterval;
    public static int freeroam;
    public static int trackingstickuses;
    public static int playersfortrackingstick;
    public static int maxchestcontent;
    public static boolean randomItemDurability;
    public static boolean teleAtEnd;
    public static int maxTeam;

    // Reward info
    public static boolean giveReward;
    public static int cash;

    // Rollback config info
    public static boolean breakblocks;
    public static List<Integer> blocks;

    // Random chest
    public static boolean randomChest;
    public static int randomChestInterval;
    public static int randomChestMaxContent;

    // Misc items
    public static ItemStack firework;

    // Events Setting
    public static boolean checkPlayerMoveEvent;
    public static boolean preventAttackingBeforeStarted;

    // Languages Start
    public static String l_game_not_ready;
    public static String l_joined_the_game;
    public static String l_left_the_game;
    public static String l_players_to_start;
    public static String l_game_is_full;
    public static String l_welcome_to_hg;
    public static String l_pick_kits_tip;
    public static String l_kits;
    public static String l_last_line;

    public static String l_you_won;
    public static String l_for_win_hg;
    public static String l_won_at_arena;
    public static String l_winner_list_and;

    public static String l_Chest_drop_at;

    public static String l_Sign_click_to_join;
    public static String l_Sign_game_status;
    public static String l_Sign_alive;

    public static String l_Status_running;
    public static String l_Status_stopped;
    public static String l_Status_waiting;
    public static String l_Status_broken;
    public static String l_Status_rollback;
    public static String l_Status_not_ready;
    public static String l_Status_beginning;
    public static String l_Status_count_down;

    public static String l_SBDisplay_title;
    public static String l_SBDisplay_players_alive;

    public static String l_dont_have_permission;
    // Languages End

    private static Configuration config;

    public Config(HG plugin)
    {
        if (!new File(plugin.getDataFolder(), "config.yml").exists())
        {
            Util.log("Config not found. Generating default config!");
            plugin.saveDefaultConfig();
        }

        config = plugin.getConfig().getRoot();
        config.options().copyDefaults(true);
        plugin.reloadConfig();
        config = plugin.getConfig();

        spawnmobs = config.getBoolean("settings.spawn-mobs");
        spawnmobsinterval = config.getInt("settings.spawn-mobs-interval") * 20;
        freeroam = config.getInt("settings.free-roam");
        trackingstickuses = config.getInt("settings.trackingstick-uses");
        playersfortrackingstick = config.getInt("settings.players-for-trackingstick");
        maxchestcontent = config.getInt("settings.max-chestcontent");
        randomItemDurability = config.getBoolean("settings.random-durability");
        teleAtEnd = config.getBoolean("settings.teleport-at-end");
        maxTeam = config.getInt("settings.max-team-size");
        giveReward = config.getBoolean("reward.enabled");
        cash = config.getInt("reward.cash");
        maxTeam = config.getInt("settings.max-team-size");
        giveReward = config.getBoolean("reward.enabled");
        cash = config.getInt("reward.cash");
        breakblocks = config.getBoolean("rollback.allow-block-break");
        blocks = config.getIntegerList("rollback.editable-blocks");
        randomChest = config.getBoolean("random-chest.enabled");
        randomChestInterval = config.getInt("random-chest.interval") * 20;
        randomChestMaxContent = config.getInt("random-chest.max-chestcontent");

        checkPlayerMoveEvent = config.getBoolean("events.check-player-move-event");
        preventAttackingBeforeStarted = config
                .getBoolean("events.prevent-attacking-before-started");

        l_game_not_ready = config.getString("languages.game_not_ready");
        l_joined_the_game = config.getString("languages.joined_the_game");
        l_left_the_game = config.getString("languages.left_the_game");
        l_players_to_start = config.getString("languages.players_to_start");
        l_game_is_full = config.getString("languages.game_is_full");
        l_welcome_to_hg = config.getString("languages.welcome_to_hg");
        l_pick_kits_tip = config.getString("languages.pick_kits_tip");
        l_kits = config.getString("languages.kits");
        l_last_line = config.getString("languages.last_line");

        l_you_won = config.getString("languages.you_won");
        l_for_win_hg = config.getString("languages.for_win_hg");
        l_won_at_arena = config.getString("languages.won_at_arena");
        l_winner_list_and = config.getString("languages.winner_list_and");

        l_Chest_drop_at = config.getString("languages.Chest_drop_at");

        l_Sign_click_to_join = config.getString("languages.Sign_click_to_join");
        l_Sign_game_status = config.getString("languages.Sign_game_status");
        l_Sign_alive = config.getString("languages.Sign_alive");

        l_Status_running = config.getString("languages.Status_running");
        l_Status_stopped = config.getString("languages.Status_stopped");
        l_Status_waiting = config.getString("languages.Status_waiting");
        l_Status_broken = config.getString("languages.Status_broken");
        l_Status_rollback = config.getString("languages.Status_rollback");
        l_Status_not_ready = config.getString("languages.Status_not_ready");
        l_Status_beginning = config.getString("languages.Status_beginning");
        l_Status_count_down = config.getString("languages.Status_count_down");

        l_SBDisplay_title = config.getString("languages.SBDisplay_title");
        l_SBDisplay_players_alive = config.getString("languages.SBDisplay_players_alive");

        l_dont_have_permission = config.getString("languages.dont_have_permission");

        if (giveReward)
        {
            try
            {
                Vault.setupEconomy();
            }
            catch (NoClassDefFoundError e)
            {
                Util.log("Unable to setup vault! Rewards will not be given out..");
                giveReward = false;
            }
        }
        // Firework setup info
        ItemStack i = new ItemStack(Material.FIREWORK, 64);
        FireworkMeta fm = (FireworkMeta) i.getItemMeta();
        List<Color> c = new ArrayList<Color>();
        c.add(Color.ORANGE);
        c.add(Color.RED);
        FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c)
                .withFade(c).with(Type.BALL_LARGE).trail(true).build();
        fm.addEffect(e);
        fm.setPower(3);
        i.setItemMeta(fm);

        firework = i;
    }
}
