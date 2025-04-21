package org.swallows.swallowsbot.command.permissionzero;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.swallows.swallowsbot.SwallowsBot;
import org.swallows.swallowsbot.Util;
import org.swallows.swallowsbot.command.CommandBase;
import org.swallows.swallowsbot.data.raffle.RaffleData;
import org.swallows.swallowsbot.data.raffle.RaffleDataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ModifyRaffle extends CommandBase {
    public static final ModifyRaffle INSTANCE = new ModifyRaffle();

    public ModifyRaffle() {
        super("修改抽奖",
                1,
                new MessageChainBuilder()
                        .append("指令介绍：").append(CommandBase.WRAP)
                        .append("修改原有抽奖的属性。").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "修改抽奖 <页码> <编号>").append(CommandBase.WRAP)
                        .append("中奖人数 <中奖人数>").append(CommandBase.WRAP)
                        .append("抽奖模式 <模式名>").append(CommandBase.WRAP)
                        .append("抽奖开始时间 <x年x月x日x时x分x秒>").append(CommandBase.WRAP)
                        .append("抽奖开奖时间 <x年x月x日x时x分x秒>").append(CommandBase.WRAP)
                        .append("参数介绍：").append(CommandBase.WRAP)
                        .append("<抽奖名> | 选定指定的。").append(CommandBase.WRAP)
                        .append("<模式名> | ‘普通’-平均概率、‘权重’-依据群主规则制定，默认为’普通。‘").append(CommandBase.WRAP)
                        .append("<x年x月x日x时x分x秒> | x为设定时间，可少填，例如‘x年-x时’，余下未填会默认为当前时间。若输入错误的文字将跳过该设置，例如将’2026年‘写成’2026饭‘将忽略掉此项。若时间范围超出常理，例如'70秒'则会忽略此项。").append(CommandBase.WRAP)
                        .append("注意事项：").append(CommandBase.WRAP)
                        .append("不得将开始时间设置在开奖时间之后！否则抽奖不予创建！").append(CommandBase.WRAP)
                        .append("不得将开奖时间设置在当前时间或开奖时间之前！否则抽奖不予创建！")
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {

        if(args.size() == 3) {

            if(Util.isInteger(args.get(1)) && Util.isInteger(args.get(2))) {
                Util.sendMessageToGroup( new MessageChainBuilder()
                                .append("亲，请问您需要修改的数据呢？")
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

        if(args.size() > 3 && args.size() % 2 == 1) {

            if(Util.isInteger(args.get(1)) && Util.isInteger(args.get(2))) {
                List<RaffleData> GroupCbs = Util.getGroupCbs(event);

                int no = (Integer.parseInt(args.get(1)) - 1) * 10 + Integer.parseInt(args.get(2));

                if(no > GroupCbs.size() || no < 1) {
                    Util.sendMessageToGroup( new MessageChainBuilder()
                                    .append("没有这项抽奖数据！")
                                    .build()
                            , event.getGroup()
                    );
                    return;
                }

                Map<String, Object> cmdData = new HashMap<>();

                for (int i = 3; i < args.size(); i += 2) {
                    cmdData.put(args.get(i), args.get(i + 1));
                }

                Set<String> setString = Set.of("中奖人数", "抽奖模式", "抽奖开始时间", "抽奖开奖时间");

                if(Util.hasUnexpectedKeys(cmdData, setString)) {
                    Util.sendMessageToGroup(
                            new MessageChainBuilder()
                                    .append("请检查参数名是否正确！")
                                    .build()
                            , event.getGroup());
                }

                else {

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

                    MessageChainBuilder msc = new  MessageChainBuilder();

                    if(cmdData.containsKey("中奖人数")) {
                        if(Util.isInteger(cmdData.get("中奖人数").toString())) {
                            int amount = Integer.parseInt(cmdData.get("中奖人数").toString());
                            msc.append("中奖人数：").append(String.valueOf(raffle.getAmount())).append(" -> ").append(String.valueOf(amount)).append(CommandBase.WRAP);
                            raffle.setAmount(amount);
                        }
                    }

                    if(cmdData.containsKey("抽奖模式")) {
                        String mode = (String) cmdData.get("抽奖模式");
                        String oldMode = null;

                        if(raffle.getMode().equals("N")) {
                            oldMode = "普通";
                        }
                        if(raffle.getMode().equals("W")) {
                            oldMode = "权重";
                        }
                        if(mode.equals("普通")) {
                            msc.append("抽奖模式：").append(oldMode).append(" -> ").append(mode).append(CommandBase.WRAP);
                            raffle.setMode("N");
                        }
                        if(mode.equals("权重")) {
                            msc.append("抽奖模式：").append(oldMode).append(" -> ").append(mode).append(CommandBase.WRAP);;
                            raffle.setMode("W");
                        }
                        else {
                            msc.append("模式不明！不予修改！").append(CommandBase.WRAP);
                        }
                    }

                    LocalDateTime now = LocalDateTime.now();

                    String rt = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

                    if(cmdData.containsKey("抽奖开始时间") && cmdData.containsKey("抽奖开奖时间")) {
                        String st = Util.parseTime((String) cmdData.get("抽奖开始时间"), now);
                        String et = Util.parseTime((String) cmdData.get("抽奖开奖时间"), now);

                        if(!Util.compareTime(st, et)) {
                            msc.append("开始时间晚于开奖时间！不予修改！").append(CommandBase.WRAP);
                        }
                        if(!Util.compareTime(rt, et)) {
                            msc.append("开奖时间早于当前时间！不予修改！").append(CommandBase.WRAP);
                        }
                        else {
                            msc.append("抽奖开始时间：").append(Util.formTimeToChinese(raffle.getStartTime())).append(" -> ").append(Util.formTimeToChinese(st)).append(CommandBase.WRAP);
                            msc.append("抽奖开奖时间：").append(Util.formTimeToChinese(raffle.getEndTime())).append(" -> ").append(Util.formTimeToChinese(et)).append(CommandBase.WRAP);
                            raffle.setStartTime(st);
                            raffle.setEndTime(et);
                        }

                    }

                    if(cmdData.containsKey("抽奖开始时间") && !cmdData.containsKey("抽奖开奖时间")) {
                        String st = Util.parseTime((String) cmdData.get("抽奖开始时间"), now);
                        String et = Util.parseTime(raffle.getEndTime(), now);

                        if(!Util.compareTime(st, et)) {
                            msc.append("开始时间晚于开奖时间！不予修改").append(CommandBase.WRAP);
                        }
                        else {
                            msc.append("抽奖开始时间：").append(Util.formTimeToChinese(raffle.getStartTime())).append(" -> ").append(Util.formTimeToChinese(st)).append(CommandBase.WRAP);
                            raffle.setEndTime(st);
                        }
                    }

                    if(!cmdData.containsKey("抽奖开始时间") && cmdData.containsKey("抽奖开奖时间")) {
                        String st = Util.parseTime(raffle.getStartTime(), now);
                        String et = Util.parseTime((String) cmdData.get("抽奖开奖时间"), now);

                        if(!Util.compareTime(rt, et)) {
                            msc.append("开奖时间早于当前时间！不予修改").append(CommandBase.WRAP);
                        }
                        if(!Util.compareTime(st, et)) {
                            msc.append("开奖时间早于当前！不予修改").append(CommandBase.WRAP);
                        }
                        else {
                            msc.append("抽奖开奖时间：").append(Util.formTimeToChinese(raffle.getEndTime())).append(" -> ").append(Util.formTimeToChinese(et)).append(CommandBase.WRAP);
                            raffle.setEndTime(et);
                        }
                    }
                    RaffleDataLoader.saveRaffle(raffle);
                    msc.append("若需要，请输入：").append(SwallowsBot.COMMAND_PREFIX).append("查询抽奖 ").append(args.get(1)).append(" ").append(args.get(2)).append("查询该抽奖。");
                    Util.sendMessageToGroup(msc.build(), event.getGroup());
                }

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
