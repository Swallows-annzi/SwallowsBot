package org.swallows.swallowsbot.command;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

public abstract class CommandBase {

    public static final MessageChain WRAP = MiraiCode.deserializeMiraiCode("\n");

    public final String CommandName;
    public final MessageChain Description;
    public final int Permission;

    public CommandBase(String commandName, int permission, MessageChain description) {
        this.CommandName = commandName;
        this.Permission = permission;
        this.Description = description;
    }

    public abstract void execute(GroupMessageEvent event, List<String> args);
}
