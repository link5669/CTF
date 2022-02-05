package com.milesacq;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CTF extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        getLogger().info("Setting up CTF!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    private TextComponent getTextComponent(String advName, String[] progress, int num) {
        TextComponent message = new TextComponent();
        message.setText(progress[num]);
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(advName)));
        return message;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ctf")) {
            getLogger().info("ctf!");
            Location testLocation = new Location(this.getServer().getWorld("world"), 0,0,0 );
            Location testLocation2 = new Location(this.getServer().getWorld("world"), 0,60,0 );
            Game newGame = new Game(testLocation, testLocation2);
        }
        return false;
    }
}