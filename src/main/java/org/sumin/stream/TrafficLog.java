package org.sumin.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.flink.api.common.typeinfo.TypeInfo;
import org.apache.flink.kinesis.shaded.com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"vpcId","subnetId","interfaceId","packets","start","scores"})
public class TrafficLog implements Serializable {
    public String vpcId;
    public String subnetId;
    public String interfaceId;
    @TypeInfo(PacketTypeInfo.class)
    public List<Integer> packets;
    public String start;
    @TypeInfo(PacketTypeInfo.class)
    public List<Double> scores;
    public Long getTime() throws ParseException {
        SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return setTime.parse(this.start).toInstant().toEpochMilli();
    }
    @Override
    public String toString() {
        return "Log vpcId: " + this.vpcId +
                " Log subnetId: " + this.subnetId +
                " Log interfaceId: " + this.interfaceId +
                " Log packets: " + this.packets.toString() +
                " Log start: " + this.start +
                " Log scores: " + this.scores.toString();
    }
    @Override
    public int hashCode() {
        return this.vpcId.hashCode() + this.subnetId.hashCode() + this.interfaceId.hashCode() + this.start.hashCode();
    }

}
