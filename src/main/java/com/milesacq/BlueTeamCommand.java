package com.milesacq;

import org.bukkit.command.CommandSender;

public class BlueTeamCommand extends TeamCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {
        super.setTeamType(TeamType.BLUE);
        super.handleTeamCommand(args, sender);
    }
}
