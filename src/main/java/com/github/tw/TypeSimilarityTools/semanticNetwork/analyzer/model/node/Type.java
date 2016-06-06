package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = TST.TYPE)
public class Type extends Resource {

    public Type() {
    }

    public Type(String uri, Set<Type> superClasses) {
        super(uri);
        this.superClasses = superClasses;
    }

    public Type(String uri) {
        super(uri);
    }

    @Relationship(type=TST.SUBCLASSOF, direction = Relationship.OUTGOING)
    private Set<Type> superClasses = new HashSet<Type>();

    @Relationship(type=TST.REASONEDSUBCLASSOF, direction = Relationship.OUTGOING)
    private Set<Type> reasonedSuperClasses;

    private int instanceCount = -1;

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    private Integer depth = -1;

    public void addSuperClass(Type... types) {
        Collections.addAll(this.superClasses, types);
    }

    public Set<Type> getSuperClasses() {
        return superClasses;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public void addReasonedSuperClass(Type... types) {
        if (this.reasonedSuperClasses == null) {
            this.reasonedSuperClasses = new HashSet<>();
        }
        Collections.addAll(this.reasonedSuperClasses, types);
    }

    public Set<Type> getReasonedSuperClasses() {
        return reasonedSuperClasses;
    }
}
