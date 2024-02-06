package me.github.lilyvxv.quests;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.github.lilyvxv.quests.api.Database;
import me.github.lilyvxv.quests.api.QuestsAPI;
import me.github.lilyvxv.quests.commands.QuestsCommand;
import me.github.lilyvxv.quests.listeners.PlayerJoinListener;
import me.github.lilyvxv.quests.listeners.PlayerLeaveListener;
import me.github.lilyvxv.quests.util.ConfigUtil;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

public final class Quests extends ExtendedJavaPlugin implements QuestsAPI {

    public static ExtendedJavaPlugin plugin;
    public static ConfigUtil questsConfig;
    public static Logger logger;
    public static MiniMessage miniMessage = MiniMessage.miniMessage();
    public static QuestsAPI questsAPI;
    public static Database database;

    @Override
    public void load() {
        questsAPI = this;

        CommandAPIBukkitConfig commandAPIConfig = new CommandAPIBukkitConfig(getPlugin(this.getClass()));
        CommandAPI.onLoad(commandAPIConfig);
    }

    @Override
    public void enable() {
        CommandAPI.onEnable();

        // Set some required variables
        plugin = this;
        logger = getLogger();

        FileConfiguration pluginConfig = getConfig();
        questsConfig = new ConfigUtil(pluginConfig);

        // Register the plugins' events
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), plugin);
        pluginManager.registerEvents(new PlayerLeaveListener(), plugin);

        // Register the plugins' commands
        new QuestsCommand().register();

        // Start the loop that caches the database on the configured interval
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (database.cacheNext.size() > 0) {
                database.cachePlayers(database.cacheNext);
                database.cacheNext.clear();
            }
        }, 0, questsConfig.saveInterval);
    }

    @Override
    public void disable() {
        CommandAPI.onDisable();
        database.cachePlayers(database.cacheNext);
    }
}
