package com.milesacq;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CTF extends JavaPlugin implements Listener, CommandExecutor {
    public Game newGame;
    public Team redTeam;
    public Team blueTeam;
    private ItemStack[] blueInv;
    private ItemStack[] redInv;

    @Override
    public void onEnable() {
        getLogger().info("Setting up CTF!");
        getServer().getPluginManager().registerEvents(this, this);
        newGame = new Game();
    }

    @Override
    public void onDisable() {
        if (this.newGame != null) {
            getLogger().info("bye bye");
            redTeam.removeBar();
            blueTeam.removeBar();
        }
        for (Player player : Bukkit.getOnlinePlayers()){
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    private TextComponent getTextComponent(String advName, String[] progress, int num) {
        TextComponent message = new TextComponent();
        message.setText(progress[num]);
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(advName)));
        return message;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ctf")) {
            if (args[0].equalsIgnoreCase("start")) {
                Location blueFlagStart = new Location(this.getServer().getWorld("world"), -289.5, 84.5, -216.5);
                Location redFlagStart = new Location(this.getServer().getWorld("world"), -289.5, 84.5, -222.5);
                Location redFlagGoal = new Location(this.getServer().getWorld("world"), -291.5, 84.5, -216.5);
                Location blueFlagGoal = new Location(this.getServer().getWorld("world"), -289.5, 84.5, -220.5);
                newGame.setCoords(redFlagStart, blueFlagStart, redFlagGoal, blueFlagGoal);
                IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
                getLogger().info("ctf!");
            } else if (args[0].equalsIgnoreCase("blueteam")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (args[2] == null) {
                        sender.sendMessage("please specify number of players");
                    } else {
                        blueTeam = new Team(Integer.parseInt(args[2]), false);
                    }
                } else if (args[1].equalsIgnoreCase("add")) {
                    Player player = Bukkit.getPlayer(args[2]);
                    if (blueTeam.addPlayer(player)) {
                        sender.sendMessage("Successfuly added!");
                    } else {
                        sender.sendMessage("Couldn't add!");
                    }
                } else if (args[1].equalsIgnoreCase("list")) {
                    getLogger().info(blueTeam.toString());
                }
            } else if (args[0].equalsIgnoreCase("redteam")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (args[2] == null) {
                        sender.sendMessage("please specify number of players");
                    } else {
                        redTeam = new Team(Integer.parseInt(args[2]), true);
                    }
                } else if (args[1].equalsIgnoreCase("add")) {
                    Player player = Bukkit.getPlayer(args[2]);
                    if (redTeam.addPlayer(player)) {
                        sender.sendMessage("Successfuly added!");
                    } else {
                        sender.sendMessage("Couldn't add!");
                    }
                } else if (args[1].equalsIgnoreCase("list")) {
                    getLogger().info(redTeam.toString());
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                for (Player player2 : Bukkit.getOnlinePlayers()){
                    if (player2.getName().equalsIgnoreCase(sender.getName())) {
                        newGame.giveWool(player2);
                    }
                }
            } else if (args[0].equalsIgnoreCase("reddeaths")) {
                getLogger().info(Integer.toString(redTeam.getDeaths()));
            } else if (args[0].equalsIgnoreCase("bluedeaths")) {
                getLogger().info(Integer.toString(blueTeam.getDeaths()));
            }
        }
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (newGame.checkValidLocation(event.getBlock().getLocation())) {
            event.setCancelled(false);
        } else if (event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:blue_wool}") || event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:red_wool}")){
            event.setCancelled(true);
        }
        if (newGame.checkRedGoal(event.getBlock()) && event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:red_wool}")) {
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, redInv[i]);
            }
            newGame.getRedGoalLocation().getBlock().setType(Material.AIR);
            newGame.getRedStartLocation().getBlock().setType(Material.RED_WOOL);
            if (newGame.addPoint(redTeam)) {
                winGame(redTeam);
            }
        } else if (newGame.checkBlueGoal(event.getBlock()) && event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:blue_wool}")) {
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, blueInv[i]);
            }
            newGame.getRedGoalLocation().getBlock().setType(Material.AIR);
            newGame.getRedStartLocation().getBlock().setType(Material.BLUE_WOOL);
            if (newGame.addPoint(blueTeam)) {
                winGame(blueTeam);
            }
        }
    }

    private void winGame(Team team) {
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendTitle(team.toString() + "wins!", "yes they did");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (newGame.checkRedFlag(event.getBlock())) {
            getLogger().info("red flag broken");
            if (checkTeam(event.getPlayer()) == 2) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                pickedUpFlag(event.getPlayer(), false);
            }
        } else if (newGame.checkBlueFlag(event.getBlock())) {
            getLogger().info("blue flag broken");
            if (checkTeam(event.getPlayer()) == 1) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                pickedUpFlag(event.getPlayer(), true);
                //fill inventory with wool
            }
        }
    }

    //true is blue, false is red
    private void pickedUpFlag(Player player, boolean color) {
        ItemStack wool;
        if (color) {
            wool = new ItemStack(Material.BLUE_WOOL);
        } else {
            wool = new ItemStack(Material.RED_WOOL);
        }
        if (color) {
            this.blueInv = new ItemStack[36];
            for (int i = 0; i < 36; i++) {
                this.blueInv[i] = player.getInventory().getItem(i);
            }
        } else {
            this.redInv = new ItemStack[36];
            for (int i = 0; i < 36; i++) {
                this.redInv[i] = player.getInventory().getItem(i);
            }
        }
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, wool);
        }
    }

    //retyrns 1 if on blue team, 2 if on red team, 0 if on no team
    private int checkTeam(Player player) {
        if (blueTeam != null) {
            if (blueTeam.search(player)) {
                return 1;
            }
        }
        if (redTeam != null) {
            if (redTeam.search(player)) {
                return 2;
            }
        }
        return 0;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        int team = checkTeam(event.getEntity());
        if (team == 1) {
            blueTeam.addDeath();
            IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
        } else if (team == 2) {
            redTeam.addDeath();
            IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
        }
        if (blueTeam.getDeaths() == 100) {
            if (newGame.addPoint(blueTeam)) {
                winGame(blueTeam);
            }
            blueTeam.zeroDeaths();
        } else if (redTeam.getDeaths() == 100) {
            if (newGame.addPoint(redTeam)) {
                winGame(redTeam);
            }
            redTeam.zeroDeaths();
        }
    }
}