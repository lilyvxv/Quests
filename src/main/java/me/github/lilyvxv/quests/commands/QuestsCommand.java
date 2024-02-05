package me.github.lilyvxv.quests.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import me.github.lilyvxv.quests.menus.QuestListMenu;
import org.bukkit.entity.Player;

import static me.github.lilyvxv.quests.Quests.LOGGER;

public class QuestsCommand {

    public QuestsCommand() {

    }

    private static void execute(Player player, CommandArguments arguments) {
        new QuestListMenu(player).open();
    }

    public void register() {
        new CommandAPICommand("quests")
                .withPermission("quests.menu")
                .executesPlayer(QuestsCommand::execute)
                .register();
    }
}
