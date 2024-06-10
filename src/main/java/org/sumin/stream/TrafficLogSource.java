package org.sumin.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.flink.kinesis.shaded.com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"vpcId","subnetId","interfaceId","logStatus","packets","bytes","start","end"})
public class TrafficLogSource implements Serializable {
    public String vpcId;
    public String subnetId;
    public String interfaceId;
    public String logStatus;
    public int packets;
    public int bytes;
    public long start;
    public long end;
    public Long getTime() throws ParseException {
//        SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//        System.out.println(setTime.format(new Date(this.start*1000L)));
//        System.out.println(setTime.format(new Date(System.currentTimeMillis())));
        return this.start * 1000L;
    }
    @Override
    public String toString() {
        return "Log vpcId: " + this.vpcId +
                " Log subnetId: " + this.subnetId +
                " Log interfaceId: " + this.interfaceId +
                " Log logStatus: " + this.logStatus +
                " Log packets: " + this.packets +
                " Log bytes: " + this.bytes +
                " Log start: " + this.start +
                " Log end: " + this.end;
    }
    @Override
    public int hashCode() {
        return this.vpcId.hashCode() + this.subnetId.hashCode() + this.interfaceId.hashCode();
    }
}
