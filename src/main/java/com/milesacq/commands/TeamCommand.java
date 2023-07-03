package com.milesacq.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.milesacq.GameSingleton;
import com.milesacq.Team;
import com.milesacq.enums.TeamType;

public abstract class TeamCommand {

    abstract void execute(CommandSender sender, String[] args);

    protected void handleTeamCommand(String[] args, CommandSender sender, TeamType color, String name, ChatColor chatColor, BarColor barColor, Material woolMaterial) {
        if (args[1].equalsIgnoreCase("create")) {
            if (args[2] == null) {
                sender.sendMessage("please specify number of players");
            } else {
                GameSingleton.addTeam(new Team(Integer.parseInt(args[2]), color, name, chatColor, barColor, woolMaterial));
                if (args.length > 3) {
                    if (args.length - 3 > Integer.parseInt(args[2])) {
                        sender.sendMessage("Too many arguments!");
                        return;
                    }
                    for (int i = 3; i < args.length; i++) {
                        Team team = args[0].equals("redteam") ? GameSingleton.getTeam("Red") : GameSingleton.getTeam("Blue");
                         for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.toString().equals("CraftPlayer{name=" + args[i] + "}")) {
                                if (team.addPlayer(player)) {
                                    sender.sendMessage("Successfuly added " + args[2] + "players!");
                                    return;
                                } else {
                                    sender.sendMessage("Couldn't add! Team already full");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            teamCommand(sender, args);
        }
    }

    private void teamCommand(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase("add")) {
            handleAdd(args, sender);
        } else if (args[1].equalsIgnoreCase("list")) {
            Team team = args[0] == "redteam" ? GameSingleton.getTeam("Red") : GameSingleton.getTeam("Blue");
            sender.sendMessage(team.toString());
        }
    }

    private void handleAdd(String[] args, CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.toString().equals("CraftPlayer{name=" + args[2] + "}")) {
                    Team teamSearch = GameSingleton.findPlayerTeam(player.getName());
                if (teamSearch != null) {
                    sender.sendMessage("Player already on Team " + teamSearch.getName()); 
                }
                Team team = args[0].equals("redteam") ? GameSingleton.getTeam("Red") : GameSingleton.getTeam("Blue");
                if (team.addPlayer(player)) {
                    sender.sendMessage("Successfuly added!");
                    return;
                } else {
                    sender.sendMessage("Couldn't add! Team already full");
                    return;
                }
            }
        }
        sender.sendMessage("Player not in game!");
    }
}