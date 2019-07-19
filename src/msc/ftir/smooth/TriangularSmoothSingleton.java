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
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static msc.ftir.smooth.SlidingAvgSmoothSingleton.count;

/**
 *
 * @author Pramuditha Buddhini
 */
public class TriangularSmoothSingleton implements SlidingWindow {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    public ArrayList<InputData> originalPoints = new ArrayList<InputData>();
    SortedMap<BigDecimal, BigDecimal> originalPointList = new TreeMap<BigDecimal, BigDecimal>();
    public ArrayList<BigDecimal> smoothedPoints = new ArrayList<BigDecimal>();
    private static volatile TriangularSmoothSingleton instance;
    private int listSize = 0 ;
    public static int count = 0;
 

    public TriangularSmoothSingleton() {
        conn = Javaconnect.ConnecrDb();

        qdata();
        fillMap();

    }

//    static {
//        instance = new TriangularSmoothSingleton();
//    }
    public void reset() {
        instance = null;
        count = 0;
        qdata();
    }

    public void reverse() {

        reset();
        count = 0;
        qdata();//qdata()
        smoothedPoints.clear();//empty smmothed points array
        clearAvgTable();//empty table

        for (int i = 0; i < originalPoints.size(); i++) {
            smoothedPoints.add(originalPoints.get(i).getTransmittance());
        }
    }

    public static TriangularSmoothSingleton getInstance() {
        instance = new TriangularSmoothSingleton();
        return instance;
    }

    public static void main(String[] args) {
        TriangularSmoothSingleton nw = new TriangularSmoothSingleton();

        for (BigDecimal wn : nw.originalPointList.keySet()) {
            BigDecimal key = wn;
            BigDecimal value = nw.originalPointList.get(wn);
            System.out.println(key + "," + value);
        }
        System.out.println("Size2 = " + nw.originalPointList.size());
        System.out.println("Size1 = " + nw.originalPoints.size());
    }

    public ArrayList<InputData> qdata() {

        String sql = "select * from input_data";
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData d;
            originalPoints.clear();
            while (rs.next()) {
                d = new InputData(rs.getInt("ID"),rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
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

    public void fillMap() {
        for (int i = 0; i < this.originalPoints.size(); i++) {

            originalPointList.put(this.originalPoints.get(i).getWavenumber(), this.originalPoints.get(i).getTransmittance());
        }
    }

    @Override
    public void cal_5point_avg() {

        if (!smoothedPoints.isEmpty()) {
            for (int i = 0; i < smoothedPoints.size(); i++) {
                originalPoints.get(i).setTransmittance(smoothedPoints.get(i));
            }
        }
        smoothedPoints.clear();

        smoothedPoints.clear();
        double sum = 0;

        BigDecimal entry = null;
        int rindex = 0;
        int listSize = originalPoints.size();

        BigDecimal start0 = originalPoints.get(0).getTransmittance();//0 1st element
        BigDecimal start1 = originalPoints.get(1).getTransmittance();//1 2nd element
        BigDecimal end1 = originalPoints.get(listSize - 1).getTransmittance();//n-2 (n-1)th element
        BigDecimal end2 = originalPoints.get(listSize - 2).getTransmittance();//n-1 (n)th element

        smoothedPoints.add(start0);
        smoothedPoints.add(start1);

        for (rindex = 2; rindex < listSize - 2; rindex++) {

            double n1 = (originalPoints.get(rindex - 2).getTransmittance()).doubleValue();
            double n2 = (originalPoints.get(rindex - 1).getTransmittance()).doubleValue();
            double n3 = (originalPoints.get(rindex).getTransmittance()).doubleValue();
            double n4 = (originalPoints.get(rindex + 1).getTransmittance()).doubleValue();
            double n5 = (originalPoints.get(rindex + 2).getTransmittance()).doubleValue();

            sum = n1 + 2 * n2 + 3 * n3 + 2 * n4 + n5;

//            avg = sum/5;
            entry = BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(9), 8, RoundingMode.HALF_UP);

            smoothedPoints.add(entry);

        }
        smoothedPoints.add(end1);
        smoothedPoints.add(end2);
        updateSmoothedValue();
        count++;

    }

    @Override
    public void updateSmoothedValue() {
        clearAvgTable();
        String fullarrays = "";
        for (int i = 0; i < originalPoints.size(); i++) {
            String twoarrays = "(" + originalPoints.get(i).getWavenumber() + " , " + smoothedPoints.get(i) + ")";
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

    @Override
    public void cal_3point_avg() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cal_7point_avg() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cal_9point_avg() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    


}
