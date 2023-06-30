package com.milesacq;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.milesacq.CoordinateType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CTF extends JavaPlugin implements Listener, CommandExecutor {

    private String WORLDNAME = "world";

    @Override
    public void onEnable() {
        getLogger().info("Setting up CTF!");
        getServer().getPluginManager().registerEvents(this, this);
        new GameSingleton();
        GameSingleton.setWorld(this.getServer().getWorld(WORLDNAME));
    }

    @Override
    public void onDisable() {
        if (GameSingleton.getRedTeam() != null) {
            GameSingleton.getRedTeam().removeBar();
        }
        if (GameSingleton.getBlueTeam() != null) {
            GameSingleton.getBlueTeam().removeBar();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ctf")) {
            String commandName = args[0].toLowerCase();
            if (commandName.equals("start")) {
                startGame();
            } else if (commandName.equals("blueteam")) {
                new BlueTeamCommand().execute(sender, args);
            } else if (commandName.equals("redteam")) {
                new RedTeamCommand().execute(sender, args);
            } else if (commandName.equals("setup")) {
                new SetupCommand().execute(sender, args);
            } else if (args[0].equalsIgnoreCase("give")) {
                new GiveCommand().execute(sender, args);
            } else if (args[0].equalsIgnoreCase("help")) {
                new CTFHelpCommand().execute(sender, args);
            }
        }
        return true;
    }

    void startGame() {
        Location blueFlagStart = new Location(this.getServer().getWorld(WORLDNAME),
                GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 0),
                GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 2));
        Location redFlagStart = new Location(this.getServer().getWorld(WORLDNAME),
                GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 0),
                GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 2));
        Location redFlagGoal = new Location(this.getServer().getWorld(WORLDNAME),
                GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 0),
                GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 2));
        Location blueFlagGoal = new Location(this.getServer().getWorld(WORLDNAME),
                GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 0),
                GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 1),
                GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 2));
        GameSingleton.setCoords(redFlagStart, blueFlagStart, redFlagGoal, blueFlagGoal);
        IndividualScoreboard board = new IndividualScoreboard(GameSingleton.getBlueTeam(), GameSingleton.getRedTeam());
    }

    private void teamCommand(CommandSender sender, String[] args, Team team) {
        if (args[1].equalsIgnoreCase("add")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.toString().equals("CraftPlayer{name=" + args[2] + "}")) {
                    if (GameSingleton.getRedTeam() != null) {
                        if (GameSingleton.getRedTeam().search(player)) {
                            sender.sendMessage("Player already on Red Team");
                            return;
                        }
                    }
                    if (GameSingleton.getBlueTeam() != null) {
                        if (GameSingleton.getBlueTeam().search(player)) {
                            sender.sendMessage("Player already on Blue Team");
                            return;
                        }
                    }
                    if (team.addPlayer(player)) {
                        sender.sendMessage("Successfuly added!");
                        return;
                    } else {
                        sender.sendMessage("Couldn't add! Team already full");
                        return;
                    }
                }
            }
            sender.sendMessage("Player not in game!");
        } else if (args[1].equalsIgnoreCase("list")) {
            getLogger().info(team.toString());
        }
    }

    private void setup(CoordinateType coordType, Location placedBlockLocation) {
        GameSingleton.setCoords(coordType, 0, placedBlockLocation.getX() + .5);
        GameSingleton.setCoords(coordType, 1, placedBlockLocation.getY() + .5);
        GameSingleton.setCoords(coordType, 2, placedBlockLocation.getZ() + .5);
        GameSingleton.setSetupStep(GameSingleton.getSetupStep() + 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (GameSingleton.getBlueTeam() == null || GameSingleton.getRedTeam() == null) {
            System.out.println("neither");
            return;
        }
        String placedBlock = event.getBlock().getBlockData().toString();
        Location placedBlockLocation = event.getBlock().getLocation();
        if (GameSingleton.getSetupStep() != 6) {
            switch (GameSingleton.getSetupStep()) {
                case 0:
                    setup(CoordinateType.BLUESTARTCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at red flag start");
                    return;
                case 1:
                    setup(CoordinateType.REDSTARTCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at blue flag goal (opposite color!)");
                    return;
                case 2:
                    setup(CoordinateType.BLUEGOALCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at red flag goal (opposite color!)");
                    return;
                case 3:
                    setup(CoordinateType.REDGOALCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at red team spawn");
                    return;
                case 4:
                    setup(CoordinateType.REDRESPAWNLOCATION, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at blue team spawn");
                    return;
                case 5:
                    setup(CoordinateType.BLUERESPAWNLOCATION, placedBlockLocation);
                    event.getPlayer().sendMessage("Setup complete!");
                    try {
                        FileWriter myWriter = new FileWriter(GameSingleton.getConfigString() + ".txt");
                        myWriter.write(GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 0) + "\n"
                                + GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 1) + "\n"
                                + GameSingleton.getCoords(CoordinateType.BLUESTARTCOORDS, 2) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 0) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 1) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDSTARTCOORDS, 2) + "\n"
                                + GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 0) + "\n"
                                + GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 1) + "\n"
                                + GameSingleton.getCoords(CoordinateType.BLUEGOALCOORDS, 2) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 0) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 1) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDGOALCOORDS, 2) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 0) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 1) + "\n"
                                + GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 2)
                                + "\n" + GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 0) + "\n"
                                + GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 1) + "\n"
                                + GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 2) + "\n");
                        myWriter.close();
                        event.getPlayer().sendMessage("Created " + GameSingleton.getConfigString() + ".txt!");
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                    return;
            }
        }
        System.out.println("1");
        if (!(placedBlock.equals("CraftBlockData{minecraft:blue_wool}") || placedBlock.equals("CraftBlockData{minecraft:red_wool}"))) {
            event.setCancelled(false);
            return;
        }
        System.out.println("2");
        if (placedBlock.equals("CraftBlockData{minecraft:blue_wool}") && blockEquals(GameSingleton.getBlueGoalLocation(), placedBlockLocation)) {
            event.setCancelled(false);
        } else {
            event.setCancelled(!placedBlock.equals("CraftBlockData{minecraft:red_wool}") || !blockEquals(GameSingleton.getRedGoalLocation(), placedBlockLocation));
        }
        System.out.println("3");
        if (GameSingleton.checkRedGoal(event.getBlock()) && placedBlock.equals("CraftBlockData{minecraft:red_wool}")) {
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, GameSingleton.getInvItem(TeamType.RED, i));
            }
            checkPoint(event, GameSingleton.getRedTeam());
        } else if (GameSingleton.checkBlueGoal(event.getBlock()) && placedBlock.equals("CraftBlockData{minecraft:blue_wool}")) {
            System.out.println("4");
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, GameSingleton.getInvItem(TeamType.BLUE, i));
            }
            checkPoint(event, GameSingleton.getBlueTeam());
        }
    }

    private void checkPoint(BlockPlaceEvent event, Team team) {
        if (team.equals(GameSingleton.getRedTeam())) {
            if (GameSingleton.getBlueStartLocation().getBlock().equals(Material.BLUE_WOOL)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("The other team has your flag!");
            }
            GameSingleton.setBlocks();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Blue team scored a point!");
            }
            team = GameSingleton.getBlueTeam();
        } else {
            if (GameSingleton.getRedStartLocation().getBlock().equals(Material.RED_WOOL)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("The other team has your flag!");
            }
            GameSingleton.setBlocks();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Red team scored a point!");
            }
            team = GameSingleton.getRedTeam();
        }
        if (GameSingleton.addPoint(team)) {
            winGame(team);
        }
    }

    private void winGame(Team team) {
        if (team.equals(GameSingleton.getRedTeam())) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.RED + "Red team wins!", "yes they did");
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.BLUE + "Blue team wins!", "yes they did");
            }
        }
        GameSingleton.getRedTeam().removeBar();
        GameSingleton.getBlueTeam().removeBar();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (GameSingleton.getBlueTeam() == null || GameSingleton.getRedTeam() == null) {
            return;
        }
        event.getPlayer().sendMessage(String.valueOf(GameSingleton.getSetupStep()));
        if (GameSingleton.getSetupStep() < 6) {
            return;
        }
        if (GameSingleton.checkRedFlag(event.getBlock())) {
            if (checkTeam(event.getPlayer()) == 2) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                event.setDropItems(false);
                pickedUpFlag(event.getPlayer(), false);
            }
        } else if (GameSingleton.checkBlueFlag(event.getBlock())) {
            if (checkTeam(event.getPlayer()) == 1) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                event.setDropItems(false);
                pickedUpFlag(event.getPlayer(), true);
            }
        }
    }

    // true is blue, false is red
    private void pickedUpFlag(Player player, boolean color) {
        ItemStack wool;
        if (color) {
            wool = new ItemStack(Material.BLUE_WOOL);
        } else {
            wool = new ItemStack(Material.RED_WOOL);
        }
        if (color) {
            GameSingleton.clearInv(TeamType.BLUE);
            for (int i = 0; i < 36; i++) {
                GameSingleton.setInv(TeamType.BLUE, player.getInventory().getItem(i), i);
            }
        } else {
            GameSingleton.clearInv(TeamType.RED);
            for (int i = 0; i < 36; i++) {
                GameSingleton.setInv(TeamType.RED, player.getInventory().getItem(i), i);
            }
        }
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, wool);
        }
        for (Player playerCurr : Bukkit.getOnlinePlayers()) {
            playerCurr.sendMessage(player.getDisplayName() + "has the flag!");
        }
    }

    // retyrns 1 if on blue team, 2 if on red team, 0 if on no team
    private int checkTeam(Player player) {
        if (GameSingleton.getBlueTeam() != null) {
            if (GameSingleton.getBlueTeam().search(player)) {
                return 1;
            }
        }
        if (GameSingleton.getRedTeam() != null) {
            if (GameSingleton.getRedTeam().search(player)) {
                return 2;
            }
        }
        return 0;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (GameSingleton.getBlueTeam() == null || GameSingleton.getRedTeam() == null) {
            return;
        }
        int team = checkTeam(event.getEntity());
        if (team == 1) {
            if (GameSingleton.checkRedStartEmpty()) {
                event.getDrops().clear();
                GameSingleton.getRedStartLocation().getBlock().setType(Material.RED_WOOL);
            }
            GameSingleton.getBlueTeam().addDeath();
            IndividualScoreboard board = new IndividualScoreboard(GameSingleton.getBlueTeam(),
                    GameSingleton.getRedTeam());
        } else if (team == 2) {
            if (GameSingleton.checkBlueStartEmpty()) {
                event.getDrops().clear();
                GameSingleton.getBlueStartLocation().getBlock().setType(Material.BLUE_WOOL);
            }
            GameSingleton.getRedTeam().addDeath();
            IndividualScoreboard board = new IndividualScoreboard(GameSingleton.getBlueTeam(),
                    GameSingleton.getRedTeam());
        }
        if (GameSingleton.getBlueTeam().getDeaths() == 100) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Blue team scored a point!");
            }
            if (GameSingleton.addPoint(GameSingleton.getBlueTeam())) {
                winGame(GameSingleton.getBlueTeam());
            }
            GameSingleton.getBlueTeam().zeroDeaths();
        } else if (GameSingleton.getRedTeam().getDeaths() == 100) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Red team scored a point!");
            }
            if (GameSingleton.addPoint(GameSingleton.getRedTeam())) {
                winGame(GameSingleton.getRedTeam());
            }
            GameSingleton.getRedTeam().zeroDeaths();
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (GameSingleton.getBlueTeam() == null || GameSingleton.getRedTeam() == null) {
            return;
        }
        if (GameSingleton.getRedTeam().search(event.getPlayer())) {
            Location spawn = new Location(this.getServer().getWorld(WORLDNAME),
                    GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 0),
                    GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 1),
                    GameSingleton.getCoords(CoordinateType.REDRESPAWNLOCATION, 2));
            event.setRespawnLocation(spawn);
        } else if (GameSingleton.getBlueTeam().search(event.getPlayer())) {
            Location spawn = new Location(this.getServer().getWorld(WORLDNAME),
                    GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 0),
                    GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 1),
                    GameSingleton.getCoords(CoordinateType.BLUERESPAWNLOCATION, 2));
            event.setRespawnLocation(spawn);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameSingleton.getBlueTeam() == null || GameSingleton.getRedTeam() == null) {
            return;
        }
        if (GameSingleton.getBlueTeam().search(event.getPlayer())
                || GameSingleton.getRedTeam().search(event.getPlayer())) {
            GameSingleton.getBlueTeam().showBar(event.getPlayer());
            GameSingleton.getRedTeam().showBar(event.getPlayer());
            IndividualScoreboard board = new IndividualScoreboard(GameSingleton.getBlueTeam(),
                    GameSingleton.getRedTeam());
        }
    }

    private boolean blockEquals(Location one, Location two) {
        if (one.getX() == two.getX()) {
            if (one.getY() == two.getY()) {
                return one.getZ() == two.getZ();
            }
        }
        return false;
    }
}
