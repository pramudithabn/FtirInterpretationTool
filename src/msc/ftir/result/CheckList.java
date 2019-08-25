/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.result;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import msc.ftir.main.Javaconnect;
import msc.ftir.main.MainWindow;
import net.proteanit.sql.DbUtils;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author Pramuditha Buddhini
 */
public class CheckList extends javax.swing.JFrame {

    private static Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pst = null;
    private static Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    private XYPlot xyplotT = null;
    private LabeledXYDataset ds = null;
    private XYSeriesCollection dataSet0 = (XYSeriesCollection) MainWindow.getPeakset();
    private XYSeries series0 = dataSet0.getSeries(0);

    /**
     * Creates new form CheckList
     */
    public CheckList() {
        initComponents();
        setLocationRelativeTo(null);
        conn = Javaconnect.ConnecrDb();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            }
        });
        
        ds = MainWindow.getLabeled_dataset();

        
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultListTable = new javax.swing.JTable();
        fixButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resultListTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        resultListTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultListTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(resultListTable);

        fixButton.setText("Fix");
        fixButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fixButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fixButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fixButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixButtonActionPerformed

        int r = resultListTable.getSelectedRow();

        String data1 = (String) resultListTable.getValueAt(r, 1);
        String data2 = (String) resultListTable.getValueAt(r, 2);
        String data3 = (String) resultListTable.getValueAt(r, 3);
        int data4 = (int) resultListTable.getValueAt(r, 4);

        //display labeled point on spectrum
        try {

//            String sql = "select * from library where BOND_VIBMODE = \"" + data2.trim() + "\" AND FUNCTIONAL_GROUP = \"" + data3.trim() + "\" ";
//            String sql = "SELECT BOND_VIBMODE, FUNCTIONAL_GROUP, COMPOUND_TYPE, COMPOUND_CATEGORY   from library where ID = " + data4;
            String sql = "SELECT round(result.wavenumber) as 'Wavenumber', library.BOND_VIBMODE As 'Vib. Mode/ Bond', library.FUNCTIONAL_GROUP As 'Functional Group', library.COMPOUND_TYPE As 'Compound Type', library.COMPOUND_CATEGORY As 'Compound Category'  from library, result where library.ID = " + data4 + " and result.lib_index = " + data4 + " and library.ID = result.lib_index  LIMIT 1";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            double x1 = 0;
            String s = null;
            while (rs.next()) {
                x1 = rs.getDouble("Wavenumber");
                s = rs.getString("Vib. Mode/ Bond");
            }

            for (Object i : series0.getItems()) {
                XYDataItem item = (XYDataItem) i;
                double x = item.getXValue();
                double y = item.getYValue();

                if (Math.abs(x - x1) < 1) {
//                    System.out.println(x + "," + y + "," + s);

                    ds.update(x, s);

                }
//                else {

//                    System.out.println(x + "," + y + "," + s);
//                }
            }

//            int a = ds.getItemCount(0);
//
//            System.out.println("-------------------------------");
//            for (int k = 0; k < a; k++) {
//
//                System.out.println(ds.getXValue(0, k) + "," + ds.getYValue(0, k) + "," + ds.getLabel(0, k));
//            }
//            System.out.println("-------------------------------");

            createReportSpectrum(ds, createBaselineDataset(), MainWindow.comPanel);

//            JFrame f = new JFrame();
//            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            JFreeChart chart = createChart(ds);
//            ChartPanel chartPanel = new ChartPanel(chart) {
//
//                @Override
//                public Dimension getPreferredSize() {
//                    return new Dimension(400, 320);
//                }
//            };
//            f.add(chartPanel);
//            f.pack();
//            f.setLocationRelativeTo(null);
//            f.setVisible(true);
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        //set selected row to the table
        try {

            String sql = "SELECT round(result.wavenumber) as 'Wavenumber', library.BOND_VIBMODE As 'Vib. Mode/ Bond', library.FUNCTIONAL_GROUP As 'Functional Group', library.COMPOUND_TYPE As 'Compound Type', library.COMPOUND_CATEGORY As 'Compound Category'  from library, result where library.ID = " + data4 + " and result.lib_index = " + data4 + " and library.ID = result.lib_index  LIMIT 1";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            MainWindow.printTable.setModel(TableModel(rs));

        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        this.dispose();


    }//GEN-LAST:event_fixButtonActionPerformed

    private void resultListTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultListTableMouseClicked
        //to select and unselect rows
        {
            int row = resultListTable.getSelectedRow();
            boolean val = (boolean) resultListTable.getValueAt(row, 5);

            if (val == true) {
                resultListTable.setValueAt(false, row, 5);
            } else if (val == false) {
                resultListTable.setValueAt(true, row, 5);
            }
        }


    }//GEN-LAST:event_resultListTableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CheckList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CheckList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CheckList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CheckList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CheckList().setVisible(true);
            }
        });
    }

    public static DefaultTableModel TableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

//    private static JFreeChart createChart(final XYDataset dataset) {
//        NumberAxis domain = new NumberAxis("Unit");
//        NumberAxis range = new NumberAxis("Price");
//
//        domain.setAutoRangeIncludesZero(false);
//        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
//        renderer.setBaseItemLabelGenerator(new LabelGenerator());
//        renderer.setBaseItemLabelPaint(Color.green.darker());
//        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
//        renderer.setBaseItemLabelFont(renderer.getBaseItemLabelFont().deriveFont(14f));
//        renderer.setBaseItemLabelsVisible(true);
//        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
//        XYPlot plot = new XYPlot(dataset, domain, range, renderer);
//        JFreeChart chart = new JFreeChart("Unit Price", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
//        return chart;
//    }

    public void createReportSpectrum(XYDataset set1, XYDataset set2, JPanel panel) {

        panel.removeAll();
        panel.revalidate();
        panel.repaint();

        XYPlot plot = new XYPlot();

        XYDataset collection1 = set1;
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(false, true);	// Shapes only
        ValueAxis domain1 = new NumberAxis("Wavenumber (cm-1)");
        ValueAxis range1 = new NumberAxis("Transmittance %");
        domain1.setAutoRange(true);
        domain1.setInverted(true);
        range1.setAutoRange(true);
        renderer1.setBaseItemLabelGenerator(new LabelGenerator());
        renderer1.setBaseItemLabelPaint(Color.black.darker());
//        renderer1.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer1.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.BASELINE_RIGHT, TextAnchor.BASELINE_RIGHT, -Math.PI / 2));
        renderer1.setBaseItemLabelFont(renderer1.getBaseItemLabelFont().deriveFont(14f));
        renderer1.setBaseItemLabelsVisible(true);
        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

        plot.setDataset(0, collection1);
        plot.setRenderer(0, renderer1);
        plot.setDomainAxis(0, domain1);
        plot.setRangeAxis(0, range1);
        xyplotT = plot;

        XYDataset collection2 = set2;
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);	// Lines only
        renderer2.setSeriesPaint(0, Color.blue);

        ValueAxis domain2 = new NumberAxis("");
        ValueAxis range2 = new NumberAxis("");

        plot.setDataset(1, collection2);
        plot.setRenderer(1, renderer2);
        plot.setDomainAxis(1, domain2);
        plot.setRangeAxis(1, range2);

        domain2.setAutoRange(true);
        domain2.setInverted(true);
        domain2.setVisible(false);
        range2.setVisible(false);

        JFreeChart duelchart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        ChartPanel chartPanel_com = new ChartPanel(duelchart);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(chartPanel_com, BorderLayout.CENTER);
        panel.validate();
        panel.setPreferredSize(new Dimension(654, 350));
        panel.setVisible(true);

        renderer1.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));

        CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        Crosshair xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair.setLabelVisible(false);
        Crosshair yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair.setLabelVisible(false);
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);

        XYPointerAnnotation pointer = new XYPointerAnnotation("", 0, 0, 7.0 * Math.PI / 4.0);
        pointer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pointer.setTipRadius(3.0);
        pointer.setBaseRadius(15.0);
        pointer.setPaint(Color.blue);
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        pointer.setBackgroundPaint(Color.yellow);

        XYPointerAnnotation pointer2 = new XYPointerAnnotation("", 0, 0, 7.0 * Math.PI / 4.0);
        pointer2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pointer2.setTipRadius(3.0);
        pointer2.setBaseRadius(15.0);
        pointer2.setPaint(Color.blue);
        pointer2.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        pointer2.setBackgroundPaint(new Color(255, 255, 255));

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);

        chartPanel_com.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                ChartEntity entity = event.getEntity();

                if (entity != null && entity instanceof XYItemEntity) {

                    try {
                        XYItemEntity ent = (XYItemEntity) entity;

                        int sindex = ent.getSeriesIndex();
                        int iindex = ent.getItem();

                        double x = set1.getXValue(sindex, iindex);

                        CheckList c = new CheckList();
//                        c.setVisible(true);

                        String sql1 = "SET @row_number=0";
                        PreparedStatement pst1 = conn.prepareStatement(sql1);
                        ResultSet rst = pst1.executeQuery();

                        String sql = "SELECT (@row_number:=@row_number + 1) As 'No.', round(`WAVENUMBER`,0) AS 'Wavenumber', `BOND` AS 'Bond', `FUNCTIONAL_GROUP` AS 'Functional Group',LIB_INDEX AS 'Lib. Index' from result where wavenumber = " + x;
                        pst = conn.prepareStatement(sql);
                        rs = pst.executeQuery();

                        DefaultTableModel model = new DefaultTableModel() {
                            public Class<?> getColumnClass(int column) {
                                switch (column) {

                                    case 0:
                                        return String.class;

                                    case 1:
                                        return String.class;

                                    case 2:
                                        return String.class;

                                    case 3:
                                        return String.class;

                                    case 4:
                                        return String.class;

                                    case 5:
                                        return String.class;

                                    case 6:
                                        return Boolean.class;

                                    default:
                                        return String.class;
                                }

                            }

                            @Override
                            public boolean isCellEditable(int rowIndex, int columnIndex) {
                                if (columnIndex == 5) {
                                    return true;
                                }
                                return false;
                            }

                        };

                        c.resultListTable.setModel(model);
                        model.addColumn("No.");
                        model.addColumn("Wavenumber(cm-1)");
                        model.addColumn("Bond");
                        model.addColumn("Functional Group");
                        model.addColumn("Lib. Index");
                        model.addColumn("Select");

                        int i = 0;

                        if (rs.next() == false) {
                            JOptionPane.showMessageDialog(null, "No results found!");
                        } else {
                            do {
                                model.addRow(new Object[0]);
                                model.setValueAt(rs.getString("No."), i, 0);
                                model.setValueAt(rs.getString("Wavenumber"), i, 1);
                                model.setValueAt(rs.getString("Bond"), i, 2);
                                model.setValueAt(rs.getString("Functional Group"), i, 3);
                                model.setValueAt(rs.getInt("Lib. Index"), i, 4);
                                model.setValueAt(false, i, 5);
                                c.setVisible(true);
                                i++;

                                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                                rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
                                rightRenderer.setForeground(Color.BLUE);

                                DefaultTableCellRenderer redRenderer = new DefaultTableCellRenderer();
                                redRenderer.setHorizontalAlignment(JLabel.RIGHT);
                                redRenderer.setForeground(Color.RED);

                                c.resultListTable.setShowGrid(true);
                                c.resultListTable.setGridColor(Color.LIGHT_GRAY);
                                c.resultListTable.setShowHorizontalLines(false);
                                c.resultListTable.getColumnModel().getColumn(0).setPreferredWidth(20);
                                c.resultListTable.getColumnModel().getColumn(1).setPreferredWidth(40);
                                c.resultListTable.getColumnModel().getColumn(2).setPreferredWidth(230);
                                c.resultListTable.getColumnModel().getColumn(3).setPreferredWidth(140);
                                c.resultListTable.getColumnModel().getColumn(4).setPreferredWidth(0);
                                c.resultListTable.getColumnModel().getColumn(5).setPreferredWidth(50);
                                CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();

                                c.resultListTable.getColumnModel().getColumn(5).setCellRenderer(checkBoxRenderer);
                            } while (rs.next());
                        }

//                        if (rs.next() == false) {
//                            JOptionPane.showMessageDialog(null, "No results found!", "Error!", JOptionPane.WARNING_MESSAGE);
//                        }else{
//                            c.setVisible(true);
//                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {

                xyplotT.removeAnnotation(pointer);
                xyplotT.removeAnnotation(pointer2);

                ChartEntity chartentity = event.getEntity();
                Rectangle2D dataArea = chartPanel_com.getScreenDataArea();
                ValueAxis xAxis = xyplotT.getDomainAxis();
                ValueAxis yAxis = xyplotT.getRangeAxis();

                if (chartentity != null && chartentity instanceof XYItemEntity) {
                    XYItemEntity e = (XYItemEntity) chartentity;
                    try {
                        int i = e.getItem();
                        int s = e.getSeriesIndex();
                        double x = collection1.getXValue(s, i);
                        double y = collection1.getYValue(s, i);

                        if (y > 0) {
                            pointer.setX(x);
                            pointer.setY(y);
                            pointer.setText("X = " + df.format(x) + " , Y = " + df.format(y));

                            xyplotT.addAnnotation(pointer);
                        }
                        xCrosshair.setValue(x);
                        yCrosshair.setValue(y);
                        chartPanel_com.addOverlay(crosshairOverlay);
                        chartPanel_com.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } catch (Exception exp) {
                    }

                }
                if (!(chartentity instanceof XYItemEntity)) {

                    chartPanel_com.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                    double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, xyplotT.getDomainAxisEdge());
                    double y = yAxis.java2DToValue(event.getTrigger().getY(), dataArea, xyplotT.getRangeAxisEdge());

//                    double x = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
//                    double y = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
//                    double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
                    pointer2.setX(x);
                    pointer2.setY(y);
                    pointer2.setText("X = " + df.format(x) + " , Y = " + df.format(y));
                    xyplotT.addAnnotation(pointer2);

                    xCrosshair.setValue(x);
                    yCrosshair.setValue(y);
                    chartPanel_com.addOverlay(crosshairOverlay);

                    if (!(x < 4000 || y > 0)) {

                        xyplotT.removeAnnotation(pointer);
                        xyplotT.removeAnnotation(pointer2);
                        chartPanel_com.removeOverlay(crosshairOverlay);
                    }

                }

            }

        }
        );

        resultListTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(Color.YELLOW);
                    c.setForeground(Color.BLUE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });
    }

    private XYDataset createBaselineDataset() throws SQLException {

        String query1 = "select WAVENUMBER, TRANSMITTANCE AS 'Baseline Corrected Spectrum' from baseline_data";
        XYDataset baseline_dataset = new JDBCXYDataset(conn, query1);
        return baseline_dataset;

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton fixButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable resultListTable;
    // End of variables declaration//GEN-END:variables
}
