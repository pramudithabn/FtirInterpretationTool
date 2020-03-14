/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.smooth;

import msc.ftir.main.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartPanel;
import msc.ftir.main.MouseMarker;

/**
 * A method to smooth a line based on the McMaster line smoothing algorithm
 *
 * @author Pramuditha Buddhini
 */
public class DefaultSmooth {

    public static ArrayList<InputData> rowDataList = new ArrayList<InputData>();
    public static ArrayList<BigDecimal> avgPointList = new ArrayList<BigDecimal>();
    ArrayList<BigDecimal> gapDifferenceList = new ArrayList<BigDecimal>();
    ArrayList<Integer> filteredIDList = new ArrayList<Integer>();
    ArrayList<Integer> filteredPointList = new ArrayList<Integer>();
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    int rindex = 0;
    int listSize = 0;
    ChartPanel chartPanel;
    private BigDecimal minScale;
    private BigDecimal maxScale;
    private BigDecimal smoothingFactor;
    private int startx, endx;
    public static int count = 0;

    public BigDecimal getMinScale() {
        return minScale;
    }

    public void setMinScale(BigDecimal minScale) {
        this.minScale = minScale;
    }

    public BigDecimal getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(BigDecimal maxScale) {
        this.maxScale = maxScale;
    }

    public BigDecimal getSmoothingFactor() {
        return smoothingFactor;
    }

    public void setSmoothingFactor(BigDecimal smoothingFactor) {
        this.smoothingFactor = smoothingFactor;
    }
    private static volatile DefaultSmooth instance;

    public DefaultSmooth() {
        conn = Javaconnect.ConnecrDb();

        qdata();

    }

    public static DefaultSmooth getInstance() {

        instance = new DefaultSmooth();

        return instance;
    }

    public void reset() {
        instance = null;
        count = 0;
        qdata();
    }

    public ArrayList<InputData> qdata() {

        String sql = "select * from input_data";
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData d;
            rowDataList.clear();
            while (rs.next()) {
                d = new InputData(rs.getInt("ID"), rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                rowDataList.add(d);

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

        listSize = rowDataList.size();
        return rowDataList;

    }

    public void calPointAverage() {

        BigDecimal sum = null;
        BigDecimal avg = null;

        BigDecimal first = rowDataList.get(0).getTransmittance();
        BigDecimal last = rowDataList.get(listSize - 1).getTransmittance();
        avgPointList.add(first);

        for (rindex = 1; rindex < listSize - 1; rindex++) {

            BigDecimal n1 = rowDataList.get(rindex - 1).getTransmittance();
            BigDecimal n2 = rowDataList.get(rindex).getTransmittance();
            BigDecimal n3 = rowDataList.get(rindex + 1).getTransmittance();

            sum = n1.add(n2);
            sum = sum.add(n3);
            avg = sum.divide(BigDecimal.valueOf(3), 8, RoundingMode.HALF_UP);

            avgPointList.add(avg);

        }
        avgPointList.add(last);
    }

    public void loadAvgDataTable() throws SQLException {

        for (int i = 0; i < avgPointList.size(); i++) {

            Statement statement = conn.createStatement();
            String qry = "insert into avg_data (WAVENUMBER , TRANSMITTANCE) values (?,?)";

            try {
                pst = conn.prepareStatement(qry);

                BigDecimal wavenum = rowDataList.get(i).getWavenumber();
                BigDecimal transmittance = avgPointList.get(i);
                rowDataList.get(i).setTransmittance(transmittance);

                pst.setBigDecimal(1, wavenum);
                pst.setBigDecimal(2, transmittance);

                pst.execute();

            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    pst.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

        }
        System.out.println("DB avg table loaded");

    }

    public void general_avg_algorithm_3point(int scale) {

        avgPointList.clear();

        BigDecimal diff = null;

        for (rindex = 1; rindex < listSize - 1; rindex++) {

            BigDecimal n1 = rowDataList.get(rindex - 1).getTransmittance();
            BigDecimal n2 = rowDataList.get(rindex).getTransmittance();

            diff = n1.subtract(n2);

            BigDecimal d = diff.abs();

            gapDifferenceList.add(d);

        }

        int minIndex = gapDifferenceList.indexOf(Collections.min(gapDifferenceList));
        int maxIndex = gapDifferenceList.indexOf(Collections.max(gapDifferenceList));

        minScale = gapDifferenceList.get(minIndex);
        maxScale = gapDifferenceList.get(maxIndex);

        smoothingFactor = ((maxScale.subtract(minScale)).divide(BigDecimal.valueOf(100))).multiply(BigDecimal.valueOf(scale));

//        System.out.println("\n Smoothing Factor " + smoothingFactor);
        BigDecimal sum = null;
        BigDecimal avg = null;

        BigDecimal first = rowDataList.get(0).getTransmittance();
        BigDecimal last = rowDataList.get(listSize - 1).getTransmittance();
        avgPointList.add(first);

        for (rindex = 1; rindex < listSize - 1; rindex++) {

            int res;

            res = (gapDifferenceList.get(rindex - 1)).compareTo(smoothingFactor);

            if (res == 1 | res == 0) {
                avgPointList.add(rowDataList.get(rindex - 1).getTransmittance());

            } else if (res == -1) {

                BigDecimal n1 = rowDataList.get(rindex - 1).getTransmittance();
                BigDecimal n2 = rowDataList.get(rindex).getTransmittance();
                BigDecimal n3 = rowDataList.get(rindex + 1).getTransmittance();

                sum = n1.add(n2);
                sum = sum.add(n3);
                avg = sum.divide(BigDecimal.valueOf(3), 8, RoundingMode.HALF_UP);

                avgPointList.add(avg);
//                System.out.println("added");

            }

        }
        avgPointList.add(last);
        updateSmoothedValue();

    }

    //for selected section only smoothing parameters passed start,end
    public void general_avg_algorithm_3point(int scale, int start, int end) {

        avgPointList.clear();

        BigDecimal diff = null;

        for (rindex = 1; rindex < listSize - 1; rindex++) {

            BigDecimal n1 = rowDataList.get(rindex - 1).getTransmittance();
            BigDecimal n2 = rowDataList.get(rindex).getTransmittance();

            diff = n1.subtract(n2);

            BigDecimal d = diff.abs();

            gapDifferenceList.add(d);

        }

        int minIndex = gapDifferenceList.indexOf(Collections.min(gapDifferenceList));
        int maxIndex = gapDifferenceList.indexOf(Collections.max(gapDifferenceList));

        minScale = gapDifferenceList.get(minIndex);
        maxScale = gapDifferenceList.get(maxIndex);

        smoothingFactor = ((maxScale.subtract(minScale)).divide(BigDecimal.valueOf(100))).multiply(BigDecimal.valueOf(scale));

//        System.out.println("\n Smoothing Factor " + smoothingFactor);
        BigDecimal sum = null;
        BigDecimal avg = null;

        BigDecimal first = rowDataList.get(0).getTransmittance();
        BigDecimal last = rowDataList.get(listSize - 1).getTransmittance();
        avgPointList.add(first);

        for (rindex = start; rindex < end + 1; rindex++) {

            int res;

            res = (gapDifferenceList.get(rindex - 1)).compareTo(smoothingFactor);

            if (res == 1 | res == 0) {
                avgPointList.add(rowDataList.get(rindex - 1).getTransmittance());

            } else if (res == -1) {

                BigDecimal n1 = rowDataList.get(rindex - 1).getTransmittance();
                BigDecimal n2 = rowDataList.get(rindex).getTransmittance();
                BigDecimal n3 = rowDataList.get(rindex + 1).getTransmittance();

                sum = n1.add(n2);
                sum = sum.add(n3);
                avg = sum.divide(BigDecimal.valueOf(3), 8, RoundingMode.HALF_UP);

                avgPointList.add(avg);
//                System.out.println("added");

            }

        }
        avgPointList.add(last);
//        updateSmoothedValue();

    }

    /*public void updateSmoothedValue() {
//        clearAvgTable();
        String fullarrays = "";
        for (int i = 0; i < avgPointList.size(); i++) {
            String twoarrays = "(" + rowDataList.get(i).getWavenumber() + " , " + avgPointList.get(i) + ")";
            fullarrays = fullarrays + twoarrays + ",";
        }
        fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

        String sql = "INSERT INTO avg_data (wavenumber,transmittance)  VALUES " + fullarrays;
        System.out.println(sql);
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            pst.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {

            }
        }

    }*/
    public void updateSmoothedValue(int start, int end) {
//        clearAvgTable();
        String fullarrays = "";
        int i, j;
        System.out.println(start + " " + (end - 1));
        for (i = start, j = 0; i < end && j < avgPointList.size(); i++, j++) { //this line has used for i,j both at once
            String twoarrays = "(" + rowDataList.get(i).getWavenumber() + " , " + avgPointList.get(j) + ")";
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
            System.out.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {

            }
        }

    }
    
    public void updateSmoothedValue() {
        clearAvgTable();
        String fullarrays = "";
        for (int i = 0; i < rowDataList.size(); i++) {
            String twoarrays = "(" + rowDataList.get(i).getWavenumber() + " , " + avgPointList.get(i) + ")";
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
            System.out.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {

            }
        }

    }

    public void reverse() {

        reset();
        qdata();//qdata()
        avgPointList.clear();//empty smmothed points array
        clearAvgTable();//empty table

        for (int i = 0; i < rowDataList.size(); i++) {
            avgPointList.add(rowDataList.get(i).getTransmittance());
        }
    }

    private void clearAvgTable() {

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
//        System.out.println("Table cleared");

    }

    //smooth the selected section only
    public void marked_section_smoothing_algorithm() {

        BigDecimal diff = null;
        double start = MouseMarker.getMarkerStart();
        double end = MouseMarker.getMarkerEnd();
        Double d2 = new Double(start);
        Double d3 = new Double(end);
        int startIndex = 0, endIndex = 0;

        for (int i = 0; i < listSize; i++) {

            double w = rowDataList.get(i).getWavenumber().doubleValue();
            Double d1 = new Double(w);

            if ((d2 < d1) && (d1 < d3)) {
                int x = rowDataList.get(i).getId();
                filteredIDList.add(x);
            }
        }

        startx = getIndexById(filteredIDList.get(0));
        endx = getIndexById(filteredIDList.get(filteredIDList.size() - 1));

    }

    //select a section and run a smooth only to that section, other sections remain same
    public void smooth_selected_section() {
        clearAvgTable();
        marked_section_smoothing_algorithm();
        updateUnsmoothedSection(0, startx);
        general_avg_algorithm_3point(100, startx, endx); //give starting and ending+1 sections as parameters
        updateSmoothedValue(startx, endx);
        updateUnsmoothedSection(endx, listSize);
    }

    private int getIndexById(int id) {
        for (int i = 0; i < rowDataList.size(); i++) {
            if (rowDataList != null && (rowDataList.get(i).getId() == id)) {
                return i;
            }
        }
        return -1;// not there is list
    }

//Update selected section
    public void updateUnsmoothedSection(int start, int end) {
//        clearAvgTable();
        String fullarrays = "";
        System.out.println(start+" "+(end-1));
        for (int i = start; i < end; i++) {
            String twoarrays = "(" + rowDataList.get(i).getWavenumber() + " , " + rowDataList.get(i).getTransmittance() + ")";
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
            System.out.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {

            }
        }

    }

}
