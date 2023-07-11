package com.milesacq;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GameSingleton {
    private static World world;
    private static ArrayList<Team> teams = new ArrayList<Team>();
    private static int setupStep = -1;
    private static String configString;
    private static double[] centerCoords = new double[3];

    public static void removeBossBars() {
        for (Team team : teams) {
            team.removeBar();
        }
    }

    public static void setCenterCoords(int i, double coords) {
        centerCoords[i] = coords;
    }

    public static void setCenterCoords(double[] coords) {
        centerCoords = coords;
    }

    public static double getCenterCoords(int i) {
        return centerCoords[i];
    }
    public static void clearTeams() {
        teams = new ArrayList<Team>();
    }

    public static void addTeam(Team team) {
        teams.add(team);
    }

    public static Team getTeam(String name) {
        for (Team team : teams) {
            if (team.getName().equals(name)) {
                return team;
            }
        }
        return null;
    }

    public static Team findPlayerTeam(String player) {
        for (Team team : teams) {
            if (team.search(player)) {
                return team;
            }
        }
        return null;
    }

    public static boolean isOnTeam(Player player) {
        for (Team team : teams) {
            if (team.search(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Team> getTeams() {
        return teams;
    }

    public static void showBossBars(Player player) {
        for (Team team : teams) {
            team.showBossBar(player);
        }
    }

    public static void setConfigString(String in) {
        configString = in;
    }

    public static String getConfigString() {
        return configString;
    }

    public static void setSetupStep(int i) {
        setupStep = i;
    }

    public static int getSetupStep() {
        return setupStep;
    }

    public static void giveWool(Player player) {
        ItemStack blueWool = new ItemStack(Material.BLUE_WOOL, 1);
        ItemMeta blueMeta = blueWool.getItemMeta();
        blueMeta.setDisplayName("Blue Flag");
        blueWool.setItemMeta(blueMeta);
        blueMeta.addEnchant(Enchantment.LUCK, 10, true);
        blueWool.setItemMeta(blueMeta);
        player.getInventory().setItem(1, blueWool);
        ItemStack redWool = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta redMeta = blueWool.getItemMeta();
        redMeta.setDisplayName("Red Flag");
        redWool.setItemMeta(redMeta);
        redMeta.addEnchant(Enchantment.LUCK, 10, true);
        redWool.setItemMeta(redMeta);
        player.getInventory().setItem(2, redWool);
    }

    public static void setWorld(World in) {
        world = in;
    }

    public static World getWorld() {
        return world;
    }
}
