package com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.node;

import com.github.tw.TypeSimilarityTools.semanticNetwork.analyzer.model.namespace.TST;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = TST.CALCULATION)
public class Calculation extends Entity {
    public Calculation() {
    }

    public Calculation(String uriA, String uriB) {
        this.uriA = uriA;
        this.uriB = uriB;
    }

    private String uriA;
    private String uriB;

    private double hso = -2;
    private double instanceCooc = -2;
    private double jcn = -2;
    private double lch = -2;
    private double lchMod = -2;
    private double lin = -2;
    private double path = -2;
    private double pathMod = -2;
    private double resnik = -2;
    private double wuPalmer = -2;

    public String getUriA() {
        return uriA;
    }

    public void setUriA(String uriA) {
        this.uriA = uriA;
    }

    public String getUriB() {
        return uriB;
    }

    public void setUriB(String uriB) {
        this.uriB = uriB;
    }

    public double getHso() {
        return hso;
    }

    public void setHso(double hso) {
        this.hso = hso;
    }

    public double getInstanceCooc() {
        return instanceCooc;
    }

    public void setInstanceCooc(double instanceCooc) {
        this.instanceCooc = instanceCooc;
    }

    public double getJcn() {
        return jcn;
    }

    public void setJcn(double jcn) {
        this.jcn = jcn;
    }

    public double getLch() {
        return lch;
    }

    public void setLch(double lch) {
        this.lch = lch;
    }

    public double getLchMod() {
        return lchMod;
    }

    public void setLchMod(double lchMod) {
        this.lchMod = lchMod;
    }

    public double getLin() {
        return lin;
    }

    public void setLin(double lin) {
        this.lin = lin;
    }

    public double getPath() {
        return path;
    }

    public void setPath(double path) {
        this.path = path;
    }

    public double getPathMod() {
        return pathMod;
    }

    public void setPathMod(double pathMod) {
        this.pathMod = pathMod;
    }

    public double getResnik() {
        return resnik;
    }

    public void setResnik(double resnik) {
        this.resnik = resnik;
    }

    public double getWuPalmer() {
        return wuPalmer;
    }

    public void setWuPalmer(double wuPalmer) {
        this.wuPalmer = wuPalmer;
    }
}
