package com.milesacq;

import java.io.Console;
import java.time.chrono.Chronology;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedTeamCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        handleTeamCommand(args, sender, false);
    }

    private void handleTeamCommand(String[] args, CommandSender sender, boolean color) {
        if (args[1].equalsIgnoreCase("create")) {
            if (args[2] == null) {
                sender.sendMessage("please specify number of players");
            } else {
                GameSingleton.setRedTeam(new Team(Integer.parseInt(args[2]), color));
            }
        } else {
            teamCommand(sender, args, color ? GameSingleton.getBlueTeam() : GameSingleton.getRedTeam());
        }
    }

    private void teamCommand(CommandSender sender, String[] args, Team team) {
        if (args[1].equalsIgnoreCase("add")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.toString().equals("CraftPlayer{name=" + args[2] + "}")) {
                    if (GameSingleton.getRedTeam() != null) {
                        if (GameSingleton.getRedTeam().search(player)) {
                            sender.sendMessage("Player already on Red Team");
                            return;
                        }
                    }
                    if (GameSingleton.getBlueTeam() != null) {
                        if (GameSingleton.getBlueTeam().search(player)) {
                            sender.sendMessage("Player already on Blue Team");
                            return;
                        }
                    }
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
            System.out.println(team.toString());
        }
    }
}