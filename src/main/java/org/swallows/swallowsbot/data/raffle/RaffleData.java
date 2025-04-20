package org.swallows.swallowsbot.data.raffle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaffleData {

    private String RaffleTime;
    private String RaffleName;
    //状态码 0:结束 1:进行 2:暂停 -1中止
    private int State;
    private int Amount;
    private Long OriginatorID;
    private Long GroupID;
    private String StartTime;
    private String EndTime;
    //模式 N:普通 W:权重
    private String Mode;
    private List<Long> Participants = new ArrayList<>();
    private List<Long> Jackpots = new ArrayList<>();

    public RaffleData() {}

    public RaffleData(String RaffleName, Long OriginatorID) {
        this.RaffleName = RaffleName;
        this.OriginatorID = OriginatorID;
    }

    public String getRaffleTime() {
        return RaffleTime;
    }

    public String getRaffleName() {
        return RaffleName;
    }

    public int getState() {
        return State;
    }

    public int getAmount() {
        return Amount;
    }

    public Long getOriginatorID() {
        return OriginatorID;
    }

    public Long getGroupID() {
        return GroupID;
    }

    public String getStartTime() {
        return StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public String getMode() {
        return Mode;
    }

    public List<Long> getParticipants() {
        return Participants;
    }

    public List<Long> getJackpots() {
        return Jackpots;
    }

    public void setRaffleTime(String raffleTime) {
        RaffleTime = raffleTime;
    }

    public void setRaffleName(String raffleName) {
        RaffleName = raffleName;
    }

    public void setState(int state) {
        State = state;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public void setOriginatorID(Long OriginatorID) {
        this.OriginatorID = OriginatorID;
    }

    public void setGroupID(Long GroupID) {
        this.GroupID = GroupID;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public void setMode(String Mode) {
        this.Mode = Mode;
    }

    public void setParticipants(List<Long> Participants) {
        this.Participants = Participants;
    }

    public void setJackpots(List<Long> Jackpots) {
        this.Jackpots = Jackpots;
    }

    public void addParticipants(Long ParticipantID) {
        Participants.add(ParticipantID);
    }

    public void addJackpots(Long JackpotID) {
        Jackpots.add(JackpotID);
    }

    public void removeParticipants(Long ParticipantID) {
        Participants.remove(ParticipantID);
    }

    public void removeJackpots(Long JackpotID) {
        Jackpots.remove(JackpotID);
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("RaffleTime", RaffleTime);
        data.put("RaffleName", RaffleName);
        data.put("State", State);
        data.put("Amount", Amount);
        data.put("OriginatorID", OriginatorID);
        data.put("GroupID", GroupID);
        data.put("StartTime", StartTime);
        data.put("EndTime", EndTime);
        data.put("Mode", Mode);
        data.put("Participants", Participants);
        data.put("Jackpots", Jackpots);

        return data;
    }

}
