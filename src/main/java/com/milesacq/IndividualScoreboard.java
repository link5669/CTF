package com.milesacq;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class IndividualScoreboard {
    public IndividualScoreboard(Team blueTeam, Team redTeam) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard redBoard = manager.getNewScoreboard();
        Objective obj = redBoard.registerNewObjective("Map: ", "testMap",
                ChatColor.translateAlternateColorCodes('&', "&a&l<< &2 CAPTURE THE FLAG &a&l>>"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = obj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        score.setScore(4);

        Score score5 = obj.getScore(ChatColor.RED + "Your team: RED");
        score5.setScore(3);

        Score score2 = obj.getScore(ChatColor.AQUA + "Online Players: " + ChatColor.DARK_AQUA + Bukkit.getOnlinePlayers().size());
        score2.setScore(2);

        Score score3 = obj.getScore(ChatColor.GRAY + "Deaths: " + redTeam.getDeaths() + "/100");
        score3.setScore(1);

        Score score4 = obj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        score4.setScore(0);

        for (Player online : Bukkit.getOnlinePlayers()) {
            System.out.println(online.toString());
            if (redTeam.search(online)) {
                online.setScoreboard(redBoard);
            }
        }

        Scoreboard blueBoard = manager.getNewScoreboard();
        Objective blueObj = blueBoard.registerNewObjective("Map: ", "testMap",
                ChatColor.translateAlternateColorCodes('&', "&a&l<< &2& CAPTURE THE FLAG &a&l>>"));
        blueObj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score blueScore = blueObj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        blueScore.setScore(4);

        Score blueScore5 = obj.getScore(ChatColor.BLUE + "Your team: BLUE");
        blueScore5.setScore(3);

        Score blueScore2 = blueObj.getScore(ChatColor.AQUA + "Online Players: " + ChatColor.DARK_AQUA + Bukkit.getOnlinePlayers().size());
        blueScore2.setScore(2);

        Score blueScore3 = blueObj.getScore(ChatColor.GRAY + "Deaths: " + blueTeam.getDeaths() + "/100");
        blueScore3.setScore(1);

        Score blueScore4 = blueObj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        blueScore4.setScore(0);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (blueTeam.search(online)) {
                online.setScoreboard(blueBoard);
            }
        }
    }
}
