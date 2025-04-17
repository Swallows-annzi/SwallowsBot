package org.swallows.swallowsbot.event;

import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.BotEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;

public class GroupMessageEventHandler implements ListenerHost {

    public GroupMessageEventHandler() {

        GlobalEventChannel.INSTANCE
                .filter(ev -> ev instanceof BotEvent && Util.isBot(((BotEvent) ev).getBot().getId()))
                .subscribeAlways(GroupMessageEvent.class, this::onMessage);
        
    }

    public void onMessage(GroupMessageEvent event) {
        if(Util.isGroupID(event.getGroup().getId())) {
            MessageChain message = event.getMessage();
            SwallowsBot.INSTANCE.logger.info("喵喵喵");
        }
    }
}
