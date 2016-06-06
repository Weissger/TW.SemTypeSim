package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;
import com.sun.corba.se.impl.protocol.INSServerRequestDispatcher;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = TST.INSTANCE)
public class Instance extends Resource {

    public Instance() {
    }

    public Instance(String uri, Set<Type> types) {
        super(uri);
        this.types = types;
    }

    public Instance(String uri) {
        super(uri);
    }

    // TYPE
    @Relationship(type=TST.HASTYPE, direction = Relationship.OUTGOING)
    public Set<Type> types = new HashSet<Type>();

    public void addType(Type types) {
        Collections.addAll(this.types, types);
    }

    public Set<Type> getTypes() {
        return types;
    }

    // CALCULATION
    @Relationship(type=TST.INCALCULATION, direction = Relationship.OUTGOING)
    public Set<Calculation> calculations;

    public Set<Calculation> getCalculations() {
        return calculations;
    }

    public void addCalculation(Calculation... calc) {
        if (this.calculations == null) {
            this.calculations = new HashSet<>();
        }
        Collections.addAll(this.calculations, calc);
    }

    // REASONED TYPES
    @Relationship(type=TST.REASONEDHASTYPE, direction = Relationship.OUTGOING)
    private Set<Type> reasonedTypes;

    public void addReasonedType(Type... types) {
        if (this.reasonedTypes == null) {
            this.reasonedTypes = new HashSet<>();
        }
        Collections.addAll(this.reasonedTypes, types);
    }

    public void addReasonedType(Collection<Type> types) {
        if (this.reasonedTypes == null) {
            this.reasonedTypes = new HashSet<>();
        }
        this.reasonedTypes.addAll(types);
    }

    public Set<Type> getReasonedTypes() {
        return reasonedTypes;
    }

//    // REASONED INSTANCES
//    @Relationship(type=TST.HASINSTANCE, direction = Relationship.OUTGOING)
//    private Set<Instance> instances;
//
//    public void addInstance(Instance... instances) {
//        if (this.instances == null) {
//            this.instances = new HashSet<>();
//        }
//        Collections.addAll(this.instances, instances);
//    }
//
//    public void addInstance(Collection<Instance> instances) {
//        if (this.instances == null) {
//            this.instances = new HashSet<>();
//        }
//        this.instances.addAll(instances);
//    }
//
//    public Set<Instance> getInstances() {
//        return instances;
//    }

}
