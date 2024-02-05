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

    private List<Document> getQuestsAsDocumentList(List<QuestInfo> quests) {
        if (quests == null) {
            return null;
        }

        return quests.stream()
                .map(QuestInfo::toDocument)
                .toList();
    }

    public Document toDocument() {
        return new Document("playerUUID", playerUUID.toString())
                .append("activeQuests", getQuestsAsDocumentList(activeQuests))
                .append("completedQuests", getQuestsAsDocumentList(completedQuests));
    }

    private static List<QuestInfo> getQuestsFromDocumentList(List<Document> questsList) {
        if (questsList == null) {
            return null;
        }

        return questsList.stream()
                .map(QuestInfo::fromDocument)
                .toList();
    }

    public static PlayerInfo fromDocument(Document document) {
        UUID playerUUID = UUID.fromString(document.getString("playerUUID"));
        List<QuestInfo> activeQuests = getQuestsFromDocumentList(document.getList("activeQuests", Document.class));
        List<QuestInfo> completedQuests = getQuestsFromDocumentList(document.getList("completedQuests", Document.class));

        return new PlayerInfo(playerUUID, activeQuests, completedQuests);
    }
}
