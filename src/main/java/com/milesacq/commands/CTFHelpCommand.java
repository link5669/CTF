package com.milesacq.commands;

import org.bukkit.command.CommandSender;

public class CTFHelpCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Configure a map with /ctf setup filename");
        sender.sendMessage("Get a red and blue flag with /ctf give");
        sender.sendMessage("To start a game, create red and blue teams and specify the number of players with /ctf redteam create 1");
        sender.sendMessage("Add players to a team with /ctf blueteam add link5669");
        sender.sendMessage("See all the players on a team with /ctf blueteam list");
        sender.sendMessage("Start the game with /ctf start");
    }   
}
