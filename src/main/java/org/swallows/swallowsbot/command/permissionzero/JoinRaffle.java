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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class JoinRaffle extends CommandBase {
    public static final JoinRaffle INSTANCE = new JoinRaffle();

    public JoinRaffle() {
        super("加入抽奖",
                0,
                new MessageChainBuilder()
                        .append("指令介绍：").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "加入抽奖").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "加入抽奖 <页码> <序号>").append(CommandBase.WRAP)
                        .append("若不输入<页码>与<序号>则默认为离当前时间最近创建的抽奖").append(CommandBase.WRAP)
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {

        if(args.size() == 1) {
            List<RaffleData> GroupCbs = Util.getGroupCbs(event);
            Collections.reverse(GroupCbs);

            Member user = event.getSender();

            for (RaffleData raffle : GroupCbs) {

                LocalDateTime now = LocalDateTime.now();

                String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
                String st = Util.parseTime(raffle.getStartTime(), now);

                if(raffle.getState() == 1 && Util.isParticipants(raffle, user.getId()) && Util.compareTime(st, time)) {
                    Util.sendMessageToGroup( new MessageChainBuilder()
                                    .append(
                                            new At(user.getId()).plus(CommandBase.WRAP)
                                                    .plus("您当前已加入抽奖！")
                                    )
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                if(raffle.getState() == 1 && !Util.isParticipants(raffle, user.getId()) && Util.compareTime(st, time)) {
                    raffle.addParticipants(user.getId());
                    RaffleDataLoader.saveRaffle(raffle);
                    Util.sendMessageToGroup( new MessageChainBuilder()
                                    .append(
                                            new At(user.getId()).plus(CommandBase.WRAP)
                                                    .plus("成功加入抽奖！")
                                    )
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }
            }

            Util.sendMessageToGroup( new MessageChainBuilder()
                            .append(
                                    new At(user.getId()).plus(CommandBase.WRAP)
                                            .plus("当前无可参加的抽奖！")
                            )
                            .build()
                    , event.getGroup()
            );
        }

        if(args.size() == 3) {
            List<RaffleData> GroupCbs = Util.getGroupCbs(event);

            Member user = event.getSender();

            if(Util.isInteger(args.get(1)) && Util.isInteger(args.get(2))) {
                int no = (Integer.parseInt(args.get(1)) - 1) * 10 + Integer.parseInt(args.get(2));

                if(no > GroupCbs.size() || no < 1) {
                    Util.sendMessageToGroup( new MessageChainBuilder()
                                    .append(
                                            new At(user.getId()).plus(CommandBase.WRAP)
                                                    .plus("没有这项抽奖！")
                                    )
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                RaffleData raffle = GroupCbs.get(no - 1);

                LocalDateTime now = LocalDateTime.now();
                String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
                String st = Util.parseTime(raffle.getStartTime(), now);

                if(!Util.compareTime(st, time)) {
                    Util.sendMessageToGroup( new MessageChainBuilder()
                                    .append(
                                            new At(user.getId()).plus(CommandBase.WRAP)
                                                    .plus("暂未到开始时间！")
                                    )
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                raffle.addParticipants(user.getId());
                RaffleDataLoader.saveRaffle(raffle);

                Util.sendMessageToGroup( new MessageChainBuilder()
                                .append(
                                        new At(user.getId()).plus(CommandBase.WRAP)
                                                .plus("成功加入抽奖！")
                                )
                                .build()
                        , event.getGroup()
                );
            }

            else {
                Util.sendMessageToGroup( new MessageChainBuilder()
                                .append(
                                        new At(user.getId()).plus(CommandBase.WRAP)
                                                .plus("请输入正确的页码或序号！")
                                )
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
