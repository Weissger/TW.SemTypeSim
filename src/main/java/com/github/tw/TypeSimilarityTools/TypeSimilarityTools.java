package com.github.tw.TypeSimilarityTools;

import com.github.tw.TypeSimilarityTools.algorithms.SimilarityAlgorithm;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.Queries;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.SemanticAnalyzer;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.TypeDouble;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.edge.HasType;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.edge.SubClassOf;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Instance;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.exception.CypherException;
import org.neo4j.ogm.session.Session;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by weissger on 10.05.16.
 */
public class TypeSimilarityTools {

    public SemanticAnalyzer getSemanticAnalyzer() {
        return semanticAnalyzer;
    }

    private SemanticAnalyzer semanticAnalyzer;

    public TypeSimilarityTools() {
        try {
            this.semanticAnalyzer = new SemanticAnalyzer();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public TypeSimilarityTools(String address, String user, String password) {
        try {
            this.semanticAnalyzer = new SemanticAnalyzer(address, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.print("Couldn't init analyzer.");
            System.exit(-1);
        }
    }

    public void loadSubClassNetFromCsv(String path, String database) {
        Session session = this.semanticAnalyzer.getSessionFactory().openSession();
        session.query(Queries.Load.nodesFromCsv(path, Type.class.getAnnotation(NodeEntity.class).label(), Type.class.getAnnotation(NodeEntity.class).label()), Collections.EMPTY_MAP);
        session.query(Queries.Load.labelsFromCsv(path, Type.class.getAnnotation(NodeEntity.class).label(), Type.class.getAnnotation(NodeEntity.class).label(), database), Collections.EMPTY_MAP);
        session.query(Queries.Load.relationsFromCsv(path, Type.class.getAnnotation(NodeEntity.class).label(), Type.class.getAnnotation(NodeEntity.class).label(), SubClassOf.class.getAnnotation(RelationshipEntity.class).type()), Collections.EMPTY_MAP);
    }

    public void loadHasTypeFromCsv(String path, String database) {
        Session session = this.semanticAnalyzer.getSessionFactory().openSession();
        session.query(Queries.Load.nodesFromCsv(path, Instance.class.getAnnotation(NodeEntity.class).label(), Type.class.getAnnotation(NodeEntity.class).label()), Collections.EMPTY_MAP);
        session.query(Queries.Load.labelsFromCsv(path, Instance.class.getAnnotation(NodeEntity.class).label(), Type.class.getAnnotation(NodeEntity.class).label(), database), Collections.EMPTY_MAP);
        session.query(Queries.Load.relationsFromCsv(path, Instance.class.getAnnotation(NodeEntity.class).label(), Type.class.getAnnotation(NodeEntity.class).label(), HasType.class.getAnnotation(RelationshipEntity.class).type()), Collections.EMPTY_MAP);
    }

    public void loadSubClassNet(File file, String database) throws IOException {
        loadSubClassNet(database, file);
    }

    public void loadSubClassNet(String graph, File file) throws IOException {
        loadSubClassNodes(graph, file);
        loadSubClassEdges(graph, file);
    }

    private void loadSubClassNodes(String graph, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("#")) {
                String[] splits = line.split(" ");
                if (splits.length != 3) {
                    throw new IOException("Parser error. Line split didn't result in 3 parts. " + line + " Split: " + splits.toString());
                } else {
                    Type typeA = new Type(splits[0].substring(1, splits[0].length() - 1));
                    Type typeB = new Type(splits[2].substring(1, splits[2].length() - 2));
                    silentNodeAdd(typeA);
                    silentNodeAdd(typeB);
                }
            }
        }
    }

    private void loadSubClassEdges(String graph, File file) throws IOException {
        Session session = this.semanticAnalyzer.getSessionFactory().openSession();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("#")) {
                String[] splits = line.split(" ");
                if (splits.length != 3) {
                    throw new IOException("Parser error. Line split didn't result in 3 parts. " + line + " Split: " + splits.toString());
                } else {
                    Type typeA = session.queryForObject(Type.class, Queries.getTypeByUri(splits[0].substring(1, splits[0].length() - 1)), Collections.EMPTY_MAP);
                    Type typeB = session.queryForObject(Type.class, Queries.getTypeByUri(splits[2].substring(1, splits[2].length() - 2)), Collections.EMPTY_MAP);
                    typeA.addSuperClass(typeB);
                    session.save(typeA);
                }
            }
        }
    }

    private void silentNodeAdd(Type type) {
        Session session = this.semanticAnalyzer.getSessionFactory().openSession();
        try {
            session.save(type);
        } catch (CypherException c) {
            System.err.println(c.getDescription());
        }
    }

    // TODO get most descriptive type (Most unsimiliar to root node)
    public <T extends SimilarityAlgorithm> Type getMostDescriptiveType(Class<T> algo, String uri) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Instance instance = this.semanticAnalyzer.getSession().loadAll(Instance.class, new Filter("uri", uri)).iterator().next();
        Set<Type> types = this.semanticAnalyzer.getTransitiveTypeOfs(instance);
        return this.getMostDescriptiveType(algo, types);
    }

    public <T extends SimilarityAlgorithm> Type getMostDescriptiveType(Class<T> algo, Set<Type> types) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Type root = this.semanticAnalyzer.getTypeByURI(TST.RDFSRESOURCE, 0);
        return this.rankTypesBySimilarity(algo, root, types).getFirst().getType();
    }

    public <T extends SimilarityAlgorithm> LinkedList<TypeDouble> rankTypesBySimilarity(Class<T> algo, Type compareToType, Set<Type> types) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        SimilarityAlgorithm algoInstance = algo.getDeclaredConstructor(SemanticAnalyzer.class).newInstance(this.semanticAnalyzer);

        LinkedList<TypeDouble> ranking = new LinkedList<>();
        types.remove(compareToType);

        for (Type type : types) {
            double sim = algoInstance.getSimilarity(compareToType.getUri(), type.getUri());
            ranking.add(new TypeDouble(type, sim));
        }
        ranking.sort(new Comparator<TypeDouble>() {
            @Override
            public int compare(TypeDouble o1, TypeDouble o2) {
                return o1.compareTo(o2);
            }
        });
        return ranking;
    }

    // TODO choose set of types describing the instance (choose most descriptive by what means if it got the same score?)

    public <T extends SimilarityAlgorithm> LinkedList<TypeDouble> rankTypesByAverageInterTypeSimilarity(Class<T> algo, String uri) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Instance instance = this.semanticAnalyzer.getSession().loadAll(Instance.class, new Filter("uri", uri)).iterator().next();
        Set<Type> types = this.semanticAnalyzer.getTransitiveTypeOfs(instance);
        return this.rankTypesByAverageInterTypeSimilarity(algo, types);
    }

    // Rank types to each other (clusters)
    public <T extends SimilarityAlgorithm> LinkedList<TypeDouble> rankTypesByAverageInterTypeSimilarity(Class<T> algo, Set<Type> types) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        SimilarityAlgorithm algoInstance = algo.getDeclaredConstructor(SemanticAnalyzer.class).newInstance(this.semanticAnalyzer);

        HashMap<Type, HashMap<Type, Double>> results = new HashMap();
        for (Type type : types) {
            results.put(type, new HashMap<Type, Double>());
        }

        Iterator it = types.iterator();
        while (it.hasNext()) {
            Type typeA = (Type) it.next();
            it.remove();
            for (Type typeB : types) {
                if (!(typeA == typeB)) {
                    double sim = algoInstance.getSimilarity(typeA.getUri(), typeB.getUri());
                    results.get(typeA).put(typeB, sim);
                    results.get(typeB).put(typeA, sim);
                }
            }
        }

        LinkedList<TypeDouble> ranking = new LinkedList<>();

        for (HashMap.Entry typeEntry :  results.entrySet()) {
            double average = 0;
            for (Object entry : ((HashMap) typeEntry.getValue()).entrySet()) {
                    average += (double)((HashMap.Entry) entry).getValue();
            }
            average /= results.size();
            ranking.add(new TypeDouble((Type) typeEntry.getKey(), average));
        }

        ranking.sort(new Comparator<TypeDouble>() {
            @Override
            public int compare(TypeDouble o1, TypeDouble o2) {
                return o1.compareTo(o2);
            }
        });

        return ranking;
    }

    public <T extends SimilarityAlgorithm> double calculate(Class<T> algo, String uriA, String uriB) {
        try {
            SimilarityAlgorithm algoInstance = algo.getDeclaredConstructor(SemanticAnalyzer.class).newInstance(this.semanticAnalyzer);
            return algoInstance.getSimilarity(uriA, uriB);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return (double)-1.0;
        }
    }

    public <T extends SimilarityAlgorithm> double calculateInstanceSimByAvgTypeSim(Class<T> algo, String uriA, String uriB) {
        SimilarityAlgorithm algoInstance;
        try {
            algoInstance = algo.getDeclaredConstructor(SemanticAnalyzer.class).newInstance(this.semanticAnalyzer);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return (double)-1.0;
        }
        Instance instanceA = this.semanticAnalyzer.getSession().loadAll(Instance.class, new Filter("uri", uriA)).iterator().next();
        Instance instanceB = this.semanticAnalyzer.getSession().loadAll(Instance.class, new Filter("uri", uriB)).iterator().next();
        Set<Type> typesA = this.semanticAnalyzer.getTransitiveTypeOfs(instanceA);
        Set<Type> typesB = this.semanticAnalyzer.getTransitiveTypeOfs(instanceB);

        Set<Type> intersection = new HashSet<>(typesA);
        intersection.retainAll(typesB);

        Set<Type> reducedA = new HashSet<>(typesA);
        reducedA.removeAll(typesB);

        Set<Type> reducedB = new HashSet<>(typesB);
        reducedB.removeAll(typesA);

        HashMap<Type, HashMap<Type, Double>> results = new HashMap<>();
        // for each non shared class of instance A
        //noinspection Duplicates
        for (Type typeA : reducedA) {
            results.put(typeA, new HashMap<Type, Double>());

            // look through all (even shared classes) for good match
            for (Type typeB : typesB) {
                double tmpSim = algoInstance.getSimilarity(typeA.getUri(), typeB.getUri());
                results.get(typeA).put(typeB, tmpSim);
            }
        }

        // for each non shared class of instance B
        //noinspection Duplicates
        for (Type typeB : reducedB) {
            results.put(typeB, new HashMap<Type, Double>());

            // look through all (even shared classes) for good match
            for (Type typeA : typesA) {
                double tmpSim = algoInstance.getSimilarity(typeB.getUri(), typeA.getUri());
                results.get(typeB).put(typeA, tmpSim);
            }
        }

        if (algoInstance.getMaxScore() == Double.POSITIVE_INFINITY) {
            double average = intersection.size();

            for (Map.Entry o : results.entrySet()) {
                double max = 0;
                for (Object o1 : ((HashMap) o.getValue()).entrySet()) {
                    double value = (double) ((Map.Entry) o1).getValue();

                    // infinity to 1
                    value = ((2 / Math.PI) * Math.atan((Math.PI / 2) * 1 * value));
                    if (max < value) {
                        max = value;
                    }
                }
                average += max;
            }

            average /= (intersection.size() + results.size());
            return average;
        } else {
            double average = (intersection.size() * algoInstance.getMaxScore());

            for (Map.Entry o : results.entrySet()) {
                double max = 0;
                for (Object o1 : ((HashMap) o.getValue()).entrySet()) {
                    double value = (double) ((Map.Entry) o1).getValue();
                    if (max < value) {
                        max = value;
                    }
                }
                average += max;
            }

            average /= (intersection.size() + results.size());
            return average;
        }
    }

    public static void main(String[] args) throws Exception {

    }

}
