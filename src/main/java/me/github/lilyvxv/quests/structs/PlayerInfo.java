package me.github.lilyvxv.quests.structs;

import org.bson.Document;

import java.util.List;
import java.util.UUID;

public class PlayerInfo {

    public final UUID playerUUID;
    public List<QuestInfo> activeQuests;
    public List<QuestInfo> completedQuests;

    public PlayerInfo(UUID playerUUID, List<QuestInfo> activeQuests, List<QuestInfo> completedQuests) {
        this.playerUUID = playerUUID;
        this.activeQuests = activeQuests;
        this.completedQuests = completedQuests;
    }
}
