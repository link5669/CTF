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
            }
        } else {
            teamCommand(sender, args);
        }
    }

    private void teamCommand(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase("add")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.toString().equals("CraftPlayer{name=" + args[2] + "}")) {
                    for (Team team : GameSingleton.getTeams()) {
                        if (team.search(player)) {
                            sender.sendMessage("Player already on Team " + team.getName());
                        }
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
        } else if (args[1].equalsIgnoreCase("list")) {
            Team team = args[0] == "redteam" ? GameSingleton.getTeam("Red") : GameSingleton.getTeam("Blue");
            System.out.println(team.toString());
        }
    }
}
