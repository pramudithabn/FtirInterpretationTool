package msc.ftir.main;

import msc.ftir.util.Utils;
import msc.ftir.baseline.InterpolatedBL;
import msc.ftir.valleys.ValleysLocator;
import msc.ftir.smooth.*;
import msc.ftir.baseline.RegressionBL;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.proteanit.sql.DbUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import javax.swing.JFileChooser;
import java.sql.*;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import msc.ftir.util.FileType;
import static msc.ftir.util.FileType.XLS;
import static msc.ftir.util.FileType.XLXS;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.util.Properties;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Pramuditha Buddhini
 */
public class MainWindow extends javax.swing.JFrame {

    private static Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pst = null;
    private String fileName;
    private ArrayList<Integer> errorLine = new ArrayList<>();
    private boolean dataformatvalidity;
    public Object[][] dataArray = new Object[1000][2];
    private FileType fileType;
    private int sliderValue;
    private SlidingAvgSmooth ls = null;
    private TriangularSmooth tri = null;
    private DefaultSmooth ds = null;
    private SlidingAvgSmooth_Selection sl = null;
    private TriangularSmooth_Selection ts = null;
    private SavitzkyGolayFilter sgf = null;

    private RegressionBL bc = null;
    public static Boolean newInstance = false;
    private ArrayList<Integer> sliderValuesList = new ArrayList<Integer>();
    private int prev = 0;
    private boolean inputvalidity = true;
    private Properties p = new Properties();
    public static int points = 0;
    public static XYPlot plot = null;
    private double thresh = 0;
    private static int lowerBoundX = 0, upperBoundX = 0;
    public static double lowerBoundT = 0, upperBoundT = 0;

    private JFreeChart spec = null, chart = null, duelchart = null, smoothedSpec = null, smoothed_chart = null;
    private XYDataset input_dataset = null;
    private int sliderPreviousValue, sliderCurrentValue, h_current, h_old;

    private int threshCurrent, threshPrevious = 0; //threshold values by sliders for valley detection
    private int noiseThreshCurrent, noiseThreshPrevious = 0; //threshold values by sliders for valley detection

    private ValleysLocator v1;

    public static int getPoints() {
        return points;
    }

    public static void setPoints(int points) {
        MainWindow.points = points;
    }
    private static int algorithm = 1;

    public static int getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(int algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Creates new form HelloWorld
     */
    public MainWindow() {

        initComponents();

        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        JFrame.setDefaultLookAndFeelDecorated(true);

        conn = Javaconnect.ConnecrDb();
        clearAll();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit?", "Exit",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    dispose();
                } else {
                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
        });
        sliderValuesList.add(0);
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
        pointsbuttonGroup = new javax.swing.ButtonGroup();
        blmethodButtonGroup = new javax.swing.ButtonGroup();
        specPanel = new javax.swing.JPanel();
        jToolBar = new javax.swing.JToolBar();
        button_specgen = new javax.swing.JButton();
        smootheSelection = new javax.swing.JButton();
        peakButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        filePathText = new javax.swing.JTextField();
        openButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        resultsPanel = new javax.swing.JPanel();
        smoothningSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        smAlgoCombo = new javax.swing.JComboBox<>();
        iterateButton = new javax.swing.JButton();
        filterPassLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        val_label1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        threepoints = new javax.swing.JRadioButton();
        fivepoints = new javax.swing.JRadioButton();
        sevenpoints = new javax.swing.JRadioButton();
        val_label2 = new javax.swing.JLabel();
        ninepoints = new javax.swing.JRadioButton();
        resetSmoothButton = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        threshSlider1 = new javax.swing.JSlider();
        jLabel8 = new javax.swing.JLabel();
        noiseSlider = new javax.swing.JSlider();
        jLabel11 = new javax.swing.JLabel();
        h_slider = new javax.swing.JSlider();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        baselineMethodCombo = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        lineCheckBox = new javax.swing.JCheckBox();
        splineCheckBox = new javax.swing.JCheckBox();
        cubicSplineCheckBox = new javax.swing.JCheckBox();
        baselineButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        threshText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        threshold = new javax.swing.JButton();
        thresholdSlider = new javax.swing.JSlider();
        Valleys = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        neighborSlider = new javax.swing.JSlider();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        rsPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        comPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        printMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        displayMenu = new javax.swing.JMenu();
        optionsMenu = new javax.swing.JMenu();
        toolsMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();

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
        setTitle("FTIR Interpreter");

        specPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ORIGINAL SPECTRUM", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout specPanelLayout = new javax.swing.GroupLayout(specPanel);
        specPanel.setLayout(specPanelLayout);
        specPanelLayout.setHorizontalGroup(
            specPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 675, Short.MAX_VALUE)
        );
        specPanelLayout.setVerticalGroup(
            specPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );

        jToolBar.setRollover(true);
        jToolBar.setFocusable(false);

        button_specgen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/Line Chart_20px.png"))); // NOI18N
        button_specgen.setText("Plot");
        button_specgen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_specgenActionPerformed(evt);
            }
        });
        jToolBar.add(button_specgen);

        smootheSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/Plot_20px.png"))); // NOI18N
        smootheSelection.setText("Section Smooth");
        smootheSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smootheSelectionActionPerformed(evt);
            }
        });
        jToolBar.add(smootheSelection);

        peakButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/Sort Up_20px.png"))); // NOI18N
        peakButton.setText("Peak");
        peakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peakButtonActionPerformed(evt);
            }
        });
        jToolBar.add(peakButton);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/Broom_20px.png"))); // NOI18N
        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jToolBar.add(clearButton);

        jButton1.setText("SGF");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar.add(jButton1);

        jButton2.setText("Valley Algo");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar.add(jButton2);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        openButton.setText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filePathText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(openButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filePathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(openButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTabbedPane1.setName(""); // NOI18N

        smoothningSlider.setMajorTickSpacing(10);
        smoothningSlider.setPaintLabels(true);
        smoothningSlider.setPaintTicks(true);
        smoothningSlider.setToolTipText("");
        smoothningSlider.setValue(1);
        smoothningSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                smoothningSliderStateChanged(evt);
            }
        });
        smoothningSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                smoothningSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                smoothningSliderMouseReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Smoothness");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Algorithm");

        smAlgoCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Savitzky-Golay Filter", "Unweighted Sliding Average ", "Triangular Smoothing", " " }));
        smAlgoCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smAlgoComboActionPerformed(evt);
            }
        });

        iterateButton.setText("Iterate");
        iterateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iterateButtonActionPerformed(evt);
            }
        });

        filterPassLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Filter passes ");

        val_label1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        val_label1.setForeground(new java.awt.Color(255, 0, 0));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Filter width");

        pointsbuttonGroup.add(threepoints);
        threepoints.setSelected(true);
        threepoints.setText("3 - points");

        pointsbuttonGroup.add(fivepoints);
        fivepoints.setText("5 - points");
        fivepoints.setEnabled(false);

        pointsbuttonGroup.add(sevenpoints);
        sevenpoints.setText("7 - points");
        sevenpoints.setEnabled(false);

        val_label2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        val_label2.setForeground(new java.awt.Color(255, 0, 0));

        pointsbuttonGroup.add(ninepoints);
        ninepoints.setText("9 - points");
        ninepoints.setEnabled(false);

        resetSmoothButton.setText("Resest");
        resetSmoothButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetSmoothButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultsPanelLayout = new javax.swing.GroupLayout(resultsPanel);
        resultsPanel.setLayout(resultsPanelLayout);
        resultsPanelLayout.setHorizontalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(filterPassLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resetSmoothButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(iterateButton))
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(resultsPanelLayout.createSequentialGroup()
                                .addComponent(smAlgoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(val_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(resultsPanelLayout.createSequentialGroup()
                                .addComponent(threepoints)
                                .addGap(18, 18, 18)
                                .addComponent(fivepoints)
                                .addGap(18, 18, 18)
                                .addComponent(sevenpoints)
                                .addGap(18, 18, 18)
                                .addComponent(ninepoints)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(val_label2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(smoothningSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        resultsPanelLayout.setVerticalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(smAlgoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(val_label1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(threepoints)
                        .addComponent(fivepoints)
                        .addComponent(sevenpoints)
                        .addComponent(ninepoints))
                    .addComponent(val_label2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(smoothningSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)))
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(iterateButton)
                            .addComponent(resetSmoothButton)))
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(filterPassLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Smoothing", resultsPanel);

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

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                .addContainerGap())
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data         ", tablePanel);

        threshSlider1.setMajorTickSpacing(1);
        threshSlider1.setPaintTicks(true);
        threshSlider1.setValue(1);
        threshSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                threshSlider1MouseReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel8.setText("Threshold");

        noiseSlider.setMajorTickSpacing(1);
        noiseSlider.setPaintTicks(true);
        noiseSlider.setValue(1);
        noiseSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                noiseSliderMouseReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel11.setText("Height");

        h_slider.setMajorTickSpacing(1);
        h_slider.setPaintTicks(true);
        h_slider.setValue(1);
        h_slider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                h_sliderMouseReleased(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel12.setText("Noise");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(threshSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(h_slider, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(noiseSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(46, 46, 46))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(threshSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(19, 19, 19)
                        .addComponent(jLabel11))
                    .addComponent(h_slider, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addComponent(noiseSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(114, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Peak Bottoms", jPanel4);

        jLabel9.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel9.setText("Connect points by");

        baselineMethodCombo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        baselineMethodCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Regression", "Interpolation" }));
        baselineMethodCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baselineMethodComboActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel10.setText("Method");

        blmethodButtonGroup.add(lineCheckBox);
        lineCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lineCheckBox.setText("Line");

        blmethodButtonGroup.add(splineCheckBox);
        splineCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        splineCheckBox.setText("Spline");

        blmethodButtonGroup.add(cubicSplineCheckBox);
        cubicSplineCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cubicSplineCheckBox.setText("Cubic Spline");

        baselineButton.setText("Finish");
        baselineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baselineButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baselineMethodCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(splineCheckBox)
                            .addComponent(lineCheckBox)
                            .addComponent(cubicSplineCheckBox))))
                .addContainerGap(341, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(baselineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(baselineMethodCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(lineCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splineCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cubicSplineCheckBox)
                .addGap(34, 34, 34)
                .addComponent(baselineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );

        jTabbedPane1.addTab("Baseline   ", jPanel3);

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel4.setText("Threshold");

        threshold.setText("Add");
        threshold.setFocusable(false);
        threshold.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        threshold.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        threshold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thresholdActionPerformed(evt);
            }
        });

        thresholdSlider.setMajorTickSpacing(10);
        thresholdSlider.setMinorTickSpacing(1);
        thresholdSlider.setPaintLabels(true);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setToolTipText("");
        thresholdSlider.setValue(1);
        thresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                thresholdSliderStateChanged(evt);
            }
        });

        Valleys.setText("Load");
        Valleys.setFocusable(false);
        Valleys.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Valleys.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Valleys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ValleysActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel7.setText("Valleys");

        neighborSlider.setMajorTickSpacing(1);
        neighborSlider.setMaximum(5);
        neighborSlider.setPaintTicks(true);
        neighborSlider.setValue(1);
        neighborSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                neighborSliderMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Valleys, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(thresholdSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(threshText, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(threshold)))
                .addContainerGap(114, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(neighborSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(threshText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(threshold))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(thresholdSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(Valleys, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(neighborSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Valleys    ", jPanel2);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        rsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PROCESSING", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout rsPanelLayout = new javax.swing.GroupLayout(rsPanel);
        rsPanel.setLayout(rsPanelLayout);
        rsPanelLayout.setHorizontalGroup(
            rsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        rsPanelLayout.setVerticalGroup(
            rsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(rsPanel);

        comPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OUTPUT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout comPanelLayout = new javax.swing.GroupLayout(comPanel);
        comPanel.setLayout(comPanelLayout);
        comPanelLayout.setHorizontalGroup(
            comPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        comPanelLayout.setVerticalGroup(
            comPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(comPanel);

        fileMenu.setText("File");

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);
        fileMenu.add(jSeparator1);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setText("Open File");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        printMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        printMenuItem.setText("Print");
        fileMenu.add(printMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");
        jMenuBar1.add(editMenu);

        displayMenu.setText("Display");
        jMenuBar1.add(displayMenu);

        optionsMenu.setText("Options");
        jMenuBar1.add(optionsMenu);

        toolsMenu.setText("Tools");
        jMenuBar1.add(toolsMenu);

        helpMenu.setText("Help");
        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLayeredPane1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(specPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
            .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(specPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(263, 263, 263)
                                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(1, 1, 1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed

        fileChooser();

        if (validateFileType()) {

            try {

                String[] choices = {"Intensity vs Transmittance", "Intensity vs Absorbance"};
                String input = (String) JOptionPane.showInputDialog(null, "Select input type",
                        "Input type", JOptionPane.QUESTION_MESSAGE, null, // Use
                        // default
                        // icon
                        choices, // Array of choices
                        choices[0]); // Initial choice

                if (input.equals("Intensity vs Transmittance")) {

                    readFile();
                    generate_spectrum(specPanel, "input_data"); //original spectrum

//                    Scale s = new Scale();
//                    s.fixScale(0, 100, "input_data");
//                    create_spectrum(s.getFixedScaleList(), specPanel);
                    //smoothed spec by auto function
//                    DefaultSmooth ls = new DefaultSmooth();
//                    ls.general_avg_algorithm_3point(sliderValue);
//                    createSmoothed_spectrum(ls.rowDataList, ls.avgPointList);
                    SavitzkyGolayFilter sg = new SavitzkyGolayFilter();
                    sg.applyFilter_3points();

                    combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                    //end of function

                    //Valleys graph plot
                    showValleys();

//                    DefaultSmooth ls = new DefaultSmooth();
//                    ls.general_avg_algorithm_3point(sliderValue);
//                    Scale s2 = new Scale();
//                    s2.fixScale(0, 100, "avg_data");
//                    create_spectrum(s.getFixedScaleList(), rsPanel);
                    //show peaks graph
                    /*   ValleysLocator ml = new ValleysLocator();
                    ml.point3Valleys();

                    switch (algorithm) {
                        case 1:
                            createDuel(createValleyDataset(ml.getValleyCandidates()), createSmoothedDataset());
                            break;
                        case 2:
                            createDuel(createValleyDataset(ml.getValleyCandidates()), createSmoothedDataset());
                            break;
                        case 3:
                            createDuel(createValleyDataset(ml.getValleyCandidates()), createSmoothedDataset());
                            break;
                   } */
                    //end of peak function
                } else if (input.equals("Intensity vs Absorbance")) {
                    readFile();
                    TransToAbs ab = new TransToAbs();

                    generate_spectrum(specPanel, "abs_data");

                }

            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {

            JOptionPane.showMessageDialog(null, "Invalid file format!", "Error", JOptionPane.ERROR_MESSAGE);

        }

        //create one dataset for input_table
        try {
            input_dataset = createInputDataset();
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end

//        showDefaultTrendLine();
//        thresholdSlider.setMaximum(upperBoundT);
//     
//        thresholdSlider.setMinimum(lowerBoundT);
//        thresholdSlider.setPaintTicks(true);
//        thresholdSlider.setPaintLabels(true);

    }//GEN-LAST:event_openButtonActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void peakButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_peakButtonActionPerformed
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {

            v1.peakTopSet();
//        ml.cal_Minimas(0);

//            switch (algorithm) {
//                case 1:
//                    ds = DefaultSmooth.getInstance();
            createDuel(createDataset(v1.peakTopSet(), fileName), createSmoothedDataset(), comPanel);
//                    break;
//                case 2:
////                    ls = SlidingAvgSmooth.getInstance();
//                    createDuel(ml.createDataset(), createSmoothedDataset(), comPanel);
//                    break;
//                case 3:
////                    tri = TriangularSmooth.getInstance();
//                    createDuel(ml.createDataset(), createSmoothedDataset(), comPanel);
//                    break;
//                case 4:
////                    sgf = SavitzkyGolayFilter.getInstance();
//                    createDuel(ml.createDataset(), createSmoothedDataset(), comPanel);
//                    break;
//
//            }
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_peakButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearAll();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void button_specgenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_specgenActionPerformed
        generate_spectrum(specPanel, "input_data");
    }//GEN-LAST:event_button_specgenActionPerformed

    private void smootheSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smootheSelectionActionPerformed
        rsPanel.removeAll();
        rsPanel.revalidate();
        rsPanel.repaint();
//        createSmoothed_spectrum(ls.rowDataList, ls.avgPointList); //could be used to show graph of selected section only
//        ls.updateSmoothedValue();
        switch (MainWindow.getAlgorithm()) {
            case 1:
                DefaultSmooth df = new DefaultSmooth();
                df.smooth_selected_section();
                generate_spectrum(rsPanel, "avg_data");
                break;
            case 2:
//                SlidingAvgSmooth_Selection sl = new SlidingAvgSmooth_Selection();
                sl = SlidingAvgSmooth_Selection.getInstance();
                sl.smooth_selected_section();
                generate_spectrum(rsPanel, "avg_data");
                break;
            case 3:
//                TriangularSmooth_Selection ts = new TriangularSmooth_Selection();
                ts = TriangularSmooth_Selection.getInstance();
                ts.smooth_selected_section();
                generate_spectrum(rsPanel, "avg_data");
                //
                break;

            case 4:
//                TriangularSmooth_Selection ts = new TriangularSmooth_Selection();
                sgf = SavitzkyGolayFilter.getInstance();
//                sgf.smooth_selected_section();
                generate_spectrum(rsPanel, "avg_data");
                //
                break;
        }
    }//GEN-LAST:event_smootheSelectionActionPerformed

    private void ValleysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ValleysActionPerformed
        showValleys();
//        try {
//            ml = new ValleysLocator();
//            ml.point3Valleys();
//
//            createDuel(createValleyDataset(ml.getValleyCandidates()), createSmoothedDataset(), comPanel);
//
//        } catch (SQLException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_ValleysActionPerformed

    private void smoothningSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_smoothningSliderStateChanged
        /*
        smoothningSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {

                if (!(smoothningSlider.getValueIsAdjusting())) {
                    int sliderValue = smoothningSlider.getValue();
                    Scale s = new Scale();

                    int listSize = sliderValuesList.size();
                    prev = (int) sliderValuesList.get(listSize - 1);

                    if (checkValidity() && sliderValue != 0 && !(sliderValue <= prev)) {
                        val_label1.setText(null);
                        val_label2.setText(null);

                        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Default")) {
                            algorithm = 1;
                            ds = DefaultSmooth.getInstance();
                            ds.general_avg_algorithm_3point(sliderValue);

//                            s.fixScale(0, 100, "avg_data");
//                            create_spectrum(s.getFixedScaleList(), rsPanel);
//                           
//                            createSmoothed_spectrum(ds.rowDataList, ds.avgPointList); //old
                            try {
                                XYDataset xyDataSet = createSmoothedDataset();

                                combined2Charts(input_dataset, xyDataSet, rsPanel);

                            } catch (SQLException ex) {
                                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            filterPassLabel.setText(Integer.toString(sliderValue));
                        }

                        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
                            algorithm = 2;
                            ls = SlidingAvgSmooth.getInstance();

                            if (threepoints.isSelected()) {
                                points = 3;
                                for (int i = 0; i < sliderValue - prev; i++) {
                                    ls.cal_5point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                                    try {
                                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            } else if (fivepoints.isSelected()) {
                                points = 5;
                                for (int i = 0; i < sliderValue - prev; i++) {
                                    ls.cal_5point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                                    try {
                                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            } else if (sevenpoints.isSelected()) {
                                points = 7;
                                for (int i = 0; i < sliderValue - prev; i++) {
                                    ls.cal_7point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                                    try {
                                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            } else if (ninepoints.isSelected()) {
                                points = 9;
                                for (int i = 0; i < sliderValue - prev; i++) {
                                    ls.cal_9point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                                    try {
                                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }
                            filterPassLabel.setText(Integer.toString(ls.count));
                        }
                        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Triangular Smoothing")) {
                            algorithm = 3;
                            tri = TriangularSmooth.getInstance();
                            for (int i = 0; i < sliderValue - prev; i++) {
                                tri.cal_5point_avg();
//                                createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
                                try {
                                    combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                                } catch (SQLException ex) {
                                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            filterPassLabel.setText(Integer.toString(ls.count));
                        }
                        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Savitzky-Golay Filter")) {

                            algorithm = 4;
                            sgf = SavitzkyGolayFilter.getInstance();
                            if (threepoints.isSelected()) {
                                points = 3;
                                for (int i = 0; i < sliderValue - prev; i++) {
                                    sgf.applyFilter_3points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                                    try {
                                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            } else if (fivepoints.isSelected()) {
                                points = 5;
                                for (int i = 0; i < sliderValue - prev; i++) {
                                    sgf.applyFilter_5points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                                    try {
                                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }

                            }
                            filterPassLabel.setText(Integer.toString(sgf.count));
                        }
//                        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Default")) {
//                            algorithm = 1;
//                            ds = DefaultSmooth.getInstance();
//                            ds.general_avg_algorithm_3point(sliderValue);
//                            createSmoothed_spectrum(ds.rowDataList, ds.avgPointList);
//                            filterPassLabel.setText(Integer.toString(sliderValue));
//                        }

                        if (sliderValue > prev) {
                            sliderValuesList.add(sliderValue);
                        }

                        showValleys();
                    } else {
                        //Ignore repeating values and 0
//                        System.out.println("Invalid Inputs");
                    }

                }

            }

        });

         */
    }//GEN-LAST:event_smoothningSliderStateChanged
    private boolean checkValidity() {

        if ((smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Select..."))) {
            inputvalidity = false;
            val_label1.setText("* Invaild!");
        }
        if (!threepoints.isSelected() && !fivepoints.isSelected() && !sevenpoints.isSelected() && !ninepoints.isSelected()) {
            inputvalidity = false;
            val_label2.setText("* Invalid!");
        }
        return inputvalidity;
    }
    private void smAlgoComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smAlgoComboActionPerformed

        /*        smAlgoCombo.addActionListener(new ActionListener() {
            Enumeration<AbstractButton> enumeration = pointsbuttonGroup.getElements();

            public void actionPerformed(ActionEvent e) {
                if (smAlgoCombo.getSelectedItem().equals("Triangular Smoothing")) {

                   threepoints.setSelected(true);
                   fivepoints.setSelected(false);
                   sevenpoints.setSelected(false);
                   ninepoints.setSelected(false);
                }else if(smAlgoCombo.getSelectedItem().equals("Triangular Smoothing")) {
                    
                    while (enumeration.hasMoreElements()) {
                        enumeration.nextElement().setEnabled(false);
                        
                    }
                }
                else {

                    while (enumeration.hasMoreElements()) {

                        enumeration.nextElement().setEnabled(true);
                    }
                }
            }
        });*/
        Object obj = evt.getSource();
        Enumeration<AbstractButton> enumeration = pointsbuttonGroup.getElements();
        smoothningSlider.setValue(1);
        if (obj == smAlgoCombo) {
            if (smAlgoCombo.getSelectedItem().equals("Triangular Smoothing")) {
                algorithm = 3;
                threepoints.setEnabled(false);
                fivepoints.setSelected(true);
                fivepoints.setEnabled(true);
                sevenpoints.setEnabled(false);
                ninepoints.setEnabled(false);

            } else if (smAlgoCombo.getSelectedItem().equals("Default")) {
                algorithm = 1;

                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement().setEnabled(false);

                }
            } else if (smAlgoCombo.getSelectedItem().equals("Unweighted Sliding Average ")) {
                algorithm = 2;

                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement().setEnabled(true);

                }
            } else if (smAlgoCombo.getSelectedItem().equals("Savitzky-Golay Filter")) {
                algorithm = 4;
                threepoints.setSelected(true);
                threepoints.setEnabled(true);
                fivepoints.setEnabled(true);
                sevenpoints.setEnabled(false);
                ninepoints.setEnabled(false);
            } else {
                algorithm = 0;
                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement().setEnabled(true);
                    enumeration.nextElement().setSelected(false);

                }
            }
        }

    }//GEN-LAST:event_smAlgoComboActionPerformed

    private void iterateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iterateButtonActionPerformed
        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
            ls = SlidingAvgSmooth.getInstance();
            ls.cal_5point_avg();
            filterPassLabel.setText(Integer.toString(ls.count));
            createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);
        }
    }//GEN-LAST:event_iterateButtonActionPerformed

    private void resetSmoothButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSmoothButtonActionPerformed
//        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
//            filterPassLabel.setText(null);
//            ls = SlidingAvgSmooth.getInstance();
//            ls.reverse();
//            createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);
//            smoothningSlider.setValue(1);

        filterPassLabel.setText(null);
        smoothningSlider.setValue(1);
        sliderValuesList.clear();
        sliderValuesList.add(0);

        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
            ls = SlidingAvgSmooth.getInstance();
            ls.reverse();
            createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);
        }
        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Triangular Smoothing")) {
            tri = TriangularSmooth.getInstance();
            tri.reverse();
            createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
        }
        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Default")) {
            ds = DefaultSmooth.getInstance();
            ds.reverse();
        }


    }//GEN-LAST:event_resetSmoothButtonActionPerformed

    private void thresholdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thresholdActionPerformed
        try {
            plot.clearRangeMarkers();
            //horizontal line drawer
            double thresh = Double.valueOf(threshText.getText());
            ValueMarker marker = new ValueMarker(thresh);
            marker.setLabel("Threshold Level");
            marker.setLabelAnchor(RectangleAnchor.CENTER);
            marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
            marker.setPaint(Color.BLACK);
            plot.addRangeMarker(marker);
            //end

            //discard points those who cannot attain threshold
            comPanel.removeAll();
            comPanel.revalidate();
            comPanel.repaint();
            v1.discard_below_threshold(BigDecimal.valueOf(thresh));

            switch (algorithm) {
                case 1:
                    createDuel(createValleyDataset(v1.getValleyCandidates()), createSmoothedDataset(), comPanel);
                    break;
                case 2:
                    createDuel(createValleyDataset(v1.getValleyCandidates()), createSmoothedDataset(), comPanel);
                    break;
                case 3:
                    createDuel(createValleyDataset(v1.getValleyCandidates()), createSmoothedDataset(), comPanel);
                    break;

            }
            //end
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_thresholdActionPerformed

    private void thresholdSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_thresholdSliderStateChanged
        thresholdSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ValueMarker marker = null;

                plot.clearRangeMarkers();

                if (!(thresholdSlider.getValueIsAdjusting())) {
                    try {
                        double thresholdValue = thresholdSlider.getValue() / 100.00;

                        //horizontal line drawer
                        marker = new ValueMarker(thresholdValue);
                        marker.setLabel("Threshold Level");
                        marker.setLabelAnchor(RectangleAnchor.CENTER);
                        marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
                        marker.setPaint(Color.BLACK);
                        plot.addRangeMarker(marker);
                        //end

                        v1 = new ValleysLocator();
                        v1.point3Valleys();
                        //discard points those who cannot attain threshold
                        comPanel.removeAll();
                        comPanel.revalidate();
                        comPanel.repaint();
                        v1.discard_below_threshold(BigDecimal.valueOf(thresholdValue));

                        switch (algorithm) {
                            case 1:
                                createDuel(createValleyDataset(v1.getValleyCandidates()), createSmoothedDataset(), comPanel);
                                break;
                            case 2:
                                createDuel(createValleyDataset(v1.getValleyCandidates()), createSmoothedDataset(), comPanel);
                                break;
                            case 3:
                                createDuel(createValleyDataset(v1.getValleyCandidates()), createSmoothedDataset(), comPanel);
                                break;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        });
    }//GEN-LAST:event_thresholdSliderStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            FileInputStream in = new FileInputStream("src/msc/ftir/util/file.properties");
            Properties props = new Properties();
            props.load(in);
            String p = props.getProperty("jfilechooser.browser.filepath");
            System.out.println(p);
            in.close();
            JFileChooser chooser = new JFileChooser(p, null);
            chooser.showOpenDialog(this);

            File dataFile = chooser.getCurrentDirectory();
            String newDirectoryLoc = dataFile.getAbsolutePath().replace("\\", "/");

            FileOutputStream out = new FileOutputStream("src/msc/ftir/util/file.properties");
            props.setProperty("jfilechooser.browser.filepath", newDirectoryLoc);
            System.out.println(newDirectoryLoc);
            props.store(out, null);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void baselineMethodComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baselineMethodComboActionPerformed
        Object obj1 = evt.getSource();
        if (obj1 == baselineMethodCombo) {
            Enumeration<AbstractButton> enumeration = blmethodButtonGroup.getElements();

            if (obj1 == baselineMethodCombo) {
                if (baselineMethodCombo.getSelectedItem().equals("Select...")) {
                    while (enumeration.hasMoreElements()) {
                        enumeration.nextElement().setEnabled(false);
                    }
                } else if (baselineMethodCombo.getSelectedItem().equals("Regression")) {
                    lineCheckBox.setEnabled(true);
                    splineCheckBox.setEnabled(true);
                    cubicSplineCheckBox.setEnabled(false);
                } else if (baselineMethodCombo.getSelectedItem().equals("Interpolation")) {
                    lineCheckBox.setEnabled(true);
                    splineCheckBox.setEnabled(false);
                    cubicSplineCheckBox.setEnabled(true);
                }
            }
        }
    }//GEN-LAST:event_baselineMethodComboActionPerformed

    private void baselineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baselineButtonActionPerformed
        try {
            if (baselineMethodCombo.getSelectedItem().toString().equalsIgnoreCase("Regression")) {
                bc = new RegressionBL();

                if (lineCheckBox.isSelected()) {

                    bc.drawRegressionLine(smoothed_chart, createValleyDataset(v1.getPeaktops()), lowerBoundX, upperBoundX);
                    System.out.println("sssss" + v1.getCandidates().size());
                }
                if (splineCheckBox.isSelected()) {

                    bc.drawPolynomialFit(smoothed_chart, createValleyDataset(v1.getPeaktops()), lowerBoundX, upperBoundX);
                }

//                create_spectrum(bc.getDifferencewithLine(), rsPanel, "Baseline Corrected");
                combined2Charts(createDataset(bc.getDifferencewithLine(), "Baseline Corrected"), createSmoothedDataset(), comPanel);
            }
            if (baselineMethodCombo.getSelectedItem().toString().equalsIgnoreCase("Interpolation")) {
                SortedMap<BigDecimal, BigDecimal> mapi = null;
                InterpolatedBL intpol = new InterpolatedBL();

                if (lineCheckBox.isSelected()) {
                    mapi = intpol.linearInterp(createValleyDataset(v1.getPeaktops()), v1.getCandidates().size());
                }

                if (cubicSplineCheckBox.isSelected()) {
                    mapi = intpol.cubicInterp(createValleyDataset(v1.getPeaktops()), v1.getCandidates().size());
                }
                combined2Charts(createDataset(mapi, "Interpolated data"), createSmoothedDataset(), rsPanel);
                combined2Charts(createDataset(intpol.getDifferencewithLine(), "Baseline Corrected"), createSmoothedDataset(), comPanel);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_baselineButtonActionPerformed

    private void smoothningSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_smoothningSliderMouseReleased

        sliderCurrentValue = smoothningSlider.getValue();

        if (sliderCurrentValue < sliderPreviousValue) {
            System.out.println("Decreasing.");
            reverseSmooth();

        } else if (sliderCurrentValue > sliderPreviousValue) {
            System.out.println("Increaseing.");
            performSmooth();
        }

        sliderPreviousValue = sliderCurrentValue;

    }//GEN-LAST:event_smoothningSliderMouseReleased

    private void smoothningSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_smoothningSliderMousePressed
        sliderPreviousValue = smoothningSlider.getValue();
    }//GEN-LAST:event_smoothningSliderMousePressed

    private void neighborSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_neighborSliderMouseReleased
        try {
            //take value of the slider
            //set it as the number of neighbour points considered when finding the valleys
            int neighbours = neighborSlider.getValue();
            ValleysLocator v = new ValleysLocator();
            v.point3Valleys();
            v.steepning(neighbours);

            comPanel.removeAll();
            comPanel.revalidate();
            comPanel.repaint();

            createDuel(createValleyDataset(v.getValleyCandidates()), createSmoothedDataset(), comPanel);
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_neighborSliderMouseReleased

    private void threshSlider1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_threshSlider1MouseReleased
        threshCurrent = threshSlider1.getValue();

        if (threshCurrent < threshPrevious) {

            reverseThresh();

        } else if (threshCurrent > threshPrevious) {

            performThresh();
        }

        threshPrevious = threshCurrent;
    }//GEN-LAST:event_threshSlider1MouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            ValleysLocator v1 = new ValleysLocator();
            v1.cal_1storder_derivative();
            v1.cal_2ndorder_derivative();
            v1.candidateSet();
            v1.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
            v1.adjustNoiseLevel(noiseThreshCurrent);
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end
    }//GEN-LAST:event_jButton2ActionPerformed

    private void noiseSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_noiseSliderMouseReleased
        noiseThreshCurrent = noiseSlider.getValue();

        if (noiseThreshCurrent < noiseThreshPrevious) {

            reverseNoiseFilter();

        } else if (noiseThreshCurrent > noiseThreshPrevious) {

            performNoiseFilter();
        }

        noiseThreshPrevious = noiseThreshCurrent;

    }//GEN-LAST:event_noiseSliderMouseReleased

    private void h_sliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_h_sliderMouseReleased
        h_current = h_slider.getValue();

        if (h_current < h_old) {

            h_filter_reverse();

        } else if (h_current > h_old) {

            h_filter_current();
        }

        h_old = h_current;
    }//GEN-LAST:event_h_sliderMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Valleys;
    private javax.swing.JButton baselineButton;
    private javax.swing.JComboBox<String> baselineMethodCombo;
    private javax.swing.ButtonGroup blmethodButtonGroup;
    private javax.swing.JButton button_specgen;
    private javax.swing.JButton clearButton;
    private javax.swing.JPanel comPanel;
    private javax.swing.JCheckBox cubicSplineCheckBox;
    public static javax.swing.JTable dataTable;
    private javax.swing.JMenu displayMenu;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTextField filePathText;
    private javax.swing.JLabel filterPassLabel;
    private javax.swing.JRadioButton fivepoints;
    private javax.swing.JSlider h_slider;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton iterateButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JCheckBox lineCheckBox;
    private javax.swing.JSlider neighborSlider;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JRadioButton ninepoints;
    private javax.swing.JSlider noiseSlider;
    private javax.swing.JButton openButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JButton peakButton;
    private javax.swing.ButtonGroup pointsbuttonGroup;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JButton resetSmoothButton;
    private javax.swing.JPanel resultsPanel;
    public javax.swing.JPanel rsPanel;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JRadioButton sevenpoints;
    private javax.swing.JComboBox<String> smAlgoCombo;
    private javax.swing.JButton smootheSelection;
    private javax.swing.JSlider smoothningSlider;
    private javax.swing.JPanel specPanel;
    private javax.swing.JCheckBox splineCheckBox;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JRadioButton threepoints;
    private javax.swing.JSlider threshSlider1;
    private javax.swing.JTextField threshText;
    private javax.swing.JButton threshold;
    private javax.swing.JSlider thresholdSlider;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JLabel val_label1;
    private javax.swing.JLabel val_label2;
    // End of variables declaration//GEN-END:variables

    private void update_table() {

        try {
            String sql = "select ID AS \"INDEX\", WAVENUMBER , TRANSMITTANCE from input_data";

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

    private void generate_spectrum(JPanel jpanel, String tableName) {
        try {
            jpanel.removeAll();
            jpanel.revalidate();
            jpanel.repaint();
//            String query1 = "select WAVENUMBER, TRANSMITTANCE from abs_data";
//            String query1 = "select WAVENUMBER, TRANSMITTANCE from input_data";

            String query1 = "select WAVENUMBER, TRANSMITTANCE from " + tableName;
//            System.out.println(query1);

            JDBCXYDataset dataset = new JDBCXYDataset(conn, query1);

//            JFreeChart spec = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);
            spec = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);
            spec.setBorderVisible(false);
            spec.setBackgroundPaint(Color.white);
            spec.getXYPlot().setDomainGridlinesVisible(true);

            ChartPanel chartPanel = new ChartPanel(spec);
//            System.out.println(chartPanel.getPreferredSize());
//            chartPanel.setPreferredSize(new Dimension(654, 350));
            chartPanel.setDomainZoomable(false);
            chartPanel.addMouseListener(new MouseMarker(chartPanel));

            BarRenderer renderer = null;
            plot = spec.getXYPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setAutoRange(true);
            renderer = new BarRenderer();

            jpanel.setLayout(new java.awt.BorderLayout());
            jpanel.add(chartPanel, BorderLayout.CENTER);
            jpanel.validate();
            jpanel.setPreferredSize(new Dimension(654, 350));
            jpanel.setVisible(true);

            NumberAxis domain = (NumberAxis) plot.getDomainAxis();
            domain.setAutoRange(true);
            domain.setInverted(true);

            lowerBoundX = (int) plot.getDomainAxis().getRange().getLowerBound();
            upperBoundX = (int) plot.getDomainAxis().getRange().getUpperBound();

            lowerBoundT = plot.getRangeAxis().getRange().getLowerBound();
            upperBoundT = plot.getRangeAxis().getRange().getUpperBound();

//            System.out.println(lowerBoundX + " " + upperBoundX + "     " + lowerBoundT + " " + upperBoundT);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void show_peaks(JPanel jpanel, XYDataset dataset) {
        try {

            JFreeChart spec = ChartFactory.createScatterPlot("", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);

            spec.setBorderVisible(false);

            spec.getXYPlot().setDomainGridlinesVisible(false);

            ChartPanel chartPanel = new ChartPanel(spec);
//            System.out.println(chartPanel.getPreferredSize());
            chartPanel.setPreferredSize(new Dimension(654, 350));
            chartPanel.setDomainZoomable(true);

            BarRenderer renderer = null;
            XYPlot plot = spec.getXYPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setAutoRange(true);
            renderer = new BarRenderer();

            jpanel.setLayout(new java.awt.BorderLayout());
            jpanel.add(chartPanel, BorderLayout.CENTER);
            jpanel.validate();
            jpanel.setPreferredSize(new Dimension(654, 350));
            jpanel.setVisible(true);

            NumberAxis domain = (NumberAxis) plot.getDomainAxis();
            domain.setAutoRange(true);
            domain.setInverted(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void fileChooser() {

////        JFileChooser chooser = new MyFileChooser();
//        JFileChooser chooser = Utils.getFileChooser();
//        int returnVal = chooser.showOpenDialog(this);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            Utils.setLastDir(chooser.getSelectedFile());
//
//        }
////        File workingDirectory = new File(System.getProperty("user.home"));
////        chooser.setCurrentDirectory(workingDirectory);
//
////        chooser.setSelectedFile(workingDirectory);
////        chooser.showOpenDialog(null);
//        File dataFile = chooser.getSelectedFile();
////        fileName = dataFile.getAbsolutePath();
//        fileName = dataFile.getAbsolutePath().replace("\\", "/");
//        filePathText.setText(fileName);
//        p.setProperty(fileName, "Last.dir");
        try {
            //get previous location
            FileInputStream in = new FileInputStream("src/msc/ftir/util/file.properties");
            Properties props = new Properties();
            props.load(in);
            String p = props.getProperty("jfilechooser.browser.filepath");
            in.close();
            JFileChooser chooser = new JFileChooser(p, null);
            chooser.showOpenDialog(this);

            //for file type validation
            File dataFile = chooser.getSelectedFile();
            fileName = dataFile.getAbsolutePath();
            fileName = dataFile.getAbsolutePath().replace("\\", "/");
            //end

            //store current directory
            File dataFile2 = chooser.getCurrentDirectory();
            String newDirectoryLoc = dataFile2.getAbsolutePath().replace("\\", "/");

            FileOutputStream out = new FileOutputStream("src/msc/ftir/util/file.properties");
            props.setProperty("jfilechooser.browser.filepath", newDirectoryLoc);
            filePathText.setText(fileName);
            props.store(out, null);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }

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
//                System.out.println(line);
                boolean m = regrexMatch.matches();
//                System.out.println(m);

                if (!regrexMatch.matches()) {
                    ++invalid_input;
                    errorLine.add(lineNumber + 1);

                } else {
                    ++valid_input;

                }

                ++lineNumber;

            }
            br.close();

//            System.out.println(invalid_input + " invalid inputs found at line #" + Arrays.toString(errorLine.toArray()));
            System.out.println("Invalid inputs found!");
//            System.out.println("valid inputs = " + valid_input);
//            System.out.println("Total Number of lines = " + lineNumber);
            if (invalid_input > 0) {

//                String msg = "Data format errors are found at line #" + Arrays.toString(errorLine.toArray());
                String msg = "Data format errors found!";
                JOptionPane optionPane = new JOptionPane();
                optionPane.setMessage(msg);
                optionPane.setMessageType(JOptionPane.WARNING_MESSAGE);
                JDialog dialog = optionPane.createDialog(null, "Data format error!");
                dialog.setVisible(true);
                dataformatvalidity = false;

            } else {
//                System.out.println("Data format is correct!");
                dataformatvalidity = true;

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    private boolean validateFileType() {

        if (fileName.toLowerCase().endsWith("txt")) {
            fileType = FileType.TXT;
            return true;
        } else if (fileName.toLowerCase().endsWith("csv")) {
            fileType = FileType.CSV;
            return true;
        } else if (fileName.toLowerCase().endsWith("xls")) {
            fileType = FileType.XLS;
            return true;
        } else if (fileName.toLowerCase().endsWith("dpt")) {
            fileType = FileType.DPT;
            return true;
        } else {
//            JOptionPane.showMessageDialog(null, "Please select a valid file!", "Error", JOptionPane.ERROR_MESSAGE);
//                System.exit(0);

            return false;
        }
        /*
        String filetype = "(?:[\\w]\\:|\\\\)(\\\\[a-zA-Z_\\-\\s0-9\\.]+)+\\.(txt|CSV|csv|dpt|xls|xlsx)";
        Pattern fileExtPattern = Pattern.compile(filetype);

        Matcher mtch = fileExtPattern.matcher(fileName);
        if (mtch.matches()) {

            System.out.println(fileName);
            System.out.println(mtch.matches());
            return true;

        } else {
            return false;
        }*/
    }

    private void upload() {

        try {
            final String commentChar = "#";
            System.out.println(fileName);
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            String[] value = null;
            int numLines = 0;

//            && line.matches("\\d{3,4}\\.\\d{5,6}(\\,|[ \\t])\\d{1,2}\\.\\d{5,6}(\\,|[ \\t]*)")
//              && line.matches("[^#].*")
            while ((line = br.readLine()) != null) {

                if (line.startsWith(commentChar) | line.equals("")) {
                    //skip lines starting with # and empty lines
                    System.out.println(line + " Skipped");
                    continue;
                }

                switch (fileType) {
                    case CSV:
                        value = line.split(",");
                        break;
                    case TXT:
                        value = line.split("\\s+");
                        break;
                    case DPT:
                        value = line.split("\\s+"); //whitespace regex DPT
                }
                if (fileType == XLS | fileType == XLXS) {
                    try {

                        FileInputStream input = new FileInputStream(fileName);
                        POIFSFileSystem fs = new POIFSFileSystem(input);
                        Workbook workbook;
                        workbook = WorkbookFactory.create(fs);
                        Sheet sheet = workbook.getSheetAt(0);
                        Row row;
                        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                            row = (Row) sheet.getRow(i);
                            String name = row.getCell(0).getStringCellValue();
                            String add = row.getCell(1).getStringCellValue();

                            double wavenumber = row.getCell(2).getNumericCellValue();

                            double transmittance = row.getCell(3).getNumericCellValue();

                            String sql = "INSERT INTO input_data (wavenumber, transmittance) VALUES('" + wavenumber + "','" + transmittance + "')";
                            pst = conn.prepareStatement(sql);
                            pst.execute();
                            System.out.println("Import rows " + i);
                        }
                        conn.commit();
                        pst.close();
                        conn.close();
                        input.close();
                        System.out.println("Successfully imported excel to mysql table");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                //sb.append("insert into input_data (WAVENUMBER , TRANSMITTANCE)" + "values ('" + value[0].trim() + "','" + value[1].trim() + "');");
                String sql = "insert into input_data (WAVENUMBER , TRANSMITTANCE)" + "values ('" + value[0].trim() + "','" + value[1].trim() + "')";
                pst = conn.prepareStatement(sql);
                pst.executeUpdate();
                numLines++;

                /*
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
                 */
            }
            System.out.print("Uploaded\t");
            System.out.println(numLines + " lines");
            /*pst = conn.prepareStatement();
            pst.addBatch(sb.toString());
            pst.executeUpdate();
             */
            br.close();

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

    public void arrayFill() {

        int row = dataTable.getRowCount();
        int col = dataTable.getColumnCount();

        for (int i = 0; i < dataTable.getRowCount(); i++) {

            for (int j = 0; j < dataTable.getColumnCount(); j++) {//y = f(x) values

                dataArray[i][j] = dataTable.getModel().getValueAt(i, j);

                System.out.println(dataArray.getClass().toString());

            }

        }

    }

    public void createSmoothed_spectrum(ArrayList<InputData> rowDataList, ArrayList<BigDecimal> averagedList) {
        try {
            rsPanel.removeAll();
            rsPanel.revalidate();
            rsPanel.repaint();
//            String query2 = "select WAVENUMBER, TRANSMITTANCE from avg_data";
//            JDBCXYDataset dataset = new JDBCXYDataset(conn, query2);
            XYDataset dataset1 = createDataset(rowDataList, averagedList);

            smoothedSpec = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset1, PlotOrientation.VERTICAL, false, true, true);

            smoothedSpec.setBorderVisible(false);
            smoothedSpec.getXYPlot().setDomainGridlinesVisible(true);

            ChartPanel chartPanel = new ChartPanel(smoothedSpec);

//            chartPanel.setPreferredSize(new Dimension(654, 350));
            chartPanel.setDomainZoomable(true);
            chartPanel.setHorizontalAxisTrace(true);
            chartPanel.setVerticalAxisTrace(true);

            BarRenderer renderer = null;
            XYPlot plot = smoothedSpec.getXYPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setAutoRange(true);
            renderer = new BarRenderer();

            rsPanel.setLayout(new java.awt.BorderLayout());
            rsPanel.add(chartPanel, BorderLayout.CENTER);
            rsPanel.validate();
            rsPanel.setPreferredSize(new Dimension(654, 350));
            rsPanel.setVisible(true);

            NumberAxis domain = (NumberAxis) plot.getDomainAxis();
            domain.setAutoRange(true);
            domain.setInverted(true);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void create_spectrum(SortedMap<BigDecimal, BigDecimal> map, JPanel panel, String s) {
        try {
            panel.removeAll();
            panel.revalidate();
            panel.repaint();
//            String query2 = "select WAVENUMBER, TRANSMITTANCE from avg_data";
//            JDBCXYDataset dataset = new JDBCXYDataset(conn, query2);
            XYDataset dataset = createDataset(map, s);

            JFreeChart chart = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);

            chart.setBorderVisible(false);
            chart.getXYPlot().setDomainGridlinesVisible(true);

            ChartPanel chartPanel = new ChartPanel(chart);

            chartPanel.setPreferredSize(new Dimension(654, 350));
            chartPanel.setDomainZoomable(true);

            BarRenderer renderer = null;
//            XYPlot plot = chart.getXYPlot(); //old
            plot = chart.getXYPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setAutoRange(true);
            renderer = new BarRenderer();

            panel.setLayout(new java.awt.BorderLayout());
            panel.add(chartPanel, BorderLayout.CENTER);
            panel.validate();
            panel.setPreferredSize(new Dimension(654, 350));
            panel.setVisible(true);

            NumberAxis domain = (NumberAxis) plot.getDomainAxis();
            domain.setAutoRange(true);
            domain.setInverted(true);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void clearTables() {
        int p = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear data?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
        PreparedStatement pst1 = null;

        if (p == 0) {
            String sql1 = "delete from input_data";

            try {
                pst1 = conn.prepareStatement(sql1);
                pst1.execute();

                JOptionPane.showMessageDialog(null, "Delete Successful!");

                specPanel.removeAll();
                specPanel.revalidate();
                specPanel.repaint();

                rsPanel.removeAll();
                rsPanel.revalidate();
                rsPanel.repaint();

            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    pst1.close();

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            update_table();

        }
    }

    private void clearAll() {

        newInstance = true;
        filterPassLabel.setText(null);
        filePathText.setText(null);
        PreparedStatement pst2 = null;
        PreparedStatement pst3 = null;
        String sql1 = "delete from input_data";
        String sql2 = "delete from avg_data";
        String sql3 = "delete from abs_data";

        try {
            pst = conn.prepareStatement(sql1);
            pst.execute();

            pst2 = conn.prepareStatement(sql2);
            pst2.execute();

            pst3 = conn.prepareStatement(sql3);
            pst3.execute();

            specPanel.removeAll();
            specPanel.revalidate();
            specPanel.repaint();

            rsPanel.removeAll();
            rsPanel.revalidate();
            rsPanel.repaint();

            comPanel.removeAll();
            comPanel.revalidate();
            comPanel.repaint();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                pst.close();
                pst2.close();
                pst3.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
//        System.out.println("All cleared");
        update_table();

    }

    public void readFile() throws FileNotFoundException, IOException, SQLException {
        String value = " ";
        validateFileType();

        try {

            switch (fileType) {
                case CSV:
                    value = ",";//for cvs
                    break;
                case TXT:
                    value = " ";//for txt
                    break;
                case DPT:
                    value = "\t"; //for DPT
                }

            String qry = "LOAD DATA LOCAL INFILE '" + fileName + "' INTO TABLE input_data FIELDS TERMINATED BY '" + value + "' LINES TERMINATED BY '\\r\\n' (wavenumber, transmittance)";

//            String qrydel = "DELETE FROM input_data WHERE wavenumber = 0.00000000";
//            System.out.println(qry);
            pst = conn.prepareStatement(qry);
            pst.executeUpdate();

//            pst = conn.prepareStatement(qrydel);
//            pst.executeUpdate();
            update_table();

        } catch (Exception e) {
            System.out.println(e);
        } finally {

            pst.close();
        }

    }

    public void createDuel(XYDataset set1, XYDataset set2, JPanel panel) {

        XYPlot plot = new XYPlot();

        XYDataset collection1 = set1;
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(false, true);	// Shapes only
        ValueAxis domain1 = new NumberAxis("Wavenumber (cm-1)");
        ValueAxis range1 = new NumberAxis("Transmittance %");
        domain1.setAutoRange(true);
        domain1.setInverted(true);
        range1.setAutoRange(true);

        plot.setDataset(0, collection1);
        plot.setRenderer(0, renderer1);
        plot.setDomainAxis(0, domain1);
        plot.setRangeAxis(0, range1);

        XYDataset collection2 = set2;
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, true);	// Lines only
        ValueAxis domain2 = new NumberAxis("");
        ValueAxis range2 = new NumberAxis("");

        plot.setDataset(1, collection2);
        plot.setRenderer(1, renderer2);
        plot.setDomainAxis(1, domain2);
        plot.setRangeAxis(1, range2);

//        plot.mapDatasetToDomainAxis(0, 1);
//        plot.mapDatasetToRangeAxis(0, 1);
        domain2.setAutoRange(true);
        domain2.setInverted(true);
        domain2.setVisible(false);
        range2.setVisible(false);

//        plot.mapDatasetToDomainAxis(1, 1);
//        plot.mapDatasetToRangeAxis(1, 1);
        duelchart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        ChartPanel chartPanel = new ChartPanel(duelchart);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
        panel.setPreferredSize(new Dimension(654, 350));
        panel.setVisible(true);

    }

    //two charts ftir and smoothed one together
    public void combined2Charts(XYDataset set1, XYDataset set2, JPanel panel) {

        panel.removeAll();
        panel.revalidate();
        panel.repaint();

        XYPlot plot = new XYPlot();

        XYDataset collection1 = set1;
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);	// Shapes only
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer1.setSeriesPaint(0, Color.magenta);
        ValueAxis domain1 = new NumberAxis("Wavenumber (cm-1)");
        ValueAxis range1 = new NumberAxis("Transmittance %");
        domain1.setAutoRange(true);
        domain1.setInverted(true);
        range1.setAutoRange(true);

        plot.setDataset(0, collection1);
        plot.setRenderer(0, renderer1);
        plot.setRenderer(0, xylineandshaperenderer1);
        plot.setDomainAxis(0, domain1);
        plot.setRangeAxis(0, range1);

        XYDataset collection2 = set2;
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);	// Lines only
        ValueAxis domain2 = new NumberAxis("");
        ValueAxis range2 = new NumberAxis("");
        XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer2.setSeriesPaint(0, Color.gray);

        plot.setDataset(1, collection2);
        plot.setRenderer(1, renderer2);
        plot.setRenderer(1, xylineandshaperenderer2);
        plot.setDomainAxis(1, domain2);
        plot.setRangeAxis(1, range2);

//        plot.mapDatasetToDomainAxis(0, 1);
//        plot.mapDatasetToRangeAxis(0, 1);
        domain2.setAutoRange(true);
        domain2.setInverted(true);
        domain2.setVisible(false);
        range2.setVisible(false);

//        plot.mapDatasetToDomainAxis(1, 1);
//        plot.mapDatasetToRangeAxis(1, 1);
        smoothed_chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        ChartPanel chartPanel = new ChartPanel(smoothed_chart);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
        panel.setPreferredSize(new Dimension(654, 350));
        panel.setVisible(true);
    }

    //three charts ftir and smoothed one together
    public void combined3Charts(XYDataset set1, XYDataset set2, XYDataset set3, JPanel panel) { //valley, smoothed, interpolated

        panel.removeAll();
        panel.revalidate();
        panel.repaint();

        XYPlot plot = new XYPlot();

        XYDataset collection1 = set1;
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);	// Shapes only
        ValueAxis domain1 = new NumberAxis("Wavenumber");
        ValueAxis range1 = new NumberAxis("Transmittance");
        domain1.setAutoRange(true);
        domain1.setInverted(true);
        range1.setAutoRange(true);

        plot.setDataset(0, collection1);
        plot.setRenderer(0, renderer1);
        plot.setDomainAxis(0, domain1);
        plot.setRangeAxis(0, range1);

        XYDataset collection3 = set3;
        XYItemRenderer renderer3 = new XYLineAndShapeRenderer(true, false);	// Shapes only
        ValueAxis domain3 = new NumberAxis("Wavenumber");
        ValueAxis range3 = new NumberAxis("Transmittance");
        domain1.setAutoRange(true);
        domain1.setInverted(true);
        range1.setAutoRange(true);

        plot.setDataset(2, collection3);
        plot.setRenderer(2, renderer3);
        plot.setDomainAxis(2, domain3);
        plot.setRangeAxis(2, range3);

        XYDataset collection2 = set2;
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);	// Lines only
        ValueAxis domain2 = new NumberAxis("");
        ValueAxis range2 = new NumberAxis("");

        plot.setDataset(1, collection2);
        plot.setRenderer(1, renderer2);
        plot.setDomainAxis(1, domain2);
        plot.setRangeAxis(1, range2);

//        plot.mapDatasetToDomainAxis(0, 1);
//        plot.mapDatasetToRangeAxis(0, 1);
        domain2.setAutoRange(true);
        domain2.setInverted(true);
        domain2.setVisible(false);
        range2.setVisible(false);

//        plot.mapDatasetToDomainAxis(1, 1);
//        plot.mapDatasetToRangeAxis(1, 1);
        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
        panel.setPreferredSize(new Dimension(654, 350));
        panel.setVisible(true);
    }

    private XYDataset createBaseLineDataset() {
        final XYSeries baselinePoints = new XYSeries("Baseline Points");

        for (BigDecimal wavelength : bc.getLinePoints().keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = bc.getLinePoints().get(wavelength);
            baselinePoints.add(key, value);

        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(baselinePoints);
        return dataset;
    }

    private XYDataset create2BaseLineDataset() {
        final XYSeries baselinePoints = new XYSeries("Baseline Points");

        for (BigDecimal wavelength : bc.getLine2Points().keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = bc.getLine2Points().get(wavelength);
            baselinePoints.add(key, value);
        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(baselinePoints);
        return dataset;
    }

    private XYDataset createDataset(SortedMap<BigDecimal, BigDecimal> map, String series_name) {
        final XYSeries baselinePoints = new XYSeries(series_name);

        for (BigDecimal wavelength : map.keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = map.get(wavelength);
            baselinePoints.add(key, value);

        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(baselinePoints);
        return dataset;
    }

    private XYDataset createDataset(ArrayList<InputData> rowDataList, ArrayList<BigDecimal> averagedList) {
        final XYSeries smoothedLine = new XYSeries("smoothedLine");
        for (int i = 0; i < averagedList.size(); i++) {
            smoothedLine.add(rowDataList.get(i).getWavenumber(), averagedList.get(i));
        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(smoothedLine);
        return dataset;
    }

    private XYDataset createValleyDataset(SortedMap<BigDecimal, BigDecimal> pointList) {
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

    private XYDataset createSmoothedDataset() throws SQLException {

        String query1 = "select WAVENUMBER, TRANSMITTANCE AS 'Smoothed Spectrum' from avg_data";
        JDBCXYDataset dataset = new JDBCXYDataset(conn, query1);
        return dataset;

    }

    private XYDataset createInputDataset() throws SQLException {
        String query1 = "select WAVENUMBER, TRANSMITTANCE AS 'Original Spectrum' from input_data";
        JDBCXYDataset dataset = new JDBCXYDataset(conn, query1);

        return dataset;
    }

    public void showDefaultTrendLine() {
        bc = new RegressionBL();
        bc.drawRegressionLine(spec, input_dataset, lowerBoundX, upperBoundX);//good to get default trend line

    }

    public void showValleys() {

//        comPanel.removeAll();
//        comPanel.revalidate();
//        comPanel.repaint();
//
//        try {
//            ValleysLocator vl = new ValleysLocator();
//            vl.point3Valleys();
//
//            createDuel(createValleyDataset(vl.getValleyCandidates()), createSmoothedDataset(), comPanel);
//
//        } catch (SQLException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//        }//end
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1 = new ValleysLocator();
            v1.cal_1storder_derivative();
            v1.cal_2ndorder_derivative();
            v1.candidateSet();
//            v1.discardBelowThresh(thresh1, lowerBoundT, upperBoundT);
//            v1.adjustNoiseLevel(thresh2);
            v1.evaluateNeighbourhood();
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end

    }

    public void performSmooth() {
        int sliderValue = smoothningSlider.getValue();

        if (checkValidity() && sliderValue != 0) {
            val_label1.setText(null);
            val_label2.setText(null);

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Default")) {
                algorithm = 1;
                ds = DefaultSmooth.getInstance();

                for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                    ds.general_avg_algorithm_3point(sliderValue);
                }
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                filterPassLabel.setText(Integer.toString(sliderValue));
            }

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
                algorithm = 2;
                ls = SlidingAvgSmooth.getInstance();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        ls.cal_5point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (fivepoints.isSelected()) {
                    points = 5;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        ls.cal_5point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (sevenpoints.isSelected()) {
                    points = 7;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        ls.cal_7point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (ninepoints.isSelected()) {
                    points = 9;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        ls.cal_9point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                filterPassLabel.setText(Integer.toString(ls.count));
            }
            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Triangular Smoothing")) {
                algorithm = 3;
                tri = TriangularSmooth.getInstance();
                for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                    tri.cal_5point_avg();
//                                createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
                    try {
                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                filterPassLabel.setText(Integer.toString(ls.count));
            }
            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Savitzky-Golay Filter")) {
                algorithm = 4;
                sgf = SavitzkyGolayFilter.getInstance();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        sgf.applyFilter_3points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (fivepoints.isSelected()) {
                    points = 5;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        sgf.applyFilter_5points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                filterPassLabel.setText(Integer.toString(sgf.count));
            }
            showValleys();
        }

    }

    private void reverseSmooth() {

        int sliderValue = smoothningSlider.getValue();

        if (checkValidity() && sliderValue != 0) {
            val_label1.setText(null);
            val_label2.setText(null);

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Default")) {
                algorithm = 1;
                DefaultSmooth ds1 = new DefaultSmooth();

                for (int i = 0; i < sliderValue; i++) {
                    ds1.general_avg_algorithm_3point(sliderValue);
                }
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                filterPassLabel.setText(Integer.toString(sliderValue));
            }

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
                algorithm = 2;
                SlidingAvgSmooth s1 = new SlidingAvgSmooth();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue; i++) {
                        s1.cal_3point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (fivepoints.isSelected()) {
                    points = 5;
                    for (int i = 0; i < sliderValue; i++) {
                        s1.cal_5point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (sevenpoints.isSelected()) {
                    points = 7;
                    for (int i = 0; i < sliderValue; i++) {
                        s1.cal_7point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (ninepoints.isSelected()) {
                    points = 9;
                    for (int i = 0; i < sliderValue; i++) {
                        s1.cal_9point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                filterPassLabel.setText(Integer.toString(ls.count));
            }
            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Triangular Smoothing")) {
                algorithm = 3;
                tri.reset();
                tri = TriangularSmooth.getInstance();
                for (int i = 0; i < sliderValue; i++) {
                    tri.cal_5point_avg();
//                                createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
                    try {
                        combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                filterPassLabel.setText(Integer.toString(ls.count));
            }
            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Savitzky-Golay Filter")) {
                algorithm = 4;
                sgf.reset();
                sgf = SavitzkyGolayFilter.getInstance();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue; i++) {
                        sgf.applyFilter_3points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (fivepoints.isSelected()) {
                    points = 5;
                    for (int i = 0; i < sliderValue; i++) {
                        sgf.applyFilter_5points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), rsPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                filterPassLabel.setText(Integer.toString(sgf.count));
            }
            showValleys();
        }
    }

    private void reverseThresh() {

        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1 = new ValleysLocator();
            v1.cal_1storder_derivative();
            v1.cal_2ndorder_derivative();
            v1.candidateSet();
            v1.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end

    }

    private void performThresh() {
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end
    }

    private void reverseNoiseFilter() {

        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1 = new ValleysLocator();
            v1.cal_1storder_derivative();
            v1.cal_2ndorder_derivative();
            v1.candidateSet();
            v1.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
            v1.adjustNoiseLevel(noiseThreshCurrent);
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end

    }

    private void performNoiseFilter() {
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1.adjustNoiseLevel(noiseThreshCurrent);
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end
    }

    private void h_filter_reverse() {
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1 = new ValleysLocator();
            v1.cal_1storder_derivative();
            v1.cal_2ndorder_derivative();
            v1.candidateSet();
            v1.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
            v1.cal_h(h_current);
            v1.evaluateNeighbourhood();
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end
    }

    private void h_filter_current() {
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1.cal_h(h_current);
            v1.evaluateNeighbourhood();
            createDuel(createValleyDataset(v1.getCandidates()), createSmoothedDataset(), comPanel);

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end
    }

}
