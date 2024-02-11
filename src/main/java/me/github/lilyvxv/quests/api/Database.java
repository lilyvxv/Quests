package me.github.lilyvxv.quests.api;

import me.github.lilyvxv.quests.structs.PlayerInfo;
import me.github.lilyvxv.quests.structs.QuestInfo;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface Database {

    /**
     * A field that stores the Database cache
     */
    Map<UUID, PlayerInfo> playerInfoCache = new ConcurrentHashMap<>();

    /**
     * A field that stores a list of players that will be cached on the next cycle
     */
    List<Player> cacheNext = new ArrayList<>();

    /**
     * Method that will be called when the PlayerJoin event is fired
     * @param player The event Player
     */
    void onPlayerJoin(Player player);

    /**
     * Method that will force cache a Player
     * @param player The player to forcefully cache
     */
    void cachePlayer(Player player);

    /**
     * Method that will force cache a Collection of Player's
     * @param players The Collection of Players to cache
     */
    void cachePlayers(Collection<Player> players);

    /**
     * Method that will fetch a Player from the Database
     * @param player The Player to fetch the Record of from the Database
     * @return A PlayerInfo instance containing Quest data about the requested Player
     */
    PlayerInfo fetchPlayerRecord(Player player);

    /**
     * A method to update the progress of a Quest on a Player
     * @param player The Player to update the Quest progress of
     * @param quest The target Quest to update the progress of
     * @param progress The progress to set for the selected Quest
     */
    void updateQuestProgress(Player player, QuestInfo quest, double progress);

    /**
     * A method to mark a Quest as completed in the players Database record
     * @param player The Player to set the completed state for a Quest on
     * @param quest The target Quest to set the completed state for
     */
    void completeQuest(Player player, QuestInfo quest);

    /**
     * A method to check if a Player has a Quest completed
     * @param player The Player to check the status of a Quest for
     * @param quest The Quest to check the completed status of
     * @return The result of the query, true if a Quest is completed, false if it is not
     */
    boolean playerHasCompletedQuest(Player player, QuestInfo quest);

    /**
     * A method to fetch the data of a Quest from a Player
     * @param player The Player to fetch a quest from
     * @param quest The Quest to fetch from the Player
     * @return The data of the Quest that was requested from the Database
     */
    QuestInfo getQuestFromPlayer(Player player, QuestInfo quest);

    /**
     * A method to add a Quest to a Player in the Database
     * @param player The Player to add a Quest to
     * @param quest The Quest that is being added to the Player
     */
    void addQuestToPlayer(Player player, QuestInfo quest);

    /**
     * A method to check if a Player has a Quest enabled
     * @param player The Player to set the enabled state for a Quest on
     * @param quest The target Quest to set the enabled state for
     * @return The result of the query, true if a Quest is enabled, false if it is not
     */
    boolean playerHasQuestEnabled(Player player, QuestInfo quest);
}
