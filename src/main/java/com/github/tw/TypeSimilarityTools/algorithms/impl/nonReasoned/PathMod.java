package com.github.tw.TypeSimilarityTools.algorithms.impl.nonReasoned;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithmSupport;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.neo4j.kernel.impl.util.register.NeoRegister;

/**
 * Created by weissger on 25.04.16.
 */
public class PathMod extends SimilarityAlgorithmSupport {
    public PathMod(SemanticAnalyzer analyzer) {
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
        Type lcs = this.analyzer.getLcs(conceptA, conceptB);
        Integer pathA = this.analyzer.getLongestPathLengthToSubsumer(conceptA, lcs.getUri());
        Integer pathB = this.analyzer.getLongestPathLengthToSubsumer(conceptB, lcs.getUri());
        return 1 / (double) (pathA + pathB + 1);
    }
}
