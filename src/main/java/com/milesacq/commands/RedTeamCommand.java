package com.milesacq.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;

import com.milesacq.enums.TeamType;

public class RedTeamCommand extends TeamCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        super.handleTeamCommand(args, sender, TeamType.RED, "Red", ChatColor.RED, BarColor.RED, Material.RED_WOOL);
    }
}