package org.swallows.swallowsbot;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.swallows.swallowsbot.command.CommandBase;
import org.swallows.swallowsbot.command.permissionzero.Help;
import org.swallows.swallowsbot.data.config.ConfigLoader;
import org.swallows.swallowsbot.event.GroupMessageEventHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SwallowsBot extends JavaPlugin {
    public static final SwallowsBot INSTANCE = new SwallowsBot();

    public static final String BOT_NAME = "Swallows-Bot";
    public static final String BOT_VERSION = "1.0.0";
    public static final char COMMAND_PREFIX = '$';

    public final MiraiLogger logger = getLogger();
    private final Map<String, CommandBase> registeredCommands   = new LinkedHashMap<>();

    private SwallowsBot() {
        super(new JvmPluginDescriptionBuilder("org.swallows.swallowsbot.SwallowsBot", BOT_VERSION)
                .name(BOT_NAME)
                .author("Swallows_")
                .build());
    }

    @Override
    public void onEnable() {

        ConfigLoader.load();

        logger.info("载入监听器...");
        registerCommand(Help.INSTANCE.CommandName, Help.INSTANCE);
        logger.info("载入监听器完毕");

        GlobalEventChannel.INSTANCE.registerListenerHost(new GroupMessageEventHandler());
    }

    @Override
    public void onDisable() {

        logger.info("正在保存配置文件");
        ConfigLoader.saveConfig();

        logger.info("卸载监听器...");
        unregisterCommand(Help.INSTANCE.CommandName);
        logger.info("卸载监听器完毕");

    }

    public void registerCommand(String commandName, CommandBase command) {
        registeredCommands.put(commandName, command);
    }

    public void unregisterCommand(String commandName) {
        registeredCommands.remove(commandName);
    }

    public Map<String, CommandBase> getRegisteredCommands() {
        return registeredCommands;
    }
}
