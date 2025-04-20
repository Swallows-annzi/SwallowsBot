package org.swallows.swallowsbot.data.raffle;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;
import org.swallows.swallowsbot.data.SwallowsDir;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RaffleDataLoader {

    public static final String RAFFLE_PATH = SwallowsBot.BOT_NAME + "/Raffle/";

    public static RaffleData createRaffle(GroupMessageEvent event, List<String> args, String time) {
        RaffleData raffle = new RaffleData(args.get(1), event.getSender().getId());
        raffle.setRaffleTime(time);
        raffle.setGroupID(event.getGroup().getId());
        raffle.setState(1);
        SwallowsBot.INSTANCE.addRaffle(raffle);
        return raffle;
    }

    public static void formRaffle() {
        File fromRaffleDir = SwallowsDir.SwallowsRaffleDir;

        if(fromRaffleDir.exists() && fromRaffleDir.isDirectory()) {
            File[] files = fromRaffleDir.listFiles();
            if(files != null) {
                for (File file : files) {
                    if(file.isFile()) {
                        RaffleData raffle = Util.createRaffleData(file.getName());
                        SwallowsBot.INSTANCE.addRaffle(raffle);
                        SwallowsBot.INSTANCE.logger.info("已读取到{" + file.getName() + "}抽奖文件");
                    }
                }
                SwallowsBot.INSTANCE.logger.info("已读取" + files.length + "个抽奖文件");
            }
        }
    }

    public static void saveRaffle(RaffleData raffle) {

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setExplicitStart(false);
        options.setExplicitEnd(false);

        Yaml yaml = new Yaml(options);

        try (FileWriter writer = new FileWriter(RAFFLE_PATH + raffle.getRaffleTime() + "-" + raffle.getRaffleName() + ".yml")) {
            yaml.dump(raffle.getData(), writer);
        } catch (IOException e) {
            SwallowsBot.INSTANCE.logger.error(e);
        }
    }

    public static void saveRaffles(Map<String, RaffleData> raffles) {
        for (RaffleData raffle : raffles.values()) {
            saveRaffle(raffle);
        }
        SwallowsBot.INSTANCE.logger.info("已保存全部共" + raffles.size() + "个抽奖！");
    }
}
