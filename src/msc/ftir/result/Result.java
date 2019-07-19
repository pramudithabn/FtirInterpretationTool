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
    private String bond;
    private String functional_group;
    
    public Result(BigDecimal w, String b, String f){
        this.wavenumber = w;
        this.bond = b;
        this.functional_group =f;
                
    }

    public BigDecimal getWavenumber() {
        return wavenumber;
    }

    public void setWavenumber(BigDecimal wavenumber) {
        this.wavenumber = wavenumber;
    }

    public String getBond() {
        return bond;
    }

    public void setBond(String bond) {
        this.bond = bond;
    }

    public String getFunctional_group() {
        return functional_group;
    }

    public void setFunctional_group(String functional_group) {
        this.functional_group = functional_group;
    }
    
  
    
   
    
    
}
