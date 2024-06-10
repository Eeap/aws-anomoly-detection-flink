package org.sumin.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchema;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class KinesisStreamDeserializer implements KinesisDeserializationSchema<TrafficLogSource> {
    private ObjectMapper objectMapper;
    @Override
    public TypeInformation<TrafficLogSource> getProducedType() {
        return TypeInformation.of(TrafficLogSource.class);
    }

    @Override
    public void open(DeserializationSchema.InitializationContext context) throws Exception {
        objectMapper = new ObjectMapper();
        KinesisDeserializationSchema.super.open(context);
    }

    @Override
    public TrafficLogSource deserialize(byte[] bytes, String partitionKey, String seqNum, long approxArrivalTimestamp, String stream, String shardId) throws IOException {
        return getTrafficLogSource(bytes);
    }
    private TrafficLogSource getTrafficLogSource(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        GZIPInputStream gis = new GZIPInputStream(bis);
        InputStreamReader reader = new InputStreamReader(gis);
        BufferedReader in = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }

        CloudWatchLog o = objectMapper.readValue(sb.toString(), CloudWatchLog.class);
        if (o.getMessageType().equals("CONTROL_MESSAGE")) {
            return new TrafficLogSource("NODATA", "NODATA", "NODATA", "NODATA", 0, 0, 0, 0);
        }
        String message = o.getLogEvents().get(0).getMessage();
        System.out.println(message);
        String[] s = message.split(" ");
        if (s[3].equals("NODATA")) {
            return new TrafficLogSource(s[0], s[1], s[2], s[3], 0, 0, Long.parseLong(s[6]), Long.parseLong(s[7]));
        }
        else {
            return new TrafficLogSource(s[0], s[1], s[2], s[3], Integer.parseInt(s[4]), Integer.parseInt(s[5]), Long.parseLong(s[6]), Long.parseLong(s[7]));
        }
    }
}
