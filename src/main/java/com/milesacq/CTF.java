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

import com.milesacq.commands.BlueTeamCommand;
import com.milesacq.commands.CTFHelpCommand;
import com.milesacq.commands.GiveCommand;
import com.milesacq.commands.RedTeamCommand;
import com.milesacq.commands.SetupCommand;
import com.milesacq.enums.CoordinateType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;

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
        File worldLoadDir = new File("./world");
        if (!worldLoadDir.exists()){
            worldLoadDir.mkdirs();
             try {
                FileUtils.copyDirectory(new File("./maps/manors"), worldLoadDir, true);
                Bukkit.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        GameSingleton.removeBossBars();
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
                sender.sendMessage("Make red team next!");
                new BlueTeamCommand().execute(sender, args);
            } else if (commandName.equals("redteam")) {
                sender.sendMessage("Add players! And run setup!");
                new RedTeamCommand().execute(sender, args);
            } else if (commandName.equals("setup")) {
                new SetupCommand().execute(sender, args);
            } else if (commandName.equals("give")) {
                new GiveCommand().execute(sender, args);
            } else if (commandName.equals("help")) {
                new CTFHelpCommand().execute(sender, args);
            } else if (commandName.equals("chat")) {
                StringBuilder result = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    result.append(args[i]).append(" ");
                }
                String concatenatedString = result.toString().trim();
                GameSingleton.findPlayerTeam(sender.getName()).sendMessage(concatenatedString);
            } else if (commandName.equals("load")) {
                File folder = new File("./maps");
                File[] listOfFiles = folder.listFiles();
                for (File file : listOfFiles) {
                    System.out.println(file.getName());
                    if (file.getName().equals(args[1])) {
                        System.out.println("1");
                        File mapToLoad = new File("./maps/" + args[1]);
                        try {
                            FileUtils.deleteDirectory(new File("./world"));
                            File worldLoadDir = new File("./world");
                            worldLoadDir.mkdirs();
                            FileUtils.copyDirectory(mapToLoad, worldLoadDir, true);
                            Bukkit.shutdown();
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return true;
    }

    void startGame() {
        Team blue = GameSingleton.getTeam("Blue");
        Team red = GameSingleton.getTeam("Red");
        blue.setOpponentTeam(red);
        red.setOpponentTeam(blue);
        World world = this.getServer().getWorld(WORLDNAME);
        for (Team team : GameSingleton.getTeams()) {
            team.setGoalBlock(new Location(world, team.getCoords(CoordinateType.GOALCOORDS,0), team.getCoords(CoordinateType.GOALCOORDS,1),team.getCoords(CoordinateType.GOALCOORDS,2)).getBlock());
            team.setStartBlock(new Location(world, team.getCoords(CoordinateType.STARTCOORDS,0), team.getCoords(CoordinateType.STARTCOORDS,1),team.getCoords(CoordinateType.STARTCOORDS,2)).getBlock());
        }
        new IndividualScoreboard(GameSingleton.getTeams());
        for (Team team : GameSingleton.getTeams()) {
            team.setBlocks();
            team.warpToSpawn();
            team.setFullHealthAndHunger();
            team.setSurvival();
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        String placedBlock = event.getBlock().getBlockData().toString();
        Location placedBlockLocation = event.getBlock().getLocation();
        int setupStep = GameSingleton.getSetupStep();

        if (setupStep != 6) {
            switch (setupStep) {
                case 0:
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.STARTCOORDS, 0, placedBlockLocation.getX() + .5);
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.STARTCOORDS, 1, placedBlockLocation.getY() + .5);
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.STARTCOORDS, 2, placedBlockLocation.getZ() + .5);
                    GameSingleton.setSetupStep(GameSingleton.getSetupStep() + 1);
                    event.getPlayer().sendMessage("Place block at red flag start");
                    break;
                case 1:
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.STARTCOORDS, 0, placedBlockLocation.getX() + .5);
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.STARTCOORDS, 1, placedBlockLocation.getY() + .5);
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.STARTCOORDS, 2, placedBlockLocation.getZ() + .5);
                    GameSingleton.setSetupStep(GameSingleton.getSetupStep() + 1);
                    event.getPlayer().sendMessage("Place block at blue flag goal (opposite color!)");
                    break;
                case 2:
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.GOALCOORDS, 0, placedBlockLocation.getX() + .5);
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.GOALCOORDS, 1, placedBlockLocation.getY() + .5);
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.GOALCOORDS, 2, placedBlockLocation.getZ() + .5);
                    GameSingleton.setSetupStep(GameSingleton.getSetupStep() + 1);
                    event.getPlayer().sendMessage("Place block at red flag goal (opposite color!)");
                    break;
                case 3:
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.GOALCOORDS, 0, placedBlockLocation.getX() + .5);
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.GOALCOORDS, 1, placedBlockLocation.getY() + .5);
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.GOALCOORDS, 2, placedBlockLocation.getZ() + .5);
                    GameSingleton.setSetupStep(GameSingleton.getSetupStep() + 1);
                    event.getPlayer().sendMessage("Place block at blue team spawn");
                    break;
                case 4:
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.RESPAWNCOORDS, 0, placedBlockLocation.getX() + .5);
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.RESPAWNCOORDS, 1, placedBlockLocation.getY() + .5);
                    GameSingleton.getTeam("Blue").setCoords(CoordinateType.RESPAWNCOORDS, 2, placedBlockLocation.getZ() + .5);
                    GameSingleton.setSetupStep(GameSingleton.getSetupStep() + 1);
                    event.getPlayer().sendMessage("Place block at red team spawn");
                    break;
                case 5:
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.RESPAWNCOORDS, 0, placedBlockLocation.getX() + .5);
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.RESPAWNCOORDS, 1, placedBlockLocation.getY() + .5);
                    GameSingleton.getTeam("Red").setCoords(CoordinateType.RESPAWNCOORDS, 2, placedBlockLocation.getZ() + .5);
                    GameSingleton.setSetupStep(GameSingleton.getSetupStep() + 1);
                    setupAndWriteConfig(event, placedBlockLocation);
                    break;
            }
        }

        //nullifies cancel on any block other than blue or red wool
        if (!(placedBlock.equals(BLUE_WOOL_NAMESPACED) || placedBlock.equals(RED_WOOL_NAMESPACED))) {
            event.setCancelled(false);
            return;
        }
        if (setupStep < 6) {
            return;
        }
        if (placedBlock.equals(BLUE_WOOL_NAMESPACED) && blockEquals(GameSingleton.getTeam("Blue").getGoalBlock().getLocation(), placedBlockLocation)) {
            event.setCancelled(false);
        } else {
            event.setCancelled(!(placedBlock.equals(RED_WOOL_NAMESPACED) && blockEquals(GameSingleton.getTeam("Red").getGoalBlock().getLocation(), placedBlockLocation)));
        }
        if (blockEquals(GameSingleton.getTeam("Red").getGoalBlock().getLocation(), event.getBlock().getLocation()) && placedBlock.equals(RED_WOOL_NAMESPACED)) {
            updateInventoryAndCheckPoint(event, GameSingleton.getTeam("Blue"));
        } else if (blockEquals(GameSingleton.getTeam("Blue").getGoalBlock().getLocation(), event.getBlock().getLocation()) && placedBlock.equals(BLUE_WOOL_NAMESPACED)) {        
            updateInventoryAndCheckPoint(event, GameSingleton.getTeam("Red"));
        }
    }

    private void setupAndWriteConfig(BlockPlaceEvent event, Location placedBlockLocation) {
        event.getPlayer().sendMessage("Setup complete!");
        try (FileWriter myWriter = new FileWriter("./config/ctf/" + GameSingleton.getConfigString() + ".txt")) {
            StringBuilder configData = new StringBuilder();
            CoordinateType[] coordinateTypes = {
                CoordinateType.STARTCOORDS, 
                CoordinateType.GOALCOORDS, 
                CoordinateType.RESPAWNCOORDS, 
            };
            for (CoordinateType coordinateType : coordinateTypes){
                for (Team team : GameSingleton.getTeams()) {
                    for (int i = 0; i < 3; i++) {
                        configData.append(team.getCoords(coordinateType, i)).append("\n");
                    }
                }
            }
            myWriter.write(configData.toString());
            event.getPlayer().sendMessage("Created " + GameSingleton.getConfigString() + ".txt!");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void updateInventoryAndCheckPoint(BlockPlaceEvent event, Team team) {
        for (int i = 0; i < 36; i++) {
            event.getPlayer().getInventory().setItem(i, team.getInventoryItem(i));
        }
        checkPoint(event);
    }

    private void checkPoint(BlockPlaceEvent event) {
        Team team = GameSingleton.findPlayerTeam(event.getPlayer().getName());
        if (team.getOpponentTeam().getStartBlock().equals(team.getWoolMaterial())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("The other team has your flag!");
        }
        for (Team teamTemp : GameSingleton.getTeams()) {
            teamTemp.setBlocks();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(team.getName() + " team scored a point!");
        }
        if (team.addPoint()) {
            winGame(team);
        }
    }

    private void winGame(Team team) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(team.getChatColor() + team.getName() + " team wins!", "but everyone's a winner in my heart <3",10,70,20);
        }
        GameSingleton.removeBossBars();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        GameSingleton.clearTeams();
        try {
            FileUtils.deleteDirectory(new File("./world"));
            File dir = new File("./world_default");
            if (!dir.isDirectory()) {
                System.err.println("There is no directory @ given path");
            } else {
                FileUtils.copyDirectory(new File("./world_default"), new File("./world"), true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter("eula.txt", "UTF-8");
            writer.println("eula=true");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bukkit.shutdown();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (GameSingleton.getTeams().size() < 2) {
            return;
        }
        if (GameSingleton.getSetupStep() < 6) {
            return;
        }
        Team team = GameSingleton.findPlayerTeam(event.getPlayer().getName());
        if (team.checkFlag(event.getBlock())) {
            event.getPlayer().sendMessage("That's your flag!");
            event.setCancelled(true);
        } else if (team.getOpponentTeam().checkFlag(event.getBlock())) {
            event.setCancelled(false);
            event.setDropItems(false);
            pickedUpFlag(event.getPlayer());
        }
    }

    private void pickedUpFlag(Player player) {
        Team team = GameSingleton.findPlayerTeam(player.getName());
        team.clearInventory();
        for (int i = 0; i < 36; i++) {
            team.setInventory(player.getInventory().getItem(i), i);
        }
        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, new ItemStack(team.getOpponentTeam().getWoolMaterial()));
        }
        for (Player playerCurr : Bukkit.getOnlinePlayers()) {
            playerCurr.sendMessage(player.getDisplayName() + " has the flag!");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Team team = GameSingleton.findPlayerTeam(event.getEntity().getName());
        if (team == null) {
            return;
        }
        if (team.getOpponentTeam().checkStartEmpty()) {
            event.getDrops().clear();
            team.getOpponentTeam().setStartBlock();
        }
        team.addDeath();
        new IndividualScoreboard(GameSingleton.getTeams());
        if (team.getDeaths() == 100) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(team.getName() + " team scored a point!");
            }
            if (team.addPoint()) {
                winGame(team);
            }
            team.zeroDeaths();
        }
    }
    
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Team team = GameSingleton.findPlayerTeam(event.getPlayer().getName());
        System.out.println(team.getName());
        Location spawn = new Location(this.getServer().getWorld(WORLDNAME),
                team.getCoords(CoordinateType.RESPAWNCOORDS, 0),
                team.getCoords(CoordinateType.RESPAWNCOORDS, 1),
                team.getCoords(CoordinateType.RESPAWNCOORDS, 2));
        event.setRespawnLocation(spawn);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameSingleton.isOnTeam(event.getPlayer())) {
            GameSingleton.showBossBars(event.getPlayer());
            new IndividualScoreboard(GameSingleton.getTeams());
        }
    }

    private boolean blockEquals(Location one, Location two) {
        return (one.getX() == two.getX() && one.getY() == two.getY() && one.getZ() == two.getZ());
    }
}