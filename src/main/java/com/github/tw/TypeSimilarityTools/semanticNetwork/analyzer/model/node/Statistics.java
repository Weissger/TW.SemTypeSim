package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = TST.STATISTICS)
public class Statistics extends Entity {
    public Statistics() {
    }

    private int typeCount = -1;
    private int instanceCount = -1;
    private int maxDepth = -1;

    private int transitiveReductionDeleteCount = -1;
    private long transitiveReductionTime = -1;

    private int addedRootEdgesCount = -1;
    private long addedRootEdgesTime = -1;

    private long instanceCountingDuration = -1;

    private long typeDepthCalculationDuration = -1;

    private long hasTypeReasonTime = -1;

    public int getTypeCount() {
        return typeCount;
    }

    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getTransitiveReductionDeleteCount() {
        return transitiveReductionDeleteCount;
    }

    public void setTransitiveReductionDeleteCount(int transitiveReductionDeleteCount) {
        this.transitiveReductionDeleteCount = transitiveReductionDeleteCount;
    }

    public long getTransitiveReductionTime() {
        return transitiveReductionTime;
    }

    public void setTransitiveReductionTime(long transitiveReductionTime) {
        this.transitiveReductionTime = transitiveReductionTime;
    }

    public int getAddedRootEdgesCount() {
        return addedRootEdgesCount;
    }

    public void setAddedRootEdgesCount(int addedRootEdgesCount) {
        this.addedRootEdgesCount = addedRootEdgesCount;
    }

    public long getAddedRootEdgesTime() {
        return addedRootEdgesTime;
    }

    public void setAddedRootEdgesTime(long addedRootEdgesTime) {
        this.addedRootEdgesTime = addedRootEdgesTime;
    }

    public long getInstanceCountingDuration() {
        return instanceCountingDuration;
    }

    public void setInstanceCountingDuration(long instanceCountingDuration) {
        this.instanceCountingDuration = instanceCountingDuration;
    }

    public long getTypeDepthCalculationDuration() {
        return typeDepthCalculationDuration;
    }

    public void setTypeDepthCalculationDuration(long typeDepthCalculationDuration) {
        this.typeDepthCalculationDuration = typeDepthCalculationDuration;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public void setHasTypeReasonTime(long hasTypeReasonTime) {
        this.hasTypeReasonTime = hasTypeReasonTime;
    }
}
