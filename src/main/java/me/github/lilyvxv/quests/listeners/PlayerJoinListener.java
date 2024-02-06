package me.github.lilyvxv.quests.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.github.lilyvxv.quests.Quests.*;


public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        // Check if the player has a record in the database
        database.onPlayerJoin(event.getPlayer());
    }
}
