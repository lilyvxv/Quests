package me.github.lilyvxv.quests.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static me.github.lilyvxv.quests.Quests.MINI_MESSAGE;

public class PlayerUtil {


    public static void sendPlayerMessage(Player player, Component message) {
        player.sendMessage(MINI_MESSAGE.deserialize(ConfigUtil.PLUGIN_PREFIX).append(message));
    }
}
