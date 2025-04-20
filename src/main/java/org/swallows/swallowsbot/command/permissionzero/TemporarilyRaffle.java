package org.swallows.swallowsbot.command.permissionzero;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;
import org.swallows.swallowsbot.command.CommandBase;
import org.swallows.swallowsbot.data.raffle.RaffleData;
import org.swallows.swallowsbot.data.raffle.RaffleDataLoader;

import java.util.List;

public class TemporarilyRaffle extends CommandBase {
    public static final TemporarilyRaffle INSTANCE = new TemporarilyRaffle();

    public TemporarilyRaffle() {
        super("暂停抽奖",
                1,
                new MessageChainBuilder()
                        .append("指令介绍：").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "暂停抽奖 <页码> <序号>").append(CommandBase.WRAP)
                        .append("暂时停止所选中的抽奖。")
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {

        if(args.size() == 3) {
            List<RaffleData> GroupCbs = Util.getGroupCbs(event);

            if(Util.isInteger(args.get(1)) && Util.isInteger(args.get(2))) {
                int no = (Integer.parseInt(args.get(1)) - 1) * 10 + Integer.parseInt(args.get(2));

                if(no > GroupCbs.size() || no < 1) {
                    Util.sendMessageToGroup( new MessageChainBuilder()
                                    .append("没有这项抽奖数据！")
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                RaffleData raffle = GroupCbs.get(no - 1);

                if(raffle.getState() == -1) {
                    Util.sendMessageToGroup(
                            new MessageChainBuilder()
                                    .append("该抽奖已被中止！").append(CommandBase.WRAP)
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                if(raffle.getState() == 0) {
                    Util.sendMessageToGroup(
                            new MessageChainBuilder()
                                    .append("该抽奖已结束！").append(CommandBase.WRAP)
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                if(raffle.getState() == 2) {
                    Util.sendMessageToGroup(
                            new MessageChainBuilder()
                                    .append("该抽奖已被暂停！").append(CommandBase.WRAP)
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                raffle.setState(2);
                RaffleDataLoader.saveRaffle(raffle);
                Util.sendMessageToGroup(
                        new MessageChainBuilder()
                                .append("已暂停该抽奖！").append(CommandBase.WRAP)
                                .append("若需要，请输入：").append(SwallowsBot.COMMAND_PREFIX).append("查询抽奖 ").append(args.get(1)).append(" ").append(args.get(2)).append("查询该抽奖。")
                                .build()
                        , event.getGroup()
                );
            }

            else {
                Util.sendMessageToGroup(
                        new MessageChainBuilder()
                                .append("请输入正确的页码或序号！")
                                .build()
                        , event.getGroup()
                );
            }
        }

        else {
            Util.sendMessageToGroup(this.Description, event.getGroup());
        }
    }
}
