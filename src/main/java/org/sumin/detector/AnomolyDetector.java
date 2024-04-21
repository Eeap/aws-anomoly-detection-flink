package org.sumin.detector;

import com.amazon.randomcutforest.RandomCutForest;

public class AnomolyDetector {
    public void test() {
        RandomCutForest forest = RandomCutForest.builder().dimensions(2).sampleSize(256).numberOfTrees(50).build();
    }
}
