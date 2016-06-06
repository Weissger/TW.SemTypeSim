package com.github.tw.TypeSimilarityTools.algorithms.impl;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;

import java.net.URI;

/**
 * Created by weissger on 25.04.16.
 */
public class Jcn extends SimilarityAlgorithmSupport {
    public Jcn(SemanticAnalyzer analyzer) {
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

    public double getSimilarity(String conceptA, String conceptB) {
        Type lcs = this.analyzer.getLcs(conceptA, conceptB);

        // Depth 0 cause IC doesn't need anything else than direct properties
        double icA = this.analyzer.getInformationContent(this.analyzer.getTypeByURI(conceptA, 0));
        double icB = this.analyzer.getInformationContent(this.analyzer.getTypeByURI(conceptB, 0));
        double icS = this.analyzer.getInformationContent(lcs);
        double distance = icA + icB - 2 * icS;
        return 1 / distance;
    }
}
