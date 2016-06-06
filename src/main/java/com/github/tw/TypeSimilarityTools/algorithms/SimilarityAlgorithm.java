package com.github.tw.TypeSimilarityTools.algorithms;

import java.net.URI;

/**
 * Created by weissger on 25.04.16.
 */
public interface SimilarityAlgorithm {

    double getSimilarity(String conceptA, String conceptB);

    double getMaxScore();
    double getMinScore();
}
