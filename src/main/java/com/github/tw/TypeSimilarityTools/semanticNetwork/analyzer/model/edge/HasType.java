package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.edge;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "HASTYPE")
public class HasType {
    private Long id;

    @StartNode
    private Type subType;

    @EndNode
    private Type superType;
}
