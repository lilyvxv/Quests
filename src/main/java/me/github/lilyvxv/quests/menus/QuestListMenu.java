package me.github.lilyvxv.quests.menus;

import me.github.lilyvxv.quests.structs.QuestInfo;
import me.github.lilyvxv.quests.util.ConfigUtil;
import me.github.lilyvxv.quests.util.PlayerUtil;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.github.lilyvxv.quests.Quests.*;

public class QuestListMenu extends Gui {

    private static final MenuScheme BORDER = new MenuScheme()
            .mask("111111111")
            .mask("100000001")
            .mask("100000001")
            .mask("100000001")
            .mask("100000001")
            .mask("111111111");

    private static final MenuScheme QUESTS = new MenuScheme()
            .mask("000000000")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("011111110")
            .mask("000000000");

    public QuestListMenu(Player player) {
        super(player, 6, "&8Quests");
    }

    @Override
    public void redraw() {
        // Fill the border slots of the menu
        MenuPopulator populator = BORDER.newPopulator(this);

        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = borderItem.getItemMeta();
        borderMeta.displayName(miniMessage.deserialize(""));
        borderMeta.setCustomModelData(1);
        borderItem.setItemMeta(borderMeta);

        while (populator.hasSpace()) {
            populator.accept(Item.builder(borderItem).build());
        }

        // Fill the slots with our registered quests
        populator = QUESTS.newPopulator(this);
        Player player = getPlayer();

        for (QuestInfo quest : questsConfig.availableQuests) {
            ItemStack itemStack = quest.toItemStack();

            if (database.playerHasQuestEnabled(player, quest)) {
                itemStack.addUnsafeEnchantment(Enchantment.LUCK, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemStack.setItemMeta(itemMeta);
            }

            populator.accept(ItemStackBuilder.of(itemStack)
                    .build(() -> {
                        if (database.playerHasCompletedQuest(player, quest)) {
                            PlayerUtil.sendPlayerMessage(player, questsConfig.getMessage("quest.already_completed"));
                        } else {
                            database.addQuestToPlayer(player, quest);
                            redraw();
                        }
                    }));
        }

        // Fill the blank slots of the quest populator.
        ItemStack questSlotFiller = new ItemStack(Material.STONE_BUTTON);
        ItemMeta itemMeta = questSlotFiller.getItemMeta();
        itemMeta.displayName(Component.text(""));
        questSlotFiller.setItemMeta(itemMeta);

        while (populator.hasSpace()) {
            populator.accept(ItemStackBuilder.of(questSlotFiller)
                    .build(() -> {}));
        }
    }
}