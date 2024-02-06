package me.github.lilyvxv.quests.database;

import me.github.lilyvxv.quests.structs.PlayerInfo;
import me.github.lilyvxv.quests.structs.QuestInfo;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.github.lilyvxv.quests.Quests.questsConfig;

public class Converters {

    // QuestInfo converters
    public static Document questInfoToDocument(QuestInfo questInfo) {
        return new Document("questId", questInfo.questId)
                .append("questName", questInfo.questName)
                .append("questProgress", questInfo.questProgress);
    }

    public static QuestInfo questInfoFromDocument(Document document) {
        String questId = document.getString("questId");
        return questsConfig.getQuestById(questId);
    }

    private static List<Document> getQuestsAsDocumentList(List<QuestInfo> quests) {
        if (quests == null) {
            return null;
        }

        return quests.stream()
                .map(Converters::questInfoToDocument)
                .collect(Collectors.toList());
    }

    private static List<QuestInfo> getQuestsFromDocumentList(List<Document> questsList) {
        if (questsList == null) {
            return null;
        }

        return questsList.stream()
                .map(Converters::questInfoFromDocument)
                .collect(Collectors.toList());
    }

    // PlayerInfo converters
    public static Document playerInfoToDocument(PlayerInfo playerInfo) {
        return new Document("playerUUID", playerInfo.playerUUID.toString())
                .append("activeQuests", getQuestsAsDocumentList(playerInfo.activeQuests))
                .append("completedQuests", getQuestsAsDocumentList(playerInfo.completedQuests));
    }

    public static PlayerInfo playerInfoFromDocument(Document document) {
        UUID playerUUID = UUID.fromString(document.getString("playerUUID"));
        List<QuestInfo> activeQuests = getQuestsFromDocumentList(document.getList("activeQuests", Document.class));
        List<QuestInfo> completedQuests = getQuestsFromDocumentList(document.getList("completedQuests", Document.class));

        return new PlayerInfo(playerUUID, activeQuests, completedQuests);
    }
}
