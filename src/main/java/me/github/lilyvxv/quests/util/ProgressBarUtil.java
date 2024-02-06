package me.github.lilyvxv.quests.util;

public class ProgressBarUtil {

    public static String generateProgressBar(int progress) {
//        if (progress < 0 || progress > 100) {
//            logger.warning("Invalid progress bar value. Progress should be between 0 and 100.");
//            return "";
//        }

        progress = Math.min(progress, 100);
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