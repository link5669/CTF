package com.milesacq;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Game {
    private Block redStart;
    private Block blueStart;
    private Block redGoal;
    private Block blueGoal;

    public Location getRedGoalLocation() {
        return this.redGoal.getLocation();
    }

    public Location getBlueGoalLocation() {
        return this.blueGoal.getLocation();
    }

    public Location getBlueStartLocation() {
        return this.blueStart.getLocation();
    }

    public Location getRedStartLocation() {
        return this.redStart.getLocation();
    }

    public void setCoords(Location redStart, Location blueStart, Location redGoal, Location blueGoal) {
        this.redStart = redStart.getBlock();;
        this.blueStart = blueStart.getBlock();
        this.redGoal = redGoal.getBlock();
        this.blueGoal = blueGoal.getBlock();
    }

    public boolean checkRedFlag(Block testBlock) {
        return blockEquals(this.redStart, testBlock);
    }

    public boolean checkRedStartEmpty() {
        return this.redStart.getType().equals(Material.AIR);
    }

    public boolean checkBlueStartEmpty() {
        return this.blueStart.getType().equals(Material.AIR);
    }

    public boolean checkBlueFlag(Block testBlock) {
        return blockEquals(this.blueStart, testBlock);
    }

    public boolean checkRedGoal(Block testBlock) {
        return blockEquals(this.redGoal, testBlock);
    }

    public boolean checkBlueGoal(Block testBlock) {
        return blockEquals(this.blueGoal, testBlock);
    }

    public boolean addPoint(Team team) {
        return team.addPoint();
    }

    public void giveWool(Player player) {
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

    private boolean blockEquals(Block one, Block two) {
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
