package org.swallows.swallowsbot.command.permissionzero;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
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
                new MessageChainBuilder()
                        .append("指令介绍：").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "帮助").append(CommandBase.WRAP)
                        .append("获取您当前权限可使用的指令。").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "帮助 <指令名>").append(CommandBase.WRAP)
                        .append("获取该指令的食用方法。")
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {

        if(args.size() == 1){

            MessageChainBuilder builder = new MessageChainBuilder().append("指令列表：").append(CommandBase.WRAP);
            Member sender = event.getSender();
            int UserLevel = Util.getSenderLevel(event);

            Collection<CommandBase> Commands = SwallowsBot.INSTANCE.getRegisteredCommands().values();
            for (CommandBase command : Commands) {
                if (!(command.Permission > UserLevel) || Util.isAdmin(sender.getId())) {
                    builder.append(SwallowsBot.COMMAND_PREFIX).append(command.CommandName).append(CommandBase.WRAP);
                }
            }

            Util.sendMessageToGroup(builder.build(), event.getGroup());

        }
        else if (args.size() == 2) {

            if(SwallowsBot.INSTANCE.getRegisteredCommands().containsKey(args.get(1))){
                Util.sendMessageToGroup(SwallowsBot.INSTANCE.getRegisteredCommands().get(args.get(1)).Description, event.getGroup());
            }
            else {
                Util.sendMessageToGroup(this.Description, event.getGroup());
            }

        }
        else {
            Util.sendMessageToGroup(this.Description, event.getGroup());
        }
    }
}
