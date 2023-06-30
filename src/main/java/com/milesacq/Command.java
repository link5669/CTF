package com.milesacq;

import org.bukkit.command.CommandSender;

abstract class Command {
    abstract public void execute(CommandSender sender, String[] args);
}
