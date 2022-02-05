package com.milesacq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Game {
    private Location redGoal;
    private Location blueGoal;

    public Game(Location redGoal, Location blueGoal) {
//        getLogger().info("alskdj");
        this.redGoal = redGoal;
        this.blueGoal = blueGoal;
        BossBar redBar = Bukkit.createBossBar(
                ChatColor.DARK_RED + "Red Score: " ,
                BarColor.RED,
                BarStyle.SEGMENTED_6);
        BossBar blueBar = Bukkit.createBossBar(
                ChatColor.DARK_BLUE + "Red Score: " ,
                BarColor.BLUE,
                BarStyle.SEGMENTED_6);
        for (Player p:Bukkit.getOnlinePlayers()){
            redBar.addPlayer(p);
//            getLogger().info("added " + p.getDisplayName() );
        }
    }
//    @EventHandler
//    public void onPlace(BlockPlaceEvent event) {
//        if (event.getBlock().getLocation().equals(this.redGoal)) {
//            increaseRedScore();
//        } else if (event.getBlock().getLocation().equals(this.blueGoal)) {
//            increaseBlueScore();
//        }
//    }
//
//    private void increaseBlueScore() {
//        getLogger().info("Blue scored");
//    }
//
//    private void increaseRedScore() {
//        getLogger().info("Red scored");
//    }
}
