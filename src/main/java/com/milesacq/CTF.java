package com.milesacq;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

import java.io.FileWriter;
import java.io.IOException;

public class CTF extends JavaPlugin implements Listener {
    private String BLUE_WOOL_NAMESPACED = "CraftBlockData{minecraft:blue_wool}";
    private String RED_WOOL_NAMESPACED = "CraftBlockData{minecraft:red_wool}";
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
        World world = this.getServer().getWorld(WORLDNAME);
        Location blueFlagStart = createLocation(world, CoordinateType.BLUESTARTCOORDS);
        Location redFlagStart = createLocation(world, CoordinateType.REDSTARTCOORDS);
        Location redFlagGoal = createLocation(world, CoordinateType.REDGOALCOORDS);
        Location blueFlagGoal = createLocation(world, CoordinateType.BLUEGOALCOORDS);
        GameSingleton.setCoords(redFlagStart, blueFlagStart, redFlagGoal, blueFlagGoal);
        new IndividualScoreboard(GameSingleton.getBlueTeam(), GameSingleton.getRedTeam());
    }

    private Location createLocation(World world, CoordinateType coordinateType) {
        return new Location(world, GameSingleton.getCoords(coordinateType, 0), 
            GameSingleton.getCoords(coordinateType, 1),GameSingleton.getCoords(coordinateType, 2));
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
        int setupStep = GameSingleton.getSetupStep();

        if (setupStep != 6) {
            switch (setupStep) {
                case 0:
                    setupAndSendMessage(event, CoordinateType.BLUESTARTCOORDS, placedBlockLocation,
                            "Place block at red flag start");
                    break;
                case 1:
                    setupAndSendMessage(event, CoordinateType.REDSTARTCOORDS, placedBlockLocation,
                            "Place block at blue flag goal (opposite color!)");
                    break;
                case 2:
                    setupAndSendMessage(event, CoordinateType.BLUEGOALCOORDS, placedBlockLocation,
                            "Place block at red flag goal (opposite color!)");
                    break;
                case 3:
                    setupAndSendMessage(event, CoordinateType.REDGOALCOORDS, placedBlockLocation,
                            "Place block at red team spawn");
                    break;
                case 4:
                    setupAndSendMessage(event, CoordinateType.REDRESPAWNLOCATION, placedBlockLocation,
                            "Place block at blue team spawn");
                    break;
                case 5:
                    setupAndWriteConfig(event, placedBlockLocation);
                    break;
            }
        }

        if (!(placedBlock.equals(BLUE_WOOL_NAMESPACED) || placedBlock.equals(RED_WOOL_NAMESPACED))) {
            event.setCancelled(false);
            return;
        }

        if (placedBlock.equals(BLUE_WOOL_NAMESPACED) && blockEquals(GameSingleton.getBlueGoalLocation(), placedBlockLocation)) {
            event.setCancelled(false);
        } else {
            event.setCancelled(!(placedBlock.equals(RED_WOOL_NAMESPACED) && blockEquals(GameSingleton.getRedGoalLocation(), placedBlockLocation)));
        }

        if (GameSingleton.checkRedGoal(event.getBlock()) && placedBlock.equals(RED_WOOL_NAMESPACED)) {
            updateInventoryAndCheckPoint(event, GameSingleton.getRedTeam(), TeamType.RED);
        } else if (GameSingleton.checkBlueGoal(event.getBlock()) && placedBlock.equals(BLUE_WOOL_NAMESPACED)) {
            updateInventoryAndCheckPoint(event, GameSingleton.getBlueTeam(), TeamType.BLUE);
        }
    }

    private void setupAndSendMessage(BlockPlaceEvent event, CoordinateType coordinateType, Location placedBlockLocation, String message) {
        setup(coordinateType, placedBlockLocation);
        event.getPlayer().sendMessage(message);
    }

    private void setupAndWriteConfig(BlockPlaceEvent event, Location placedBlockLocation) {
        setup(CoordinateType.BLUERESPAWNLOCATION, placedBlockLocation);
        event.getPlayer().sendMessage("Setup complete!");

        try (FileWriter myWriter = new FileWriter(GameSingleton.getConfigString() + ".txt")) {
            StringBuilder configData = new StringBuilder();
            CoordinateType[] coordinateTypes = {
                CoordinateType.BLUESTARTCOORDS, CoordinateType.REDSTARTCOORDS,
                CoordinateType.BLUEGOALCOORDS, CoordinateType.REDGOALCOORDS,
                CoordinateType.REDRESPAWNLOCATION, CoordinateType.BLUERESPAWNLOCATION
            };

            for (CoordinateType coordinateType : coordinateTypes) {
                for (int i = 0; i < 3; i++) {
                    configData.append(GameSingleton.getCoords(coordinateType, i)).append("\n");
                }
            }
            myWriter.write(configData.toString());
            event.getPlayer().sendMessage("Created " + GameSingleton.getConfigString() + ".txt!");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void updateInventoryAndCheckPoint(BlockPlaceEvent event, Team team, TeamType teamType) {
        for (int i = 0; i < 36; i++) {
            event.getPlayer().getInventory().setItem(i, GameSingleton.getInvItem(teamType, i));
        }
        checkPoint(event, team);
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
                player.sendTitle(ChatColor.RED + "Red team wins!", "but everyone's a winner in my heart <3",10,70,20);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.BLUE + "Blue team wins!", "but everyone's a winner in my heart <3",10,70,20);
            }
        }
        GameSingleton.removeBossBars();
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
            if (checkTeam(event.getPlayer()) == TeamType.RED) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                event.setDropItems(false);
                pickedUpFlag(event.getPlayer(), TeamType.RED);
            }
        } else if (GameSingleton.checkBlueFlag(event.getBlock())) {
            if (checkTeam(event.getPlayer()) == TeamType.BLUE) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                event.setDropItems(false);
                pickedUpFlag(event.getPlayer(), TeamType.BLUE);
            }
        }
    }

    private void pickedUpFlag(Player player, TeamType color) {
        ItemStack wool;
        wool = (color == TeamType.BLUE) ? new ItemStack(Material.BLUE_WOOL) : new ItemStack(Material.RED_WOOL);
        if (color == TeamType.BLUE) {
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

    private TeamType checkTeam(Player player) {
        if (GameSingleton.getBlueTeam() != null) {
            if (GameSingleton.getBlueTeam().search(player)) {
                return TeamType.BLUE;
            }
        }
        if (GameSingleton.getRedTeam() != null) {
            if (GameSingleton.getRedTeam().search(player)) {
                return TeamType.RED;
            }
        }
        return TeamType.NONE;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (GameSingleton.getBlueTeam() == null || GameSingleton.getRedTeam() == null) {
            return;
        }
        TeamType team = checkTeam(event.getEntity());
        if (team == TeamType.BLUE) {
            if (GameSingleton.checkRedStartEmpty()) {
                event.getDrops().clear();
                GameSingleton.getRedStartLocation().getBlock().setType(Material.RED_WOOL);
            }
            GameSingleton.getBlueTeam().addDeath();
            new IndividualScoreboard(GameSingleton.getBlueTeam(), GameSingleton.getRedTeam());
        } else if (team == TeamType.RED) {
            if (GameSingleton.checkBlueStartEmpty()) {
                event.getDrops().clear();
                GameSingleton.getBlueStartLocation().getBlock().setType(Material.BLUE_WOOL);
            }
            GameSingleton.getRedTeam().addDeath();
            new IndividualScoreboard(GameSingleton.getBlueTeam(), GameSingleton.getRedTeam());
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
        if (GameSingleton.getBlueTeam().search(event.getPlayer()) || GameSingleton.getRedTeam().search(event.getPlayer())) {
            GameSingleton.getBlueTeam().showBar(event.getPlayer());
            GameSingleton.getRedTeam().showBar(event.getPlayer());
            new IndividualScoreboard(GameSingleton.getBlueTeam(), GameSingleton.getRedTeam());
        }
    }

    private boolean blockEquals(Location one, Location two) {
        return (one.getX() == two.getX() && one.getY() == two.getY() && one.getZ() == two.getZ());
    }
}