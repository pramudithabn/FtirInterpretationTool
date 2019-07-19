/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.result;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author Pramuditha Buddhini
 */
public class ThisMouseListener implements ChartMouseListener {

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {

        System.out.println("Click event!");
        XYPlot xyPlot2 = event.getChart().getXYPlot();
        // Problem: the coordinates displayed are the one of the previously selected point !
        System.out.println(xyPlot2.getDomainCrosshairValue() + " "
                + xyPlot2.getRangeCrosshairValue());
        
        
        
        
        
//        int mouseX = event.getTrigger().getX();
//        int mouseY = event.getTrigger().getY();
//        System.out.println("x = " + mouseX + ", y = " + mouseY);
//        Point2D p = chartPanel.translateScreenToJava2D(new Point(mouseX, mouseY));
//        XYPlot plot = (XYPlot) chart.getPlot();
//        Rectangle2D plotArea = chartPanel.getScreenDataArea();
//        ValueAxis domainAxis = plot.getDomainAxis();
//        RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
//        ValueAxis rangeAxis = plot.getRangeAxis();
//        RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
//        double chartX = domainAxis.java2DToValue(p.getX(), plotArea,domainAxisEdge);
//        double chartY = rangeAxis.java2DToValue(p.getY(), plotArea,rangeAxisEdge);
//        System.out.println("Chart: x = " + chartX + ", y = " + chartY);
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent arg0) {
        // TODO Auto-generated method stub

    }
}
