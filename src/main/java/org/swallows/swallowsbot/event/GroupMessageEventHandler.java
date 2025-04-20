package org.swallows.swallowsbot.event;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;
import org.swallows.swallowsbot.command.CommandBase;

import java.util.Arrays;
import java.util.List;

public class GroupMessageEventHandler implements ListenerHost {

    public GroupMessageEventHandler() {

        GlobalEventChannel.INSTANCE
                .filter(ev -> ev instanceof BotEvent && Util.isBot(((BotEvent) ev).getBot().getId()))
                .subscribeAlways(GroupMessageEvent.class, this::onMessage);

    }

    public void onMessage(GroupMessageEvent event) {
        if(Util.isGroupID(event.getGroup().getId())) {
            MessageChain message = event.getMessage();
            String text = message.contentToString();
            if(text.startsWith(String.valueOf(SwallowsBot.COMMAND_PREFIX)) && !text.equals(String.valueOf(SwallowsBot.COMMAND_PREFIX))) {
                String command = text.substring(1).trim();

                List<String> args = Arrays.asList(command.replace("\n", " ").trim().split(" "));

                if(SwallowsBot.INSTANCE.getRegisteredCommands().containsKey(args.get(0))) {

                    CommandBase cmd = SwallowsBot.INSTANCE.getRegisteredCommands().get(args.get(0));

                    if(Util.getSenderLevel(event) >= cmd.Permission) {
                        Member user = event.getSender();
                        SwallowsBot.INSTANCE.logger.info("接受来自" + event.getSenderName() + user.getId() + "的命令:" + args);
                        cmd.execute(event, args);
                    }
                    else {
                        Util.sendMessageToGroup(new MessageChainBuilder()
                                .append("权限不足！").append(CommandBase.WRAP)
                                .build() , event.getGroup());
                    }

                }
                else {
                    Util.sendMessageToGroup(new MessageChainBuilder()
                            .append("未知命令！").append(CommandBase.WRAP)
                            .append("请输入“" + SwallowsBot.COMMAND_PREFIX + "帮助”获取到可使用命令")
                            .build() , event.getGroup());
                }
            }
        }
    }
}
