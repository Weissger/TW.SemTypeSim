package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.edge;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Instance;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "SUBCLASSOF")
public class SubClassOf {
    private Long id;

    @StartNode
    private Instance instance;

    @EndNode
    private Type type;
}
