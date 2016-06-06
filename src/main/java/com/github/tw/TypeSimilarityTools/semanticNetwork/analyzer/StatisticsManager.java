package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Statistics;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.session.Session;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by weissger on 20.05.16.
 */
public class StatisticsManager {
    private Session session;
    private Statistics statistics;

    public StatisticsManager(Session session) {
        this.session = session;
    }

    public Statistics getStatistics() {
        if (statistics != null) {
            return statistics;
        } else {
            Collection<Statistics> stats = this.session.loadAll(Statistics.class);
            if (stats.iterator().hasNext()) {
                return stats.iterator().next();
            } else {
                return this.initDataBase();
            }
        }
    }

    private Statistics initDataBase() {
        Statistics stats = new Statistics();

        // Add unique constaint on uri
        this.session.query(Queries.Preparation.addUniqueConstraint(Type.class.getAnnotation(NodeEntity.class).label(), "uri"), Collections.EMPTY_MAP);

        // Add root node if needed
        this.session.query(Queries.Preparation.getAddUniqueNode(TST.RDFSRESOURCE), Collections.EMPTY_MAP);

        // Add missing edges to root
        Long addingRoot = System.nanoTime();
        stats.setAddedRootEdgesCount((Integer) this.session.query(Queries.Preparation.getAddSubClassOfRDFSResourceToTopClasses("addedCount"), Collections.EMPTY_MAP).iterator().next().get("addedCount"));
        Long addingRootDuration = System.nanoTime() - addingRoot;
        stats.setAddedRootEdgesTime(addingRootDuration);

        this.session.save(stats);

        // Transitive reduction of not needed SubClassOf edges
        Long reductionStartTime = System.nanoTime();
        stats.setTransitiveReductionDeleteCount((Integer) this.session.query(Queries.Preparation.getReduction("delCount"), Collections.EMPTY_MAP).iterator().next().get("delCount"));
        Long reductionDuration = System.nanoTime() - reductionStartTime;
        stats.setTransitiveReductionTime(reductionDuration);

        this.session.save(stats);

        // Reasoning and insertion of ReasonedHasType edges
        Long hasTypeReasonStartTime = System.nanoTime();
        this.session.query(Queries.Preparation.addReasonedHasTypeEdges(), Collections.EMPTY_MAP);
        Long hasTypeReasonDuration = System.nanoTime() - hasTypeReasonStartTime;
        stats.setHasTypeReasonTime(hasTypeReasonDuration);

        this.session.save(stats);

        // Instance counting for types
        Long countingStartTime = System.nanoTime();
        this.session.query(Queries.Preparation.addAllInstanceCounts(), Collections.EMPTY_MAP);
        Long countingDuration = System.nanoTime() - countingStartTime;
        stats.setInstanceCountingDuration(countingDuration);

        this.session.save(stats);

        stats.setTypeCount((Integer) this.session.query(Queries.Preparation.getTypeCount("count"), Collections.EMPTY_MAP).iterator().next().get("count"));
        stats.setInstanceCount((Integer) this.session.query(Queries.Preparation.getInstanceCount("count"), Collections.EMPTY_MAP).iterator().next().get("count"));
        stats.setMaxDepth((Integer) this.session.query(Queries.Preparation.getMaxDepth("maxDepth"), Collections.EMPTY_MAP).iterator().next().get("maxDepth"));

        this.session.save(stats);

        //calculate depth of each type
        Long depthStartTime = System.nanoTime();
        this.session.query(Queries.Preparation.addTypeDepths(TST.RDFSRESOURCE), Collections.EMPTY_MAP);
        Long depthDuration = System.nanoTime() - depthStartTime;
        stats.setTypeDepthCalculationDuration(depthDuration);

        this.session.save(stats);
        return stats;
    }

    public Statistics forceReload() {
        if (this.statistics != null) {
            this.session.delete(this.statistics);
        }
        return initDataBase();
    }

}
