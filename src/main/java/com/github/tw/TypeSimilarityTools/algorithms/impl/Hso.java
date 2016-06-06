package com.github.tw.TypeSimilarityTools.algorithms.impl;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by weissger on 25.04.16.
 */
public class Hso extends SimilarityAlgorithmSupport {
    public Hso(SemanticAnalyzer analyzer) {
        super(analyzer);
    }

    private int CONSTANT_C = 8;

    // TODO WHAT IS MIN AND MAX / HOW TO CALC HSO
    @Override
    public double getMinScore() {
        return 0;
    }

    @Override
    public double getMaxScore() {
        return CONSTANT_C;
    }

    @Override
    public double getSimilarity(String conceptA, String conceptB) {
        LinkedHashMap path = this.analyzer.getShortestPath(conceptA, conceptB);
        return CONSTANT_C - (Integer) path.get("length") - ((ArrayList)path.get("directions")).size();
    }
}
