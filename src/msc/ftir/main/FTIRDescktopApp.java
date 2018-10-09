package msc.ftir.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import net.proteanit.sql.DbUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JFileChooser;
import java.sql.*;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import java.util.*;
import java.util.regex.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.jdbc.JDBCXYDataset;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Pramuditha Buddhini
 */
public class FTIRDescktopApp extends javax.swing.JFrame {

    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    private String fileName;
    ArrayList<Integer> errorLine = new ArrayList<>();
    boolean dataformatvalidity;
    private Object[][] dataArray = new Object[1000][2];

    /**
     * Creates new form HelloWorld
     */
    public FTIRDescktopApp() {

        initComponents();
        
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        setSize(d.width, d.height-30);
        
        setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH );
        JFrame.setDefaultLookAndFeelDecorated(true);

        conn = Javaconnect.ConnecrDb();
        update_table();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        fileBrowserButton = new javax.swing.JButton();
        uploadButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        button_specgen = new javax.swing.JButton();
        specPanel = new javax.swing.JPanel();
        rsPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        dataTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(dataTable);

        fileBrowserButton.setText("...");
        fileBrowserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileBrowserButtonActionPerformed(evt);
            }
        });

        uploadButton.setText("Upload");
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Data");

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        jButton1.setText("Peak");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        button_specgen.setText("View Spectrum");
        button_specgen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_specgenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(uploadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(fileBrowserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addGap(320, 320, 320)
                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(button_specgen, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))))))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileBrowserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uploadButton)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(clearButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_specgen)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );

        specPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout specPanelLayout = new javax.swing.GroupLayout(specPanel);
        specPanel.setLayout(specPanelLayout);
        specPanelLayout.setHorizontalGroup(
            specPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 696, Short.MAX_VALUE)
        );
        specPanelLayout.setVerticalGroup(
            specPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 391, Short.MAX_VALUE)
        );

        rsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout rsPanelLayout = new javax.swing.GroupLayout(rsPanel);
        rsPanel.setLayout(rsPanelLayout);
        rsPanelLayout.setHorizontalGroup(
            rsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        rsPanelLayout.setVerticalGroup(
            rsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("New");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Open File");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Save");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Print");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Display");
        jMenuBar1.add(jMenu3);

        jMenu4.setText("Options");
        jMenuBar1.add(jMenu4);

        jMenu5.setText("Tools");
        jMenuBar1.add(jMenu5);

        jMenu6.setText("Help");
        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(specPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(specPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileBrowserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileBrowserButtonActionPerformed

        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File dataFile = chooser.getSelectedFile();
        fileName = dataFile.getAbsolutePath();
        jTextField1.setText(fileName);


    }//GEN-LAST:event_fileBrowserButtonActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadButtonActionPerformed
        if (fileName == null) {
            JOptionPane.showMessageDialog(null, "Please select a file!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (validateFileType()) {

            vaidateDataFormat();

            if (dataformatvalidity) {

                upload();

            } else {

                int p = JOptionPane.showConfirmDialog(null, "Do you want to skip lines and proceed?", "Confirm ", JOptionPane.YES_NO_OPTION);

                if (p == JOptionPane.YES_OPTION) {
                    upload();
                } else {

                    System.out.println("Upload canceled");

                }

            }

            update_table();

        } else {

            JOptionPane.showMessageDialog(null, "Invalid file format!", "Error", JOptionPane.ERROR_MESSAGE);

        }

    }//GEN-LAST:event_uploadButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
//        vaidateDataFormat();
//        validateFileType();
//        arrayFill();
        calDerivatives();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        int p = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear data?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);

        if (p == 0) {
            String sql = "delete from input_data";
            try {
                pst = conn.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(null, "Delete Successful!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            update_table();
        }
    }//GEN-LAST:event_clearButtonActionPerformed

    private void button_specgenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_specgenActionPerformed

        generate_spectrum();
//            createSpectrum();


    }//GEN-LAST:event_button_specgenActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FTIRDescktopApp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton button_specgen;
    private javax.swing.JButton clearButton;
    public javax.swing.JTable dataTable;
    private javax.swing.JButton fileBrowserButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel rsPanel;
    private javax.swing.JPanel specPanel;
    private javax.swing.JButton uploadButton;
    // End of variables declaration//GEN-END:variables

    private void update_table() {

        try {
            String sql = "select WAVENUMBER , TRANSMITTANCE from input_data";

            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            dataTable.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }

    private void generate_spectrum() {
        try {

            String query1 = "select WAVENUMBER, TRANSMITTANCE from input_data";
            JDBCXYDataset dataset = new JDBCXYDataset(Javaconnect.ConnecrDb(), query1);
            
            JFreeChart spec = ChartFactory.createXYLineChart("FTIR Spectrum", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);
    
            spec.setBorderVisible(false);

            spec.getXYPlot().setDomainGridlinesVisible(false);
            
            
           
            ChartPanel chartPanel = new ChartPanel(spec);
            System.out.println(chartPanel.getPreferredSize());
            chartPanel.setPreferredSize(new Dimension(700,500));
            chartPanel.setDomainZoomable(true);

            BarRenderer renderer = null;
            XYPlot plot = spec.getXYPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setAutoRange(true);
            renderer = new BarRenderer();
            //old
//            JFrame frame = new JFrame();
//            frame.add(chartPanel);
//            frame.setVisible(true);
//            frame.setSize(1000, 600);
//            frame.pack();
//            chartPanel.setPreferredSize(new Dimension(1000,600));

            specPanel.setLayout(new java.awt.BorderLayout());
            specPanel.add(chartPanel, BorderLayout.CENTER);
            specPanel.validate();
            specPanel.setPreferredSize(new Dimension(700,500));
            specPanel.setVisible(true);
           

            NumberAxis domain = (NumberAxis) plot.getDomainAxis();
            domain.setAutoRange(true);
            domain.setInverted(true);

            System.out.print("Yes");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void fileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File dataFile = chooser.getSelectedFile();
        fileName = dataFile.getAbsolutePath();
        jTextField1.setText(fileName);

    }

    private void vaidateDataFormat() {
        //##YUNITS=%T starting line
        //397.336096 9.683705 start point
        //3842.201472 0.566372 end point

        Matcher regrexMatch = null;
//        String point = "\\d{3,4}\\.\\d{6}\\s\\d{1,2}\\.\\d{6}\\s"; //[0-9]{3,4}\\.[0-9]{6}\\s[0-9]{1,2}\\.[0-9]{6}
//       String point = "\\d{3,4}\\.\\d{5,6}[ \\t]\\d{1,2}\\.\\d{5,6}[ \\t]*"; //for DPT,txt as well
        String point = "\\d{3,4}\\.\\d{5,6}(\\,|[ \\t])\\d{1,2}\\.\\d{5,6}(\\,|[ \\t]*)"; //working for DPT,txt,cvs

        int invalid_input = -1; //skip the first line ##YUNITS=%T
        int valid_input = 0;
        int lineNumber = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {

                Pattern input_pattern = Pattern.compile(point);
                regrexMatch = input_pattern.matcher(line);
//                System.out.println("aaa" + line + "aaa");
                System.out.println(line);
                boolean m = regrexMatch.matches();
                System.out.println(m);

                if (!regrexMatch.matches()) {
                    ++invalid_input;
                    errorLine.add(lineNumber + 1);

                } else {
                    ++valid_input;

                }

                ++lineNumber;

            }
            br.close();

            System.out.println(invalid_input + " invalid inputs found at line #" + Arrays.toString(errorLine.toArray()));
            System.out.println("valid inputs = " + valid_input);
            System.out.println("Total Number of lines = " + lineNumber);

            if (invalid_input > 0) {

                String msg = "Data format errors are found at line #" + Arrays.toString(errorLine.toArray());

                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessage(msg);
                optionPane.setMessageType(JOptionPane.WARNING_MESSAGE);
                JDialog dialog = optionPane.createDialog(null, "Data format error!");
                dialog.setVisible(true);
                dataformatvalidity = false;

            } else {
                System.out.println("Data format is correct!");
                dataformatvalidity = true;

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private boolean validateFileType() {

        String filetype = "(?:[\\w]\\:|\\\\)(\\\\[a-zA-Z_\\-\\s0-9\\.]+)+\\.(txt|csv|dpt|doc|docx|xls|xlsx)";
        Pattern fileExtPattern = Pattern.compile(filetype);

        Matcher mtch = fileExtPattern.matcher(fileName);
        if (mtch.matches()) {

            System.out.println(fileName);
            System.out.println(mtch.matches());
            return true;

        } else {
            return false;
        }
    }

    private void upload() {

        try {

            System.out.println(fileName);
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            String[] value = null;

            while ((line = br.readLine()) != null) {

                //Make sure the line is not null, not empty, and contains valid data format
                if (!line.equals("") && line.matches("\\d{3,4}\\.\\d{5,6}(\\,|[ \\t])\\d{1,2}\\.\\d{5,6}(\\,|[ \\t]*)")) {

                    String cvsfiletype = "(?:[\\w]\\:|\\\\)(\\\\[a-zA-Z_\\-\\s0-9\\.]+)+\\.(csv)";
                    String txtfiletype = "(?:[\\w]\\:|\\\\)(\\\\[a-zA-Z_\\-\\s0-9\\.]+)+\\.(txt)";
                    String xlsfiletype = "(?:[\\w]\\:|\\\\)(\\\\[a-zA-Z_\\-\\s0-9\\.]+)+\\.(xls|xlsx)";
                    String docfiletype = "(?:[\\w]\\:|\\\\)(\\\\[a-zA-Z_\\-\\s0-9\\.]+)+\\.(doc|docx)";
                    String dptfiletype = "(?:[\\w]\\:|\\\\)(\\\\[a-zA-Z_\\-\\s0-9\\.]+)+\\.(dpt)";

                    Pattern fileExtPatternCVS = Pattern.compile(cvsfiletype);
                    Pattern fileExtPatternTXT = Pattern.compile(txtfiletype);
                    Pattern fileExtPatternXLS = Pattern.compile(xlsfiletype);
                    Pattern fileExtPatternDOC = Pattern.compile(docfiletype);
                    Pattern fileExtPatternDPT = Pattern.compile(dptfiletype);

                    Matcher mtch1 = fileExtPatternCVS.matcher(fileName);
                    Matcher mtch2 = fileExtPatternTXT.matcher(fileName);
                    Matcher mtch3 = fileExtPatternXLS.matcher(fileName);
                    Matcher mtch4 = fileExtPatternDOC.matcher(fileName);
                    Matcher mtch5 = fileExtPatternDPT.matcher(fileName);

                    //if the file is CVS
                    if (mtch1.matches()) {
                        value = line.split(","); //if the file is CVS
                    } //if the file is text
                    else if (mtch2.matches()) {
                        value = line.split("\\s+"); //whitespace regex TXT
                    } else if (mtch5.matches()) {
                        value = line.split("\\s+"); //whitespace regex DPT
                    }

                    String sql = "insert into input_data (WAVENUMBER , TRANSMITTANCE)" + "values ('" + value[0] + "','" + value[1] + "')";
                    pst = conn.prepareStatement(sql);
                    pst.executeUpdate();
                }
            }
            br.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void arrayFill() {

        int row = dataTable.getRowCount();
        int col = dataTable.getColumnCount();

        for (int i = 0; i < dataTable.getRowCount(); i++) {

            for (int j = 0; j < dataTable.getColumnCount(); j++) {//y = f(x) values

                dataArray[i][j] = dataTable.getModel().getValueAt(i, j);
//                System.out.println(dataTable.getValueAt(i, j));
//                System.out.println(dataArray[i][j]);

                System.out.println(dataArray.getClass().toString());

            }

        }

    }

    public void calDerivatives() {
        long[] dArray = new long[dataTable.getRowCount()];
//        double[] arrDouble = new double[stringArray.length];
//
//        for(int i=0; i<stringArray.length; i++)
//        {
////            arrDouble[i] = convert.ToDouble(stringArray[i]);
//        }
        for (int i = 1; i < dataTable.getRowCount(); i++) {

            if (i == dataTable.getRowCount()) {
                break;
            }

            double derivative;

            double y1 = (double) dataTable.getModel().getValueAt(i + 1, 1);
            double y2 = (double) dataTable.getModel().getValueAt(i - 1, 1);
            double x1 = (double) dataTable.getModel().getValueAt(i + 1, 0);
            double x2 = (double) dataTable.getModel().getValueAt(i - 1, 0);

            derivative = ((y1 - y2) / (x1 - x2)) * 10000;

//            DecimalFormat df = new DecimalFormat("#.####");
//
//            String d = df.format(derivative);
//            
            long d = Math.round(derivative);

            System.out.println("At x = " + dataTable.getModel().getValueAt(i, 0) + " f'(x) = " + d);

            dArray[i] = d;

        }

        for (int k = 0; k < dArray.length; k++) {
            System.out.print(dArray[k] + ",");
        }

//        ArrayList a = new ArrayList();
//        a = locateDownwardSpickes(dArray);
//
//        for (int k = 0; k < a.size(); k++) {
//            System.out.println(a.get(k));
//        }
    }

    public ArrayList locateDownwardSpickes(long arr[]) {

        ArrayList spikeindex = new ArrayList();
        for (int i = 0; i < arr.length; i++) {

            if (arr[i] == 0 && arr[i - 1] < 0 && arr[i + 1] > 0) {
                spikeindex.add(i);
            }

        }
        return spikeindex;
    }
}