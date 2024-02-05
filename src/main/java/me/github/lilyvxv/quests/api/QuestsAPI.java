package me.github.lilyvxv.quests.api;

import me.github.lilyvxv.quests.structs.PlayerInfo;
import me.github.lilyvxv.quests.structs.QuestInfo;
import me.github.lilyvxv.quests.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static me.github.lilyvxv.quests.Quests.*;

public interface QuestsAPI {

    default void addQuest(Quest quest) {
        quest.register();
    }

    default void registerQuest(QuestInfo questInfo) {
        CONFIG.addQuest(questInfo);
    }

    default void registerDatabase(Database database) {
        CONFIG.externalDatabase = database;
    }

    default void sendPlayerMessage(Player player, Component message) {
        PlayerUtil.sendPlayerMessage(player, message);
    }

    default PlayerInfo fetchPlayer(Player player) {
        return cachingDatabase.fetchPlayerRecord(player);
    }

    default boolean playerHasQuestEnabled(Player player, QuestInfo questInfo) {
        return cachingDatabase.playerHasQuestEnabled(player, questInfo);
    }

    default void updateQuestProgress(Player player, QuestInfo questInfo, double progress) {
        cachingDatabase.updateQuestProgress(player, questInfo, progress);
    }

    default QuestInfo getQuestFromPlayer(Player player, QuestInfo questInfo) {
        return cachingDatabase.getQuestFromPlayer(player, questInfo);
    }

    default void completeQuest(Player player, QuestInfo questInfo) {
        cachingDatabase.completeQuest(player, questInfo);
    }
}
