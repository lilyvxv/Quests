package me.github.lilyvxv.quests.structs;

import me.github.lilyvxv.quests.api.Quest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.github.lilyvxv.quests.Quests.questsConfig;
import static me.github.lilyvxv.quests.Quests.miniMessage;
import static me.github.lilyvxv.quests.util.ProgressBarUtil.generateProgressBar;

public class QuestInfo {

    // Generic
    public final String questId;
    public final String questName;
    public boolean bypassesCache;
    public double questProgress;

    // Menu
    public final Component menuItemName;
    public final Material menuMaterial;
    public final List<Component> menuLoreLines;

    // Handler
    public final Quest handler;

    public QuestInfo(String questId, String questName, boolean bypassesCache, double questProgress, Component menuItemName, Material menuMaterial, List<Component> menuLoreLines, Quest handler) {
        this.questId = questId;
        this.questName = questName;
        this.bypassesCache = bypassesCache;
        this.questProgress = questProgress;

        this.menuItemName = menuItemName;
        this.menuMaterial = menuMaterial;
        this.menuLoreLines = menuLoreLines;

        this.handler = handler;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.menuMaterial, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(menuItemName);

        Component progressBar = miniMessage.deserialize(String.format("<reset><blue>%s</blue></reset>", generateProgressBar((int) questProgress)))
                .decoration(TextDecoration.ITALIC, false);
        List<Component> combinedLore = new ArrayList<>(menuLoreLines);
        combinedLore.add(progressBar);

        meta.lore(combinedLore);
        item.setItemMeta(meta);
        return item;
    }
}
