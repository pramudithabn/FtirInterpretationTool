/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.result;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import msc.ftir.main.InputData;
import msc.ftir.main.Javaconnect;

/**
 *
 * @author Pramuditha Buddhini
 */
public class Predict {

    private Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private int listSize;
    private BigDecimal f;
    private InputData d;
    public static ArrayList<InputData> valleyList = new ArrayList<InputData>();
    //results

    public ArrayList<Result> resultset = new ArrayList<Result>();

    public static void main(String[] args) {
        Predict p = new Predict();
        p.getResults();
    }

    public Predict() {
        conn = Javaconnect.ConnecrDb();
        getCandidates();
    }

    public void getCandidates() {

        String sql = "SELECT * FROM `candidates`";
        valleyList.clear();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                d = new InputData(rs.getInt("ID"), rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                valleyList.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Javaconnect.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        listSize = valleyList.size();
    }

    public void getResults() {
        resultset.clear();
        for (int r = 0; r < listSize; r++) {
            f = valleyList.get(r).getWavenumber();
            if (f.doubleValue() >= 1000) { //exclude fingerprint region
                //get results from library
//                String sql = "Select * from library where " + f + "  <= start_frq AND " + f + ">= end_frq";
                //update 09/03/2020
                String sql = "Select * from library2 where " + f + "  <= start_frq AND " + f + ">= end_frq";
                try {
                    pst = conn.prepareStatement(sql);
                    rs = pst.executeQuery();
                    Result rst;
                    while (rs.next()) {
                        rst = new Result(f, rs.getString("BOND_VIBMODE"), rs.getString("FUNCTIONAL_GROUP"), rs.getString("COMPOUND_TYPE"), rs.getString("COMPOUND_CATEGORY"), rs.getString("LABEL"), rs.getInt("ID"));
                        resultset.add(rst);
                    }
                } catch (SQLException ex) {
                    System.err.println(ex);
                    Logger.getLogger(Javaconnect.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        rs.close();
                        pst.close();
                    } catch (Exception e) {
                        System.err.print(e);
                    }
                }
            }
        }
    }

    public void find_min() {
        for (int i = 0; i < resultset.size(); i++) {
            Result rst1 = resultset.get(i);
        }
    }

    public void updateResultsTable() {

        clearTable();
        String fullarrays = "";

        for (int i = 0; i < resultset.size(); i++) {

            BigDecimal w = resultset.get(i).getWavenumber();
            String bond = resultset.get(i).getBondVibMode();
            String fngrp = resultset.get(i).getFunctional_group();
            String type = resultset.get(i).getCompound_type();
            String compound = resultset.get(i).getCompoundCategory();
            int libIndex = resultset.get(i).getLibIndex();
            //update 09/03/2020
//            String twoarrays = "(" + w + " ,\" " + bond + "\" , \"" + fngrp + "\" , \"" + type + "\" , \"" + compound + "\" , \"" + libIndex + "\")";
            String twoarrays = "(" + w + " ,\" " + bond + "\" , \"" + fngrp + "\" , \"" + type + "\" , \"" + compound + "\" , \"" + libIndex + "\")";
            fullarrays = fullarrays + twoarrays + ",";
        }

        fullarrays = fullarrays.substring(0, fullarrays.length() - 1);
        String sql = "INSERT INTO result ( WAVENUMBER,BOND, FUNCTIONAL_GROUP, COMPOUND_TYPE,  COMPOUND_CATEGORY, LIB_INDEX )  VALUES " + fullarrays;
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
                System.err.println(e);
            }
        }
    }

    private void clearTable() {
        String sql1 = "delete from result";
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
