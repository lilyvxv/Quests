package me.github.lilyvxv.quests.database;

import me.github.lilyvxv.quests.api.Database;
import me.github.lilyvxv.quests.structs.PlayerInfo;
import me.github.lilyvxv.quests.structs.QuestInfo;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static me.github.lilyvxv.quests.Quests.*;

public class CachingDatabase implements Database {

    private final MongoDatabase mongoDatabase;

    public CachingDatabase(String connectionUri, String databaseName, String tableName) {
        this.mongoDatabase = new MongoDatabase(connectionUri, databaseName, tableName);
    }

    public void register () {

    }

    public void createPlayerRecord(Player player) {
        // Create a new blank player record using their UUID
        UUID playerUUID = player.getUniqueId();
        PlayerInfo playerInfo = new PlayerInfo(playerUUID, new ArrayList<>(), new ArrayList<>());
        playerInfoCache.put(playerUUID, playerInfo);
    }

    public void loadPlayerRecord(Player player, PlayerInfo playerInfo) {
        // Insert an existing player record using their UUID
        UUID playerUUID = player.getUniqueId();
        playerInfoCache.put(playerUUID, playerInfo);
    }

    public PlayerInfo fetchPlayerRecord(Player player) {
        // Fetch player's record using their UUID
        UUID playerUUID = player.getUniqueId();
        PlayerInfo playerInfo = playerInfoCache.get(playerUUID);
        if (playerInfo.activeQuests != null) {
            playerInfo.activeQuests = new ArrayList<>(playerInfo.activeQuests);
        }
        if (playerInfo.completedQuests != null) {
            playerInfo.completedQuests = new ArrayList<>(playerInfo.completedQuests);
        }
        return playerInfo;
    }

    public void addQuestToPlayer(Player player, QuestInfo quest) {
        // Fetch player's record using their UUID
        if (playerHasQuestEnabled(player, quest)) {
            return;
        }

        PlayerInfo playerInfo = fetchPlayerRecord(player);

        if (playerInfo != null) {
            List<QuestInfo> currentQuests = playerInfo.activeQuests;

            if (currentQuests == null) {
                currentQuests = new ArrayList<>();
            }

            currentQuests.add(quest);
            playerInfo.activeQuests = currentQuests;
        }
    }

    public boolean playerHasQuestEnabled(Player player, QuestInfo quest) {
        // Check if a player has a specific quest enabled
        PlayerInfo playerInfo = fetchPlayerRecord(player);

        if (playerInfo.activeQuests != null) {
            for (QuestInfo activeQuest : playerInfo.activeQuests) {
                if (Objects.equals(activeQuest.questId, quest.questId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean playerHasCompletedQuest(Player player, QuestInfo quest) {
        // Check if a player has completed a specific quest
        PlayerInfo playerInfo = fetchPlayerRecord(player);

        if (playerInfo.activeQuests != null) {
            for (QuestInfo completedQuest : playerInfo.completedQuests) {
                if (Objects.equals(completedQuest.questId, quest.questId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateQuestProgress(Player player, QuestInfo quest, double progress) {
        // Update the progress of a specific quest for a player
        PlayerInfo playerInfo = fetchPlayerRecord(player);

        if (playerInfo != null && playerInfo.activeQuests != null) {
            for (QuestInfo activeQuest : playerInfo.activeQuests) {
                if (Objects.equals(activeQuest.questId, quest.questId)) {
                    activeQuest.questProgress += progress;

                    if (activeQuest.questProgress >= 100) {
                        completeQuest(player, activeQuest);
                        activeQuest.handler.issueReward(player);
                    } else {
                        if (!cacheNext.contains(player) && !quest.bypassesCache) {
                            cacheNext.add(player);
                        }
                    }

                    if (quest.bypassesCache) {
                        cachePlayer(player);
                    }

                    return;
                }
            }
        }
    }

    public void completeQuest(Player player, QuestInfo quest) {
        // Move a specific quest from the activeQuest's ArrayList to the completedQuest's one
        PlayerInfo playerInfo = fetchPlayerRecord(player);

        if (playerInfo != null) {
            List<QuestInfo> activeQuests = playerInfo.activeQuests;
            List<QuestInfo> completedQuests = playerInfo.completedQuests;

            if (activeQuests != null && activeQuests.contains(quest)) {
                activeQuests.remove(quest);

                if (completedQuests == null) {
                    completedQuests = new ArrayList<>();
                }

                completedQuests.add(quest);

                playerInfo.activeQuests = activeQuests;
                playerInfo.completedQuests = completedQuests;

                cachePlayer(player);
            }
        }
    }

    public QuestInfo getQuestFromPlayer(Player player, QuestInfo quest) {
        // Get a specific quest for a player based on the QuestInfo object
        PlayerInfo playerInfo = fetchPlayerRecord(player);

        if (playerInfo != null && playerInfo.activeQuests != null) {
            for (QuestInfo activeQuest : playerInfo.activeQuests) {
                if (Objects.equals(activeQuest.questId, quest.questId)) {
                    return activeQuest;
                }
            }
        }

        return null;
    }

    public void onPlayerJoin(Player player) {
        try {
            boolean playerExists = mongoDatabase.playerHasRecordAsync(player)
                    .get();

            if (!playerExists) {
                mongoDatabase.createPlayerRecordAsync(player);
                this.createPlayerRecord(player);
            } else {
                loadPlayerRecord(player, mongoDatabase.fetchPlayerRecordAsync(player).get());
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.severe("Error with MongoDB: " + e);
        }
    }

    public void cachePlayer(Player player) {
        PlayerInfo playerInfo = this.fetchPlayerRecord(player);
        mongoDatabase.updatePlayerRecordAsync(playerInfo);
    }

    public void cachePlayers(Collection<Player> players) {
        List<PlayerInfo> playerInfoList = players
                .stream()
                .map(this::fetchPlayerRecord).toList();

        List<CompletableFuture<Void>> updateFutures = new ArrayList<>();

        for (PlayerInfo playerInfo : playerInfoList) {
            CompletableFuture<Void> updateFuture = mongoDatabase.updatePlayerRecordAsync(playerInfo);
            updateFutures.add(updateFuture);
        }

        CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0])).join();
    }
}
