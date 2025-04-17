package org.swallows.swallowsbot.command.permissionzero;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;
import org.swallows.swallowsbot.command.CommandBase;

import java.util.Collection;
import java.util.List;

public class Help extends CommandBase {
    public static final Help INSTANCE = new Help();

    private Help() {
        super("帮助",
                0,
                1,
                0,
                new MessageChainBuilder()
                        .append("指令介绍：获取当前群聊可用指令列表")
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {
        MessageChainBuilder builder = new MessageChainBuilder().append("指令列表：").append(CommandBase.WRAP);

        Member sender = event.getSender();
        int UserLevel = sender.getPermission().getLevel();

        Collection<CommandBase> Commands = SwallowsBot.INSTANCE.getRegisteredCommands().values();

        for (CommandBase command : Commands) {
            if (!(command.Permission > UserLevel) || Util.isAdmin(sender.getId())) {
                builder.append(SwallowsBot.COMMAND_PREFIX).append(command.CommandName).append(CommandBase.WRAP);
            }
        }

        Util.sendMessageToGroup(builder.build(), event.getGroup());
    }
}
