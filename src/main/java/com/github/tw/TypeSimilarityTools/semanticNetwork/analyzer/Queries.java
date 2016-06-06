package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Resource;
import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node.Type;
import org.neo4j.cypher.internal.compiler.v2_2.functions.Str;
import org.neo4j.ogm.annotation.NodeEntity;

import java.text.MessageFormat;
/**
 * Created by weissger on 25.04.16.
 */
public class Queries {

    //fetch
    public static String getTypeByUri(String value) {
        return getByProperty(Type.class.getAnnotation(NodeEntity.class).label(), "uri", value);
    }

    public static String getByUri(String label, String value) {
        return getByProperty(label, "uri", value);
    }

    public static String getByProperty(String label, String property, String value) {
        return MessageFormat.format("MATCH (a:{0} '{'{1}: ''{2}'''}') return a", label, property, value);
    }

    public static String getRandomNodes(String type, int number, String outBinding) {
        return "MATCH (" + outBinding + ":" + type + ") RETURN " + outBinding + ", rand() as r ORDER BY r LIMIT " + number;
    }

    public static class Load {
        // TODO USING PERIODIC COMMIT support
        public static String nodesFromCsv(String path, String labelLeft, String labelRight) {
            return MessageFormat.format("\n" +
                    "LOAD CSV FROM ''{0}'' AS line FIELDTERMINATOR '';'' WITH line\n" +
                    "MERGE (line[0]:{1} '{' uri: line[0]'}')\n" +
                    "MERGE (line[1]:{2} '{' uri: line[1]'}')\n", path, labelLeft, labelRight);
        }

        public static String labelsFromCsv(String path, String labelLeft, String labelRight, String database) {
            return MessageFormat.format("\n" +
                    "LOAD CSV FROM ''{0}'' AS line FIELDTERMINATOR '';'' WITH line\n" +
                    "MATCH (c:{1} '{'uri: line[0]'}')\n" +
                    "MATCH (c2:{2} '{'uri: line[1]'}')\n" +
                    "SET c :{3}\n" +
                    "SET c2 :{3}\n", path, labelLeft, labelRight, database);
        }

        public static String relationsFromCsv(String path, String labelLeft, String labelRight, String propertyLabel) {
            return MessageFormat.format("\n" +
                    "LOAD CSV FROM ''{0}'' AS line FIELDTERMINATOR '';''\n" +
                    "MATCH (t:{1} '{'uri: line[0]'}'), (t2:{2} '{'uri: line[1]'}') CREATE UNIQUE (t)-[:{3}]->(t2)", path, labelLeft, labelRight, propertyLabel);
        }
    }

    public static class Preparation {
        // Prepare
        public static String getTypeCount(String outBinding) {
            return "MATCH (a:"+TST.TYPE+") return count(a) AS " + outBinding;
        }

        public static String getInstanceCount(String outBinding) {
            return "MATCH (a:"+TST.INSTANCE+") return count(a) AS " + outBinding;
        }

        @Deprecated
        //Works before reasoning
        public static String getAddAllInstanceCounts() {
            return "MATCH (i:"+TST.TYPE+") WITH i MATCH (i)<-[:"+TST.SUBCLASSOF+"*0..]-(type2:"+TST.TYPE+")<-[:HAS"+TST.TYPE+"]-(instance:INSTANCE) WITH i, count(DISTINCT instance) AS c SET i.instanceCount = c";
        }

        //Only after reasoning
        public static String addAllInstanceCounts() {
            return "MATCH (i:"+TST.TYPE+") WITH i MATCH (i)<-[:HAS"+TST.REASONEDHASTYPE+"]-(instance:INSTANCE) WITH i, count(DISTINCT instance) AS c SET i.instanceCount = c";
        }

        public static String getReduction(String outBinding) {
            return "MATCH (o:TYPE)<-[:SUBCLASSOF]-(n:TYPE)-[rel:SUBCLASSOF]->(m:TYPE) WITH n, rel, m, o MATCH p = (o)-[:SUBCLASSOF*1..]->(m) DELETE rel return count(n) AS " + outBinding;
        }

        public static String getAddUniqueNode(String uri) {
            return "MERGE (root:"+TST.TYPE+" {uri: \""+uri+"\"})";
        }

        public static String getAddSubClassOfRDFSResourceToTopClasses(String outBinding) {
            return "MATCH (a:"+TST.TYPE+"), (r:"+TST.TYPE+" {uri: \""+TST.RDFSRESOURCE+"\"}) WHERE NOT (a:"+TST.TYPE+")-->(:"+TST.TYPE+") CREATE (a)-[:"+TST.SUBCLASSOF+"]->(r) return count(a) AS " + outBinding;
        }

        public static String addUniqueConstraint(String label, String property) {
            return MessageFormat.format("CREATE CONSTRAINT ON (a:{0}) ASSERT a.{1} IS UNIQUE", label, property);
        }

        public static String addTypeDepths(String root) {
            return "MATCH (a:"+TST.TYPE+"), (b:"+TST.TYPE+" {uri: \""+root+"\"}), p = shortestPath((a)-[:"+TST.SUBCLASSOF+"*0..]-(b)) WITH a, LENGTH(p) AS depth SET a.depth = depth";
        }

        public static String addReasonedHasTypeEdges() {
            return "MATCH (instance:"+TST.INSTANCE+")-[:"+TST.HASTYPE+"]->()-[:"+TST.SUBCLASSOF+"*0..]->(type:"+TST.TYPE+") WITH DISTINCT type, instance CREATE (instance)-[:"+TST.REASONEDHASTYPE+"]->(type)";
        }

        public static String getMaxDepth(String outBinding) {
            return "MATCH p = (a:"+TST.TYPE+") -[:"+ TST.SUBCLASSOF+"*0..]->(b:"+TST.TYPE+") return MAX(length(p)) AS " + outBinding;
        }
    }

//    //Analyze
//    @Deprecated
//    public static String getTransitiveInstanceCountNoReasoned(String uri, String outBinding) {
//        return "MATCH (type:"+TST.TYPE+" {uri:\"" + uri + "\"})<-[*0..]-(type2:"+TST.TYPE+")<--(instance:INSTANCE)" +
//                " RETURN count(DISTINCT instance) AS " + outBinding;
//    }

    public static String getTransitiveInstanceCount(String uri, String outBinding) {
        return "MATCH (type:"+TST.TYPE+" {uri:\"" + uri + "\"})<-[:"+TST.REASONEDHASTYPE+"]-(instance:INSTANCE)" +
                " RETURN count(DISTINCT instance) AS " + outBinding;
    }

//    public static String getMaxDepth(String outBinding) {
//        return "MATCH p = (a:"+TST.TYPE+") -[:"+ TST.SUBCLASSOF+"*0..]->(b:"+TST.TYPE+") return MAX(length(p)) AS " + outBinding;
//    }

    public static String getShortestPathLength(String uriA, String uriB, String outBinding) {
        return "MATCH (a:"+TST.TYPE+" {uri: \""+uriA+"\"}), (b:"+TST.TYPE+" {uri: \""+uriB+"\"}), p = shortestPath((a)-[:"+TST.SUBCLASSOF+"*0..]-(b)) RETURN length(p) AS " + outBinding;
    }

    //TODO May not be performant
    public static String getLongestPathLength(String uriA, String uriB, String outBinding) {
        return "MATCH (a:"+TST.TYPE+" {uri: \""+uriA+"\"}), (b:"+TST.TYPE+" {uri: \""+uriB+"\"}), p = (a)-[:"+TST.SUBCLASSOF+"*0..]-(b) return MAX(length(p)) AS " + outBinding;
    }

    public static String getLongestPathLengthToSubsumer(String uriA, String uriB, String outBinding) {
        return "MATCH (a:"+TST.TYPE+" {uri: \""+uriA+"\"}), (b:"+TST.TYPE+" {uri: \""+uriB+"\"}), p = (a)-[:"+TST.SUBCLASSOF+"*0..]->(b) return MAX(length(p)) AS " + outBinding;
    }

    public static String getShortestPath(String uriA, String uriB, String outBinding) {
        return "MATCH (a:"+TST.TYPE+" {uri: \""+uriA+"\"}), (b:"+TST.TYPE+" {uri: \""+uriB+"\"}), p = shortestPath((a)-[:"+TST.SUBCLASSOF+"*0..]-(b)) RETURN p AS " + outBinding;
    }

    @Deprecated
    public static String getSharedInstancesUnreasoned(String uriA, String uriB, String outBinding) {
        return "MATCH (a:"+TST.TYPE+" {uri: \""+uriA+"\"}), (b:"+TST.TYPE+" {uri: \""+uriB+"\"}), (a)<-[:"+TST.SUBCLASSOF+"*0..]-(:"+TST.TYPE+")<--(instance:"+TST.INSTANCE+")-->(:"+TST.TYPE+")-[:"+TST.SUBCLASSOF+"*0..]->(b) RETURN count(instance) AS " + outBinding;
    }

    @Deprecated
    public static String getSharedInstances(String uriA, String uriB, String outBinding) {
        return "MATCH (a:"+TST.TYPE+" {uri: \""+uriA+"\"}), (b:"+TST.TYPE+" {uri: \""+uriB+"\"}), (a)<-[:"+TST.REASONEDHASTYPE+"]-(instance:"+TST.INSTANCE+")-[:"+TST.REASONEDHASTYPE+"]->(b) RETURN count(instance) AS " + outBinding;
    }

    public static String getLcs(String uriA, String uriB) {
        return "MATCH (typeA:"+TST.TYPE+" {uri:\"" + uriA + "\"})-[*0..]->(type:"+TST.TYPE+")," +
                " (typeB:"+TST.TYPE+" {uri:\"" + uriB + "\"})-[*0..]->(type:"+TST.TYPE+")," +
                " (type:"+TST.TYPE+")<-[*0..]-(type2:"+TST.TYPE+")<--(instance:INSTANCE)" +
                " RETURN type, count(DISTINCT instance) AS count" +
                " ORDER BY count" +
                " LIMIT 1";
    }

    // Distinct - multiple paths = multiple matches
    // TODO maybe better perfromance possible?
    public static String getTransitiveParents(String uri, String outBinding) {
        return "MATCH (typeA:"+TST.TYPE+" {uri:\"" + uri + "\"})-[:SUBCLASSOF*0..]->(type:"+TST.TYPE+")" +
                " RETURN DISTINCT type AS " + outBinding;
    }

    public static String getTransitiveTypeOf(String uri, String outBinding) {
        return "MATCH (typeA:"+TST.INSTANCE+" {uri:\"" + uri + "\"})-[*0..]->(type:"+TST.TYPE+")" +
                " RETURN DISTINCT type AS " + outBinding;
    }
}
