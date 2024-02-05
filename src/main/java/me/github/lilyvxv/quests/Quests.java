package me.github.lilyvxv.quests;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.github.lilyvxv.quests.api.QuestsAPI;
import me.github.lilyvxv.quests.commands.QuestsCommand;
import me.github.lilyvxv.quests.database.CachingDatabase;
import me.github.lilyvxv.quests.listeners.PlayerJoinListener;
import me.github.lilyvxv.quests.listeners.PlayerLeaveListener;
import me.github.lilyvxv.quests.listeners.TickStartListener;
import me.github.lilyvxv.quests.util.ConfigUtil;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public final class Quests extends ExtendedJavaPlugin implements QuestsAPI {

    public static ExtendedJavaPlugin plugin;
    public static Logger LOGGER;
    public static MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static ConfigUtil CONFIG;
    public static QuestsAPI QUESTS_API;
    public static CachingDatabase cachingDatabase;

    @Override
    public void load() {
        QUESTS_API = this;

        CommandAPIBukkitConfig commandAPIConfig = new CommandAPIBukkitConfig(getPlugin(this.getClass()));
        CommandAPI.onLoad(commandAPIConfig);
    }

    @Override
    public void enable() {
        CommandAPI.onEnable();

        plugin = this;

        LOGGER = getLogger();
        FileConfiguration pluginConfig = getConfig();
        LOGGER.info(pluginConfig.toString());
        CONFIG = new ConfigUtil(pluginConfig);

        cachingDatabase = new CachingDatabase(CONFIG.backend);

        // Register the plugins' events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), plugin);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), plugin);
        getServer().getPluginManager().registerEvents(new TickStartListener(), plugin);

        // Register the plugins' commands
        new QuestsCommand().register();
    }

    @Override
    public void disable() {
        CommandAPI.onDisable();
        cachingDatabase.onServerStop();
    }
}
