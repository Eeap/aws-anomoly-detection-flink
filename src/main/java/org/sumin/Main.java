package org.sumin;

import com.amazon.randomcutforest.RandomCutForest;
import com.amazon.randomcutforest.runner.LineTransformer;
import org.sumin.detector.AnomolyDetector;
import org.sumin.stream.DataStreamJob;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {
//    private List<String> result;
    public static void main(String[] args) {
        DataStreamJob dataStreamJob = new DataStreamJob();
        try {
            dataStreamJob.run();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        for (int i = 0; i < 1000; i++) {
//            runAnomolyDetector(i);
//        }

        System.out.println("anomoly detector");
    }

//    private void runAnomolyDetector(int index) {
//        double[] data = makeData(index);
//        List<String> test = new AnomolyDetector((RandomCutForest forest) -> new LineTransformer() {
//            @Override
//            public List<String> getResultValues(double[] point) {
//                double score = forest.getAnomalyScore(point);
//                forest.update(point);
//                return Collections.singletonList(Double.toString(score));
//            }
//
//            @Override
//            public List<String> getEmptyResultValue() {
//                return Collections.singletonList("NA");
//            }
//
//            @Override
//            public List<String> getResultColumnNames() {
//                return Collections.singletonList("anomaly_score");
//            }
//
//            @Override
//            public RandomCutForest getForest() {
//                return forest;
//            }
//        }).run(data);
//        result.addAll(test);
//        System.out.println(result);
//    }
//
//    private static double[] makeData(int index) {
//        double[] data = new double[100];
//        for (int i = 0; i < data.length; i++) {
//            if (index % 300 == 0) {
//                data[i] = 7;
//            } else {
//                data[i] = randomValue();
//            }
//        }
//        return data;
//    }
//
//    private static double randomValue() {
//        Random random = new Random();
//        return random.nextDouble()+4;
//    }
}