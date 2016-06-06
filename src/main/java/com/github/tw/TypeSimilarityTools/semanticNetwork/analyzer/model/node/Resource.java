package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node;

public abstract class Resource extends Entity {
    public Resource(String uri) {
        this.uri = uri;
    }

    public Resource() {
    }

    public String getUri() {
        return uri;
    }

    private String uri;
}
