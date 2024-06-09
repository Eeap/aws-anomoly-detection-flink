package org.sumin.stream;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.sumin.inference.AnomolyInference;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimeZone;

public class EventProcessWindowFunction extends ProcessWindowFunction<TrafficLogSource, TrafficLog, Tuple3<String,String,String>, TimeWindow> {
    ValueState<TrafficLog> trafficState;
    String startTime;
    @Override
    public void process(Tuple3<String,String,String> key, ProcessWindowFunction<TrafficLogSource, TrafficLog, Tuple3<String,String,String>, TimeWindow>.Context context, Iterable<TrafficLogSource> iterable, Collector<TrafficLog> collector) throws Exception {
        trafficState = context.globalState().getState(new ValueStateDescriptor<>("trafficState", TrafficLog.class));
        int packetCnt = 0;
        // packet count
        for (TrafficLogSource trafficLogSource : iterable) {
            packetCnt += trafficLogSource.packets;
        }
        // traffic state update
        TrafficLog trafficLog = trafficState.value();
        if (trafficLog == null) {
            trafficState.update(new TrafficLog(
                    key.f0,
                    key.f1,
                    key.f2,
                    new ArrayList<>(Arrays.asList(packetCnt)),
                    startTime,
                    new ArrayList<>()
            ));
        }
        else {
            trafficLog.packets.add(packetCnt);
            trafficState.update(trafficLog);
        }
        // check time out (60min)
        boolean checkResult = checkTimeOut(context);
        if (checkResult) {
            // need to implement inference and sink logic
            String result = callInference(trafficState.value());
            // need to implement traffic transformation logic
//                    inference.inference(trafficState.value());
            trafficLog = trafficState.value();
            // score update
//            trafficLog.scores.add(result);
            // sink trafficLog
            collector.collect(trafficLog);
            // clear traffic state
            trafficState.clear();
            startTime = null;
        }
    }

    private static String callInference(TrafficLog value) {
        AnomolyInference inference = new AnomolyInference();
        try {
            inference.run(transformTraffic(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private boolean checkTimeOut(ProcessWindowFunction<TrafficLogSource, TrafficLog, Tuple3<String, String, String>, TimeWindow>.Context context) {
        SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        // timer check
        if (startTime != null) {
            try {
                if (context.window().getEnd() - setTime.parse(startTime).getTime() >= Time.minutes(60).toMilliseconds()) {
                    startTime = null;
                    return true;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            startTime = setTime.format(context.window().getStart());
        }
        return false;
    }
    private static InputStream transformTraffic(TrafficLog value) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        try {
            oos.writeObject(value);
            oos.flush();
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            oos.close();
            bos.close();
        }
    }

}
