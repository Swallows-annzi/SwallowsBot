package org.swallows.swallowsbot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.message.data.MessageChain;
import org.swallows.swallowsbot.data.config.SwallowsBotConfig;

import java.util.NoSuchElementException;

public class Util {

    public static void sendMessageToGroup(MessageChain message, long botId, long groupId) {
        Bot bot;

        try {
            bot = Bot.getInstance(botId);
            Group group = bot.getGroup(groupId);
            if (group == null) {
                SwallowsBot.INSTANCE.logger.warning(String.format("Cloud not find group %s!", groupId));
                return;
            }

            if (group.getBotAsMember().isMuted() || group.getSettings().isMuteAll()) {
                return;
            }

            group.sendMessage(message);
        } catch (NoSuchElementException e) {
            SwallowsBot.INSTANCE.logger.warning(String.format("Cloud not find bot %s!", botId));
        }
    }

    public static void sendMessageToGroup(MessageChain message, Group group) {
        NormalMember bot = group.getBotAsMember();
        if (bot.isMuted() || (group.getSettings().isMuteAll() && bot.getPermission().getLevel() < 1)) {
            return;
        }
        group.sendMessage(message);
    }

    public static boolean isAdmin(long id) {
        return SwallowsBotConfig.AdminIDs.contains(id);
    }

    public static boolean isGroupID(long id) {
        return SwallowsBotConfig.GroupIDs.contains(id);
    }

    public static boolean isBot(long id) {
        return SwallowsBotConfig.BotID == id;
    }
}
