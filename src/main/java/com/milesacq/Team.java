package com.milesacq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.milesacq.enums.CoordinateType;
import com.milesacq.enums.TeamType;

public class Team {
    private String name;
    private ChatColor chatColor;
    private Player[] members;
    private BossBar bossBar;
    private int deaths;
    private ItemStack[] inv = new ItemStack[36]; 
    private double[] startCoords = new double[3]; 
    private double[] goalCoords = new double[3]; 
    private double[] respawnCoords = new double[3];
    private Block startBlock;
    private Block goalBlock;
    private Team opponentTeam;
    private Material woolMaterial;

    public Team(int numMembers, TeamType color, String name, ChatColor chatColor, BarColor barColor, Material woolMaterial) {
        this.members = new Player[numMembers];
        this.name = name;
        this.woolMaterial = woolMaterial;
        this.chatColor = chatColor;
        this.bossBar = Bukkit.createBossBar(
            chatColor + name + " Score" ,
            barColor,
            BarStyle.SOLID);
        this.bossBar.setProgress(0);
        for (Player p:Bukkit.getOnlinePlayers()){
            bossBar.addPlayer(p);
        }
    }

    public Block getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(Block block) {
        startBlock = block;
    }

    public void setGoalBlock(Block block) {
        goalBlock = block;
    }

    public Block getGoalBlock() {
        return goalBlock;
    }

    public void sendMessage(String message) {
        for (Player player : members) {
            player.sendMessage(message);
        }
    }

    public Team getOpponentTeam() {
        return opponentTeam;
    }

    public void setOpponentTeam(Team team) {
        opponentTeam = team;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public void setBlocks() {
        Location goalLocation = new Location(GameSingleton.getWorld(), goalCoords[0], goalCoords[1], goalCoords[2]);
        Location startLocation = new Location(GameSingleton.getWorld(), startCoords[0], startCoords[1], startCoords[2]);
        Location playerStartLocation = new Location(GameSingleton.getWorld(), respawnCoords[0], respawnCoords[1], respawnCoords[2]);
        goalLocation.getBlock().setType(Material.AIR);
        startLocation.getBlock().setType(woolMaterial);
        playerStartLocation.getBlock().setType(Material.AIR);
    }

    public boolean checkStartEmpty() {
        return startBlock.getType().equals(Material.AIR);
    }

    public boolean checkFlag(Block testBlock) {
        return blockEquals(startBlock, testBlock);
    }

    public boolean checkGoal(Block testBlock) {
        return blockEquals(goalBlock, testBlock);
    }

    public void setStartBlock() {
        startBlock.setType(this.woolMaterial);
    }

    public Material getWoolMaterial() {
        return this.woolMaterial;
    }

    public boolean addPoint(Team team) {
        return team.addPoint();
    }

    public void setCoords(CoordinateType coordType, int index, Double value) {
        switch (coordType) {
            case STARTCOORDS: startCoords[index] = value; break;
            case GOALCOORDS: goalCoords[index] = value; break;
            case RESPAWNCOORDS: respawnCoords[index] = value; break;
        }
    }

    public void setCoords(CoordinateType coordType, double[] values) {
        switch (coordType) {
            case STARTCOORDS: startCoords = values; break;
            case GOALCOORDS: goalCoords = values; break;
            case RESPAWNCOORDS: respawnCoords = values; break;
        }
    }

    public double getCoords(CoordinateType coordType, int index) {
        switch (coordType) {
            case STARTCOORDS: return startCoords[index];
            case GOALCOORDS: return goalCoords[index];
            case RESPAWNCOORDS: return respawnCoords[index];
        }
        return -1000;
    }

    public ItemStack[] getInventory() {
        return inv;
    }

    public void clearInventory() {
        inv = new ItemStack[36];
    }

    public ItemStack getInventoryItem(int index) {
        return inv[index];
    }

    public void setInventory(ItemStack item, int index) {
        inv[index] = item;
    }

    public void removeBar() {
        this.bossBar.removeAll();
    }

    public void showBossBar(Player player) {
        this.bossBar.addPlayer(player);
    }

    public boolean addPoint() {
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

    public boolean search(String player) {
        for (Player member : this.members) {
            if (member != null) {
                if (member.getName().equals(player)) {
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

    public void warpToSpawn() {
        for (Player player : members) {
            if (player != null) {
                player.teleport(new Location(GameSingleton.getWorld(), getCoords(CoordinateType.RESPAWNCOORDS, 0), getCoords(CoordinateType.RESPAWNCOORDS, 1), getCoords(CoordinateType.RESPAWNCOORDS, 2)));
            }
        }
    }

    public void setFullHealthAndHunger() {
        for (Player player : members) {
            if (player != null) {
                player.setHealth(20);
                player.setFoodLevel(20);
            }
        }
    }

    public void setSurvival() {
        for (Player player : members) {
            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
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

    private static boolean blockEquals(Block one, Block two) {
        if (one.getLocation().getX() == two.getLocation().getX()) {
            if (one.getLocation().getY() == two.getLocation().getY()) {
                if (one.getLocation().getZ() == two.getLocation().getZ()) {
                    return true;
                }
            }
        }
        return false;
    }
}
