package com.github.tw.TypeSimilarityTools.algorithms.impl.smoothed;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;

/**
 * Created by weissger on 25.04.16.
 */
public class Lin extends SimilarityAlgorithmSupport {
    public Lin(SemanticAnalyzer analyzer) {
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
        Type lcs = this.analyzer.getLcs(conceptA, conceptB);

        // Depth 0 cause IC doesn't need anything else than direct properties
        Type typeA = this.analyzer.getTypeByURI(conceptA, 0);
        Type typeB = this.analyzer.getTypeByURI(conceptB, 0);
        double icA = this.analyzer.getInformationContent(typeA);
        double icB = this.analyzer.getInformationContent(typeB);
        double icS = this.analyzer.getInformationContent(lcs);

        return 2*(icS + SMOOTH)/(icA + icB + (2 * SMOOTH));
    }
}
