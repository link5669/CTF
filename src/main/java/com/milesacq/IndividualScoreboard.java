package com.milesacq;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class IndividualScoreboard {
    public IndividualScoreboard(ArrayList<Team> teams) {
        for (Team team : teams) {
            setupScoreboard(team, team.getName(), team.getChatColor());
        }
    }

    private void setupScoreboard(Team team, String color, ChatColor chatColor) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard redBoard = manager.getNewScoreboard();
        Objective obj = redBoard.registerNewObjective("Map: ", Criteria.DUMMY,
                ChatColor.translateAlternateColorCodes('&', "&a&l<< &2 CAPTURE THE FLAG &a&l>>"));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        score.setScore(4);
        Score score5 = obj.getScore(chatColor + "Your team: " + color);
        score5.setScore(3);
        Score score2 = obj.getScore(ChatColor.AQUA + "Online Players: " + ChatColor.DARK_AQUA + Bukkit.getOnlinePlayers().size());
        score2.setScore(2);
        Score score3 = obj.getScore(ChatColor.GRAY + "Deaths: " + team.getDeaths() + "/100");
        score3.setScore(1);
        Score score4 = obj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=");
        score4.setScore(0);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (team.search(online.getName())) {
                online.setScoreboard(redBoard);
            }
        }
    }
}