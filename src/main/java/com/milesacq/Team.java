package com.milesacq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Team {
    private Player[] members;
    private int points;
    private BossBar bossBar;
    private int deaths;

    public Team(int numMembers, boolean color) {
        this.members = new Player[numMembers];
        this.points = 0;
        if (color) {
            this.bossBar = Bukkit.createBossBar(
                    ChatColor.DARK_RED + "Red Score",
                    BarColor.RED,
                    BarStyle.SOLID);
        } else {
            this.bossBar = Bukkit.createBossBar(
                    ChatColor.DARK_BLUE + "Blue Score" ,
                    BarColor.BLUE,
                    BarStyle.SOLID);
        }
        this.bossBar.setProgress(0);
        for (Player p:Bukkit.getOnlinePlayers()){
            bossBar.addPlayer(p);
        }
    }

    public void removeBar() {
        this.bossBar.removeAll();
    }

    public void showBar(Player player) {
        this.bossBar.addPlayer(player);
    }
    public boolean addPoint() {
        this.points++;
        if (this.bossBar.getProgress() > .6) {
            this.bossBar.setProgress(1);
            return true;
        } else if (this.bossBar.getProgress() > .3) {
            this.bossBar.setProgress(.6);
        } else if (this.bossBar.getProgress() == 0) {
            this.bossBar.setProgress(.3);
        }
        return false;
    }

    public int getLength() {
        return this.members.length;
    }

    public boolean search(Player player) {
        for (Player member : this.members) {
            if (member != null) {
                if (member.toString().equals(player.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean addPlayer(Player player) {
        if (this.members[0] == null) {
            this.members[0] = player;
            return true;
        } else {
            for (int i = 0; i < this.members.length; i++) {
                if (this.members[i] == null) {
                    this.members[i] = player;
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        String returnMe = "";
        for (int i = 0; i < this.members.length; i++) {
            if (this.members[i] != null) {
                returnMe += (this.members[i].getDisplayName() + ", ");
            }
        }
        return returnMe;
    }

    public void addDeath() {
        this.deaths++;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void zeroDeaths() {
        this.deaths = 0;
    }
}
