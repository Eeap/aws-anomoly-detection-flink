package org.sumin.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrafficLog {
    public String vpcId;
    public String subnetId;
    public String interfaceId;
    @TypeInfo(PacketTypeInfo.class)
    public List<Integer> packets;
    public String start;
    @TypeInfo(PacketTypeInfo.class)
    public List<Integer> scores;
    public Long getTime() throws ParseException {
        SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS Z");
        setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return setTime.parse(this.start).toInstant().toEpochMilli();
    }
    public String toString() {
        return "Log vpcId: " + this.vpcId +
                " Log subnetId: " + this.subnetId +
                " Log interfaceId: " + this.interfaceId +
                " Log packets: " + this.packets +
                " Log start: " + this.start +
                " Log scores: " + this.scores;
    }
}
