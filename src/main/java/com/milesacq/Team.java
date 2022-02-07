package com.milesacq;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Team {
    private Player[] members;

    public Team(int numMembers) {
        this.members = new Player[numMembers];
    }

    public int getLength() {
        return this.members.length;
    }

    public boolean search(Player player) {
        for (Player member : this.members) {
            if (member != null) {
                if (member.equals(player)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean addPlayer(Player player) {
        if (members[0] == null) {
            members[0] = player;
            return true;
        } else {
            for (int i = 0; i < this.members.length; i++) {
                if (members[i] == null) {
                    members[i] = player;
                    return true;
                }
            }
        }
        return false;
    }

    public String toString() {
        String returnMe = "";
        for (int i = 0; i < this.members.length; i++) {
            if (this.members[i] != null) {
                returnMe += (this.members[i].getDisplayName() + ", ");
            }
        }
        return returnMe;
    }
}
