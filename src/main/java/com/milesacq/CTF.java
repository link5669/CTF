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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CTF extends JavaPlugin implements Listener, CommandExecutor {
    public Game newGame;
    public Team redTeam;
    public Team blueTeam;
    private ItemStack[] blueInv;
    private ItemStack[] redInv;
    private static final double[] BLUESTARTCOORDS = new double[3]; //0
    private static final double[] REDSTARTCOORDS = new double[3]; //1
    private static final double[] BLUEGOALCOORDS = new double[3]; //2
    private static final double[] REDGOALCOORDS = new double[3]; //3
    private static final double[] REDRESPAWNLOCATION = new double[3]; //4
    private static final double[] BLUERESPAWNLOCATION = new double[3]; //5
    private int setupStep = 6;
    private String WORLDNAME = "CTFManors";
    private String configName;

    @Override
    public void onEnable() {
        getLogger().info("Setting up CTF!");
        getServer().getPluginManager().registerEvents(this, this);
        newGame = new Game();
    }

    @Override
    public void onDisable() {
        if (this.redTeam != null) {
            redTeam.removeBar();
        }
        if (this.blueTeam != null) {
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
                startGame();
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
            } else if (args[0].equalsIgnoreCase("setup") ) {
                if (args[2] == null) {
                    sender.sendMessage("Please specify config name");
                    return true;
                }
                configName = args[2];
                if (args[1].equalsIgnoreCase("create")) {
                    createConfig(sender);
                } else if (args[1].equalsIgnoreCase("read")) {
                    readFromFile(sender);
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                help(sender);
            }
        }
        return true;
    }

    private void startGame() {
        Location blueFlagStart = new Location(this.getServer().getWorld(WORLDNAME), BLUESTARTCOORDS[0], BLUESTARTCOORDS[1], BLUESTARTCOORDS[2]);
        Location redFlagStart = new Location(this.getServer().getWorld(WORLDNAME), REDSTARTCOORDS[0], REDSTARTCOORDS[1], REDSTARTCOORDS[2]);
        Location redFlagGoal = new Location(this.getServer().getWorld(WORLDNAME), REDGOALCOORDS[0], REDGOALCOORDS[1], REDGOALCOORDS[2]);
        Location blueFlagGoal = new Location(this.getServer().getWorld(WORLDNAME), BLUEGOALCOORDS[0], BLUEGOALCOORDS[1], BLUEGOALCOORDS[2]);
        newGame.setCoords(redFlagStart, blueFlagStart, redFlagGoal, blueFlagGoal);
        IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
    }

    private void help(CommandSender sender) {
        sender.sendMessage("Configure a map with /ctf setup filename");
        sender.sendMessage("Get a red and blue flag with /ctf give");
        sender.sendMessage("To start a game, create red and blue teams and specify the number of players with /ctf redteam create 1");
        sender.sendMessage("Add players to a team with /ctf blueteam add link5669");
        sender.sendMessage("See all the players on a team with /ctf blueteam list");
        sender.sendMessage("Start the game with /ctf start");
    }

    private void createConfig(CommandSender sender) {
        setupStep = 0;
        sender.sendMessage("Place block at blue flag start");
        try {
            File myObj = new File(configName + ".txt");
            if (myObj.createNewFile()) {
                sender.sendMessage("File created: " + myObj.getName());
            } else {
                sender.sendMessage("File already exists.");
            }
        } catch (IOException e) {
            sender.sendMessage("An error occurred.");
            e.printStackTrace();
        }
    }

    private void readFromFile(CommandSender sender) {
        try {
            File myObj = new File(configName + ".txt");
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
//                        populateSetup(data, BLUESTARTCOORDS, myReader);
//                        populateSetup(data, REDSTARTCOORDS, myReader);
//                        populateSetup(data, BLUEGOALCOORDS, myReader);
//                        populateSetup(data, REDGOALCOORDS, myReader);
//                        populateSetup(data, REDRESPAWNLOCATION, myReader);
//                        populateSetup(data, BLUERESPAWNLOCATION, myReader);
            for (int i = 0; i < 3; i++) {
                BLUESTARTCOORDS[i] = Double.parseDouble(data);
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                REDSTARTCOORDS[i] = Double.parseDouble(data);
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                BLUEGOALCOORDS[i] = Double.parseDouble(data);
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                REDGOALCOORDS[i] = Double.parseDouble(data);
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                REDRESPAWNLOCATION[i] = Double.parseDouble(data);
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                BLUERESPAWNLOCATION[i] = Double.parseDouble(data);
                if (myReader.hasNextLine()) {
                    data = myReader.nextLine();
                }
            }
            myReader.close();
            setBlocks();
        } catch (FileNotFoundException e) {
            sender.sendMessage("An error occurred.");
            e.printStackTrace();
        }
    }

    private void populateSetup(String data, double[] arr, Scanner myReader) {
        for (int i = 0; i < 3; i++) {
            arr[i] = Double.parseDouble(data);
            if (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
        }
    }

    private void teamCommand(CommandSender sender, String[] args, Team team) {
        if (args[1].equalsIgnoreCase("add")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.toString().equals("CraftPlayer{name="+args[2]+"}")) {
                    if (this.redTeam != null) {
                        if (this.redTeam.search(player)) {
                            sender.sendMessage("Player already on Red Team");
                            return;
                        }
                    }
                    if (this.blueTeam != null) {
                        if (this.blueTeam.search(player)) {
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

    private void setup(double[] arr, Location placedBlockLocation) {
        arr[0] = placedBlockLocation.getX() + .5;
        arr[1] = placedBlockLocation.getY() + .5;
        arr[2] = placedBlockLocation.getZ() + .5;
        setupStep++;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.blueTeam == null || this.redTeam == null) {
            return;
        }
        String placedBlock = event.getBlock().getBlockData().toString();
        Location placedBlockLocation = event.getBlock().getLocation();
        if (setupStep != 6) {
            switch (setupStep) {
                case 0:
                    setup(BLUESTARTCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at red flag start");
                    return;
                case 1:
                    setup(REDSTARTCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at blue flag goal (opposite color!)");
                    return;
                case 2:
                    setup(BLUEGOALCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at red flag goal (opposite color!)");
                    return;
                case 3:
                    setup(REDGOALCOORDS, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at red team spawn");
                    return;
                case 4:
                    setup(REDRESPAWNLOCATION, placedBlockLocation);
                    event.getPlayer().sendMessage("Place block at blue team spawn");
                    return;
                case 5:
                    setup(BLUERESPAWNLOCATION, placedBlockLocation);
                    event.getPlayer().sendMessage("Setup complete!");
                    try {
                        FileWriter myWriter = new FileWriter(configName + ".txt");
                        myWriter.write(BLUESTARTCOORDS[0] + "\n" + BLUESTARTCOORDS[1] + "\n" + BLUESTARTCOORDS[2] + "\n" + REDSTARTCOORDS[0] + "\n" + REDSTARTCOORDS[1] + "\n" + REDSTARTCOORDS[2] + "\n" + BLUEGOALCOORDS[0] + "\n" + BLUEGOALCOORDS[1] + "\n" + BLUEGOALCOORDS[2] + "\n" + REDGOALCOORDS[0] + "\n" + REDGOALCOORDS[1] + "\n" + REDGOALCOORDS[2] + "\n" + REDRESPAWNLOCATION[0] + "\n" + REDRESPAWNLOCATION[1] + "\n" + REDRESPAWNLOCATION[2] + "\n" + BLUERESPAWNLOCATION[0] + "\n" + BLUERESPAWNLOCATION[1] + "\n" + BLUERESPAWNLOCATION[2] + "\n");
                        myWriter.close();
                        event.getPlayer().sendMessage("Created " + configName + ".txt!");
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                    return;
            }
        }
        if (!(placedBlock.equals("CraftBlockData{minecraft:blue_wool}") || placedBlock.equals("CraftBlockData{minecraft:red_wool}"))) {
            event.setCancelled(false);
            return;
        }
        if (placedBlock.equals("CraftBlockData{minecraft:blue_wool}") && blockEquals(newGame.getBlueGoalLocation(), placedBlockLocation)) {
            event.setCancelled(false);
        } else {
            event.setCancelled(!placedBlock.equals("CraftBlockData{minecraft:red_wool}") || !blockEquals(newGame.getRedGoalLocation(), placedBlockLocation));
        }
        if (newGame.checkRedGoal(event.getBlock()) && placedBlock.equals("CraftBlockData{minecraft:red_wool}")) {
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, redInv[i]);
            }
            checkPoint(event, redTeam);
        } else if (newGame.checkBlueGoal(event.getBlock()) && placedBlock.equals("CraftBlockData{minecraft:blue_wool}")) {
            for (int i = 0; i < 36; i++) {
                event.getPlayer().getInventory().setItem(i, blueInv[i]);
            }
            checkPoint(event, blueTeam);
        }
    }

    private void setBlocks() {
        Location blueGoal = new Location(getServer().getWorld(WORLDNAME), BLUEGOALCOORDS[0], BLUEGOALCOORDS[1], BLUEGOALCOORDS[2]);
        Location redGoal = new Location(getServer().getWorld(WORLDNAME), REDGOALCOORDS[0], REDGOALCOORDS[1], REDGOALCOORDS[2]);
        Location blueStart = new Location(getServer().getWorld(WORLDNAME), BLUESTARTCOORDS[0], BLUESTARTCOORDS[1], BLUESTARTCOORDS[2]);
        Location redStart = new Location(getServer().getWorld(WORLDNAME), REDSTARTCOORDS[0], REDSTARTCOORDS[1], REDSTARTCOORDS[2]);
        Location bluePlayerStart = new Location(getServer().getWorld(WORLDNAME), BLUERESPAWNLOCATION[0], BLUERESPAWNLOCATION[1], BLUERESPAWNLOCATION[2]);
        Location redPlayerStart = new Location(getServer().getWorld(WORLDNAME), REDRESPAWNLOCATION[0], REDRESPAWNLOCATION[1], REDRESPAWNLOCATION[2]);
        blueGoal.getBlock().setType(Material.AIR);
        redGoal.getBlock().setType(Material.AIR);
        blueStart.getBlock().setType(Material.BLUE_WOOL);
        redStart.getBlock().setType(Material.RED_WOOL);
        bluePlayerStart.getBlock().setType(Material.AIR);
        redPlayerStart.getBlock().setType(Material.AIR);
    }

    private void checkPoint(BlockPlaceEvent event, Team team) {
        if (team.equals(redTeam)) {
            if (newGame.getBlueStartLocation().getBlock().equals(Material.BLUE_WOOL)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("The other team has your flag!");
            }
            setBlocks();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Blue team scored a point!");
            }
            team = blueTeam;
        } else {
            if (newGame.getRedStartLocation().getBlock().equals(Material.RED_WOOL)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("The other team has your flag!");
            }
            setBlocks();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("Red team scored a point!");
            }
            team = redTeam;
        }
        if (newGame.addPoint(team)) {
            winGame(team);
        }
    }

    private void winGame(Team team) {
        if (team.equals(redTeam)) {
            for (Player player : Bukkit.getOnlinePlayers()){
                player.sendTitle(ChatColor.RED + "Red team wins!", "yes they did");
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()){
                player.sendTitle(ChatColor.BLUE + "Blue team wins!", "yes they did");
            }
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
        if (this.blueTeam == null || this.redTeam == null) {
            return;
        }
        event.getPlayer().sendMessage(String.valueOf(setupStep));
        if (setupStep < 6) {
            return;
        }
        if (newGame.checkRedFlag(event.getBlock())) {
            if (checkTeam(event.getPlayer()) == 2) {
                event.getPlayer().sendMessage("That's your flag!");
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
                event.setDropItems(false);
                pickedUpFlag(event.getPlayer(), false);
            }
        } else if (newGame.checkBlueFlag(event.getBlock())) {
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
        for (Player playerCurr : Bukkit.getOnlinePlayers()) {
            playerCurr.sendMessage(player.getDisplayName() + "has the flag!");
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
        if (this.blueTeam == null || this.redTeam == null) {
            return;
        }
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
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (this.blueTeam == null || this.redTeam == null) {
            return;
        }
        if (redTeam.search(event.getPlayer())) {
            Location spawn = new Location(this.getServer().getWorld(WORLDNAME),REDRESPAWNLOCATION[0], REDRESPAWNLOCATION[1], REDRESPAWNLOCATION[2]);
            event.setRespawnLocation(spawn);
        } else if (blueTeam.search(event.getPlayer())){
            Location spawn = new Location(this.getServer().getWorld(WORLDNAME),BLUERESPAWNLOCATION[0], BLUERESPAWNLOCATION[1], BLUERESPAWNLOCATION[2]);
            event.setRespawnLocation(spawn);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.blueTeam == null || this.redTeam == null) {
            return;
        }
        if (blueTeam.search(event.getPlayer()) || redTeam.search(event.getPlayer())) {
            blueTeam.showBar(event.getPlayer());
            redTeam.showBar(event.getPlayer());
            IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
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
