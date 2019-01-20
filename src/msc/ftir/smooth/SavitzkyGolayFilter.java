/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.smooth;

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
import msc.ftir.baseline.BaselineCorrection;
import msc.ftir.main.InputData;
import msc.ftir.main.Javaconnect;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Pramuditha Buddhini
 */
public class SavitzkyGolayFilter {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    public ArrayList<InputData> originalPoints = new ArrayList<InputData>();
    public ArrayList<BigDecimal> transmittanceValues = new ArrayList<BigDecimal>();
    public BaselineCorrection bc;
    private static volatile SavitzkyGolayFilter instance;
    private int listSize = 0;
    private SortedMap<BigDecimal, BigDecimal> pointset = new TreeMap<BigDecimal, BigDecimal>();
    private SortedMap<BigDecimal, BigDecimal> filteredPointset = new TreeMap<BigDecimal, BigDecimal>();
    private double m1 = 0; //gradient of the regression line
    private double c1 = 0; //intercept of the regression line
    public static int count = 0;

    public SavitzkyGolayFilter() {
        conn = Javaconnect.ConnecrDb();
        qdata();

    }

    public void reset() {
        instance = null;
        count = 0;
        qdata();
    }

    public static SavitzkyGolayFilter getInstance() {
        instance = new SavitzkyGolayFilter();
        return instance;
    }

    public static void main(String[] args) {
        SavitzkyGolayFilter sgf = new SavitzkyGolayFilter();
        sgf.applyFilter_3points();
    }

    public ArrayList<InputData> qdata() {

        String sql = "select WAVENUMBER, TRANSMITTANCE from input_data";
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData d;
            originalPoints.clear();

            while (rs.next()) {
                d = new InputData(rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                originalPoints.add(d);

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
        listSize = originalPoints.size();
        return originalPoints;

    }

    public void applyFilter_3points() {

        if (!transmittanceValues.isEmpty()) {
            for (int i = 0; i < transmittanceValues.size(); i++) {
                originalPoints.get(i).setTransmittance(transmittanceValues.get(i));
            }
        }
        transmittanceValues.clear();

        BigDecimal sum = null;
        BigDecimal avg = null;
        int rindex = 0;
        int listSize = originalPoints.size();

        BigDecimal firstx = originalPoints.get(0).getWavenumber();
        BigDecimal lastx = originalPoints.get(listSize - 1).getWavenumber();
        BigDecimal firsty = originalPoints.get(0).getTransmittance();
        BigDecimal lasty = originalPoints.get(listSize - 1).getTransmittance();
        filteredPointset.put(firstx, firsty);
        transmittanceValues.add(firsty); //just to hold altered transmittance values

        for (rindex = 1; rindex < listSize - 1; rindex++) {
            pointset.clear();
            BigDecimal y1 = originalPoints.get(rindex - 1).getTransmittance();
            BigDecimal y2 = originalPoints.get(rindex).getTransmittance();
            BigDecimal y3 = originalPoints.get(rindex + 1).getTransmittance();

            BigDecimal x1 = originalPoints.get(rindex - 1).getWavenumber();
            BigDecimal x2 = originalPoints.get(rindex).getWavenumber();
            BigDecimal x3 = originalPoints.get(rindex + 1).getWavenumber();

            //find y value by regression equation
            //step 1 : add 3 successive points to the list and create a dataset 
            pointset.put(x1, y1);
            pointset.put(x2, y2);
            pointset.put(x3, y3);

            //step 2 : Get the regression line equation 
            calRegressionLine(createDataset(pointset));

//            calRegressionPolynomial(createDataset(pointset));
            //step 3 : Calculate y value for the mid point
            double x = x2.doubleValue();
            double y = m1 * x + c1;

            //step 4 : Add calculated point to result treemap
            filteredPointset.put(x2, BigDecimal.valueOf(y));
            transmittanceValues.add(BigDecimal.valueOf(y)); //just to hold altered transmittance values

        }
        filteredPointset.put(lastx, lasty);
        transmittanceValues.add(lasty); //just to hold altered transmittance values
        updateSmoothedValue();
        count++;

    }

    public void applyFilter_5points() {

        if (!transmittanceValues.isEmpty()) {
            for (int i = 0; i < transmittanceValues.size(); i++) {
                originalPoints.get(i).setTransmittance(transmittanceValues.get(i));
            }
        }
        transmittanceValues.clear();

        BigDecimal sum = null;
        BigDecimal avg = null;
        int rindex = 0;
        int listSize = originalPoints.size();

        BigDecimal w0 = originalPoints.get(0).getWavenumber();
        BigDecimal w1 = originalPoints.get(1).getWavenumber();
        BigDecimal wm = originalPoints.get(listSize - 2).getWavenumber();
        BigDecimal wn = originalPoints.get(listSize - 1).getWavenumber();
        BigDecimal t0 = originalPoints.get(0).getTransmittance();
        BigDecimal t1 = originalPoints.get(1).getTransmittance();
        BigDecimal tm = originalPoints.get(listSize - 2).getTransmittance();
        BigDecimal tn = originalPoints.get(listSize - 1).getTransmittance();
        filteredPointset.put(w0, t0);
        filteredPointset.put(w1, t1);
        transmittanceValues.add(t0); //just to hold altered transmittance values
        transmittanceValues.add(t1); //just to hold altered transmittance values

        for (rindex = 2; rindex < listSize - 2; rindex++) {
            pointset.clear();
            BigDecimal y0 = originalPoints.get(rindex - 2).getTransmittance();
            BigDecimal y1 = originalPoints.get(rindex - 1).getTransmittance();
            BigDecimal y2 = originalPoints.get(rindex).getTransmittance();
            BigDecimal y3 = originalPoints.get(rindex + 1).getTransmittance();
            BigDecimal y4 = originalPoints.get(rindex + 2).getTransmittance();
            
            BigDecimal x0 = originalPoints.get(rindex - 2).getWavenumber();
            BigDecimal x1 = originalPoints.get(rindex - 1).getWavenumber();
            BigDecimal x2 = originalPoints.get(rindex).getWavenumber();
            BigDecimal x3 = originalPoints.get(rindex + 1).getWavenumber();
            BigDecimal x4 = originalPoints.get(rindex + 2).getWavenumber();

            //find y value by regression equation
            //step 1 : add 3 successive points to the list and create a dataset 
            pointset.put(x0, y0);
            pointset.put(x1, y1);
            pointset.put(x2, y2);
            pointset.put(x3, y3);
            pointset.put(x4, y4);
            

            //step 2 : Get the regression line equation 
            calRegressionLine(createDataset(pointset));

//            calRegressionPolynomial(createDataset(pointset));
            //step 3 : Calculate y value for the mid point
            double x = x2.doubleValue();
            double y = m1 * x + c1;

            //step 4 : Add calculated point to result treemap
            filteredPointset.put(x2, BigDecimal.valueOf(y));
            transmittanceValues.add(BigDecimal.valueOf(y)); //just to hold altered transmittance values

        }
        filteredPointset.put(wm, tm);
        filteredPointset.put(wn, tn);
        transmittanceValues.add(tm); //just to hold altered transmittance values
        transmittanceValues.add(tn); //just to hold altered transmittance values
        updateSmoothedValue();
        count++;

    }

    public void updateSmoothedValue() {
        clearAvgTable();
        String fullarrays = "";

        for (BigDecimal wavelength : filteredPointset.keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = filteredPointset.get(wavelength);

            String twoarrays = "(" + key + " , " + value.setScale(8, RoundingMode.UP) + ")";
            fullarrays = fullarrays + twoarrays + ",";
        }

        fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

        String sql = "INSERT INTO avg_data (wavenumber,transmittance)  VALUES " + fullarrays;
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

    public void clearAvgTable() {

        String sql1 = "delete from avg_data";
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

    private XYDataset createDataset(SortedMap<BigDecimal, BigDecimal> pointList) {
        final XYSeries valleyPoints = new XYSeries("Valley Points");

        for (BigDecimal wavelength : pointList.keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = pointList.get(wavelength);
            valleyPoints.add(key, value);

        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(valleyPoints);
        return dataset;
    }

    public void calRegressionLine(XYDataset inputData) {
        // Get the parameters 'a' and 'c1' for an equation y = a + c1 * x,
        // fitted to the inputData using ordinary least squares regression.
        // a - regressionParameters[0], c1 - regressionParameters[1]
        double regressionParameters[] = Regression.getOLSRegression(inputData, 0);
//        System.out.println("y = " + regressionParameters[1] + "*x + " + regressionParameters[0]);
        m1 = regressionParameters[1];
        c1 = regressionParameters[0];

    }

    public void calRegressionPolynomial(XYDataset inputData) {
        // Get the parameters 'a' and 'c1' for an equation y = a + c1 * x,
        // fitted to the inputData using ordinary least squares regression.
        // a - regressionParameters[0], c1 - regressionParameters[1]
        double regressionParameters[] = Regression.getPolynomialRegression(inputData, 0, 2);

        double a0 = regressionParameters[0];
        double a1 = regressionParameters[1];
        double a2 = regressionParameters[2];

        System.out.println("y = " + a2 + "*x^2+ " + a1 + "*x + " + a0);

    }
}
