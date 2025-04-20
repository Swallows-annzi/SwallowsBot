package org.swallows.swallowsbot.data;

import org.swallows.swallowsbot.SwallowsBot;

import java.io.File;

public class SwallowsDir {

    public static File SwallowsDir;
    public static File SwallowsRaffleDir;

    public static void load() {

        SwallowsDir = new File(SwallowsBot.BOT_NAME);
        if (!SwallowsDir.exists()) {
            SwallowsDir.mkdirs();
        }

        SwallowsRaffleDir = new File(SwallowsDir, "Raffle");
        if (!SwallowsRaffleDir.exists()) {
            SwallowsRaffleDir.mkdirs();
        }

    }
}
