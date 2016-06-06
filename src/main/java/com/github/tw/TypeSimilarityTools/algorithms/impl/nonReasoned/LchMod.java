package com.github.tw.TypeSimilarityTools.algorithms.impl.nonReasoned;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;

/**
 * Created by weissger on 25.04.16.
 */
public class LchMod extends SimilarityAlgorithmSupport {
    public LchMod(SemanticAnalyzer analyzer) {
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
        Type lcs = this.analyzer.getLcs(conceptA, conceptB);

        Integer pathA = this.analyzer.getLongestPathLengthToSubsumer(conceptA, lcs.getUri());
        Integer pathB = this.analyzer.getLongestPathLengthToSubsumer(conceptB, lcs.getUri());
        Integer maxDepthOfNet = this.analyzer.getMaxDepth();
        return (double)-Math.log((double)(pathA+pathB) / (double) (2 * maxDepthOfNet));
    }
}
