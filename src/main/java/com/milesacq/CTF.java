package com.milesacq;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CTF extends JavaPlugin implements Listener, CommandExecutor {
    public Game newGame;

    @Override
    public void onEnable() {
        getLogger().info("Setting up CTF!");
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        if (this.newGame != null) {
            getLogger().info("bye bye");
            newGame.removeBars();
        }
//        newGame.removeScoreboard();
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
            Location testLocation = new Location(this.getServer().getWorld("world"), -289, 84, -216);
            Location testLocation2 = new Location(this.getServer().getWorld("world"), -289, 84, -222);
            newGame = new Game(testLocation, testLocation2);
            getLogger().info("ctf!");
        }
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (newGame.checkRedFlag(event.getBlock())) {
            getLogger().info("red flag broken");
            event.setCancelled(true);
            event.getPlayer().sendMessage("That's your flag!");
        } else if (newGame.checkBlueFlag(event.getBlock())) {
            getLogger().info("blue flag broken");
            event.setCancelled(true);
            event.getPlayer().sendMessage("That's your flag!");
        }
    }
}