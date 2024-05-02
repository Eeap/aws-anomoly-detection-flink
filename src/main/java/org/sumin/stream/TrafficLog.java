package org.sumin.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrafficLog {
    // 데이터 구조 재수정 필요
    public String vpcId;
    public String subnetId;
    public String interfaceId;
    public String start;
    public String end;
    public String eventTime;
    public Long getTime() throws ParseException {
        SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS Z");
        setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return setTime.parse(this.eventTime).toInstant().toEpochMilli();
    }
    public String toString() {
        return "Log vpcId: " + this.vpcId +
                " Log subnetId: " + this.subnetId +
                " Log interfaceId: " + this.eventTime +
                " Log start: " + this.start +
                " Log end: " + this.end +
                " Log eventTime: " + this.eventTime;
    }
}
