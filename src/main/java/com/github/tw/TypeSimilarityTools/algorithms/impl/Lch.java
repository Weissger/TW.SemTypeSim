package com.github.tw.TypeSimilarityTools.algorithms.impl;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;

/**
 * Created by weissger on 25.04.16.
 */
public class Lch extends SimilarityAlgorithmSupport {
    public Lch(SemanticAnalyzer analyzer) {
        super(analyzer);
    }

    @Override
    public double getMinScore() {
        return 0;
    }

    @Override
    public double getMaxScore() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getSimilarity(String conceptA, String conceptB) {
        Integer path = this.analyzer.getShortestPathLength(conceptA, conceptB);
        Integer maxDepthOfNet = this.analyzer.getMaxDepth();
        return (double)-Math.log((double)path / (double) (2 * maxDepthOfNet));
    }
}
