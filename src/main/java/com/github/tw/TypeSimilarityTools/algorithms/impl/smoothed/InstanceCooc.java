package com.github.tw.TypeSimilarityTools.algorithms.impl.smoothed;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;

/**
 * Created by weissger on 25.04.16.
 */
public class InstanceCooc extends SimilarityAlgorithmSupport {
    public InstanceCooc(SemanticAnalyzer analyzer) {
        super(analyzer);
    }

    private static int SMOOTH = 1;

    @Override
    public double getMinScore() {
        return 0;
    }

    @Override
    public double getMaxScore() {
        return 1;
    }

    @Override
    public double getSimilarity(String conceptA, String conceptB) {
        if (conceptA.equals(conceptB)) {
            return getMaxScore();
        }
        double countA = (double)this.analyzer.getInstanceCount(conceptA);
        double countB = (double)this.analyzer.getInstanceCount(conceptB);
        double countS = (double)this.analyzer.getSharedInstanceCount(conceptA, conceptB);
        if (countA == 0 && countB == 0) {
            System.out.println("NO Instances found for: " + conceptA + " " + conceptB);
            return 0;
        }
        return (countS + SMOOTH) / (countA + countB + SMOOTH - countS);
    }
}
