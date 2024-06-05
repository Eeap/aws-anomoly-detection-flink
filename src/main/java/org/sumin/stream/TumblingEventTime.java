package org.sumin.stream;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.kinesis.shaded.org.apache.flink.connector.aws.config.AWSConfigConstants;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Properties;
import java.util.TimeZone;

public class TumblingEventTime {
    public StreamExecutionEnvironment env;
    public TumblingEventTime(StreamExecutionEnvironment env) {
        this.env = env;
    }
    public JobExecutionResult execute() throws Exception {
        // Timezone setting
        SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS Z");
        setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        // kinesis consumer config
        Properties consumerConfig = new Properties();
        consumerConfig.put(AWSConfigConstants.AWS_REGION, System.getenv("AWS_REGION"));
        consumerConfig.put(AWSConfigConstants.AWS_ACCESS_KEY_ID, System.getenv("AWS_ACCESS_KEY_ID"));
        consumerConfig.put(AWSConfigConstants.AWS_SECRET_ACCESS_KEY, System.getenv("AWS_SECRET_ACCESS_KEY"));
        consumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, System.getenv("STREAM_INITIAL_POSITION"));

        // Streaming Source
        DataStreamSource<TrafficLogSource> source = env.addSource(new FlinkKinesisConsumer<>("test-stream", new JsonDeserializationSchema<>(TrafficLogSource.class), consumerConfig));

        // Tumbling Event Time Window
        source.assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<TrafficLogSource>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                                .withTimestampAssigner((log, timestamp) -> {
                                    try {
                                        return log.getTime();
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }))
                .keyBy(new KeySelector<TrafficLogSource, Tuple3<String,String,String>>() {
                    @Override
                    public Tuple3<String, String, String> getKey(TrafficLogSource trafficLogSource) throws Exception {
                        return Tuple3.of(trafficLogSource.vpcId, trafficLogSource.subnetId, trafficLogSource.interfaceId);
                    }
                })
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .process(new EventProcessWindowFunction())
                .print();
        return env.execute("anomoly detection flink Job");
    }
}
