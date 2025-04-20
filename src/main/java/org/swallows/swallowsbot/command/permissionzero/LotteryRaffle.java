package org.swallows.swallowsbot.command.permissionzero;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;
import org.swallows.swallowsbot.command.CommandBase;
import org.swallows.swallowsbot.data.raffle.RaffleData;
import org.swallows.swallowsbot.data.raffle.RaffleDataLoader;

import java.util.Collections;
import java.util.List;

public class LotteryRaffle extends CommandBase {
    public static final LotteryRaffle INSTANCE = new LotteryRaffle();

    public LotteryRaffle() {
        super("提前开奖",
                1,
                new MessageChainBuilder()
                        .append("指令介绍：").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "提前开奖").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "提前开奖 <页码> <序号>").append(CommandBase.WRAP)
                        .append("若不输入<页码>与<序号>则默认为离当前时间最近创建的抽奖").append(CommandBase.WRAP)
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {

        if(args.size() == 1) {
            List<RaffleData> GroupCbs = Util.getGroupCbs(event);
            Collections.reverse(GroupCbs);

            for (RaffleData raffle : GroupCbs) {

                if(raffle.getState() == 1) {
                    List<Long> IDs = Util.onLottery(event, raffle);
                    raffle.setState(0);
                    raffle.setJackpots(IDs);
                    RaffleDataLoader.saveRaffle(raffle);

                    MessageChainBuilder msg = new MessageChainBuilder();

                    for (Long id : IDs) {
                        At at = new At(id);
                        msg.append(at);
                    }

                    msg.append("恭喜抽中“").append(raffle.getRaffleName()).append("”的奖励，大伙快恭喜他们！");

                    Util.sendMessageToGroup(msg.build(), event.getGroup());
                    break;
                }
                else {
                    Util.sendMessageToGroup(
                            new MessageChainBuilder()
                                    .append("当前无可开奖的抽奖！").append(CommandBase.WRAP)
                                    .build()
                            , event.getGroup()
                    );
                }
            }

        }

        if(args.size() == 3) {
            List<RaffleData> GroupCbs = Util.getGroupCbs(event);
            Collections.reverse(GroupCbs);

            if(Util.isInteger(args.get(1)) && Util.isInteger(args.get(2))) {
                int no = (Integer.parseInt(args.get(1)) - 1) * 10 + Integer.parseInt(args.get(2));

                if(no > GroupCbs.size() || no < 1) {
                    Util.sendMessageToGroup(
                            new MessageChainBuilder()
                                    .append("没有这项抽奖！").append(CommandBase.WRAP)
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                RaffleData raffle = GroupCbs.get(no - 1);
                List<Long> IDs = Util.onLottery(event, raffle);
                raffle.setState(0);
                raffle.setJackpots(IDs);
                RaffleDataLoader.saveRaffle(raffle);

                MessageChainBuilder msg = new MessageChainBuilder();

                for (Long id : IDs) {
                    At at = new At(id);
                    msg.append(at);
                }

                msg.append("恭喜抽中“").append(raffle.getRaffleName()).append("”的奖励，大伙快恭喜他们！");

                Util.sendMessageToGroup(msg.build(), event.getGroup());
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
