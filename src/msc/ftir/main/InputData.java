/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.main;

import java.math.BigDecimal;

/**
 *
 * @author Pramuditha Buddhini
 */
public class InputData {

    private int id;
    private BigDecimal wavenumber;
    private BigDecimal transmittance;

    public InputData(int i, BigDecimal w, BigDecimal t) {
        this.id = i;
        this.wavenumber = w;
        this.transmittance = t;
    }

    public InputData(BigDecimal w, BigDecimal t) {
        this.wavenumber = w;
        this.transmittance = t;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getWavenumber() {
        return wavenumber;
    }

    public void setWavenumber(BigDecimal wavelength) {
        this.wavenumber = wavelength;
    }

    public BigDecimal getTransmittance() {
        return transmittance;
    }

    public void setTransmittance(BigDecimal transmittance) {
        this.transmittance = transmittance;
    }

}
