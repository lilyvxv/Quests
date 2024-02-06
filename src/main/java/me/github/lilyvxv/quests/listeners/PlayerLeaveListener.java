package me.github.lilyvxv.quests.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.github.lilyvxv.quests.Quests.database;


public class PlayerLeaveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        // Save a players data when they leave
        database.cachePlayer(event.getPlayer());
    }
}
