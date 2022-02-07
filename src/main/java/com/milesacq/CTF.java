package com.milesacq;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CTF extends JavaPlugin implements Listener, CommandExecutor {
    public Game newGame;
    public Team redTeam;
    public Team blueTeam;

    @Override
    public void onEnable() {
        getLogger().info("Setting up CTF!");
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        if (this.newGame != null) {
            getLogger().info("bye bye");
            newGame.removeBars();
        }
//        newGame.removeScoreboard();
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
            if (args.length == 0) {
                Location redFlagStart = new Location(this.getServer().getWorld("world"), -289, 84, -216);
                Location blueFlagStart = new Location(this.getServer().getWorld("world"), -289, 84, -222);
                Location redFlagGoal = new Location(this.getServer().getWorld("world"), -291, 84, -216);
                Location blueFlagGoal = new Location(this.getServer().getWorld("world"), -289, 84, -220);
                newGame = new Game(redFlagStart, redFlagStart, redFlagGoal, blueFlagGoal);
                getLogger().info("ctf!");
            } else if (args[0].equalsIgnoreCase("blueteam")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (args[2] == null) {
                        sender.sendMessage("please specify number of players");
                    } else {
                        blueTeam = new Team(Integer.parseInt(args[2]));
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
                        redTeam = new Team(Integer.parseInt(args[2]));
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
            }
        }
        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (newGame.checkRedGoal(event.getBlock()) && event.getBlock().getBlockData().toString().equals("minecraft:red_wool")) {
            getLogger().info("red flag placed");
        } else if (newGame.checkBlueGoal(event.getBlock()) && event.getBlock().getBlockData().toString().equals("minecraft:blue_wool")) {
            getLogger().info("blue flag placed");
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
            }
        } else if (newGame.checkBlueFlag(event.getBlock())) {
            getLogger().info("blue flag broken");
            if (checkTeam(event.getPlayer()) == 1) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

    private int checkTeam(Player player) {
        if (blueTeam != null) {
            if (blueTeam.search(player)) {
                getLogger().info("red");
                return 1;
            }
        }
        if (redTeam != null) {
            if (redTeam.search(player)) {
                getLogger().info("blue");
                return 2;
            }
        }
        return 0;
    }
}