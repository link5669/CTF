package com.milesacq.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;

import com.milesacq.enums.TeamType;

public class BlueTeamCommand extends TeamCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        super.handleTeamCommand(args, sender, TeamType.BLUE, "Blue", ChatColor.BLUE, BarColor.BLUE, Material.BLUE_WOOL);
    }
}
