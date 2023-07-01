package com.milesacq;

import org.bukkit.command.CommandSender;

public class RedTeamCommand extends TeamCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        super.setTeamType(TeamType.RED);
        super.handleTeamCommand(args, sender);
    }
}