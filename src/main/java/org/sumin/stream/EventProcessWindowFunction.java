package org.sumin.stream;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class EventProcessWindowFunction extends ProcessWindowFunction<TrafficLog, String, String, TimeWindow> {
    @Override
    public void process(String aLong, ProcessWindowFunction<TrafficLog, String, String, TimeWindow>.Context context, Iterable<TrafficLog> iterable, Collector<String> collector) throws Exception {
        // 새로운 데이터 형태로 1분 동안의 데이터 집계를 저장하는 방식으로 수정
    }
}
