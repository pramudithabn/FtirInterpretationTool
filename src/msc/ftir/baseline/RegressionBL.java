/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.baseline;

import java.awt.Color;
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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.function.PolynomialFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Pramuditha Buddhini
 */
public class RegressionBL {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private static ArrayList<InputData> originalList = new ArrayList<InputData>();
    private static ArrayList<InputData> smoothedList = new ArrayList<InputData>();
    private int listSize, slistSize;
    private SortedMap<BigDecimal, BigDecimal> linePoints = new TreeMap<BigDecimal, BigDecimal>();
    public SortedMap<BigDecimal, BigDecimal> line2Points = new TreeMap<BigDecimal, BigDecimal>();
    private SortedMap<BigDecimal, BigDecimal> hdifferenceBetweenPoints = new TreeMap<BigDecimal, BigDecimal>();
    private SortedMap<BigDecimal, BigDecimal> baseLineCorrected = new TreeMap<BigDecimal, BigDecimal>();
    private double Y = 0;

    public double getY() {
        return Y;
    }
    private double y = 0;

    public SortedMap<BigDecimal, BigDecimal> getBaseLineCorrected() {
        return baseLineCorrected;
    }

    public void setBaseLineCorrected(SortedMap<BigDecimal, BigDecimal> baseLineCorrected) {
        this.baseLineCorrected = baseLineCorrected;
    }

    private double m1 = 0; //gradient of the regression line

    public double getM1() {
        return m1;
    }

    public double getC1() {
        return c1;
    }
    private double c1 = 0; //intercept of the regression line
    private double m2 = 0; //gradient of the perpendicular line to the regression line
    private double c2 = 0; //intercept of the perpendicular line to the regression line

    public SortedMap<BigDecimal, BigDecimal> getHdifferenceBetweenPoints() {
        return hdifferenceBetweenPoints;
    }

    public void setHdifferenceBetweenPoints(SortedMap<BigDecimal, BigDecimal> baseLineCorrected) {
        this.hdifferenceBetweenPoints = baseLineCorrected;
    }

    public SortedMap<BigDecimal, BigDecimal> getLine2Points() {
        return line2Points;
    }

    public void setLine2Points(SortedMap<BigDecimal, BigDecimal> line2Points) {
        this.line2Points = line2Points;
    }

    public SortedMap<BigDecimal, BigDecimal> getLinePoints() {
        return linePoints;
    }

    public void setLinePoints(SortedMap<BigDecimal, BigDecimal> linePoints) {
        this.linePoints = linePoints;
    }

    public RegressionBL() {
        conn = Javaconnect.ConnecrDb();

//        qdata();
    }

    public static void main(String[] args) {
        RegressionBL b = new RegressionBL();

    }

    private ArrayList<InputData> qdata() {
        String sql = "select WAVENUMBER, TRANSMITTANCE from input_data";
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

    private ArrayList<InputData> qdata_avg() {
        String sql = "select WAVENUMBER, TRANSMITTANCE from avg_data";
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData d;
            while (rs.next()) {
                d = new InputData(rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                smoothedList.add(d);

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

        slistSize = smoothedList.size();

        return smoothedList;
    }

    //this method not worked
    public void least_square_method(SortedMap<BigDecimal, BigDecimal> candidates) {

        double meanX = 0, meanY = 0;
        double sumX = 0, sumY = 0;
        double xi = 0, yi = 0;
        double x_diff = 0, y_diff = 0, x_square = 0;
        double m = 0; //gradient m1
        double b = 0; //interceptor c1

        for (BigDecimal wvl : candidates.keySet()) {

            double value = candidates.get(wvl).doubleValue();
            double key = wvl.doubleValue();

            sumX += key;
            sumY += value;
        }
        meanX = sumX / candidates.size();
        meanY = sumY / candidates.size();

//        System.out.println(sumX + "  " + candidates.size());
//        System.out.println(meanX + "  " + meanY);
        for (BigDecimal wvl : candidates.keySet()) {
            xi = wvl.doubleValue();
            yi = candidates.get(wvl).doubleValue();
            x_diff += (xi - meanX);
            y_diff += (yi - meanY);

            x_square = x_diff * x_diff;
        }

        m = (x_diff * y_diff) / x_square;
        b = meanY - (m * meanX);

        System.out.println("Line equation   y = (" + m + ")x + " + b);

        //now find points of the line i.e. Calculate y for each wavenumber x
        for (int i = 0; i < slistSize; i++) {
            double x = smoothedList.get(i).getWavenumber().doubleValue();
            double a = m * x + b;
            BigDecimal y = BigDecimal.valueOf(a);

            linePoints.put(BigDecimal.valueOf(x), y);

        }

//        for (BigDecimal wvl : linePoints.keySet()) {
//            double x = wvl.doubleValue();
//            double y = linePoints.get(wvl).doubleValue();
//
//            System.out.println("Line points \n " + x + " " + y);
//        }
        //two points
//        double xF = originalList.get(0).getWavenumber().doubleValue();
//        double a1 = (m * xF) + b;
//        BigDecimal yF = BigDecimal.valueOf(a1);
//        double xL = originalList.get(listSize - 1).getWavenumber().doubleValue();
//        double a2 = (m * xL) + b;
//        BigDecimal yL = BigDecimal.valueOf(a2);
//        line2Points.put(BigDecimal.valueOf(xL), yL);
//        line2Points.put(BigDecimal.valueOf(xF), yF);
//        System.out.println(xF + "," + yF + "    " + xL + "," + yL);
        getDifferencewithLine();
    }

    public SortedMap<BigDecimal, BigDecimal> getDifferencewithLine() {
        
        String sql = "SELECT TRANSMITTANCE , count(TRANSMITTANCE) FROM avg_data GROUP BY TRANSMITTANCE DESC LIMIT 1"; //mode
       
        ResultSet rss = null;
        PreparedStatement pst = null;
        

        try {
            pst = conn.prepareStatement(sql);
            rss = pst.executeQuery();
            while (rss.next()) {
                Y = rss.getBigDecimal(1).doubleValue();
//                System.out.println("YYYY"+Y);
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

        for (int i = 0; i < slistSize; i++) {
            double tr = smoothedList.get(i).getTransmittance().doubleValue();
            BigDecimal wv = smoothedList.get(i).getWavenumber();
            double x = smoothedList.get(i).getWavenumber().doubleValue();
//            double y = linePoints.get(wv).doubleValue(); //transmittance according to regression line w.r.t. to wavelength
//            double y = m1 * x + c1;
//            double a = Math.abs(tr - y);
            double a = tr - y + Y;
            BigDecimal difference = BigDecimal.valueOf(a);

            hdifferenceBetweenPoints.put(wv, difference);
        }
        
        updateBLcorrectedValue();
        return hdifferenceBetweenPoints;
//        System.out.println(" ");
//        for (BigDecimal wvl : linePoints.keySet()) {
//            double x = wvl.doubleValue();
//            double y = linePoints.get(wvl).doubleValue();
//
//        }
    }

    public void drawRegressionLine(JFreeChart chart, XYDataset inputData, int lowerB, int upperB) {
        // Get the parameters 'a' and 'c1' for an equation y = a + c1 * x,
        // fitted to the inputData using ordinary least squares regression.
        // a - regressionParameters[0], c1 - regressionParameters[1]
        double regressionParameters[] = Regression.getOLSRegression(inputData, 0);
        System.out.println("y = " + regressionParameters[1] + "*x + " + regressionParameters[0]);
        m1 = regressionParameters[1];
        c1 = regressionParameters[0];
        // Prepare a line function using the found parameters
        LineFunction2D linefunction2d = new LineFunction2D(regressionParameters[0], regressionParameters[1]);

        // Creates a dataset by taking sample values from the line function
        XYDataset dataset = DatasetUtilities.sampleFunction2D(linefunction2d, 0D, upperB, lowerB, "Fitted Regression Line");

        // Draw the line dataset
        XYPlot xyplot = chart.getXYPlot();
        xyplot.setDataset(2, dataset);
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer.setSeriesPaint(0, Color.red);
        xyplot.setRenderer(2, xylineandshaperenderer);
        
      
        
        System.out.println(" Domain axis tick mark outside length = "+xyplot.getDomainAxis().getTickMarkOutsideLength());
        System.out.println(" Domain axis tick mark inside length = "+xyplot.getDomainAxis().getTickMarkInsideLength());
//                getDomainAxis.getTickUnit().getSize();
        //get query of avg_data table
        qdata_avg();
        
        linePoints.clear();
        //calculate y values for all wavelengths (x) using regression line equation
        for (int i = 0; i < slistSize; i++) {
            double x = smoothedList.get(i).getWavenumber().doubleValue();
            y = m1 * x + c1;
            BigDecimal a = BigDecimal.valueOf(y);
            linePoints.put(BigDecimal.valueOf(x), a);

        }

        getDifferencewithLine(); //difference between actual and regression data


    }

    

    public void drawPolynomialFit(JFreeChart chart, XYDataset inputData, int lowerB, int upperB) {
        /* Returns the parameters 'a0', 'a1', 'a2', ..., 'an' for a polynomial 
     * function of order n, y = a0 + a1 * x + a2 * x^2 + ... + an * x^n,
     * fitted to the data using a polynomial regression equation.
     * The result is returned as an array with a length of n + 2,
     * where double[0] --> a0, double[1] --> a1, .., double[n] --> an.
     * and double[n + 1] is the correlation coefficient R2
     * Reference: J. D. Faires, R. L. Burden, Numerische Methoden (german
     * edition), pp. 243ff and 327ff.*/

        double regressionParameters[] = Regression.getPolynomialRegression(inputData, 0, 2);

        double a0 = regressionParameters[0];
        double a1 = regressionParameters[1];
        double a2 = regressionParameters[2];
//        double a3 = regressionParameters[3];
//        double a4 = regressionParameters[4];
//        System.out.println("y = " + a3 + "*x^3+ " + a2 + "*x^2+ " + a1 + "*x + " + a0);
        // Prepare a curve function using the found parameters
        PolynomialFunction2D polyfunction2d = new PolynomialFunction2D(regressionParameters);

        // Creates a dataset by taking sample values from the curve function
        XYDataset dataset = DatasetUtilities.sampleFunction2D(polyfunction2d, 0D, upperB, lowerB, "Fitted curve");

        // Draw the line dataset
        XYPlot xyplot = chart.getXYPlot();
        xyplot.setDataset(2, dataset);
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer.setSeriesPaint(0, Color.YELLOW);
        xyplot.setRenderer(2, xylineandshaperenderer);

        //get query of avg_data table
        qdata_avg();

        //calculate y values for all wavelengths (x) using regression line equation
        linePoints.clear();
        for (int i = 0; i < slistSize; i++) {
            double x = smoothedList.get(i).getWavenumber().doubleValue();
//            double a = (a4 * x * x * x * x) + (a3 * x * x * x) + (a2 * x * x) + (a1 * x) + a0;
            y = (a2 * x * x) + (a1 * x) + a0;
            BigDecimal a = BigDecimal.valueOf(y);
            linePoints.put(BigDecimal.valueOf(x), a);

        }

        getDifferencewithLine(); //difference between actual and regression data


    }
    
    public void updateBLcorrectedValue() {
        clearAvgTable();
        String fullarrays = "";

        for (BigDecimal wavelength : hdifferenceBetweenPoints.keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = hdifferenceBetweenPoints.get(wavelength);

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

    public void clearAvgTable() {

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
    
    public void perpendicularDistanceWithLine() {

        // y = -(1/m) + c2 eqn of the perpendicular line
        m2 = -(1 / m1);

        for (int i = 0; i < smoothedList.size(); i++) {

            double x = smoothedList.get(i).getWavenumber().doubleValue();
            double y = smoothedList.get(i).getTransmittance().doubleValue();

            c2 = y - (m2 * x);

            double x0, y0;

            x0 = (c2 - c1) / (m1 - m2);
            y0 = m1 * x0 + c1;

            double X, Y, r;
            r = hdifferenceBetweenPoints.get(BigDecimal.valueOf(x)).doubleValue();

            X = x0 - x;
            Y = Math.sqrt((r * r) - (X * X));
//            System.out.println(x + " " + Y + " " + y);

            baseLineCorrected.put(BigDecimal.valueOf(x), BigDecimal.valueOf(Y));

        }

    }

}
