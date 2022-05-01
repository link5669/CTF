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
            } else if (args[0].equalsIgnoreCase("setup")) {
                setupStep = 0;
                sender.sendMessage("Place block at blue flag start");
            } else if (args[0].equalsIgnoreCase("status")) {
                sender.sendMessage("Blue flag start: " + BLUESTARTCOORDS[0] + ", " + BLUESTARTCOORDS[1] + ", " + BLUESTARTCOORDS[2]);
                sender.sendMessage("Red flag start: " + REDSTARTCOORDS[0] + ", " + REDSTARTCOORDS[1] + ", " + REDSTARTCOORDS[2]);
                sender.sendMessage("Blue flag goal: " + BLUEGOALCOORDS[0] + ", " + BLUEGOALCOORDS[1] + ", " + BLUEGOALCOORDS[2]);
                sender.sendMessage("Red flag goal: " + REDGOALCOORDS[0] + ", " + REDGOALCOORDS[1] + ", " + REDGOALCOORDS[2]);
            }
        }
        return true;
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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        String placedBlock = event.getBlock().getBlockData().toString();
        Location placedBlockLocation = event.getBlock().getLocation();
        if (setupStep != 6) {
            switch (setupStep) {
                case 0:
                    BLUESTARTCOORDS[0] = placedBlockLocation.getX() + .5;
                    BLUESTARTCOORDS[1] = placedBlockLocation.getY() + .5;
                    BLUESTARTCOORDS[2] = placedBlockLocation.getZ() + .5;
                    event.getPlayer().sendMessage("Place block at red flag start");
                    setupStep++;
                    return;
                case 1:
                    REDSTARTCOORDS[0] = placedBlockLocation.getX() + .5;
                    REDSTARTCOORDS[1] = placedBlockLocation.getY() + .5;
                    REDSTARTCOORDS[2] = placedBlockLocation.getZ() + .5;
                    event.getPlayer().sendMessage("Place block at blue flag goal (opposite color!)");
                    setupStep++;
                    return;
                case 2:
                    BLUEGOALCOORDS[0] = placedBlockLocation.getX() + .5;
                    BLUEGOALCOORDS[1] = placedBlockLocation.getY() + .5;
                    BLUEGOALCOORDS[2] = placedBlockLocation.getZ() + .5;
                    event.getPlayer().sendMessage("Place block at red flag goal (opposite color!)");
                    setupStep++;
                    return;
                case 3:
                    REDGOALCOORDS[0] = placedBlockLocation.getX() + .5;
                    REDGOALCOORDS[1] = placedBlockLocation.getY() + .5;
                    REDGOALCOORDS[2] = placedBlockLocation.getZ() + .5;
                    event.getPlayer().sendMessage("Place block at red team spawn");
                    setupStep++;
                    return;
                case 4:
                    REDRESPAWNLOCATION[0] = placedBlockLocation.getX() + .5;
                    REDRESPAWNLOCATION[1] = placedBlockLocation.getY() + .5;
                    REDRESPAWNLOCATION[2] = placedBlockLocation.getZ() + .5;
                    event.getPlayer().sendMessage("Place block at blue team spawn");
                    setupStep++;
                    return;
                case 5:
                    BLUERESPAWNLOCATION[0] = placedBlockLocation.getX() + .5;
                    BLUERESPAWNLOCATION[1] = placedBlockLocation.getY() + .5;
                    BLUERESPAWNLOCATION[2] = placedBlockLocation.getZ() + .5;
                    event.getPlayer().sendMessage("Setup complete!");
                    setupStep++;
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
        blueGoal.getBlock().setType(Material.AIR);
        redGoal.getBlock().setType(Material.AIR);
        blueStart.getBlock().setType(Material.BLUE_WOOL);
        redStart.getBlock().setType(Material.RED_WOOL);
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
        if (blueTeam.search(event.getPlayer()) || redTeam.search(event.getPlayer())) {
            blueTeam.showBar(event.getPlayer());
            redTeam.showBar(event.getPlayer());
            IndividualScoreboard board = new IndividualScoreboard(blueTeam, redTeam);
        }
    }

    private boolean blockEquals(Location one, Location two) {
        if (one.getX() == two.getX()) {
            if (one.getY() == two.getY()) {
                if (one.getZ() == two.getZ()) {
                    return true;
                }
            }
        }
        return false;
    }
}
