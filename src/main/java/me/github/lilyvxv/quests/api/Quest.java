package me.github.lilyvxv.quests.api;

import org.bukkit.entity.Player;

public interface Quest {

    void register();

    void issueReward(Player player);
}
