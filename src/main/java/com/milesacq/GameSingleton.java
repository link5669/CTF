package com.milesacq;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class GameSingleton {
    private static World world;

    private static Block redStart;
    private static Block blueStart;
    private static Block redGoal;
    private static Block blueGoal;

    private static Team redTeam;
    private static Team blueTeam;
    private static ItemStack[] blueInv;
    private static ItemStack[] redInv;
    private static final double[] BLUESTART = new double[3]; // 0
    private static final double[] REDSTART = new double[3]; // 1
    private static final double[] BLUEGOAL = new double[3]; // 2
    private static final double[] REDGOAL = new double[3]; // 3
    private static final double[] REDRESPAWN = new double[3]; // 4
    private static final double[] BLUERESPAWN = new double[3]; // 5

    private static int setupStep = 6;

    private static String configString;

    public static void removeBossBars() {
        blueTeam.removeBar();
        redTeam.removeBar();
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

    public static void clearInv(TeamType team) {
        if (team == TeamType.RED) {
            redInv = new ItemStack[36];
        } else {
            blueInv = new ItemStack[36];
        }
    }

    public static void setInv(TeamType team, ItemStack item, int index) {
        if (team == TeamType.BLUE) {
            blueInv[index] = item;
        } else {
            redInv[index] = item;
        }
    }

    public static ItemStack getInvItem(TeamType team, int index) {
        return (team == TeamType.RED) ? redInv[index] : blueInv[index];
    }

    public static void setCoords(CoordinateType coordType, int index, Double value) {
        switch (coordType) {
            case BLUESTARTCOORDS: BLUESTART[index] = value;
            case REDSTARTCOORDS: REDSTART[index] = value;
            case BLUEGOALCOORDS: BLUEGOAL[index] = value;
            case REDGOALCOORDS: REDGOAL[index] = value;
            case REDRESPAWNLOCATION: REDRESPAWN[index] = value;
            case BLUERESPAWNLOCATION: BLUERESPAWN[index] = value;
        }
    }

    public static Double getCoords(CoordinateType coordType, int index) {
        switch (coordType) {
            case BLUESTARTCOORDS: return BLUESTART[index];
            case REDSTARTCOORDS: return REDSTART[index];
            case BLUEGOALCOORDS: return BLUEGOAL[index];
            case REDGOALCOORDS: return REDGOAL[index];
            case REDRESPAWNLOCATION: return REDRESPAWN[index];
            case BLUERESPAWNLOCATION: return BLUERESPAWN[index];
        }
        return null;
    }

    public static void setRedTeam(Team in) {
        redTeam = in;
    }

    public static void setBlueTeam(Team in) {
        blueTeam = in;
    }

    public static Team getRedTeam() {
        return redTeam;
    }

    public static Team getBlueTeam() {
        return blueTeam;
    }

    public static Location getRedGoalLocation() {
        return redGoal.getLocation();
    }

    public static Location getBlueGoalLocation() {
        return blueGoal.getLocation();
    }

    public static Location getBlueStartLocation() {
        return blueStart.getLocation();
    }

    public static Location getRedStartLocation() {
        return redStart.getLocation();
    }

    public static void setCoords(Location redStartIn, Location blueStartIn, Location redGoalIn, Location blueGoalIn) {
        redStart = redStartIn.getBlock();
        blueStart = blueStartIn.getBlock();
        redGoal = redGoalIn.getBlock();
        blueGoal = blueGoalIn.getBlock();
    }

    public static boolean checkRedFlag(Block testBlock) {
        return blockEquals(redStart, testBlock);
    }

    public static boolean checkRedStartEmpty() {
        return redStart.getType().equals(Material.AIR);
    }

    public static boolean checkBlueStartEmpty() {
        return blueStart.getType().equals(Material.AIR);
    }

    public static boolean checkBlueFlag(Block testBlock) {
        return blockEquals(blueStart, testBlock);
    }

    public static boolean checkRedGoal(Block testBlock) {
        return blockEquals(redGoal, testBlock);
    }

    public static boolean checkBlueGoal(Block testBlock) {
        return blockEquals(blueGoal, testBlock);
    }

    public static boolean addPoint(Team team) {
        return team.addPoint();
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

    public static void setWorld(World in) {
        world = in;
    }

    public static void setBlocks() {
        Location blueGoal = new Location(world, GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 0), GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 2));
        Location redGoal = new Location(world, GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 0), GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 2));
        Location blueStart = new Location(world, GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 0), GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS,2));
        Location redStart = new Location(world, GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 0), GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 2));
        Location bluePlayerStart = new Location(world, GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 0),
                GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 1), GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 2));
        Location redPlayerStart = new Location(world, GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 0),
                GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 1), GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 2));
        blueGoal.getBlock().setType(Material.AIR);
        redGoal.getBlock().setType(Material.AIR);
        blueStart.getBlock().setType(Material.BLUE_WOOL);
        redStart.getBlock().setType(Material.RED_WOOL);
        bluePlayerStart.getBlock().setType(Material.AIR);
        redPlayerStart.getBlock().setType(Material.AIR);
    }
}
