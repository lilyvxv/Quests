package me.github.lilyvxv.quests.api;

import me.github.lilyvxv.quests.structs.PlayerInfo;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface Database {

    void register();

    CompletableFuture<Void> createPlayerRecord(Player player);

    CompletableFuture<PlayerInfo> fetchPlayerRecordAsync(Player player);

    CompletableFuture<Void> updatePlayerRecordAsync(PlayerInfo playerInfo);

    CompletableFuture<Boolean> playerHasRecordAsync(Player player);
}
