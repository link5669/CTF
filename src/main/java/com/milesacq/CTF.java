package com.milesacq;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CTF extends JavaPlugin implements Listener, CommandExecutor {
    public Game newGame;
    public Team redTeam;
    public Team blueTeam;
    private ItemStack[] blueInv;
    private ItemStack[] redInv;
    private static final double[] BLUESTARTCOORDS = {-289.5, 84.5, -216.5};
    private static final double[] REDSTARTCOORDS = {-289.5, 84.5, -222.5};
    private static final double[] BLUEGOALCOORDS = {-289.5, 84.5, -220.5};
    private static final double[] REDGOALCOORDS = {-291.5, 84.5, -216.5};


    @Override
    public void onEnable() {
        getLogger().info("Setting up CTF!");
        getServer().getPluginManager().registerEvents(this, this);
        newGame = new Game();
    }

    @Override
    public void onDisable() {
        if (this.newGame != null) {
            redTeam.removeBar();
            blueTeam.removeBar();
        }
        for (Player player : Bukkit.getOnlinePlayers()){
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
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
            if (args[0].equalsIgnoreCase("start")) {
                Location blueFlagStart = new Location(this.getServer().getWorld("world"), BLUESTARTCOORDS[0], BLUESTARTCOORDS[1], BLUESTARTCOORDS[2]);
                Location redFlagStart = new Location(this.getServer().getWorld("world"), REDSTARTCOORDS[0], REDSTARTCOORDS[1], REDSTARTCOORDS[2]);
                Location redFlagGoal = new Location(this.getServer().getWorld("world"), REDGOALCOORDS[0], REDGOALCOORDS[1], REDGOALCOORDS[2]);
                Location blueFlagGoal = new Location(this.getServer().getWorld("world"), BLUEGOALCOORDS[0], BLUEGOALCOORDS[1], BLUEGOALCOORDS[2]);
                newGame.setCoords(redFlagStart, blueFlagStart, redFlagGoal, blueFlagGoal);
                IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
            } else if (args[0].equalsIgnoreCase("blueteam")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (args[2] == null) {
                        sender.sendMessage("please specify number of players");
                    } else {
                        blueTeam = new Team(Integer.parseInt(args[2]), false);
                    }
                } else {
                    teamCommand(sender, args, blueTeam);
                }
            } else if (args[0].equalsIgnoreCase("redteam")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (args[2] == null) {
                        sender.sendMessage("please specify number of players");
                    } else {
                        redTeam = new Team(Integer.parseInt(args[2]), true);
                    }
                } else {
                    teamCommand(sender, args, redTeam);
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                for (Player player2 : Bukkit.getOnlinePlayers()){
                    if (player2.getName().equalsIgnoreCase(sender.getName())) {
                        newGame.giveWool(player2);
                    }
                }
            }
        }
        return true;
    }

    private void teamCommand(CommandSender sender, String[] args, Team redTeam) {
        if (args[1].equalsIgnoreCase("add")) {
            Player player = Bukkit.getPlayer(args[2]);
            if (redTeam.addPlayer(player)) {
                sender.sendMessage("Successfuly added!");
            } else {
                sender.sendMessage("Couldn't add!");
            }
        } else if (args[1].equalsIgnoreCase("list")) {
            getLogger().info(redTeam.toString());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:blue_wool}")
                && newGame.getBlueGoalLocation().equals(event.getBlock().getLocation())) {
            event.setCancelled(false);
        } else event.setCancelled(!event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:red_wool}")
                || !newGame.getRedGoalLocation().equals(event.getBlock().getLocation()));
        if (newGame.checkRedGoal(event.getBlock()) && event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:red_wool}")) {
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, redInv[i]);
            }
            checkPoint(event, redTeam, Material.RED_WOOL);
        } else if (newGame.checkBlueGoal(event.getBlock()) && event.getBlock().getBlockData().toString().equals("CraftBlockData{minecraft:blue_wool}")) {
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, blueInv[i]);
            }
            checkPoint(event, blueTeam, Material.BLUE_WOOL);
        }
    }

    private void checkPoint(BlockPlaceEvent event, Team team, Material wool) {
        newGame.getRedGoalLocation().getBlock().setType(Material.AIR);
        newGame.getRedStartLocation().getBlock().setType(wool);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(team.toString() + " scored a point!");
        }
        if (newGame.addPoint(team)) {
            winGame(team);
        }
    }

    private void winGame(Team team) {
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendTitle(team.toString() + "wins!", "yes they did");
        }
        if (this.newGame != null) {
            redTeam.removeBar();
            blueTeam.removeBar();
        }
        for (Player player : Bukkit.getOnlinePlayers()){
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (newGame.checkRedFlag(event.getBlock())) {
            if (checkTeam(event.getPlayer()) == 2) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                pickedUpFlag(event.getPlayer(), false);
            }
        } else if (newGame.checkBlueFlag(event.getBlock())) {
            if (checkTeam(event.getPlayer()) == 1) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                pickedUpFlag(event.getPlayer(), true);
                //fill inventory with wool
            }
        }
    }

    //true is blue, false is red
    private void pickedUpFlag(Player player, boolean color) {
        ItemStack wool;
        if (color) {
            wool = new ItemStack(Material.BLUE_WOOL);
        } else {
            wool = new ItemStack(Material.RED_WOOL);
        }
        if (color) {
            this.blueInv = new ItemStack[36];
            for (int i = 0; i < 36; i++) {
                this.blueInv[i] = player.getInventory().getItem(i);
            }
        } else {
            this.redInv = new ItemStack[36];
            for (int i = 0; i < 36; i++) {
                this.redInv[i] = player.getInventory().getItem(i);
            }
        }
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, wool);
        }
    }

    //retyrns 1 if on blue team, 2 if on red team, 0 if on no team
    private int checkTeam(Player player) {
        if (blueTeam != null) {
            if (blueTeam.search(player)) {
                return 1;
            }
        }
        if (redTeam != null) {
            if (redTeam.search(player)) {
                return 2;
            }
        }
        return 0;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        int team = checkTeam(event.getEntity());
        if (team == 1) {
            if (newGame.checkRedStartEmpty()) {
                event.getDrops().clear();
                newGame.getRedStartLocation().getBlock().setType(Material.RED_WOOL);
            }
            blueTeam.addDeath();
            IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
        } else if (team == 2) {
            if (newGame.checkBlueStartEmpty()) {
                event.getDrops().clear();
                newGame.getBlueStartLocation().getBlock().setType(Material.BLUE_WOOL);
            }
            redTeam.addDeath();
            IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
        }
        if (blueTeam.getDeaths() == 100) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Blue team scored a point!");
            }
            if (newGame.addPoint(blueTeam)) {
                winGame(blueTeam);
            }
            blueTeam.zeroDeaths();
        } else if (redTeam.getDeaths() == 100) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Red team scored a point!");
            }
            if (newGame.addPoint(redTeam)) {
                winGame(redTeam);
            }
            redTeam.zeroDeaths();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println(blueTeam.search(event.getPlayer()));
        System.out.println(redTeam.search(event.getPlayer()));
        if (blueTeam.search(event.getPlayer()) || redTeam.search(event.getPlayer())) {
            blueTeam.showBar(event.getPlayer());
            redTeam.showBar(event.getPlayer());
            IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
        }
    }
}