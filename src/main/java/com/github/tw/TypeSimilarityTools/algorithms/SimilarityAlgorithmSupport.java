package com.github.tw.TypeSimilarityTools.algorithms;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;

/**
 * Created by weissger on 25.04.16.
 */
public abstract class SimilarityAlgorithmSupport implements SimilarityAlgorithm {

    protected SemanticAnalyzer analyzer;

    public SimilarityAlgorithmSupport(SemanticAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public double getMinScore() {
        return -1;
    }

    public double getMaxScore() {
        return -1;
    }
}
