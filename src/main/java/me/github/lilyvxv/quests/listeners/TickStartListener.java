package me.github.lilyvxv.quests.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.github.lilyvxv.quests.Quests.*;

public class TickStartListener implements Listener {

    private static int ticks = 0;

    @EventHandler(priority = EventPriority.MONITOR)
    private void onTickStart(ServerTickStartEvent event) {
        if (ticks == CONFIG.saveInterval) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                if (cachingDatabase.cacheNext.size() > 0) {
                    cachingDatabase.cachePlayers(cachingDatabase.cacheNext);
                    cachingDatabase.cacheNext.clear();
                }
            });

            ticks = 0;
        } else {
            ticks++;
        }
    }
}
