package com.milesacq.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.bukkit.command.CommandSender;

import com.milesacq.GameSingleton;
import com.milesacq.Team;
import com.milesacq.enums.CoordinateType;

public class SetupCommand extends Command {
    String configName;

    private void handleSetupCommand(String[] args, CommandSender sender) {
        if (args.length == 1) {
            sender.sendMessage("Please specify config name");
        } else {
            configName = args[2];
            GameSingleton.setConfigString(configName);
            if (args[1].equalsIgnoreCase("create")) {
                createConfig(sender);
            } else if (args[1].equalsIgnoreCase("read")) {
                readFromFile(sender);
            }
        }
    }

    private void createConfig(CommandSender sender) {
        GameSingleton.setSetupStep(0);
        sender.sendMessage("Place block at blue flag start");
        try {
            File configDir = new File("./config/ctf");
            if (!configDir.exists()){
                configDir.mkdirs();
            }
            File myObj = new File("./config/ctf/" + configName + ".txt");
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
            File myObj = new File("./config/ctf/" + configName + ".txt");
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            for (int i = 0; i < 3; i++) {
                GameSingleton.getTeam("Blue").setCoords(CoordinateType.STARTCOORDS, i, Double.parseDouble(data));
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                GameSingleton.getTeam("Red").setCoords(CoordinateType.STARTCOORDS, i, Double.parseDouble(data));
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                GameSingleton.getTeam("Blue").setCoords(CoordinateType.GOALCOORDS, i, Double.parseDouble(data));
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                GameSingleton.getTeam("Red").setCoords(CoordinateType.GOALCOORDS, i, Double.parseDouble(data));
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                GameSingleton.getTeam("Red").setCoords(CoordinateType.RESPAWNCOORDS, i, Double.parseDouble(data));
                data = myReader.nextLine();
            }
            for (int i = 0; i < 3; i++) {
                GameSingleton.getTeam("Blue").setCoords(CoordinateType.RESPAWNCOORDS, i, Double.parseDouble(data));
                if (myReader.hasNextLine()) {
                    data = myReader.nextLine();
                }
            }
            for (Team team : GameSingleton.getTeams()) {
                team.setBlocks();
            }
            GameSingleton.setSetupStep(6);
            myReader.close();
        } catch (FileNotFoundException e) {
            sender.sendMessage("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
       handleSetupCommand(args, sender);
    }
}
