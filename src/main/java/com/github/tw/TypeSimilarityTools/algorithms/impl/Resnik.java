package com.github.tw.TypeSimilarityTools.algorithms.impl;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;

/**
 * Created by weissger on 25.04.16.
 */
public class Resnik extends SimilarityAlgorithmSupport {
    public Resnik(SemanticAnalyzer analyzer) {
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
        if(conceptA.equals(conceptB)) {
            return this.getMaxScore();
        }
        Type lcs = this.analyzer.getLcs(conceptA, conceptB);
        return this.analyzer.getInformationContent(lcs);
    }
}
