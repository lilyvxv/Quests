package me.github.lilyvxv.quests.api;

import me.github.lilyvxv.quests.structs.PlayerInfo;
import me.github.lilyvxv.quests.structs.QuestInfo;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public interface Database {

    void register();

    // Player cache methods and fields
    Map<UUID, PlayerInfo> playerInfoCache = new ConcurrentHashMap<>();

    List<Player> cacheNext = new ArrayList<>();

    // Database methods
    void onPlayerJoin(Player player);

    void cachePlayer(Player player);

    void cachePlayers(Collection<Player> players);

    void createPlayerRecord(Player player);

    PlayerInfo fetchPlayerRecord(Player player);

    void updateQuestProgress(Player player, QuestInfo quest, double progress);

    void completeQuest(Player player, QuestInfo quest);

    boolean playerHasCompletedQuest(Player player, QuestInfo quest);

    QuestInfo getQuestFromPlayer(Player player, QuestInfo quest);

    void addQuestToPlayer(Player player, QuestInfo quest);

    boolean playerHasQuestEnabled(Player player, QuestInfo quest);
}
