package org.swallows.swallowsbot.event;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;

public class GroupMessageEventHandler extends SimpleListenerHost {

    public GroupMessageEventHandler() {
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) {
        if(Util.isBot(event.getBot().getId()) && Util.isGroupID(event.getGroup().getId())) {
            MessageChain message = event.getMessage();
            SwallowsBot.INSTANCE.logger.info(message.toString());
        }
    }
}
