package org.sumin.inference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InferenceScore {
    public List<Scores> scores;
    public List<Double> getScoresList() {
        return scores.stream()
                .map(Scores::getScore)
                .collect(Collectors.toList());
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class Scores {
        public double score;
    }
}
