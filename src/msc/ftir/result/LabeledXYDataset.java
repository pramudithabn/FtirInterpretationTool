/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.result;

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.xy.AbstractXYDataset;

/**
 *
 * @author Pramuditha Buddhini
 */
public class LabeledXYDataset extends AbstractXYDataset {

    private static final int N = 50;
    private ArrayList<Double> X = new ArrayList<Double>(N);
    private ArrayList<Double> Y = new ArrayList<Double>(N);
    private ArrayList<String> LABEL = new ArrayList<String>(N);

    public void add(double x, double y, String label) {
        this.X.add(x);
        this.Y.add(y);
        this.LABEL.add(label);
    }

    public void update(double x, String label) {

        int i = this.getItemCount(0);
        for (int k = 0; k < i; k++) {
            if ((X.get(k) == x)) {
                LABEL.set(k, label);
            }
        }
    }

    public String getLabel(int series, int item) {
        return LABEL.get(item);
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return "Unit";
    }

    @Override
    public int getItemCount(int series) {
        return LABEL.size();
    }

    @Override
    public Number getX(int series, int item) {
        return X.get(item);
    }

    @Override
    public Number getY(int series, int item) {
        return Y.get(item);
    }

}
