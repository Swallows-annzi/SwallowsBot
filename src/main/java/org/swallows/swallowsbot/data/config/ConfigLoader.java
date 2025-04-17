package org.swallows.swallowsbot.data.config;

import org.swallows.swallowsbot.SwallowsBot;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigLoader {
    private static final String CONFIG_PATH = "config/" + SwallowsBot.BOT_NAME + ".yml";

    public static void load() {
        if(new File(CONFIG_PATH).exists()) {
            Yaml yaml = new Yaml();
            try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
                SwallowsBotConfig.setData(yaml.load(fis));
                SwallowsBot.INSTANCE.logger.info("已读取配置文件");
            } catch (IOException e) {
                SwallowsBot.INSTANCE.logger.error(e);
            }
        }
        else {
            saveConfig();
            SwallowsBot.INSTANCE.logger.info("未找到配置文件！已生成配置文件并使用默认配置！");
        }
    }

    public static void saveConfig() {

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setExplicitStart(false);
        options.setExplicitEnd(false);

        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            yaml.dump(SwallowsBotConfig.getData(), writer);
        } catch (IOException e) {
            SwallowsBot.INSTANCE.logger.error(e);
        }
    }

}
