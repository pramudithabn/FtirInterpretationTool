/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.valleys;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import msc.ftir.main.InputData;
import msc.ftir.main.Javaconnect;
import msc.ftir.main.MainWindow;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Pramuditha Buddhini
 */
public class ValleysLocator {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    private static ArrayList<BigDecimal> order1_derivatives = new ArrayList<BigDecimal>();
    private ArrayList<BigDecimal> order2_derivatives = new ArrayList<BigDecimal>();
    private SortedMap<BigDecimal, BigDecimal> valleyCandidates = new TreeMap<BigDecimal, BigDecimal>();
    private SortedMap<BigDecimal, BigDecimal> tempvalleyCandidates = new TreeMap<BigDecimal, BigDecimal>();
    private ArrayList<BigDecimal> hList = new ArrayList<BigDecimal>();
    private static volatile ValleysLocator instance;
    private BigDecimal minH;
    private BigDecimal maxH;
    private double hScale;
    private double c;
    private static ArrayList<InputData> smoothedPointList = new ArrayList<InputData>();

    public static ArrayList<InputData> getSmoothedPointList() {
        return smoothedPointList;
    }
    private static ArrayList<InputData> baselineCorrectedPointList = new ArrayList<InputData>();

    public static ArrayList<InputData> getBaselineCorrectedPointList() {
        return baselineCorrectedPointList;
    }

    private int listSize;
    public ArrayList<BigDecimal> transmittanceValues = new ArrayList<BigDecimal>();

    //original data map
    private NavigableMap<BigDecimal, BigDecimal> allPoints = new TreeMap<BigDecimal, BigDecimal>();

    //baseline data map
    private NavigableMap<BigDecimal, BigDecimal> bcPoints = new TreeMap<BigDecimal, BigDecimal>();

    //candidate valley set 
    private NavigableMap<BigDecimal, BigDecimal> candidates = new TreeMap<BigDecimal, BigDecimal>();

    //peak tops set 
    private NavigableMap<BigDecimal, BigDecimal> peaktops = new TreeMap<BigDecimal, BigDecimal>();

    //baseline corrcted point set
    private NavigableMap<BigDecimal, BigDecimal> bl_points = new TreeMap<BigDecimal, BigDecimal>();

    public NavigableMap<BigDecimal, BigDecimal> getPeaktops() {
        return peaktops;
    }

    public NavigableMap<BigDecimal, BigDecimal> getCandidates() {
        return candidates;
    }

    //derivatives maps
    private NavigableMap<BigDecimal, BigDecimal> firstOrderDerivatives = new TreeMap<BigDecimal, BigDecimal>();
    private NavigableMap<BigDecimal, BigDecimal> secondOrderDerivatives = new TreeMap<BigDecimal, BigDecimal>();

    //regression parameters
    private double a0 = 0;
    private double a1 = 0;
    private double a2 = 0;

    double lowerB, upperT;

    public SortedMap<BigDecimal, BigDecimal> getValleyCandidates() {
        return valleyCandidates;
    }

    public void setValleyCandidates(SortedMap<BigDecimal, BigDecimal> valleyCandidates) {
        this.valleyCandidates = valleyCandidates;
    }

    public ValleysLocator(String tb) {
        conn = Javaconnect.ConnecrDb();
        qdata(tb);

    }

    public ArrayList<InputData> qdata(String tablename) {

        String sql = "select * from " + tablename;
        ResultSet rs = null;
        PreparedStatement pst = null;
        allPoints.clear();
        smoothedPointList.clear();

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData d;
            while (rs.next()) {
                d = new InputData(rs.getInt("ID"), rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                smoothedPointList.add(d);
                allPoints.put(rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));

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

        listSize = smoothedPointList.size();
        System.out.println("list size " + listSize);
        return smoothedPointList;

    }

    public ArrayList<InputData> qBLdata() {

        String sql = "select * from baseline_data";
        ResultSet rs = null;
        PreparedStatement pst = null;
        allPoints.clear();
        baselineCorrectedPointList.clear();
        smoothedPointList.clear();

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData d;
            while (rs.next()) {
                d = new InputData(rs.getInt("ID"), rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                baselineCorrectedPointList.add(d);
//                smoothedPointList.add(d);
                allPoints.put(rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));

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

        listSize = baselineCorrectedPointList.size();
//        System.out.println("list size " + listSize);
        return baselineCorrectedPointList;

    }

    //1. 1st order derivatives    
    public void cal_1storder_derivative(ArrayList<InputData> list) {

        double x1 = 0, x2 = 0, x3 = 0;
        double y1 = 0, y2 = 0, y3 = 0;
        BigDecimal result = null;

        x1 = list.get(0).getWavenumber().doubleValue();
        x2 = list.get(1).getWavenumber().doubleValue();

        y1 = list.get(0).getTransmittance().doubleValue();
        y2 = list.get(1).getTransmittance().doubleValue();

        result = BigDecimal.valueOf((y2 - y1) / (x2 - x1));

        firstOrderDerivatives.put(BigDecimal.valueOf(x1), result);

        for (int i = 1; i <= listSize - 2; i++) {

            x1 = list.get(i - 1).getWavenumber().doubleValue();
            BigDecimal xn = list.get(i).getWavenumber();
            x2 = list.get(i + 1).getWavenumber().doubleValue();

            y1 = list.get(i - 1).getTransmittance().doubleValue();
            y2 = list.get(i + 1).getTransmittance().doubleValue();

            double d1 = (y2 - y1) / (x2 - x1);

            result = BigDecimal.valueOf(d1);
            firstOrderDerivatives.put(xn, result);
        }

        x1 = list.get(listSize - 2).getWavenumber().doubleValue();
        x2 = list.get(listSize - 1).getWavenumber().doubleValue();
        y1 = list.get(listSize - 2).getTransmittance().doubleValue();
        y2 = list.get(listSize - 1).getTransmittance().doubleValue();

        result = BigDecimal.valueOf((y2 - y1) / (x2 - x1));

        firstOrderDerivatives.put(list.get(listSize - 1).getWavenumber(), result);

//        System.out.println("FOD " + firstOrderDerivatives.size());
    }

    //2nd order derivatives
    public void cal_2ndorder_derivative(ArrayList<InputData> list) {

        SortedMap<BigDecimal, BigDecimal> three_pointset = new TreeMap<BigDecimal, BigDecimal>();

        BigDecimal firstx = list.get(0).getWavenumber();
        BigDecimal firsty = list.get(0).getTransmittance();
        BigDecimal lastx = list.get(listSize - 1).getWavenumber();
        BigDecimal lasty = list.get(listSize - 1).getTransmittance();

        secondOrderDerivatives.put(firstx, firsty);

        for (int rindex = 1; rindex <= listSize - 2; rindex++) {

//            BigDecimal y0 = smoothedPointList.get(rindex - 2).getTransmittance();
            BigDecimal y1 = list.get(rindex - 1).getTransmittance();
            BigDecimal y2 = list.get(rindex).getTransmittance();
            BigDecimal y3 = list.get(rindex + 1).getTransmittance();
//            BigDecimal y4 = smoothedPointList.get(rindex + 2).getTransmittance();

//            BigDecimal x0 = smoothedPointList.get(rindex - 1).getWavenumber();
            BigDecimal x1 = list.get(rindex - 1).getWavenumber();
            BigDecimal x2 = list.get(rindex).getWavenumber();
            BigDecimal x3 = list.get(rindex + 1).getWavenumber();
//            BigDecimal x5 = smoothedPointList.get(rindex - 1).getWavenumber();

            //find y value by regression equation
            //step 1 : add 3 successive points to the list and create a dataset 
            three_pointset.clear();
            three_pointset.put(x1, y1);
            three_pointset.put(x2, y2);
            three_pointset.put(x3, y3);

            //step 2 : Get the regression line equation 
            calRegressionPolynomial(createDataset(three_pointset));

            //step 3 : Calculate y value for the mid point
            double x = x2.doubleValue();
            double y = a2 * x * x + a1 * x + a0;
//            System.out.println("y = "+y+"x = "+ x+"    y = " + a2 + "*x^2+ " + a1 + "*x + " + a0);

            //step 4 : Add calculated point to result treemap
            secondOrderDerivatives.put(x2, BigDecimal.valueOf(y));

        }
        secondOrderDerivatives.put(lastx, lasty);

        //print list
//        for (BigDecimal wavelength : secondOrderDerivatives.keySet()) {
//
//            BigDecimal key = wavelength;
//            BigDecimal fod = firstOrderDerivatives.get(wavelength);
//            BigDecimal sod = secondOrderDerivatives.get(wavelength);
//
//            System.out.println(key + " , " + fod + " , " + sod);
//        }
//        System.out.println("SOD " + secondOrderDerivatives.size());
    }

    //3. create a candidate set
    public NavigableMap<BigDecimal, BigDecimal> findCandidateSet() {
        candidates.clear();
        for (Entry<BigDecimal, BigDecimal> entry : firstOrderDerivatives.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            Entry<BigDecimal, BigDecimal> next = firstOrderDerivatives.higherEntry(key); //next
            Entry<BigDecimal, BigDecimal> prev = firstOrderDerivatives.lowerEntry(key);  // previous

            if ((next != null && prev != null)) {

                BigDecimal currentV = entry.getValue();
                BigDecimal nextV = next.getValue();
                BigDecimal prevV = prev.getValue();

                if (nextV.doubleValue() > 0 && prevV.doubleValue() < 0 && currentV.doubleValue() < 0) {
                    candidates.put(key, allPoints.get(key));
                }

            }

        }

//        System.out.println("Candidate size " + candidates.size());
        return candidates;
        //print all
//        for (BigDecimal name : candidates.keySet()) {
//
//            String key = name.toString();
//            String value = candidates.get(name).toString();
//            System.out.println("new list " + key + " " + value);
//        }

    }

    //4. threshold adjuster
    public void discardBelowThresh(int n, double l, double u) {

//            double max = candidates.values().stream().max(Integer::compare).get();
//            double min = candidates.values().stream().min(Integer::compare).get();
//            
//            System.out.println("Max   "+ max);
//            System.out.println("Min   "+ min);
        double max = Collections.max(candidates.values()).doubleValue();
        double min = Collections.min(candidates.values()).doubleValue();

        System.out.println("Max   " + max);
        System.out.println("Min   " + min);

        lowerB = l;
        upperT = u;
        NavigableMap<BigDecimal, BigDecimal> temp = new TreeMap<BigDecimal, BigDecimal>();

//        System.out.println(lowerB + "," + upperT);
//        System.out.println(n + "," + (100-n));
        double threshold = (((max - min) / 100) * (100 - n)) + min;

        //adjust the new value to existing noiseLevel
        if (lowerB < 0) {
            threshold = threshold + lowerB;
        }

//        System.out.println("Threshold " + threshold);
        temp.clear();

        for (BigDecimal wvl : candidates.keySet()) {

            double value = candidates.get(wvl).doubleValue();
            BigDecimal key = wvl;

            if (value < threshold) {
                temp.put(key, BigDecimal.valueOf(value));
            }
        }

        candidates.clear();
        candidates = temp;

//        System.out.println("New size " + candidates.size());
        //print all
/*        for (BigDecimal name : candidates.keySet()) {

            String key = name.toString();
            String value = candidates.get(name).toString();
            System.out.println("new list " + key + " " + value);
        }
         */
    }

    //6. noise level control
    public void adjustNoiseLevel(int n) {
        //6.1 take remaining candidates 
        //6.2 find next and prev points from original list
        //6.3 take the differences, store them
        //6.4 find max, min of differences and create a noiseLevel

        NavigableMap<BigDecimal, BigDecimal> prevDiff = new TreeMap<BigDecimal, BigDecimal>();
        NavigableMap<BigDecimal, BigDecimal> nextDiff = new TreeMap<BigDecimal, BigDecimal>();
        NavigableMap<BigDecimal, BigDecimal> temp = new TreeMap<BigDecimal, BigDecimal>();

        for (Entry<BigDecimal, BigDecimal> entry : candidates.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            Entry<BigDecimal, BigDecimal> next = allPoints.higherEntry(key); //next
            Entry<BigDecimal, BigDecimal> prev = allPoints.lowerEntry(key);  // previous
//            System.out.println(prev+"   " +key+"   "+next);

            if ((next != null && prev != null)) { //to ignore first, last points of the dataset

                double currentV = entry.getValue().doubleValue();
                double nextV = next.getValue().doubleValue();
                double prevV = prev.getValue().doubleValue();

                double nextDifference = Math.abs(currentV - nextV);
                double preDifference = Math.abs(currentV - prevV);

                nextDiff.put(key, BigDecimal.valueOf(nextDifference));
                prevDiff.put(key, BigDecimal.valueOf(preDifference));
            }

        }

        //max and min of point after
        Entry<BigDecimal, BigDecimal> maxEntryn = Collections.max(nextDiff.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double maxn = maxEntryn.getValue().doubleValue();
        System.out.println(maxEntryn.getValue());

        Entry<BigDecimal, BigDecimal> minEntryn = Collections.min(nextDiff.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double minn = minEntryn.getValue().doubleValue();
        System.out.println(minEntryn.getValue());

        //max and min of point before
        Entry<BigDecimal, BigDecimal> maxEntryb = Collections.max(prevDiff.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double maxb = maxEntryb.getValue().doubleValue();
        System.out.println(maxEntryb.getValue());

        Entry<BigDecimal, BigDecimal> minEntryb = Collections.min(prevDiff.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double minb = minEntryb.getValue().doubleValue();
        System.out.println(minEntryb.getValue());

        double min = Math.min(minn, minb);
        double max = Math.max(maxn, maxb);

        double noise_thresh = ((max - min) / 10) * n;
        System.out.println(noise_thresh);

        for (Entry<BigDecimal, BigDecimal> entry : candidates.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            double nextval = nextDiff.get(key).doubleValue();
            double prevval = prevDiff.get(key).doubleValue();

            if (nextval > noise_thresh && prevval > noise_thresh) {
                temp.put(key, candidates.get(key));
            }

        }

        candidates.clear();
        candidates = temp;
//        System.out.println("After noise removal " + candidates.size());

//        System.out.println(candidates.size()+"     "+nextDiff.size()+"   "+prevDiff.size());
    }

    //7. look for peaks in the neighbourhood
    public void evaluateNeighbourhood() {

        //1. find V & P
        NavigableMap<BigDecimal, BigDecimal> valleys = new TreeMap<BigDecimal, BigDecimal>();
        NavigableMap<BigDecimal, BigDecimal> peaks = new TreeMap<BigDecimal, BigDecimal>();
        NavigableMap<BigDecimal, BigDecimal> temp = new TreeMap<BigDecimal, BigDecimal>();

        valleys = getCandidates();
        peaks = peakTopSet();
//        System.out.println("sizes " + peaks.size() + "  " + valleys.size());

        for (Entry<BigDecimal, BigDecimal> entry : valleys.entrySet()) {
            //find lower Peak > wavelength of last Vi
            BigDecimal v = entry.getKey();
            Entry<BigDecimal, BigDecimal> p1 = peaks.lowerEntry(v); //p1, v, p3 order
            Entry<BigDecimal, BigDecimal> p3 = peaks.higherEntry(v);
            if (p1 != null && p3 != null) {
                double p1_y = p1.getValue().doubleValue();
                double p1_x = p1.getKey().doubleValue();

                double p3_y = p3.getValue().doubleValue();
                double p3_x = p3.getKey().doubleValue();

                double v_y = entry.getValue().doubleValue();
                double v_x = entry.getKey().doubleValue();

                double grad1_2 = (v_y - p1_y) / (v_x - p1_x);
                double grad2_3 = (p3_y - v_y) / (p3_x - v_x);

                if (grad1_2 < 0 && grad2_3 > 0) {
                    temp.put(v, entry.getValue());
                }

            }

            //find higher Peak < wavelength of next Vi
        }
        candidates.clear();
        candidates = temp;
//        System.out.println("NEWWWW " + candidates.size());

        /* 
        boolean right_exist_ptops = false, left_exist_ptops = false;

        for (Entry<BigDecimal, BigDecimal> entry : candidates.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            System.out.print(key + "\t");
            int r = 0, l = 0;

            //search for peaks on right side
            double pointR = firstOrderDerivatives.higherEntry(key).getValue().doubleValue(); //n+1

            if (pointR > 0) { //fod is positive and sod is concave up
                NavigableMap<BigDecimal, BigDecimal> tailmap = firstOrderDerivatives.tailMap(entry.getValue(), false);

                for (Entry<BigDecimal, BigDecimal> tailEntry : tailmap.entrySet()) {
                    double x = tailEntry.getValue().doubleValue();
                    BigDecimal k = tailEntry.getKey();

                    r++;

                    if (tailmap.lowerEntry(k).getValue() != null && tailmap.higherEntry(k).getValue() != null) {
                        BigDecimal x1 = tailmap.lowerEntry(k).getValue();
                        BigDecimal x3 = tailmap.higherEntry(k).getValue();
                        if (x1.doubleValue() > 0 && x3.doubleValue() < 0 && r <= range) {
                            right_exist_ptops = true;
                            break;

                        }
                    }

                }
                System.out.print(right_exist_ptops + "\t");
            }

            //search for peaks on left side
            double pointL = firstOrderDerivatives.lowerEntry(key).getValue().doubleValue(); //n-1
            double pointL2OD = secondOrderDerivatives.lowerEntry(key).getValue().doubleValue(); //n+1

            if (pointL < 0) {

                NavigableMap<BigDecimal, BigDecimal> headmap = firstOrderDerivatives.headMap(entry.getValue(), false);
                NavigableMap<BigDecimal, BigDecimal> reverse = headmap.descendingMap();

                for (Entry<BigDecimal, BigDecimal> headEntry : reverse.entrySet()) {
                    double d = headEntry.getValue().doubleValue();
                    BigDecimal k = headEntry.getKey();
                    BigDecimal x1 = headmap.lowerEntry(k).getValue();
                    BigDecimal x3 = headmap.higherEntry(k).getValue();
                    l++;
                    if (x1 != null && x3 != null) {
                        if (x1.doubleValue() > 0 && x3.doubleValue() < 0 && l <= range) {
                            left_exist_ptops = true;
                            break;
                        }
                    }

                }
                System.out.print(left_exist_ptops + "\t");
            }
//            System.out.println(left_exist_ptops + "   " + right_exist_ptops);
        }


             for (Entry<BigDecimal, BigDecimal> entry : candidates.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            Entry<BigDecimal, BigDecimal> pointL = candidates.lowerEntry(key); //n-1
            Entry<BigDecimal, BigDecimal> pointR = candidates.higherEntry(key); //n+1
            if (pointL != null && pointR != null) {
                double sod1 = secondOrderDerivatives.get(pointL.getKey()).doubleValue();
                double sod2 = secondOrderDerivatives.get(pointR.getKey()).doubleValue();

                if (sod1 > 0 && sod1 > 0) {
                    temp.put(key, entry.getValue());
                }

               
//            //find the id of this key
//            for (int i = 0; i < listSize; i++) {
//                if (smoothedPointList.get(i).getWavenumber().doubleValue() == key.doubleValue()) {
//                    id = smoothedPointList.get(i).getId();
//                }
//            }
            //find FOD of left entry
            NavigableMap<BigDecimal, BigDecimal> headmap = firstOrderDerivatives.headMap(entry.getValue(), true);
            NavigableMap<BigDecimal, BigDecimal> reverse = headmap.descendingMap();

            for (Entry<BigDecimal, BigDecimal> en : reverse.entrySet()) {

                BigDecimal c = en.getKey();
                double val = en.getValue().doubleValue();
                double sod = secondOrderDerivatives.get(c).doubleValue();
//                Entry<BigDecimal, BigDecimal> pointL = reverse.lowerEntry(c); //n-1
//                Entry<BigDecimal, BigDecimal> pointR = reverse.higherEntry(c); //n+1

                if (val > 0) { //slope is positive and sod is concave down
                    left_exist_ptops = false;

                }

                while (val < 0) {
                    double fod = firstOrderDerivatives.higherEntry(c).getValue().doubleValue();

                    if (fod > 0) {
                        left_exist_ptops = true;
                        break;
                    } else {
                        continue;
                    }
                }
//                if ((pointL != null && pointR != null)) {//skip first and last point
//                    //looking for peak tops -> + to - changes
//                    if ((pointL.getValue().doubleValue() > 0) && (pointR.getValue().doubleValue() < 0)) {
//                        //second order derivative >0 -> convave up
//                        //second order derivative <0 -> convave down
//                        double sod_L = secondOrderDerivatives.lowerEntry(c).getValue().doubleValue();
//                        double sod_R = secondOrderDerivatives.higherEntry(c).getValue().doubleValue();
//                        if (sod_L > 0 && sod_R < 0) {
//                            left_exist_ptops = true;
//                        }
//
//                    } else {
//                        continue;
//                    }
//                }
            }

            //find FOD of right entry
            NavigableMap<BigDecimal, BigDecimal> tailmap = firstOrderDerivatives.tailMap(entry.getValue(), true);

            for (Entry<BigDecimal, BigDecimal> ep : tailmap.entrySet()) {
                BigDecimal c = ep.getKey();
                double val = ep.getValue().doubleValue();

                if (val < 0) {
                    right_exist_ptops = false;
                }

                while (val > 0) {
                    double fod = firstOrderDerivatives.higherEntry(c).getValue().doubleValue();
                    if (fod < 0) {
                        right_exist_ptops = true;
                        break;
                    } else {
                        continue;
                    }
                }

//                Entry<BigDecimal, BigDecimal> pointL = tailmap.lowerEntry(c); //n-1
//                Entry<BigDecimal, BigDecimal> pointR = tailmap.higherEntry(c); //n+1
//
//                if ((pointL != null && pointR != null)) {//skip first and last point
//                    //looking for peak tops -> + to - changes
//                    if ((pointL.getValue().doubleValue() > 0) && (pointR.getValue().doubleValue() < 0)) {
//                        //second order derivative >0 -> convave up
//                        //second order derivative <0 -> convave down
//                        double sod_L = secondOrderDerivatives.lowerEntry(c).getValue().doubleValue();
//                        double sod_R = secondOrderDerivatives.higherEntry(c).getValue().doubleValue();
//                        if (sod_L > 0 && sod_R < 0) {
//                            right_exist_ptops = true;
//                        }
//                    } else {
//                        continue;
//                    }
//                }
            }

            if ((left_exist_ptops && right_exist_ptops)) {
                //discard point
                System.out.println(candidates.size());
           
            }
        }

        candidates.clear();
        candidates = temp;
        System.out.println(candidates.size()); }*/
    }
    //8. discard depending on the h with neighbours

    public void cal_h(int n) {
        NavigableMap<BigDecimal, BigDecimal> h_listL = new TreeMap<BigDecimal, BigDecimal>();
        NavigableMap<BigDecimal, BigDecimal> h_listR = new TreeMap<BigDecimal, BigDecimal>();
        NavigableMap<BigDecimal, BigDecimal> temp = new TreeMap<BigDecimal, BigDecimal>();

        for (Entry<BigDecimal, BigDecimal> entry : candidates.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            Entry<BigDecimal, BigDecimal> pointL = allPoints.lowerEntry(key); //n-1
            Entry<BigDecimal, BigDecimal> pointR = allPoints.higherEntry(key); //n+1

            if (pointL != null && pointR != null) {
                double diffL = Math.abs(candidates.get(key).doubleValue() - pointL.getValue().doubleValue());
                double diffR = Math.abs(candidates.get(key).doubleValue() - pointR.getValue().doubleValue());

                h_listL.put(key, BigDecimal.valueOf(diffL));
                h_listR.put(key, BigDecimal.valueOf(diffR));
            }
        }

        //max and min of point before
        Entry<BigDecimal, BigDecimal> maxEntry1 = Collections.max(h_listL.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double max1 = maxEntry1.getValue().doubleValue();

        Entry<BigDecimal, BigDecimal> maxEntry2 = Collections.max(h_listR.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double max2 = maxEntry2.getValue().doubleValue();

        Entry<BigDecimal, BigDecimal> minEntry1 = Collections.min(h_listL.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double min1 = minEntry1.getValue().doubleValue();

        Entry<BigDecimal, BigDecimal> minEntry2 = Collections.min(h_listR.entrySet(), (Entry<BigDecimal, BigDecimal> e1, Entry<BigDecimal, BigDecimal> e2) -> e1.getValue().compareTo(e2.getValue()));
        double min2 = minEntry2.getValue().doubleValue();

        double max = (max1 + max2) / 2;
        double min = (min1 + min2) / 2;
        double h = ((max - min) / 100) * (n / 10);

        for (Entry<BigDecimal, BigDecimal> entry : candidates.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            double hL = h_listL.get(key).doubleValue(); //n-1
            double hR = h_listR.get(key).doubleValue(); //n+1

            if (hL > h && hR > h) {
                temp.put(key, candidates.get(key));
            }
        }

        candidates.clear();
        candidates = temp;
        System.out.println("new reduced size = " + candidates.size());

    }

    //final: Valley alogorithm
    public void peakBottomsDetector(double down, double up) {

        ValleysLocator v1 = new ValleysLocator("baseline_data");
        v1.cal_1storder_derivative(smoothedPointList);
        v1.cal_2ndorder_derivative(smoothedPointList);
        v1.findCandidateSet();
        v1.discardBelowThresh(50, down, up);
        v1.cal_h(150);
        v1.evaluateNeighbourhood();

    }

    public void find_valley_candidates() {

        double y1 = 0, y3 = 0;
        BigDecimal x2 = null, y2 = null;

        for (int i = 1; i < listSize - 1; i++) {
            y1 = order1_derivatives.get(i - 1).doubleValue();
            x2 = smoothedPointList.get(i).getWavenumber();
            y2 = smoothedPointList.get(i).getTransmittance();
            y3 = order1_derivatives.get(i + 1).doubleValue();

            if (y1 < 0 && y3 > 0) {

                valleyCandidates.put(x2, y2);
            }

        }

        for (BigDecimal name : valleyCandidates.keySet()) {

            String key = name.toString();
            String value = valleyCandidates.get(name).toString();
            System.out.println(key + " " + value);

        }

    }

    public void removeBelowThreshold(double threshold) {
        for (BigDecimal wavelegth : valleyCandidates.keySet()) {

            double key = wavelegth.doubleValue();
            double value = valleyCandidates.get(wavelegth).doubleValue();

            if (value < threshold) {
                valleyCandidates.headMap(BigDecimal.valueOf(threshold)).clear();
            } else {
                return;
            }
        }

        for (BigDecimal name : valleyCandidates.keySet()) {

            String key = name.toString();
            String value = valleyCandidates.get(name).toString();
            System.out.println(key + " " + value);

        }
    }

    public void point3Valleys() {

        qdata("avg_data");
        valleyCandidates.clear();

        double x1 = 0, x2 = 0, x3 = 0;
        double y1 = 0, y2 = 0, y3 = 0;
        BigDecimal x_val = null, y_val = null;
        double d1 = 0, d2 = 0;
        valleyCandidates.put(smoothedPointList.get(0).getWavenumber(), smoothedPointList.get(0).getTransmittance());//first point
        for (int i = 1; i < listSize - 2; i++) {

            x1 = smoothedPointList.get(i - 1).getWavenumber().doubleValue();
            x2 = smoothedPointList.get(i).getWavenumber().doubleValue();
            x3 = smoothedPointList.get(i + 1).getWavenumber().doubleValue();
            x_val = smoothedPointList.get(i).getWavenumber();

            y1 = smoothedPointList.get(i - 1).getTransmittance().doubleValue();
            y2 = smoothedPointList.get(i).getTransmittance().doubleValue();
            y3 = smoothedPointList.get(i + 1).getTransmittance().doubleValue();
            y_val = smoothedPointList.get(i).getTransmittance();

            d1 = (y2 - y1) / (x2 - x1);
            d2 = (y3 - y2) / (x3 - x2);

//            double h1, h2, hconst;
//            h1 = Math.abs(y2 - y1);
//            h2 = Math.abs(y3 - y2);
//
//            hconst = discard_below_threshold(10);
//            System.out.println(hconst);
            if (d1 < 0 && d2 > 0) {
//                if ((h1 >= hconst) && (h2 >= hconst)) {
                valleyCandidates.put(x_val, y_val);
//                }
            }

        }
        valleyCandidates.put(smoothedPointList.get(listSize - 1).getWavenumber(), smoothedPointList.get(listSize - 1).getTransmittance()); //last point
//        for (BigDecimal wvl : valleyCandidates.keySet()) {
//
//            String key = wvl.toString();
//            String value = valleyCandidates.get(wvl).toString();
//            System.out.println(key + " " + value);
//
//        }
        System.out.println("Valley points # " + valleyCandidates.size());

    }

    public void addCandidates4neighbors() {

        double x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;
        double y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0;
        BigDecimal x_val = null, y_val = null;
        double d1 = 0, d2 = 0;//neighbours 1
        double n1 = 0, n2 = 0;//neighbours 2

        for (int i = 0; i < listSize - 4; i++) {

            x1 = smoothedPointList.get(i).getWavenumber().doubleValue();
            x2 = smoothedPointList.get(i + 1).getWavenumber().doubleValue();
            x3 = smoothedPointList.get(i + 2).getWavenumber().doubleValue();
            x4 = smoothedPointList.get(i + 3).getWavenumber().doubleValue();
            x5 = smoothedPointList.get(i + 4).getWavenumber().doubleValue();
//            x_val = smoothedPointList.get(i + 1).getWavenumber();
            x_val = smoothedPointList.get(i + 2).getWavenumber();

            y1 = smoothedPointList.get(i).getTransmittance().doubleValue();
            y2 = smoothedPointList.get(i + 1).getTransmittance().doubleValue();
            y3 = smoothedPointList.get(i + 2).getTransmittance().doubleValue();
            y4 = smoothedPointList.get(i + 3).getTransmittance().doubleValue();
            y5 = smoothedPointList.get(i + 4).getTransmittance().doubleValue();
//            y_val = smoothedPointList.get(i + 1).getTransmittance();
            y_val = smoothedPointList.get(i + 2).getTransmittance();

//            d1 = (y2 - y1) / (x2 - x1);
//            d2 = (y3 - y2) / (x3 - x2);
            d1 = (y3 - y2) / (x3 - x2);
            d2 = (y4 - y3) / (x4 - x3);

            n1 = (y2 - y1) / (x2 - x1);
            n2 = (y5 - y4) / (x5 - x4);

//            double h1, h2, hconst;
//            h1 = Math.abs(y2 - y1);
//            h2 = Math.abs(y3 - y2);
//
//            hconst = discard_below_threshold(10);
//            System.out.println(hconst);
            if (d1 > 0 && d2 < 0 && n1 > 0 && n2 < 0) {
//                if ((h1 >= hconst) && (h2 >= hconst)) {
                valleyCandidates.put(x_val, y_val);
//                }
            }

        }
        for (BigDecimal wvl : valleyCandidates.keySet()) {

            String key = wvl.toString();
            String value = valleyCandidates.get(wvl).toString();
            System.out.println(key + " " + value);

        }

    }

    public void discard_below_threshold(BigDecimal c) {

        for (BigDecimal wvl : valleyCandidates.keySet()) {

            double value = valleyCandidates.get(wvl).doubleValue();
            BigDecimal key = wvl;

            if (value > c.doubleValue()) {
                tempvalleyCandidates.put(key, BigDecimal.valueOf(value));
            }
        }

        valleyCandidates.clear();
        valleyCandidates = tempvalleyCandidates;
    }

    public void steepning(int n) {

        switch (n) {
            case 1:
                point3Valleys();
                break;

            case 2:
                point5Valley();
                break;

            case 3:
                point7Valley();
                break;

            case 4:
                point9Valley();
                break;

            case 5:
                point11Valley();
                break;

        }

    }

    public void point5Valley() {

        int index = 0;
        InputData d = null;
        SortedMap<BigDecimal, BigDecimal> tempList = new TreeMap<BigDecimal, BigDecimal>();
        tempList.clear();

        for (BigDecimal wv : valleyCandidates.keySet()) {

            BigDecimal key = wv;
            BigDecimal value = valleyCandidates.get(wv);

            //find the location index of wv in all points list
            for (int k = 0; k < listSize; k++) {
                if (wv.doubleValue() == smoothedPointList.get(k).getWavenumber().doubleValue()) {
                    d = smoothedPointList.get(k);
                    index = smoothedPointList.indexOf(d);

                }
            }

            if (index < listSize - 2 && index > 1) {
                //calculate next level neighbors slope
                double x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0;
                double y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0;
                BigDecimal x_val = null, y_val = null;
                double d1 = 0, d2 = 0;//neighbours level 1
                double n1 = 0, n2 = 0;//neighbours level 2

                x1 = smoothedPointList.get(index - 2).getWavenumber().doubleValue();
                x2 = smoothedPointList.get(index - 1).getWavenumber().doubleValue();
                x3 = smoothedPointList.get(index).getWavenumber().doubleValue();
                x4 = smoothedPointList.get(index + 1).getWavenumber().doubleValue();
                x5 = smoothedPointList.get(index + 2).getWavenumber().doubleValue();
                x_val = smoothedPointList.get(index).getWavenumber();

                y1 = smoothedPointList.get(index - 2).getTransmittance().doubleValue();
                y2 = smoothedPointList.get(index - 1).getTransmittance().doubleValue();
                y3 = smoothedPointList.get(index).getTransmittance().doubleValue();
                y4 = smoothedPointList.get(index + 1).getTransmittance().doubleValue();
                y5 = smoothedPointList.get(index + 2).getTransmittance().doubleValue();
                y_val = smoothedPointList.get(index).getTransmittance();

                d1 = (y3 - y2) / (x3 - x2);
                d2 = (y4 - y3) / (x4 - x3);

                n1 = (y2 - y1) / (x2 - x1);
                n2 = (y5 - y4) / (x5 - x4);

                if (d1 < 0 && d2 > 0 && n1 < 0 && n2 > 0) {
                    tempList.put(x_val, y_val);
                }
            }

        }
        valleyCandidates = tempList;
        System.out.println(valleyCandidates.size());

    }

    public void point7Valley() {

        int index = 0;
        InputData d = null;
        SortedMap<BigDecimal, BigDecimal> tempList = new TreeMap<BigDecimal, BigDecimal>();
        tempList.clear();

        for (BigDecimal wv : valleyCandidates.keySet()) {

            BigDecimal key = wv;
            BigDecimal value = valleyCandidates.get(wv);

            //find the location index of wv in all points list
            for (int k = 0; k < listSize; k++) {
                if (wv.doubleValue() == smoothedPointList.get(k).getWavenumber().doubleValue()) {
                    d = smoothedPointList.get(k);
                    index = smoothedPointList.indexOf(d);

                }
            }

            if (index < listSize - 3 && index > 2) {
                //calculate next level neighbors slope
                double x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0;
                double y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0, y6 = 0, y7 = 0;
                BigDecimal x_val = null, y_val = null;
                double d1 = 0, d2 = 0;//neighbours level 1
                double n1 = 0, n2 = 0;//neighbours level 2
                double m1 = 0, m2 = 0;//neighbours level 3

                x1 = smoothedPointList.get(index - 3).getWavenumber().doubleValue();
                x2 = smoothedPointList.get(index - 1).getWavenumber().doubleValue();
                x3 = smoothedPointList.get(index - 1).getWavenumber().doubleValue();
                x4 = smoothedPointList.get(index).getWavenumber().doubleValue();
                x5 = smoothedPointList.get(index + 1).getWavenumber().doubleValue();
                x6 = smoothedPointList.get(index + 2).getWavenumber().doubleValue();
                x7 = smoothedPointList.get(index + 3).getWavenumber().doubleValue();
                x_val = smoothedPointList.get(index).getWavenumber();

                y1 = smoothedPointList.get(index - 3).getTransmittance().doubleValue();
                y2 = smoothedPointList.get(index - 2).getTransmittance().doubleValue();
                y3 = smoothedPointList.get(index - 1).getTransmittance().doubleValue();
                y4 = smoothedPointList.get(index).getTransmittance().doubleValue();
                y5 = smoothedPointList.get(index + 1).getTransmittance().doubleValue();
                y6 = smoothedPointList.get(index + 2).getTransmittance().doubleValue();
                y7 = smoothedPointList.get(index + 3).getTransmittance().doubleValue();
                y_val = smoothedPointList.get(index).getTransmittance();

                d1 = (y4 - y3) / (x4 - x3);
                d2 = (y5 - y4) / (x5 - x4);

                n1 = (y3 - y2) / (x3 - x2);
                n2 = (y6 - y5) / (x6 - x5);

                m1 = (y2 - y1) / (x2 - x1);
                m2 = (y7 - y6) / (x7 - x6);

                if (d1 < 0 && d2 > 0 && n1 < 0 && n2 > 0 && m1 < 0 && m2 > 0) {
                    tempList.put(x_val, y_val);
                }
            }

        }
        valleyCandidates = tempList;
        System.out.println(valleyCandidates.size());

    }

    public void point9Valley() {
        int index = 0;
        InputData d = null;
        SortedMap<BigDecimal, BigDecimal> tempList = new TreeMap<BigDecimal, BigDecimal>();
        tempList.clear();

        for (BigDecimal wv : valleyCandidates.keySet()) {

            BigDecimal key = wv;
            BigDecimal value = valleyCandidates.get(wv);

            //find the location index of wv in all points list
            for (int k = 0; k < listSize; k++) {
                if (wv.doubleValue() == smoothedPointList.get(k).getWavenumber().doubleValue()) {
                    d = smoothedPointList.get(k);
                    index = smoothedPointList.indexOf(d);

                }
            }

            if (index < listSize - 4 && index > 3) {
                //calculate next level neighbors slope
                double x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0;
                double y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0, y6 = 0, y7 = 0, y8 = 0, y9 = 0;
                BigDecimal x_val = null, y_val = null;
                double d1 = 0, d2 = 0;//neighbours level 1
                double n1 = 0, n2 = 0;//neighbours level 2
                double m1 = 0, m2 = 0;//neighbours level 3
                double t1 = 0, t2 = 0;//neighbours level 3

                x1 = smoothedPointList.get(index - 4).getWavenumber().doubleValue();
                x2 = smoothedPointList.get(index - 3).getWavenumber().doubleValue();
                x3 = smoothedPointList.get(index - 2).getWavenumber().doubleValue();
                x4 = smoothedPointList.get(index - 1).getWavenumber().doubleValue();
                x5 = smoothedPointList.get(index).getWavenumber().doubleValue();
                x6 = smoothedPointList.get(index + 1).getWavenumber().doubleValue();
                x7 = smoothedPointList.get(index + 2).getWavenumber().doubleValue();
                x8 = smoothedPointList.get(index + 3).getWavenumber().doubleValue();
                x9 = smoothedPointList.get(index + 4).getWavenumber().doubleValue();
                x_val = smoothedPointList.get(index).getWavenumber();

                y1 = smoothedPointList.get(index - 4).getTransmittance().doubleValue();
                y2 = smoothedPointList.get(index - 3).getTransmittance().doubleValue();
                y3 = smoothedPointList.get(index - 2).getTransmittance().doubleValue();
                y4 = smoothedPointList.get(index - 1).getTransmittance().doubleValue();
                y5 = smoothedPointList.get(index).getTransmittance().doubleValue();
                y6 = smoothedPointList.get(index + 1).getTransmittance().doubleValue();
                y7 = smoothedPointList.get(index + 2).getTransmittance().doubleValue();
                y8 = smoothedPointList.get(index + 3).getTransmittance().doubleValue();
                y9 = smoothedPointList.get(index + 4).getTransmittance().doubleValue();
                y_val = smoothedPointList.get(index).getTransmittance();

                d1 = (y5 - y4) / (x5 - x4);
                d2 = (y6 - y5) / (x6 - x5);

                n1 = (y4 - y3) / (x4 - x3);
                n2 = (y7 - y6) / (x7 - x6);

                m1 = (y3 - y2) / (x3 - x2);
                m2 = (y8 - y7) / (x8 - x7);

                t1 = (y2 - y1) / (x2 - x1);
                t2 = (y9 - y8) / (x9 - x8);

                if (d1 < 0 && d2 > 0 && n1 < 0 && n2 > 0 && m1 < 0 && m2 > 0 && t1 < 0 && t2 > 0) {
                    tempList.put(x_val, y_val);
                }
            }

        }
        valleyCandidates = tempList;
        System.out.println(valleyCandidates.size());

    }

    public void point11Valley() {
        int index = 0;
        InputData d = null;
        SortedMap<BigDecimal, BigDecimal> tempList = new TreeMap<BigDecimal, BigDecimal>();
        tempList.clear();

        for (BigDecimal wv : valleyCandidates.keySet()) {

            BigDecimal key = wv;
            BigDecimal value = valleyCandidates.get(wv);

            //find the location index of wv in all points list
            for (int k = 0; k < listSize; k++) {
                if (wv.doubleValue() == smoothedPointList.get(k).getWavenumber().doubleValue()) {
                    d = smoothedPointList.get(k);
                    index = smoothedPointList.indexOf(d);

                }
            }

            if (index < listSize - 5 && index > 4) {
                //calculate next level neighbors slope
                double x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 = 0, x11 = 0;
                double y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0, y6 = 0, y7 = 0, y8 = 0, y9 = 0, y10 = 0, y11 = 0;
                BigDecimal x_val = null, y_val = null;
                double d1 = 0, d2 = 0;//neighbours level 1
                double n1 = 0, n2 = 0;//neighbours level 2
                double m1 = 0, m2 = 0;//neighbours level 3
                double t1 = 0, t2 = 0;//neighbours level 4
                double s1 = 0, s2 = 0;//neighbours level 4

                x1 = smoothedPointList.get(index - 5).getWavenumber().doubleValue();
                x2 = smoothedPointList.get(index - 4).getWavenumber().doubleValue();
                x3 = smoothedPointList.get(index - 3).getWavenumber().doubleValue();
                x4 = smoothedPointList.get(index - 2).getWavenumber().doubleValue();
                x5 = smoothedPointList.get(index - 1).getWavenumber().doubleValue();
                x6 = smoothedPointList.get(index).getWavenumber().doubleValue();
                x7 = smoothedPointList.get(index + 1).getWavenumber().doubleValue();
                x8 = smoothedPointList.get(index + 2).getWavenumber().doubleValue();
                x9 = smoothedPointList.get(index + 3).getWavenumber().doubleValue();
                x10 = smoothedPointList.get(index + 4).getWavenumber().doubleValue();
                x11 = smoothedPointList.get(index + 5).getWavenumber().doubleValue();
                x_val = smoothedPointList.get(index).getWavenumber();

                y1 = smoothedPointList.get(index - 5).getTransmittance().doubleValue();
                y2 = smoothedPointList.get(index - 4).getTransmittance().doubleValue();
                y3 = smoothedPointList.get(index - 3).getTransmittance().doubleValue();
                y4 = smoothedPointList.get(index - 2).getTransmittance().doubleValue();
                y5 = smoothedPointList.get(index - 1).getTransmittance().doubleValue();
                y6 = smoothedPointList.get(index).getTransmittance().doubleValue();
                y7 = smoothedPointList.get(index + 1).getTransmittance().doubleValue();
                y8 = smoothedPointList.get(index + 2).getTransmittance().doubleValue();
                y9 = smoothedPointList.get(index + 3).getTransmittance().doubleValue();
                y10 = smoothedPointList.get(index + 4).getTransmittance().doubleValue();
                y11 = smoothedPointList.get(index + 5).getTransmittance().doubleValue();
                y_val = smoothedPointList.get(index).getTransmittance();

                d1 = (y6 - y5) / (x6 - x5);
                d2 = (y7 - y6) / (x7 - x6);

                n1 = (y5 - y4) / (x5 - x4);
                n2 = (y8 - y7) / (x8 - x7);

                m1 = (y4 - y3) / (x4 - x3);
                m2 = (y9 - y8) / (x9 - x8);

                t1 = (y3 - y2) / (x3 - x2);
                t2 = (y10 - y9) / (x10 - x9);

                s1 = (y2 - y1) / (x2 - x1);
                s2 = (y11 - y10) / (x11 - x10);

                if (d1 < 0 && d2 > 0 && n1 < 0 && n2 > 0 && m1 < 0 && m2 > 0 && t1 < 0 && t2 > 0 && s1 < 0 && s2 > 0) {
                    tempList.put(x_val, y_val);
                }
            }

        }
        valleyCandidates = tempList;
        System.out.println(valleyCandidates.size());

    }

    private XYDataset createDataset(SortedMap<BigDecimal, BigDecimal> pointList) {
        final XYSeries three_consecutive_points = new XYSeries("3 Consecutive Points");

        for (BigDecimal wavelength : pointList.keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = pointList.get(wavelength);
            three_consecutive_points.add(key, value);

        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(three_consecutive_points);
        return dataset;
    }

    public void calRegressionPolynomial(XYDataset inputData) {

        double regressionParameters[] = Regression.getPolynomialRegression(inputData, 0, 2);

        a0 = regressionParameters[0];
        a1 = regressionParameters[1];
        a2 = regressionParameters[2];

        //print equation
//        System.out.println("y = " + a2 + "*x^2+ " + a1 + "*x + " + a0);
    }

    public NavigableMap<BigDecimal, BigDecimal> peakTopSet() {

        for (Entry<BigDecimal, BigDecimal> entry : firstOrderDerivatives.entrySet()) {

            BigDecimal key = entry.getKey(); //current key
            Entry<BigDecimal, BigDecimal> next = firstOrderDerivatives.higherEntry(key); //next
            Entry<BigDecimal, BigDecimal> prev = firstOrderDerivatives.lowerEntry(key);  // previous

            if ((next != null && prev != null)) {

                BigDecimal currentV = entry.getValue();
                BigDecimal nextV = next.getValue();
                BigDecimal prevV = prev.getValue();

                if (nextV.doubleValue() < 0 && prevV.doubleValue() > 0 && currentV.doubleValue() < 0) {
                    peaktops.put(key, allPoints.get(key));
                }

            }

        }
        System.out.println("Candidate size " + peaktops.size());
        //print all
//        for (BigDecimal name : candidates.keySet()) {
//
//            String key = name.toString();
//            String value = candidates.get(name).toString();
//            System.out.println("new list " + key + " " + value);
//        }
        return peaktops;
    }

    private XYDataset createDataset(NavigableMap<BigDecimal, BigDecimal> pointList) {
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

    public void cal_d_w_lenghts() {

        System.out.println("Candidate point list size ===== " + candidates.size());
        //1. get m,c of baseline equation
        double a = MainWindow.getM();
        double b = -1;
        double c = MainWindow.getC();
        double x1 = 0, x2 = 0;
        BigDecimal X = null;
        double d = 0, w = 0;
        String type = "";
        //4.query baseline corrected data from the baseline_data table
        String sql = "select * from baseline_data";
        ResultSet rs = null;
        PreparedStatement pst = null;
        bl_points.clear();

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            InputData bl;
            while (rs.next()) {
                bl = new InputData(rs.getInt("ID"), rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));
                bl_points.put(rs.getBigDecimal("WAVENUMBER"), rs.getBigDecimal("TRANSMITTANCE"));

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

//            Map<String, String> treemap =  new TreeMap<String, String>(Collections.reverseOrder());
        //2. get candidate point list
        for (BigDecimal wavelength : candidates.keySet()) {

            X = wavelength;
            System.out.println("X = " + X);
            double x = wavelength.doubleValue();
            double y = candidates.get(wavelength).doubleValue();

            //3. perpendicular length to point from the line ---> d vertical
            //|ax + by + c| / sqrt(a^2 + b^2)
            d = (a * x + b * y + c) / (Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));
            System.out.println("d------  " + d);
            NavigableMap<BigDecimal, BigDecimal> headmap = bl_points.headMap(X, false).descendingMap();
            NavigableMap<BigDecimal, BigDecimal> tailmap = bl_points.tailMap(X, false);

            //finding cutting peak of left side
            for (BigDecimal wlength : headmap.keySet()) {
                double x0 = wlength.doubleValue();
                double y0 = headmap.get(wlength).doubleValue();
                double l = a * x0 + c;
                double r = y0;
                final double THRESHOLD = 0.000001;

                //Threshold based floating point comparison
                if (Math.abs(l - r) < THRESHOLD) {

//                    System.out.println("x2 ="+x0);
                    x2 = x0;
                    break;

                }

            }
            //finding cutting peak of right side 
            for (BigDecimal wlength : tailmap.keySet()) {
                double x0 = wlength.doubleValue();
                double y0 = tailmap.get(wlength).doubleValue();
                double l = a * x0 + c;
                double r = y0;
                final double THRESHOLD = 0.000001;

                //Threshold based floating point comparison
                if (Math.abs(l - r) < THRESHOLD) {

//                    System.out.println("x1 ="+x0);
                    x1 = x0;
                    break;

                }

            }

            w = Math.abs(x1 - x2);
            System.out.println("w------  " + w);

            if (d*100 > w) {
                type = "sharp";
            } else if (w > d && X.doubleValue() > 3000) {
                type = "broad";
            }
            System.err.println("Type = " +type);
            //empty table
            String sql1 = "INSERT INTO `band`(`WAVENUMBER`, `D`, `W`, `TYPE`) VALUES (?,?,?,?)";
            ResultSet rs1 = null;
            PreparedStatement pst1 = null;

            try {
                pst1 = conn.prepareStatement(sql1);
                pst1.setBigDecimal(1, X);
                pst1.setBigDecimal(2, BigDecimal.valueOf(d));
                pst1.setBigDecimal(3, BigDecimal.valueOf(w));
                pst1.setString(4, type);
                pst1.execute();

            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    pst1.close();
                } catch (Exception e) {

                }
            }

        }

    }

}
