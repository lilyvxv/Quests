package me.github.lilyvxv.quests.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static me.github.lilyvxv.quests.Quests.miniMessage;
import static me.github.lilyvxv.quests.Quests.questsConfig;

public class PlayerUtil {


    public static void sendPlayerMessage(Player player, Component message) {
        player.sendMessage(miniMessage.deserialize(questsConfig.pluginPrefix).append(message));
    }
}
