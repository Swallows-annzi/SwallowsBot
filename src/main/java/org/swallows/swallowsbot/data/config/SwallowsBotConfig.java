package org.swallows.swallowsbot.data.config;

import org.swallows.swallowsbot.SwallowsBot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwallowsBotConfig {
    public static long BotID = 123456789L;

    public static List<Long> GroupIDs = Arrays.asList(667123464L);
    public static List<Long> AdminIDs = Arrays.asList(1598773037L);

    public static Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("BotID", BotID);
        data.put("GroupIDs", GroupIDs);
        data.put("AdminIDs", AdminIDs);
        return data;
    }

    public static void setData(Map<String, Object> data) {
        try {
            BotID = ((Number) data.get("BotID")).longValue();
            GroupIDs = (List<Long>) data.get("GroupIDs");
            AdminIDs = (List<Long>) data.get("AdminIDs");
        }
        catch (Exception e) {
            SwallowsBot.INSTANCE.logger.error(e);
        }

    }
}
