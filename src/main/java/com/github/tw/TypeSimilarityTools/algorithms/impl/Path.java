package com.github.tw.TypeSimilarityTools.algorithms.impl;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;

import java.net.URI;

/**
 * Created by weissger on 25.04.16.
 */
public class Path extends SimilarityAlgorithmSupport {
    public Path(SemanticAnalyzer analyzer) {
        super(analyzer);
    }

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
        Integer path = this.analyzer.getShortestPathLength(conceptA, conceptB) + 1;
        return 1 / (double) path;
    }
}
