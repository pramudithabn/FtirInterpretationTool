/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import msc.ftir.util.FileType;

/**
 *
 * @author Pramuditha Buddhini
 */
public class AbsToTrans {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    private FileType fileType;
    private ArrayList<BigDecimal> absorbance = new ArrayList<BigDecimal>(); // store them in an arraylist
    private ArrayList<BigDecimal> wavenumber = new ArrayList<BigDecimal>();
    private ArrayList<BigDecimal> transmittance = new ArrayList<BigDecimal>();
    int size;

    public AbsToTrans() {
        conn = Javaconnect.ConnecrDb();
        convert();
        updateTable();

    }

    public void convert() {

        String query1 = "select * from abs_data"; // get all the id's from the table 

        try {
            PreparedStatement stmnt = conn.prepareStatement(query1);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) {
                wavenumber.add(rs.getBigDecimal("Wavenumber"));
                absorbance.add(rs.getBigDecimal("Transmittance"));
                size = absorbance.size();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {

            }
        }
        //A = 2 - log T
        //T = 100*10^(-A)

        for (int i = 0; i < size; i++) {

            double w = wavenumber.get(i).doubleValue();

            if (w >= 400 && w <= 4000) { //skip values outside 400-4000 range
                double a = absorbance.get(i).doubleValue();
                System.out.println(a);
                double t = 100 * Math.pow(10, -a);
//            double aa = Double.valueOf(2) - Math.log10(a);
                DecimalFormat df = new DecimalFormat(".########");
                System.out.println(df.format(t));
                BigDecimal trans = new BigDecimal(t);
                BigDecimal a2 = trans.setScale(8, RoundingMode.HALF_UP);
                System.out.println(a2);
//            BigDecimal bd = abs.setScale(8, RoundingMode.HALF_UP);

                transmittance.add(a2);

            }

        }
    }

    public void updateTable() {
        clearTable();
        String fullarrays = "";
        int size = transmittance.size();
        for (int i = 0; i < size; i++) {

            double w = wavenumber.get(i).doubleValue();

            if (w >= 400 && w <= 4000) {
                String twoarrays = "(" + wavenumber.get(i) + " , " + transmittance.get(i) + ")";
                fullarrays = fullarrays + twoarrays + ",";
            }

        }
        fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

        String sql = "INSERT INTO input_data (WAVENUMBER,TRANSMITTANCE)  VALUES " + fullarrays;
//        System.out.println(sql);

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

    public void clearTable() {

        String sql1 = "delete from input_data";
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
