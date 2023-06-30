package com.milesacq;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (player2.getName().equalsIgnoreCase(sender.getName())) {
                GameSingleton.giveWool(player2);
            }
        }
    }
}
