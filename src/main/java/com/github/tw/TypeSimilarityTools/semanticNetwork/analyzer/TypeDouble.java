package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer;


import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by weissger on 31.05.16.
 */
public class TypeDouble {

    Type t;
    Double d;

    public TypeDouble(Type t, Double d) {
        this.t = t;
        this.d = d;
    }

    public Type getType() {
        return t;
    }

    public Double getDouble() {
        return d;
    }

    public int compareTo(Object o) {
        return d.compareTo(((TypeDouble) o).getDouble());
    }
}
