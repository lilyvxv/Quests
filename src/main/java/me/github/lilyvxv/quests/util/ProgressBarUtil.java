package me.github.lilyvxv.quests.util;

import static me.github.lilyvxv.quests.Quests.LOGGER;

public class ProgressBarUtil {

    public static String generateProgressBar(int progress) {
        if (progress < 0 || progress > 100) {
            LOGGER.warning("Invalid progress bar value. Progress should be between 0 and 100.");
            return "";
        }

        int barCount = progress / 10;

        StringBuilder progressBarBuilder = new StringBuilder("<white>[</white><yellow>");
        for (int i = 0; i < 10; i++) {
            if (i < barCount) {
                progressBarBuilder.append('\u2B1B');
            } else {
                progressBarBuilder.append('\u2B1C');
            }
        }

        progressBarBuilder
                .append("</yellow><white>]</white> <gray>")
                .append(progress)
                .append("%</gray>");

        return progressBarBuilder.toString();
    }
}