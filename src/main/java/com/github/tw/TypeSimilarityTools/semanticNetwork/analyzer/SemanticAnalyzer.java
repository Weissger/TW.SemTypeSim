package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Instance;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.neo4j.kernel.impl.util.register.NeoRegister;
import org.neo4j.ogm.cypher.Filter;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.service.Components;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by weissger on 10.05.16.
 */
public class SemanticAnalyzer {

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private SessionFactory sessionFactory;

    private Session session;

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    private StatisticsManager statisticsManager;

    public SemanticAnalyzer() throws SQLException {
        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        this.init();
    }

    public SemanticAnalyzer(String address, String user, String password) throws SQLException {
        Components.configuration()
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                .setURI("http://" + user + ":" + password +"@"+address);
        this.init();
    }

    private void init() {
        sessionFactory = new SessionFactory("com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node", "com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.edge");
        session = sessionFactory.openSession();
        statisticsManager = new StatisticsManager(session);
        statisticsManager.getStatistics();
    }

    public Session getSession() {
        return session;
    }

    public Type getLcs(String uriA, String uriB) {
        Set<Type> sharedParents = this.getTransitiveSuperClasses(uriA);
        sharedParents.retainAll(this.getTransitiveSuperClasses(uriB));

        Type lcs = null;
        Integer lcsInstanceCount = null;

        for (Type parent : sharedParents) {
            Integer count = this.getInstanceCount(parent);
            if (lcsInstanceCount == null || count < lcsInstanceCount) {
                lcsInstanceCount = count;
                lcs = parent;
            }
        }

        return lcs;
    }

    private Set<Type> getTransitiveSuperClasses (String uri) {
        return this.getTransitiveSuperClasses(this.getTypeByURI(uri, 0));
    }

    private Set<Type> getTransitiveSuperClasses (Type type) {
        String outBinding = "parent";
        if (type.getReasonedSuperClasses() == null) {
            Result result = session.query(Queries.getTransitiveParents(type.getUri(), outBinding), Collections.EMPTY_MAP);
            for (Map aResult : result) {
                type.addReasonedSuperClass((Type) aResult.get(outBinding));
            }
            session.save(type);
        }
        return type.getReasonedSuperClasses();
    }

    public Set<Type> getTransitiveTypeOfs (Instance instance) {
        Set<Type> allTypes = new HashSet<>();
        if (instance.getReasonedTypes() == null) {

            // Sort by depth to reduce transitive search
            ArrayList<Type> types = new ArrayList<>(instance.getTypes());
            types.sort(new Comparator<Type>() {
                @Override
                public int compare(Type o1, Type o2) {
                    return o2.getDepth().compareTo(o1.getDepth());
                }
            });

            // search transitive for each type which isn't already looked at
            for (Type type : types) {
                if (!allTypes.contains(type)) {
                    allTypes.addAll(this.getTransitiveSuperClasses(type));
                }
            }
            instance.addReasonedType(allTypes);
            session.save(instance);
        }
        return instance.getReasonedTypes();
    }

//    public Integer getTypeCount() {
//        return statisticsManager.getStatistics().getTypeCount();
//    }

    private Integer getInstanceCount() {
        return statisticsManager.getStatistics().getInstanceCount();
    }

    private Integer getInstanceCount(Type type) {
        String outBindingCount = "count";
        Integer count;
        if (type.getInstanceCount() != -1) {
            count = type.getInstanceCount();
        } else {
            Result countResult = session.query(Queries.getTransitiveInstanceCount(type.getUri(), outBindingCount), Collections.EMPTY_MAP);
            count = (Integer) countResult.iterator().next().get(outBindingCount);
            type.setInstanceCount(count);
            session.save(type);
        }
        return count;
    }

    public Type getTypeByURI(String uri, int depth) {
        return session.loadAll(Type.class, new Filter("uri", uri), depth).iterator().next();
    }

    public Integer getInstanceCount(String uri) {
        return this.getInstanceCount(this.getTypeByURI(uri, 0));
    }

    public Integer getShortestPathLength(String typeA, String typeB) {
        String outBinding = "length";
        Result result = session.query(Queries.getShortestPathLength(typeA, typeB, outBinding), Collections.EMPTY_MAP);
        return (Integer) result.iterator().next().get(outBinding);
    }

    public Integer getMaxDepth() {
        return statisticsManager.getStatistics().getMaxDepth();
    }

    private double getProbability(Type type) {
        return (double)this.getInstanceCount(type) / (double)this.getInstanceCount();
    }

    public double getInformationContent(Type type) {
        double ic = (double)-Math.log(this.getProbability(type));
        if (ic == -0.0) {
            return 0.0;
        }
        return ic;
    }

    public Integer getSharedInstanceCount(String typeA, String typeB) {
        String outBinding = "count";
        Result result = session.query(Queries.getSharedInstances(typeA, typeB, outBinding), Collections.EMPTY_MAP);
        return (Integer) result.iterator().next().get(outBinding);
    }

    public Integer getLongestPathLength(String from, String to) {
        String outBinding = "length";
        Result result = session.query(Queries.getLongestPathLength(from, to, outBinding), Collections.EMPTY_MAP);
        return (Integer) result.iterator().next().get(outBinding);
    }

    public Integer getLongestPathLengthToSubsumer(String from, String to) {
        String outBinding = "length";
        Result result = session.query(Queries.getLongestPathLengthToSubsumer(from, to, outBinding), Collections.EMPTY_MAP);
        return (Integer) result.iterator().next().get(outBinding);
    }

    public LinkedHashMap getShortestPath(String from, String to) {
        String outBinding = "path";
        Result result = session.query(Queries.getShortestPath(from, to, outBinding), Collections.EMPTY_MAP);
        return (LinkedHashMap) result.iterator().next().get(outBinding);
    }

    public Integer getDepth(Type type) {
        if (type.getDepth() == null) {
            Integer depth = this.getShortestPathLength(type.getUri(), TST.RDFSRESOURCE);
            type.setDepth(depth);
            this.session.save(type);
            return depth;
        }
        return type.getDepth();
    }
}
