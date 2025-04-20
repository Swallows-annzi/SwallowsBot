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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateRaffle extends CommandBase {
    public static final CreateRaffle INSTANCE = new CreateRaffle();

    private CreateRaffle() {
        super("创建抽奖",
                1,
                new MessageChainBuilder()
                        .append("指令介绍：").append(CommandBase.WRAP)
                        .append("创建一个新的抽奖。").append(CommandBase.WRAP)
                        .append(SwallowsBot.COMMAND_PREFIX + "创建抽奖 <抽奖名>").append(CommandBase.WRAP)
                        .append("(必填)中奖人数 <中奖人数>").append(CommandBase.WRAP)
                        .append("(选填)抽奖模式 <模式名>").append(CommandBase.WRAP)
                        .append("(选填)抽奖开始时间 <x年x月x日x时x分x秒>").append(CommandBase.WRAP)
                        .append("(必填)抽奖开奖时间 <x年x月x日x时x分x秒>").append(CommandBase.WRAP)
                        .append("参数介绍：").append(CommandBase.WRAP)
                        .append("<抽奖名> | 抽奖定义名。").append(CommandBase.WRAP)
                        .append("<模式名> | ‘普通’-平均概率、‘权重’-依据群主规则制定，默认为’普通。‘").append(CommandBase.WRAP)
                        .append("<x年x月x日x时x分x秒> | x为设定时间，可少填，例如‘x年-x时’，余下未填会默认为当前时间。若输入错误的文字将跳过该设置，例如将’2026年‘写成’2026饭‘将忽略掉此项。若时间范围超出常理，例如'70秒'则会忽略此项。").append(CommandBase.WRAP)
                        .append("注意事项：").append(CommandBase.WRAP)
                        .append("不得将开始时间设置在开奖时间之后！否则抽奖不予创建！").append(CommandBase.WRAP)
                        .append("不得将开奖时间设置在当前时间或开奖时间之前！否则抽奖不予创建！").append(CommandBase.WRAP)
                        .append("’(必填)‘与’(选填)‘在创建时不必写！").append(CommandBase.WRAP)
                        .append("开始时间若不填写则默认当前时间，在开始时间到达之后才可进行参与！")
                        .build()
        );
    }

    @Override
    public void execute(GroupMessageEvent event, List<String> args) {

        Map<String, Object> cmdData = new HashMap<>();

        if(args.size() % 2 != 0) {
            Util.sendMessageToGroup(this.Description, event.getGroup());
            return;
        }

        if(args.size() < 6) {
            Util.sendMessageToGroup(
                    new MessageChainBuilder()
                            .append("请检查是否填好所有必填项！")
                            .build()
                    , event.getGroup());
            return;
        }

        for (int i = 0; i < args.size(); i += 2) {
            cmdData.put(args.get(i), args.get(i + 1));
        }

        if(!cmdData.containsKey("中奖人数") || !cmdData.containsKey("抽奖开奖时间")) {
            Util.sendMessageToGroup(
                    new MessageChainBuilder()
                            .append("请检查是否填好所有必填项！")
                            .build()
                    , event.getGroup());
        }

        Set<String> setString = Set.of("创建抽奖", "中奖人数", "抽奖模式", "抽奖开始时间", "抽奖开奖时间");

        if(Util.hasUnexpectedKeys(cmdData, setString)) {
            Util.sendMessageToGroup(
                    new MessageChainBuilder()
                            .append("请检查参数名是否正确！")
                            .build()
                    , event.getGroup());
        }

        else {

            boolean isRaffle = true;

            MessageChainBuilder msc_t = new  MessageChainBuilder();
            MessageChainBuilder msc_f = new  MessageChainBuilder();
            msc_t.append("已创建抽奖：").append(CommandBase.WRAP);
            msc_t.append("抽奖名：").append(args.get(1)).append(CommandBase.WRAP);
            msc_f.append("创建错误：").append(CommandBase.WRAP);

            LocalDateTime now = LocalDateTime.now();

            String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            RaffleData raffle = RaffleDataLoader.createRaffle(event, args, time);

            if(cmdData.containsKey("中奖人数")) {
                if(Util.isInteger(cmdData.get("中奖人数").toString())) {
                    int amount = Integer.parseInt(cmdData.get("中奖人数").toString());
                    raffle.setAmount(amount);
                    msc_t.append("中奖人数：").append(String.valueOf(amount)).append(CommandBase.WRAP);
                }

                else {
                    msc_f.append("中奖人数非法！").append(CommandBase.WRAP);
                    isRaffle = false;
                }
            }

            if(!cmdData.containsKey("中奖人数")) {
                msc_f.append("未设置中奖人数！").append(CommandBase.WRAP);
                isRaffle = false;
            }

            if(cmdData.containsKey("抽奖模式")) {
                String mode = (String) cmdData.get("抽奖模式");

                if(mode.equals("普通")) {
                    raffle.setMode("N");
                    msc_t.append("抽奖模式：").append(mode).append(CommandBase.WRAP);
                }
                if(mode.equals("权重")) {
                    raffle.setMode("W");
                    msc_t.append("抽奖模式：").append(mode).append(CommandBase.WRAP);
                }
                else {
                    msc_f.append("模式不明！").append(CommandBase.WRAP);
                    isRaffle = false;
                }
            }

            if(!cmdData.containsKey("抽奖模式")) {
                raffle.setMode("N");
                msc_t.append("抽奖模式：普通").append(CommandBase.WRAP);
            }

            if(cmdData.containsKey("抽奖开始时间") && cmdData.containsKey("抽奖开奖时间")) {
                String st = Util.parseTime((String) cmdData.get("抽奖开始时间"), now);
                String et = Util.parseTime((String) cmdData.get("抽奖开奖时间"), now);

                if(!Util.compareTime(st, et)) {
                    msc_f.append("开始时间晚于开奖时间！").append(CommandBase.WRAP);
                    isRaffle = false;
                }
                if(!Util.compareTime(time, et)) {
                    msc_f.append("开奖时间早于当前时间！").append(CommandBase.WRAP);
                    isRaffle = false;
                }
                else {
                    raffle.setStartTime(st);
                    raffle.setEndTime(et);
                    msc_t.append("抽奖开始时间：").append(Util.formTimeToChinese(st)).append(CommandBase.WRAP);
                    msc_t.append("抽奖开奖时间：").append(Util.formTimeToChinese(et)).append(CommandBase.WRAP);
                }
            }

            if(!cmdData.containsKey("抽奖开始时间") && cmdData.containsKey("抽奖开奖时间")) {
                String et = Util.parseTime((String) cmdData.get("抽奖开奖时间"), now);
                if(!Util.compareTime(time, et)) {
                    msc_f.append("开奖时间早于当前时间！").append(CommandBase.WRAP);
                    isRaffle = false;
                }
                else {
                    raffle.setStartTime(time);
                    raffle.setEndTime(et);
                    msc_t.append("抽奖开始时间：").append(Util.formTimeToChinese(time)).append(CommandBase.WRAP);
                    msc_t.append("抽奖开奖时间：").append(Util.formTimeToChinese(et)).append(CommandBase.WRAP);
                }
            }

            if(isRaffle) {
                RaffleDataLoader.saveRaffle(raffle);
                Util.sendMessageToGroup(msc_t.build(), event.getGroup());
                SwallowsBot.INSTANCE.logger.info("创建抽奖{" + raffle.getRaffleName() + "}成功");
            }

            if(!isRaffle) {
                Util.sendMessageToGroup(msc_f.build(), event.getGroup());
                SwallowsBot.INSTANCE.removeRaffle(raffle);
            }

        }
    }

}
