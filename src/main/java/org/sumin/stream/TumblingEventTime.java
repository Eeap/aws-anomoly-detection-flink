package org.sumin.stream;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.kinesis.shaded.org.apache.flink.connector.aws.config.AWSConfigConstants;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.streaming.connectors.kinesis.config.ConsumerConfigConstants;
import org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class TumblingEventTime {
    public StreamExecutionEnvironment env;
    private Dotenv dotenv;
    public TumblingEventTime(StreamExecutionEnvironment env) {
        this.env = env;
        this.dotenv = Dotenv.load();
    }
    public JobExecutionResult execute() throws Exception {
        // Timezone setting
        SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        // kinesis consumer config
        Properties consumerConfig = new Properties();
        consumerConfig.put(AWSConfigConstants.AWS_REGION, dotenv.get("AWS_REGION"));
        consumerConfig.put(AWSConfigConstants.AWS_ACCESS_KEY_ID, dotenv.get("AWS_ACCESS_KEY_ID"));
        consumerConfig.put(AWSConfigConstants.AWS_SECRET_ACCESS_KEY, dotenv.get("AWS_SECRET_ACCESS_KEY"));
        consumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, dotenv.get("STREAM_INITIAL_POSITION"));

        // Streaming Source
        DataStreamSource<TrafficLogSource> source = env.addSource(new FlinkKinesisConsumer<>("test-stream"
                , new KinesisStreamDeserializer()
                , consumerConfig));

        /** test source code
         *         DataStreamSource<TrafficLogSource> source = env.addSource(new SourceFunction<TrafficLogSource>() {
         *             @Override
         *             public void run(SourceContext<TrafficLogSource> sourceContext) throws Exception {
         *                 SimpleDateFormat setTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
         *                 setTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
         *
         *                 while (true) {
         *                     for (int i = 0; i < 10; i++) {
         *                         sourceContext.collect(new TrafficLogSource("vpc-"+i, "subnet-"+i, "interface-"+i, null,1,1024, System.currentTimeMillis(), System.currentTimeMillis(), setTime.format(System.currentTimeMillis())));
         *                     }
         *                     Thread.sleep(1000);
         *                 }
         *             }
         *
         *             @Override
         *             public void cancel() {
         *
         *             }
         *         });
         *
         * **/
        // Tumbling Event Time Window
        source.assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<TrafficLogSource>forMonotonousTimestamps()
                                .withIdleness(Duration.ofSeconds(20))
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
//                        System.out.println("key By TrafficLogSource: " + trafficLogSource.toString());
                        return Tuple3.of(trafficLogSource.vpcId, trafficLogSource.subnetId, trafficLogSource.interfaceId);
                    }
                })
                .window(TumblingEventTimeWindows.of(Time.seconds(60)))
                .allowedLateness(Time.seconds(10))
                .process(new EventProcessWindowFunction())
                .print();
        return env.execute("anomoly detection flink Job");
    }
}
