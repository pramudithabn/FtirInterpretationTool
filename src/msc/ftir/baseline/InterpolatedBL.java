/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.baseline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import msc.ftir.main.InputData;
import msc.ftir.main.Javaconnect;
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Pramuditha Buddhini
 */
public class InterpolatedBL {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private static ArrayList<InputData> originalList = new ArrayList<InputData>();
    private ArrayList<BigDecimal> wavelenths = new ArrayList<BigDecimal>();
    private int listSize;
    private SortedMap<BigDecimal, BigDecimal> substractedPointList = new TreeMap<BigDecimal, BigDecimal>();
    private double Y = 0;

    public double getY() {
        return Y;
    }

    public InterpolatedBL() {
        conn = Javaconnect.ConnecrDb();
        qdata();
    }

    public ArrayList<InputData> qdata() {

        String sql = "select WAVENUMBER, TRANSMITTANCE from avg_data";
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData d;
            while (rs.next()) {
                d = new InputData(rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                originalList.add(d);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Javaconnect.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

        listSize = originalList.size();
        return originalList;

    }

    private SortedMap<BigDecimal, BigDecimal> interpolatedDataset = new TreeMap<BigDecimal, BigDecimal>();

    public SortedMap<BigDecimal, BigDecimal> linearInterp(XYDataset dataset, int size) {

        double[] y = new double[size + 1];
        double[] x = new double[size + 1];
//        double[] xi = new double[listSize];
        wavelenths.clear();
        interpolatedDataset.clear();

        //first point
        x[0] = originalList.get(0).getWavenumber().doubleValue();
        y[0] = originalList.get(0).getTransmittance().doubleValue();

        for (int i = 0; i < size - 1; i++) {
            x[i + 1] = dataset.getXValue(0, i);
            y[i + 1] = dataset.getYValue(0, i);
        }
        //last point
        x[size] = originalList.get(listSize - 1).getWavenumber().doubleValue();
        y[size] = originalList.get(listSize - 1).getTransmittance().doubleValue();

        for (int i = 0; i < listSize; i++) {
            double w = originalList.get(i).getWavenumber().doubleValue();
//            if (w >= x[0] && w <= x[x.length - 1]) {
            wavelenths.add(BigDecimal.valueOf(w));
//            }
        }

        LinearInterpolator li = new LinearInterpolator(); // or other interpolator
        PolynomialSplineFunction psf = li.interpolate(x, y);

        double[] yi = new double[wavelenths.size()];
        for (int i = 0; i < wavelenths.size(); i++) {

            yi[i] = psf.value(wavelenths.get(i).doubleValue());

            interpolatedDataset.put(wavelenths.get(i), BigDecimal.valueOf(yi[i]));
        }
        return interpolatedDataset;
    }

    public SortedMap<BigDecimal, BigDecimal> splineInterp(XYDataset dataset, int size) {

        double[] y = new double[size];
        double[] x = new double[size];
        double[] xi = new double[listSize];
        wavelenths.clear();
        interpolatedDataset.clear();

        for (int i = 0; i < size; i++) {
            x[i] = dataset.getXValue(0, i); //candidate point list's X value
            y[i] = dataset.getYValue(0, i); //candidate point list's Y value
        }

        for (int i = 0; i < listSize; i++) {
            double w = originalList.get(i).getWavenumber().doubleValue();
//            if (w > x[0] && w < x[x.length - 1]) {
//                xi[i] = w;
            wavelenths.add(BigDecimal.valueOf(w));

//            }
        }

        SplineInterpolator li = new SplineInterpolator(); // or other interpolator
        PolynomialSplineFunction psf = li.interpolate(x, y);

//        UnivariateInterpolator
        double[] yi = new double[wavelenths.size()];
        for (int i = 0; i < wavelenths.size(); i++) {
//            yi[i] = psf.value(xi[i]);
            yi[i] = psf.value(wavelenths.get(i).doubleValue());

            interpolatedDataset.put(wavelenths.get(i), BigDecimal.valueOf(yi[i]));
        }

        return interpolatedDataset;
    }

    public SortedMap<BigDecimal, BigDecimal> cubicInterp(XYDataset dataset, int size) {

        double[] y = new double[size + 1];
        double[] x = new double[size + 1];
        double[] xi = new double[listSize];
        wavelenths.clear();
        interpolatedDataset.clear();

        //first point
        x[0] = originalList.get(0).getWavenumber().doubleValue();
        y[0] = originalList.get(0).getTransmittance().doubleValue();

        for (int i = 0; i < size - 1; i++) {
            x[i + 1] = dataset.getXValue(0, i);
            y[i + 1] = dataset.getYValue(0, i);
        }
        //last point
        x[size] = originalList.get(listSize - 1).getWavenumber().doubleValue();
        y[size] = originalList.get(listSize - 1).getTransmittance().doubleValue();

        for (int i = 0; i < listSize; i++) {
            double w = originalList.get(i).getWavenumber().doubleValue();
//            if (w >= x[0] && w <= x[x.length - 1]) {
            wavelenths.add(BigDecimal.valueOf(w));
//            }
        }
        System.out.println("wavelength size  " + wavelenths.size());

        AkimaSplineInterpolator li = new AkimaSplineInterpolator(); // or other interpolator
        PolynomialSplineFunction psf = li.interpolate(x, y);

//        UnivariateInterpolator
        double[] yi = new double[wavelenths.size()];
        for (int i = 0; i < wavelenths.size(); i++) {
            yi[i] = psf.value(wavelenths.get(i).doubleValue());

            interpolatedDataset.put(wavelenths.get(i), BigDecimal.valueOf(yi[i]));
        }
        System.out.println("interpolatedDataset.size " + interpolatedDataset.size());
       
        return interpolatedDataset;
    }

    public SortedMap<BigDecimal, BigDecimal> getDifferencewithLine() {

//        String sql = "select avg(TRANSMITTANCE) from avg_data"; // avg
        String sql = "SELECT TRANSMITTANCE , count(TRANSMITTANCE) FROM avg_data GROUP BY TRANSMITTANCE DESC LIMIT 1"; //mode
       
        ResultSet rss = null;
        PreparedStatement pst = null;
        

        try {
            pst = conn.prepareStatement(sql);
            rss = pst.executeQuery();
            while (rss.next()) {
                Y = rss.getBigDecimal(1).doubleValue();
            }

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                rss.close();
                pst.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        for (int i = 0; i < listSize; i++) {
            double tr = originalList.get(i).getTransmittance().doubleValue();
            BigDecimal wv = originalList.get(i).getWavenumber();
            double y = interpolatedDataset.get(wv).doubleValue();
//            System.out.println("y  " + y);

//            double a = Math.abs(tr - y);
            double a = tr - y + Y;
            BigDecimal difference = BigDecimal.valueOf(a);

            substractedPointList.put(wv, difference);
        }

        updateBLcorrectedValue();

        return substractedPointList;

    }

    public void updateBLcorrectedValue() {
        clearTable();
        String fullarrays = "";

        for (BigDecimal wavelength : substractedPointList.keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = substractedPointList.get(wavelength);

            String twoarrays = "(" + key + " , " + value.setScale(8, RoundingMode.UP) + ")";
            fullarrays = fullarrays + twoarrays + ",";
        }

        fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

        String sql = "INSERT INTO baseline_data (wavenumber,transmittance)  VALUES " + fullarrays;
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {

            }
        }

    }

    public void clearTable() {

        String sql1 = "delete from baseline_data";
        try {
            pst = conn.prepareStatement(sql1);
            pst.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                pst.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}
