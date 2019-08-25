/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.result;

import java.math.BigDecimal;

/**
 *
 * @author Pramuditha Buddhini
 */
public class Result {

    private BigDecimal wavenumber;
    private String bondVibMode;
    private String compoundCategory;
//    private String bond;
    private String functional_group;
    private String compound_type;
    private int libIndex;

    public Result(BigDecimal w, String b, String f, String t ,String c,int i) {
        this.wavenumber = w;
        this.bondVibMode = b;
//        this.bond = b;
        this.functional_group = f;
        this.compoundCategory = c;
        this.compound_type = t;
        this.libIndex = i;
    }

    public String getCompound_type() {
        return compound_type;
    }

    public void setCompound_type(String compound_type) {
        this.compound_type = compound_type;
    }

    public String getBondVibMode() {
        return bondVibMode;
    }

    public void setBondVibMode(String bondVibMode) {
        this.bondVibMode = bondVibMode;
    }

    public String getCompoundCategory() {
        return compoundCategory;
    }

    public void setCompoundCategory(String compoundCategory) {
        this.compoundCategory = compoundCategory;
    }

    public BigDecimal getWavenumber() {
        return wavenumber;
    }

    public void setWavenumber(BigDecimal wavenumber) {
        this.wavenumber = wavenumber;
    }

    public int getLibIndex() {
        return libIndex;
    }

    public void setLibIndex(int libIndex) {
        this.libIndex = libIndex;
    }

//    public String getBond() {
//        return bond;
//    }
//
//    public void setBond(String bond) {
//        this.bond = bond;
//    }
    public String getFunctional_group() {
        return functional_group;
    }

    public void setFunctional_group(String functional_group) {
        this.functional_group = functional_group;
    }

}
