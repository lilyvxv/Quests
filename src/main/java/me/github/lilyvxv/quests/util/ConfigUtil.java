package me.github.lilyvxv.quests.util;

import me.github.lilyvxv.quests.Quests;
import me.github.lilyvxv.quests.database.Backend;
import me.github.lilyvxv.quests.database.CachingDatabase;
import me.github.lilyvxv.quests.structs.QuestInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.github.lilyvxv.quests.Quests.*;

public class ConfigUtil {

    private final FileConfiguration config;

    public Map<String, Component> pluginMessages = new HashMap<>();
    public String pluginPrefix;
    public List<QuestInfo> availableQuests = new ArrayList<>();

    public int saveInterval;

    public ConfigUtil(FileConfiguration config) {
        this.config = config;
        init();
    }

    public void init() {
        plugin.saveDefaultConfig();
        loadConfig(plugin.getConfig());
    }

    public void loadConfig(Configuration configuration) {
        // Load basic config options from the config.yml file
        pluginPrefix = configuration.getString("prefix", "[Quests] ");

        loadDatabaseConnection(configuration);
        loadMessages(configuration);
    }

    public void loadDatabaseConnection(Configuration configuration) {
        ConfigurationSection databaseSection = configuration.getConfigurationSection("database");
        assert databaseSection != null;

        saveInterval = databaseSection.getInt("save_interval");
        Backend backend = Backend.valueOf(databaseSection.getString("backend"));

        if (backend == Backend.MONGODB) {
            String connectionUri = databaseSection.getString("connection_uri");
            String databaseName = databaseSection.getString("database_name");
            String tableName = databaseSection.getString("table_name");
            Quests.database = new CachingDatabase(connectionUri, databaseName, tableName);
        } else if (backend == Backend.EXTERNAL) {

        }
    }


//    private static void loadQuests(Configuration configuration) {
//        // Load the 'quests' section out of the configuration
//        ConfigurationSection questsSection = configuration.getConfigurationSection("quests");
//        if (questsSection == null) {
//            return;
//        }
//
//        // Iterate over all the elements in the quests section
//        for (String questKey : questsSection.getKeys(false)) {
//            // Load the data of the iterated section
//            ConfigurationSection questSection = questsSection.getConfigurationSection(questKey);
//            if (questSection == null) {
//                continue;
//            }
//
//            // Create an instance of QuestInfo with the loaded configuration and add it to the AVAILABLE_QUESTS list
//            QuestInfo questInfo = createQuestInfo(questSection);
//            LOGGER.info(questInfo.toString());
//            AVAILABLE_QUESTS.add(questInfo);
//        }
//    }
//
//    private static QuestInfo createQuestInfo(ConfigurationSection questSection) {
//        String questId = questSection.getString("quest_id");
//        String questName = questSection.getString("name");
//        double questProgress = 0.0f;
//
//        // Grab the goal section configuration options
//        ConfigurationSection goalSection = questSection.getConfigurationSection("goal");
//        QuestType goalType = QuestType.valueOf(goalSection.getString("type"));
//        String materialString = goalSection.getString("material");
//        Material goalMaterial = (materialString != null && !materialString.isEmpty()) ? Material.valueOf(materialString) : null;
//        int goalQuantity = goalSection.getInt("quantity");
//
//        // Grab the menu section configuration options
//        ConfigurationSection menuSection = questSection.getConfigurationSection("menu");
//        Component itemName = MINI_MESSAGE.deserialize(menuSection.getString("item_name"))
//                .decoration(TextDecoration.ITALIC, false);
//        Material itemMaterial = Material.valueOf(menuSection.getString("item_material"));
//        List<String> loreLines = menuSection.getStringList("item_lore");
//
//        // Convert lore lines to Adventure Components
//        List<Component> menuLoreLines = new ArrayList<>();
//        for (String loreLine : loreLines) {
//            menuLoreLines.add(MINI_MESSAGE.deserialize(loreLine)
//                    .decoration(TextDecoration.ITALIC, false));
//        }
//
//        return new QuestInfo(questId, questName, questProgress, goalType, goalMaterial, goalQuantity, itemName, itemMaterial, menuLoreLines);
//    }

    private void loadMessages(Configuration configuration) {
        ConfigurationSection messagesSection = configuration.getConfigurationSection("messages");
        if (messagesSection == null) {
            return;
        }

        for (String key : messagesSection.getKeys(false)) {
            String path = "messages." + key;
            Component message = miniMessage.deserialize(configuration.getString(path, ""));
            pluginMessages.put(key, message);
        }
    }

    public Component getMessage(String key, @NotNull TagResolver... tagResolvers) {
        return miniMessage.deserialize(config.getString("messages." + key), tagResolvers);
    }

    public QuestInfo getQuestById(String questId) {
        for (QuestInfo quest : availableQuests) {
            if (Objects.equals(quest.questId, questId)) {
                return quest;
            }
        }
        return null;
    }

    public void addQuest(QuestInfo questInfo) {
        availableQuests.add(questInfo);
    }
}

//#quests:
//        #  1:
//        #    name: "Example Travel Quest"
//        #    quest_id: "example_travel_quest"
//        #    goal:
//        #      type: "TRAVEL"
//        #      material: ""
//        #      quantity: 100
//        #    menu:
//        #      item_name: "<color:#f266ff>Example Travel Quest</color>"
//        #      item_material: "COMPASS"
//        #      item_lore:
//        #        - "<color:#9c63ff>This quest requires you to travel <color:#bfbfbf>100m</color>!</color>"
//        #        - "<color:#bfbfbf>Reward:</color> <color:#00ff51>$100</color>"
//        #        - ""
//        #  2:
//        #    name: "Example Breaking Quest"
//        #    quest_id: "example_breaking_quest"
//        #    goal:
//        #      type: "BREAKING"
//        #      material: ""
//        #      quantity: 100
//        #    menu:
//        #      item_name: "<color:#f266ff>Example Breaking Quest</color>"
//        #      item_material: "DIAMOND_PICKAXE"
//        #      item_lore:
//        #        - "<color:#9c63ff>This quest requires you to break <color:#bfbfbf>100 blocks</color>!</color>"
//        #        - "<color:#bfbfbf>Reward:</color> <color:#00ff51>$500</color>"
//        #        - ""