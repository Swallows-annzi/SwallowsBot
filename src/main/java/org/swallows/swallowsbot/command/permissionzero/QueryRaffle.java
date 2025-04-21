package org.swallows.swallowsbot.command.permissionzero;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;
import org.swallows.swallowsbot.command.CommandBase;
import org.swallows.swallowsbot.data.raffle.RaffleData;

import java.util.List;

public class QueryRaffle extends CommandBase {
    public static final QueryRaffle INSTANCE = new QueryRaffle();

    public QueryRaffle() {
        super("查询抽奖",
                0,
                new MessageChainBuilder()
                        .append("指令介绍：").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "查询抽奖").append(CommandBase.WRAP)
                        .append("查询所有的抽奖").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "查询抽奖 <页码>").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "查询抽奖 <页码> <序号>").append(CommandBase.WRAP)
                        .append("查询该抽奖的详细信息")
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {

        if(args.size() == 1) {

            if(SwallowsBot.INSTANCE.getRegisteredCommands().isEmpty()) {
                Util.sendMessageToGroup(
                        new MessageChainBuilder()
                                .append("没有抽奖记录！")
                                .build()
                        , event.getGroup());
            }

            else {
                List<RaffleData> GroupCbs = Util.getGroupCbs(event);

                if(GroupCbs.isEmpty()) {
                    Util.sendMessageToGroup(
                            new MessageChainBuilder()
                                    .append("没有抽奖记录！")
                                    .build()
                            , event.getGroup());
                }

                else {
                    paginationSearch(event, 1, GroupCbs);
                }
            }
        }

        if(args.size() == 2) {
            List<RaffleData> GroupCbs = Util.getGroupCbs(event);

            if(Util.isInteger(args.get(1))) {
                paginationSearch(event, Integer.parseInt(args.get(1)), GroupCbs);
            }
            else {
                Util.sendMessageToGroup(
                        new MessageChainBuilder()
                                .append("请输入正确的页码！")
                                .build()
                        , event.getGroup());
            }
        }

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
                MessageChainBuilder msg = new MessageChainBuilder();

                msg.append("抽奖名：").append(raffle.getRaffleName()).append(CommandBase.WRAP);
                msg.append("当前状态：");
                if(raffle.getState() == -1)
                    msg.append("中止").append(CommandBase.WRAP);
                if(raffle.getState() == 0)
                    msg.append("结束").append(CommandBase.WRAP);
                if(raffle.getState() == 1)
                    msg.append("进行中").append(CommandBase.WRAP);
                if(raffle.getState() == 2)
                    msg.append("暂停").append(CommandBase.WRAP);
                msg.append("创建者：").append(event.getGroup().get(raffle.getOriginatorID()).getNick()).append("(").append(String.valueOf(raffle.getOriginatorID())).append(")").append(CommandBase.WRAP);
                msg.append("中奖人数：").append(String.valueOf(raffle.getAmount())).append(CommandBase.WRAP);
                msg.append("抽奖模式：");
                if(raffle.getMode().equals("N"))
                    msg.append("普通").append(CommandBase.WRAP);
                if(raffle.getMode().equals("W"))
                    msg.append("权重").append(CommandBase.WRAP);
                msg.append("参与人数：").append(String.valueOf(raffle.getParticipants().size())).append(CommandBase.WRAP);
                if(raffle.getState() != 0)
                    msg.append("中奖玩家：未开奖").append(CommandBase.WRAP);
                if(raffle.getState() == 0) {
                    List<Long> userIDs = raffle.getJackpots();
                    if(!userIDs.isEmpty()) {
                        for (int i = 0; i < userIDs.size(); i++) {
                            msg.append("中奖玩家：").append(String.valueOf(userIDs.get(i))).append("(").append(event.getGroup().get(userIDs.get(i)).getNick()).append(")");
                            if(i != userIDs.size() - 1)
                                msg.append("、");
                        }
                    }
                    else {
                        msg.append("中奖玩家：无");
                    }

                    msg.append(CommandBase.WRAP);
                }
                msg.append("抽奖创建时间：").append(Util.formTimeToChinese(raffle.getRaffleTime())).append(CommandBase.WRAP);
                msg.append("抽奖开始时间：").append(Util.formTimeToChinese(raffle.getStartTime())).append(CommandBase.WRAP);
                msg.append("抽奖开奖时间：").append(Util.formTimeToChinese(raffle.getEndTime())).append(CommandBase.WRAP);

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

        if(args.size() > 3) {
            Util.sendMessageToGroup(this.Description, event.getGroup());
        }
    }

    private static void paginationSearch(GroupMessageEvent event, int page, List<RaffleData> GroupCbs) {
        MessageChainBuilder msg = new MessageChainBuilder();

        int MaxPage = GroupCbs.size() / 10 + 1;

        if(page > MaxPage) {
            msg.append("不存在的页码！");
        }

        else {
            if(page < MaxPage) {
                for(int i = 0; i < 10; i++) {
                    msg.append(String.valueOf(i + 1)).append("、抽奖名：").append(GroupCbs.get((page - 1) * 10 + i).getRaffleName())
                            .append(" 创建时间：").append(Util.formTimeToChinese(GroupCbs.get((page - 1) * 10 + i).getRaffleTime())).append(CommandBase.WRAP);
                }
            }
            if(page == MaxPage) {
                for(int i = 0; i < GroupCbs.size() % 10; i++) {
                    msg.append(String.valueOf(i + 1)).append("、抽奖名：").append(GroupCbs.get((page - 1) * 10 + i).getRaffleName())
                            .append(" 创建时间：").append(Util.formTimeToChinese(GroupCbs.get((page - 1)* 10 + i).getRaffleTime())).append(CommandBase.WRAP);
                }
            }

            msg.append("第").append(String.valueOf(page)).append("页 / 共").append(String.valueOf(MaxPage)).append("页 | 共").append(String.valueOf(GroupCbs.size())).append("个抽奖记录");
        }

        Util.sendMessageToGroup(msg.build(), event.getGroup());
    }
}
