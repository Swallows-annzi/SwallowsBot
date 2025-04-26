package org.swallows.swallowsbot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.swallows.swallowsbot.data.config.SwallowsBotConfig;
import org.swallows.swallowsbot.data.raffle.RaffleData;
import org.swallows.swallowsbot.data.raffle.RaffleDataLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static void sendMessageToGroup(MessageChain message, long botId, long groupId) {
        Bot bot;

        try {
            bot = Bot.getInstance(botId);
            Group group = bot.getGroup(groupId);
            if (group == null) {
                SwallowsBot.INSTANCE.logger.warning(String.format("Cloud not find group %s!", groupId));
                return;
            }

            if (group.getBotAsMember().isMuted() || group.getSettings().isMuteAll()) {
                return;
            }

            group.sendMessage(message);
        } catch (NoSuchElementException e) {
            SwallowsBot.INSTANCE.logger.warning(String.format("Cloud not find bot %s!", botId));
        }
    }

    public static void sendMessageToGroup(MessageChain message, Group group) {
        NormalMember bot = group.getBotAsMember();
        if (bot.isMuted() || (group.getSettings().isMuteAll() && bot.getPermission().getLevel() < 1)) {
            return;
        }
        group.sendMessage(message);
    }

    public static int getSenderLevel(GroupMessageEvent event) {
        Member user = event.getSender();
        if(isAdmin(user.getId())) {
            return 4;
        }
        else {
            return user.getPermission().getLevel();
        }
    }

    public static boolean isAdmin(long id) {
        return SwallowsBotConfig.AdminIDs.contains(id);
    }

    public static boolean isGroupID(long id) {
        return SwallowsBotConfig.GroupIDs.contains(id);
    }

    public static boolean isBot(long id) {
        return SwallowsBotConfig.BotID == id;
    }

    public static RaffleData createRaffleData(String filename) {

        RaffleData raffleData = new RaffleData();
        Yaml yaml = new Yaml();

        try (FileInputStream fis = new FileInputStream(RaffleDataLoader.RAFFLE_PATH + filename)) {

            Map<String, Object> data = yaml.load(fis);

            raffleData.setRaffleTime((String) data.get("RaffleTime"));
            raffleData.setRaffleName((String) data.get("RaffleName"));
            raffleData.setState(((Number) data.get("State")).intValue());
            raffleData.setAmount(Integer.parseInt(data.get("Amount").toString()));
            raffleData.setOriginatorID(Long.parseLong(data.get("OriginatorID").toString()));
            raffleData.setGroupID(Long.parseLong(data.get("GroupID").toString()));
            raffleData.setStartTime((String) data.get("StartTime"));
            raffleData.setEndTime((String) data.get("EndTime"));
            raffleData.setMode(((String) data.get("Mode")));

            List<?> ParticipantList = (List<?>) data.get("Participants");
            for (Object id : ParticipantList) {
                raffleData.addParticipants(((Number) id).longValue());
            }

            List<?> JackpotList = (List<?>) data.get("Participants");
            for (Object id : JackpotList) {
                raffleData.addJackpots(((Number) id).longValue());
            }

            return raffleData;
        } catch (IOException e) {
            SwallowsBot.INSTANCE.logger.error(e);
        }
        return raffleData;
    }

    public static String parseTime(String time, LocalDateTime now) {

        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        Pattern pattern = Pattern.compile("(\\d+)年|(\\d+)月|(\\d+)日|(\\d+)时|(\\d+)分|(\\d+)秒");
        Matcher matcher = pattern.matcher(time);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                int y = Integer.parseInt(matcher.group(1));
                if (y >= 1970 && y <= 3000) year = y;
            }
            if (matcher.group(2) != null) {
                int m = Integer.parseInt(matcher.group(2));
                if (m >= 1 && m <= 12) month = m;
            }
            if (matcher.group(3) != null) {
                int d = Integer.parseInt(matcher.group(3));
                day = d; // 合法性稍后校验
            }
            if (matcher.group(4) != null) {
                int h = Integer.parseInt(matcher.group(4));
                if (h >= 0 && h <= 23) hour = h;
            }
            if (matcher.group(5) != null) {
                int min = Integer.parseInt(matcher.group(5));
                if (min >= 0 && min <= 59) minute = min;
            }
            if (matcher.group(6) != null) {
                int s = Integer.parseInt(matcher.group(6));
                if (s >= 0 && s <= 59) second = s;
            }
        }

        LocalDate validDate = LocalDate.of(year, month, day);
        LocalDateTime result = LocalDateTime.of(year, month, day, hour, minute, second);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return result.format(formatter);
    }

    public static String formTimeToChinese(String Time) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime dateTime = LocalDateTime.parse(Time, inputFormat);

        // 输出中文格式
        String chineseFormatted = String.format("%d年%d月%d日%d时%d分%d秒",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond()
        );

        return chineseFormatted;
    }

    //et晚则true，反之
    public static boolean compareTime(String st, String et) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        LocalDateTime sTime = LocalDateTime.parse(st, formatter);
        LocalDateTime eTime = LocalDateTime.parse(et, formatter);

        return !sTime.isAfter(eTime);
    }

    public static boolean hasUnexpectedKeys(Map<String, ?> map, Set<String> allowedKeys) {
        for (String key : map.keySet()) {
            if (!allowedKeys.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty())
            return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static List<RaffleData> getGroupCbs(GroupMessageEvent event) {
        RaffleData[] cbs = SwallowsBot.INSTANCE.getRaffles().values().toArray(new RaffleData[0]);

        List<RaffleData> GroupCbs = new ArrayList<>();

        for(RaffleData cb : cbs) {
            if(cb.getGroupID() == event.getGroup().getId()) {
                GroupCbs.add(cb);
            }
        }

        return GroupCbs;
    }

    public static boolean isParticipants(RaffleData raffle, Long ID) {
        return raffle.getParticipants().contains(ID);
    }

    public static List<Long> onLottery(Group group, RaffleData raffle) {

        if(raffle.getMode().equals("N")) {

            List<Long> copy = new ArrayList<>(raffle.getParticipants());

            for (Long id : copy) {
                if(group.get(id) == null) {
                    copy.remove(id);
                }
            }

            if(raffle.getAmount() >= copy.size()) {
                return copy;
            }

            Collections.shuffle(copy);
            return copy.subList(0, raffle.getAmount());
        }
        if(raffle.getMode().equals("W")) {
            return null;
        }
        else {
            return null;
        }
    }

    public static void startWatching(Bot bot) {
        RaffleData[] cbs = SwallowsBot.INSTANCE.getRaffles().values().toArray(new RaffleData[0]);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

            for (RaffleData raffle : cbs) {
                if(raffle.getEndTime().equals(time) || compareTime(raffle.getEndTime(), time)) {

                    Group group = bot.getGroup(raffle.getGroupID());

                    List<Long> IDs = onLottery(group, raffle);
                    raffle.setState(0);
                    raffle.setJackpots(IDs);
                    RaffleDataLoader.saveRaffle(raffle);

                    MessageChainBuilder msg = new MessageChainBuilder();

                    for (Long id : IDs) {
                        At at = new At(id);
                        msg.append(at);
                    }

                    msg.append("恭喜抽中").append(raffle.getRaffleName()).append("的奖励，大伙快恭喜他们！");

                    Util.sendMessageToGroup(msg.build(), group);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

}
