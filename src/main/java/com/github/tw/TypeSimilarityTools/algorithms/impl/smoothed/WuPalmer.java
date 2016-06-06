package com.github.tw.TypeSimilarityTools.algorithms.impl.smoothed;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;

/**
 * Created by weissger on 25.04.16.
 */
public class WuPalmer extends SimilarityAlgorithmSupport {
    public WuPalmer(SemanticAnalyzer analyzer) {
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
        // Depth of lcs
        double pathS = (double)this.analyzer.getDepth(lcs);
        double pathA = (double)this.analyzer.getShortestPathLength(conceptA, lcs.getUri());
        double pathB = (double)this.analyzer.getShortestPathLength(conceptB, lcs.getUri());

        return 2*(double)(pathS + SMOOTH)/(pathA + pathB + 2*(double)pathS + (4 * SMOOTH));
    }
}
