package org.swallows.swallowsbot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.utils.MiraiLogger;
import org.swallows.swallowsbot.command.CommandBase;
import org.swallows.swallowsbot.command.permissionzero.*;
import org.swallows.swallowsbot.data.SwallowsDir;
import org.swallows.swallowsbot.data.config.ConfigLoader;
import org.swallows.swallowsbot.data.config.SwallowsBotConfig;
import org.swallows.swallowsbot.data.raffle.RaffleData;
import org.swallows.swallowsbot.data.raffle.RaffleDataLoader;
import org.swallows.swallowsbot.event.GroupMessageEventHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SwallowsBot extends JavaPlugin {
    public static final SwallowsBot INSTANCE = new SwallowsBot();

    public static final String BOT_NAME = "Swallows-Bot";
    public static final String BOT_VERSION = "1.0.0";
    public static final char COMMAND_PREFIX = '$';

    public final MiraiLogger logger = getLogger();
    private final Map<String, CommandBase> registeredCommands = new LinkedHashMap<>();
    private final Map<String, RaffleData> raffles = new LinkedHashMap<>();

    private SwallowsBot() {
        super(new JvmPluginDescriptionBuilder("org.swallows.swallowsbot.SwallowsBot", BOT_VERSION)
                .name(BOT_NAME)
                .author("Swallows_")
                .build());
    }

    @Override
    public void onEnable() {

        ConfigLoader.load();
        SwallowsDir.load();
        RaffleDataLoader.formRaffle();

        logger.info("载入监听器...");
        registerCommand(Help.INSTANCE.CommandName, Help.INSTANCE);
        registerCommand(CreateRaffle.INSTANCE.CommandName, CreateRaffle.INSTANCE);
        registerCommand(ModifyRaffle.INSTANCE.CommandName, ModifyRaffle.INSTANCE);
        registerCommand(JoinRaffle.INSTANCE.CommandName, JoinRaffle.INSTANCE);
        registerCommand(LotteryRaffle.INSTANCE.CommandName, LotteryRaffle.INSTANCE);
        registerCommand(TemporarilyRaffle.INSTANCE.CommandName, TemporarilyRaffle.INSTANCE);
        registerCommand(GoRaffle.INSTANCE.CommandName, GoRaffle.INSTANCE);
        registerCommand(RemoveRaffle.INSTANCE.CommandName, RemoveRaffle.INSTANCE);
        registerCommand(QueryRaffle.INSTANCE.CommandName, QueryRaffle.INSTANCE);
        logger.info("载入监听器完毕");

        GlobalEventChannel.INSTANCE.registerListenerHost(new GroupMessageEventHandler());

        Util.startWatching(Bot.getInstance(SwallowsBotConfig.BotID));
    }

    @Override
    public void onDisable() {

        logger.info("正在保存配置文件");
        ConfigLoader.saveConfig();
        RaffleDataLoader.saveRaffles(raffles);

        logger.info("卸载监听器...");
        unregisterCommand(Help.INSTANCE.CommandName);
        unregisterCommand(CreateRaffle.INSTANCE.CommandName);
        unregisterCommand(ModifyRaffle.INSTANCE.CommandName);
        unregisterCommand(JoinRaffle.INSTANCE.CommandName);
        unregisterCommand(LotteryRaffle.INSTANCE.CommandName);
        unregisterCommand(TemporarilyRaffle.INSTANCE.CommandName);
        unregisterCommand(GoRaffle.INSTANCE.CommandName);
        unregisterCommand(RemoveRaffle.INSTANCE.CommandName);
        unregisterCommand(QueryRaffle.INSTANCE.CommandName);
        logger.info("卸载监听器完毕");

    }

    public void registerCommand(String commandName, CommandBase command) {
        registeredCommands.put(commandName, command);
    }

    public void unregisterCommand(String commandName) {
        registeredCommands.remove(commandName);
    }

    public Map<String, RaffleData> getRaffles() {
        return raffles;
    }

    public void addRaffle(RaffleData raffle) {
        raffles.put(raffle.getRaffleTime() + "-" + raffle.getRaffleName(), raffle);
    }

    public void removeRaffle(RaffleData raffle) {
        raffles.remove(raffle.getRaffleTime() + "-" + raffle.getRaffleName());
    }

    public void removeAllRaffles() {
        raffles.clear();
    }

    public Map<String, CommandBase> getRegisteredCommands() {
        return registeredCommands;
    }
}
