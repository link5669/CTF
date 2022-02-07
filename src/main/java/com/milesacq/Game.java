package com.milesacq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.List;

public class Game {
    private Block redStart;
    private Block blueStart;
    private Block redGoal;
    private Block blueGoal;
    private BossBar redBar;
    private BossBar blueBar;

    public Game(Location redStart, Location blueStart, Location redGoal, Location blueGoal) {
        this.redStart = redStart.getBlock();;
        this.blueStart = blueStart.getBlock();
        this.redGoal = redGoal.getBlock();
        this.blueGoal = blueGoal.getBlock();
        this.redBar = Bukkit.createBossBar(
                ChatColor.DARK_RED + "Red Score" ,
                BarColor.RED,
                BarStyle.SEGMENTED_6);
        this.blueBar = Bukkit.createBossBar(
                ChatColor.DARK_BLUE + "Blue Score" ,
                BarColor.BLUE,
                BarStyle.SEGMENTED_6);
        for (Player p:Bukkit.getOnlinePlayers()){
            redBar.addPlayer(p);
            blueBar.addPlayer(p);
        }
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("Map: ", "testMap",
                ChatColor.translateAlternateColorCodes('&', "&a&l<< &2& CAPTURE THE FLAG &a&l>>"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = obj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        score.setScore(3);

        Score score2 = obj.getScore(ChatColor.AQUA + "Online Players: " + ChatColor.DARK_AQUA + Bukkit.getOnlinePlayers().size());
        score2.setScore(2);

        Score score4 = obj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        score4.setScore(0);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setScoreboard(board);
        }
    }

    public void removeBars() {
        redBar.removeAll();
        blueBar.removeAll();
    }

    public boolean checkRedFlag(Block testBlock) {
        return this.redStart.equals(testBlock);
    }

    public boolean checkBlueFlag(Block testBlock) {
        return this.blueStart.equals(testBlock);
    }

    public boolean checkRedGoal(Block testBlock) {
        return this.redGoal.equals(testBlock);
    }

    public boolean checkBlueGoal(Block testBlock) {
        return this.blueGoal.equals(testBlock);
    }

//    @EventHandler
//    public void onPlace(BlockPlaceEvent event) {
//        if (event.getBlock().getLocation().equals(this.redStart)) {
//            increaseRedScore();
//        } else if (event.getBlock().getLocation().equals(this.blueStart)) {
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
