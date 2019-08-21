package msc.ftir.main;

import java.awt.BasicStroke;
import msc.ftir.baseline.InterpolatedBL;
import msc.ftir.valleys.ValleysLocator;
import msc.ftir.smooth.*;
import msc.ftir.baseline.RegressionBL;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
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
import java.text.DecimalFormat;
import java.text.MessageFormat;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
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
import javax.swing.table.DefaultTableCellRenderer;
import msc.ftir.library.LibraryFtir;
import msc.ftir.result.Predict;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYDataItem;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import javax.swing.table.*;
import static org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory.model;

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
    private SlidingAvgSmoothSingleton ls = null;
    private TriangularSmoothSingleton tri = null;
    private DefaultSmooth ds = null;
    private SlidingAvgSmooth_Selection sl = null;
    private TriangularSmooth_Selection ts = null;
    private SavitzkyGolayFilterSingleton sgf = null;
    private InterpolatedBL intpol = null;
    private Predict pr = null;
    private SavitzkyGolayFilter sg = null;
    private PreparedStatement pst2 = null, pst3 = null, pst4 = null, pst5 = null, pst6 = null, pst7 = null; //for clear all
    private XYPointerAnnotation pointer = null, pointer2 = null;
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

    private JFreeChart spec = null, chart = null, duelchart = null, smoothedSpec = null, smoothed_chart = null, charts3 = null;
    private XYDataset input_dataset = null, baseline_dataset = null, smoothed_dataset = null;
    private int sliderPreviousValue, sliderCurrentValue, h_current, h_old;

    private int threshCurrent = 2, threshPrevious = 0; //threshold values by sliders for valley detection
    private int noiseThreshCurrent, noiseThreshPrevious = 0; //threshold values by sliders for valley detection
    private boolean bltab = false;
    private ValleysLocator v1, v2;
    private XYDataset peakset = null;
    private XYPlot xyplotT = null;
    private boolean bandstab = false;
    private ChartPanel chartPanel_com = null;
    private Crosshair xCrosshair;
    private Crosshair yCrosshair;
    private CrosshairOverlay crosshairOverlay;

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

        // Screen size
//        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//
//// Screen insets
//        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
//
//// Get the real width/height
//        int width = screen.getWidth() - insets.left - insets.right;
//        int height = screen.getHeight() - insets.top - insets.bottom;
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
        progressBar.setVisible(false);
        loadingText.setVisible(false);
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
        algorithmMenu = new javax.swing.ButtonGroup();
        pointsMenu = new javax.swing.ButtonGroup();
        blConnectionMenu = new javax.swing.ButtonGroup();
        blMethodMenu = new javax.swing.ButtonGroup();
        mainSplitPane = new javax.swing.JSplitPane();
        sectionSplitPane = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        specSplitPane = new javax.swing.JSplitPane();
        specTabbedPane = new javax.swing.JTabbedPane();
        specPanel = new javax.swing.JPanel();
        smoothPanel = new javax.swing.JPanel();
        baselinePanel = new javax.swing.JPanel();
        comPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        settingsTabbedPane = new javax.swing.JTabbedPane();
        resultsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        smAlgoCombo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        threepoints = new javax.swing.JRadioButton();
        fivepoints = new javax.swing.JRadioButton();
        sevenpoints = new javax.swing.JRadioButton();
        ninepoints = new javax.swing.JRadioButton();
        resetSmoothButton = new javax.swing.JButton();
        nextButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        changeValueText = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        smoothningSlider = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        filterPassLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        baselineMethodCombo = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        lineCheckBox = new javax.swing.JCheckBox();
        splineCheckBox = new javax.swing.JCheckBox();
        cubicSplineCheckBox = new javax.swing.JCheckBox();
        nextButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        threshSlider1 = new javax.swing.JSlider();
        jLabel8 = new javax.swing.JLabel();
        predictButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        numBandsText = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        resultsFilter = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        selectAllButton = new javax.swing.JButton();
        deselectButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jToolBar = new javax.swing.JToolBar();
        smootheSelection = new javax.swing.JButton();
        peakButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        searchDatabaseButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        filePathText = new javax.swing.JTextField();
        openButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        loadingText = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        editMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        displayMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem4 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem5 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem6 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem7 = new javax.swing.JRadioButtonMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jRadioButtonMenuItem8 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem9 = new javax.swing.JRadioButtonMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jRadioButtonMenuItem10 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem11 = new javax.swing.JRadioButtonMenuItem();
        optionsMenu = new javax.swing.JMenu();
        searchMenu = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
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

        mainSplitPane.setBackground(new java.awt.Color(255, 255, 255));
        mainSplitPane.setDividerLocation(50);
        mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        sectionSplitPane.setDividerSize(10);
        sectionSplitPane.setOneTouchExpandable(true);

        jPanel6.setLayout(new java.awt.BorderLayout());

        specSplitPane.setDividerLocation(270);
        specSplitPane.setDividerSize(10);
        specSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        specSplitPane.setOneTouchExpandable(true);

        specPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ORIGINAL SPECTRUM", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout specPanelLayout = new javax.swing.GroupLayout(specPanel);
        specPanel.setLayout(specPanelLayout);
        specPanelLayout.setHorizontalGroup(
            specPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1050, Short.MAX_VALUE)
        );
        specPanelLayout.setVerticalGroup(
            specPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 241, Short.MAX_VALUE)
        );

        specTabbedPane.addTab("Original", specPanel);

        smoothPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SMOOTHING", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout smoothPanelLayout = new javax.swing.GroupLayout(smoothPanel);
        smoothPanel.setLayout(smoothPanelLayout);
        smoothPanelLayout.setHorizontalGroup(
            smoothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1050, Short.MAX_VALUE)
        );
        smoothPanelLayout.setVerticalGroup(
            smoothPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 241, Short.MAX_VALUE)
        );

        specTabbedPane.addTab("Smoothing", smoothPanel);

        baselinePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "BASELINE ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout baselinePanelLayout = new javax.swing.GroupLayout(baselinePanel);
        baselinePanel.setLayout(baselinePanelLayout);
        baselinePanelLayout.setHorizontalGroup(
            baselinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1050, Short.MAX_VALUE)
        );
        baselinePanelLayout.setVerticalGroup(
            baselinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 241, Short.MAX_VALUE)
        );

        specTabbedPane.addTab("Baseline", baselinePanel);

        specSplitPane.setLeftComponent(specTabbedPane);

        comPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RESULT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        javax.swing.GroupLayout comPanelLayout = new javax.swing.GroupLayout(comPanel);
        comPanel.setLayout(comPanelLayout);
        comPanelLayout.setHorizontalGroup(
            comPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1055, Short.MAX_VALUE)
        );
        comPanelLayout.setVerticalGroup(
            comPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );

        specSplitPane.setRightComponent(comPanel);

        jPanel6.add(specSplitPane, java.awt.BorderLayout.CENTER);

        sectionSplitPane.setRightComponent(jPanel6);

        settingsTabbedPane.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        settingsTabbedPane.setName(""); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Algorithm");

        smAlgoCombo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        smAlgoCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Savitzky-Golay Filter", "Unweighted Sliding Average ", "Triangular Smoothing" }));
        smAlgoCombo.setSelectedIndex(1);
        smAlgoCombo.setToolTipText("");
        smAlgoCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smAlgoComboActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Filter width");

        pointsbuttonGroup.add(threepoints);
        threepoints.setSelected(true);
        threepoints.setText("3 - points");
        threepoints.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                threepointsMouseClicked(evt);
            }
        });

        pointsbuttonGroup.add(fivepoints);
        fivepoints.setText("5 - points");
        fivepoints.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fivepointsMouseClicked(evt);
            }
        });

        pointsbuttonGroup.add(sevenpoints);
        sevenpoints.setText("7 - points");
        sevenpoints.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sevenpointsMouseClicked(evt);
            }
        });

        pointsbuttonGroup.add(ninepoints);
        ninepoints.setText("9 - points");
        ninepoints.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ninepointsMouseClicked(evt);
            }
        });

        resetSmoothButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        resetSmoothButton.setText("Resest");
        resetSmoothButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetSmoothButtonActionPerformed(evt);
            }
        });

        nextButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nextButton1.setText("Next");
        nextButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButton1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Change");

        changeValueText.setFocusable(false);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "(Optional Attributes)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12), new java.awt.Color(102, 102, 102))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Iterations");

        smoothningSlider.setMajorTickSpacing(10);
        smoothningSlider.setMinorTickSpacing(1);
        smoothningSlider.setPaintLabels(true);
        smoothningSlider.setPaintTicks(true);
        smoothningSlider.setToolTipText("");
        smoothningSlider.setValue(1);
        smoothningSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                smoothningSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                smoothningSliderMouseReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Filter passes ");

        filterPassLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(smoothningSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(filterPassLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(smoothningSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(filterPassLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout resultsPanelLayout = new javax.swing.GroupLayout(resultsPanel);
        resultsPanel.setLayout(resultsPanelLayout);
        resultsPanelLayout.setHorizontalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(resetSmoothButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(24, 24, 24)
                        .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(changeValueText, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(smAlgoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(threepoints)
                            .addComponent(fivepoints)
                            .addComponent(sevenpoints)
                            .addComponent(ninepoints))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 434, Short.MAX_VALUE)))
        );
        resultsPanelLayout.setVerticalGroup(
            resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(smAlgoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(threepoints))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fivepoints)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sevenpoints)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ninepoints)
                .addGap(21, 21, 21)
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(changeValueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(resultsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetSmoothButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );

        settingsTabbedPane.addTab("Smoothing", resultsPanel);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Connect points by");

        baselineMethodCombo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        baselineMethodCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Regression", "Interpolation" }));
        baselineMethodCombo.setSelectedIndex(2);
        baselineMethodCombo.setToolTipText("");
        baselineMethodCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baselineMethodComboActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Method");

        blmethodButtonGroup.add(lineCheckBox);
        lineCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lineCheckBox.setSelected(true);
        lineCheckBox.setText("Line");
        lineCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lineCheckBoxMouseClicked(evt);
            }
        });

        blmethodButtonGroup.add(splineCheckBox);
        splineCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        splineCheckBox.setText("Spline");
        splineCheckBox.setEnabled(false);
        splineCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                splineCheckBoxMouseClicked(evt);
            }
        });

        blmethodButtonGroup.add(cubicSplineCheckBox);
        cubicSplineCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cubicSplineCheckBox.setText("Cubic Spline");
        cubicSplineCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cubicSplineCheckBoxMouseClicked(evt);
            }
        });

        nextButton2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        nextButton2.setText("Next");
        nextButton2.setFocusable(false);
        nextButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(splineCheckBox)
                            .addComponent(lineCheckBox)
                            .addComponent(cubicSplineCheckBox)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(baselineMethodCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(176, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nextButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(baselineMethodCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(lineCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splineCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cubicSplineCheckBox)
                .addGap(97, 97, 97)
                .addComponent(nextButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(406, Short.MAX_VALUE))
        );

        settingsTabbedPane.addTab("Baseline   ", jPanel3);

        threshSlider1.setMajorTickSpacing(10);
        threshSlider1.setMinorTickSpacing(1);
        threshSlider1.setPaintLabels(true);
        threshSlider1.setPaintTicks(true);
        threshSlider1.setValue(2);
        threshSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                threshSlider1MouseReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Threshold");

        predictButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        predictButton.setText("Finish");
        predictButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                predictButtonActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("Bands Detected");

        numBandsText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        numBandsText.setFocusable(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numBandsText, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(predictButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(threshSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(199, 199, 199))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(threshSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel11))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(numBandsText, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(113, 113, 113)
                .addComponent(predictButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(409, Short.MAX_VALUE))
        );

        settingsTabbedPane.addTab("Bands", jPanel4);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Filter :");

        resultsFilter.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        resultsFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Alkanes", "Alkenes", "Alcohols", "Phenols", "Amines", "Amides", "Aromatics", "Carboxylic Acids", "Esters", "Ethers", "Aldehydes", "Ketones", "Miscellaneous" }));
        resultsFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultsFilterActionPerformed(evt);
            }
        });

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bond", "Functional Group"
            }
        ));
        resultTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                resultTableMouseMoved(evt);
            }
        });
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(resultTable);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("Saturated Hydrocarbons");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox2.setText("Unsaturated Hydrocarbons");

        jCheckBox3.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox3.setText("Unsaturated Cyclic Aromatics");

        jCheckBox4.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox4.setText("Halogenated Hydrocarbons");

        jCheckBox5.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox5.setText("Nitrogen Containing Compounds");

        jCheckBox6.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox6.setText("Silicon Containing Compounds");

        jCheckBox7.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox7.setText("Phosphorus Containing Compounds");

        jCheckBox8.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox8.setText("Sulfur Containing Compounds");

        jCheckBox9.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox9.setText("Oxygen Containing Compounds");

        jCheckBox10.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox10.setText("Compounds Containing Carbon To Oxygen Double Bonds");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox7)
                    .addComponent(jCheckBox8)
                    .addComponent(jCheckBox9)
                    .addComponent(jCheckBox10))
                .addGap(0, 0, 0))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox10)
                .addGap(0, 0, 0))
        );

        jScrollPane4.setViewportView(jPanel9);

        selectAllButton.setText("Select All");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        deselectButton.setText("Deselect All");
        deselectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(143, 143, 143)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(resultsFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(deselectButton)
                                    .addComponent(selectAllButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)))))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {deselectButton, selectAllButton});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(resultsFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(selectAllButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deselectButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {deselectButton, selectAllButton});

        settingsTabbedPane.addTab("Result     ", jPanel2);

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
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                .addContainerGap())
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        settingsTabbedPane.addTab("Data         ", tablePanel);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 448, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settingsTabbedPane)
                .addContainerGap())
        );

        sectionSplitPane.setLeftComponent(jPanel5);

        mainSplitPane.setBottomComponent(sectionSplitPane);

        jToolBar.setRollover(true);
        jToolBar.setFocusable(false);

        smootheSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/msc/ftir/images/Plot_20px.png"))); // NOI18N
        smootheSelection.setToolTipText("Smooth selected section");
        smootheSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smootheSelectionActionPerformed(evt);
            }
        });
        jToolBar.add(smootheSelection);

        peakButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/msc/ftir/images/Sort Up_20px.png"))); // NOI18N
        peakButton.setToolTipText("Show Peaks");
        peakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peakButtonActionPerformed(evt);
            }
        });
        jToolBar.add(peakButton);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/msc/ftir/images/Broom_20px.png"))); // NOI18N
        clearButton.setToolTipText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jToolBar.add(clearButton);

        searchDatabaseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/msc/ftir/images/Binoculars_20px.png"))); // NOI18N
        searchDatabaseButton.setToolTipText("Search Database");
        searchDatabaseButton.setFocusable(false);
        searchDatabaseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchDatabaseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        searchDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchDatabaseButtonActionPerformed(evt);
            }
        });
        jToolBar.add(searchDatabaseButton);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        openButton.setText("Open");
        openButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openButtonMouseClicked(evt);
            }
        });
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
                .addComponent(filePathText, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
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
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jToolBar.add(jPanel1);

        progressBar.setBackground(new java.awt.Color(255, 255, 255));
        progressBar.setForeground(new java.awt.Color(51, 204, 0));
        progressBar.setToolTipText("Loading...");
        progressBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        progressBar.setBorderPainted(false);
        progressBar.setIndeterminate(true);

        loadingText.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        loadingText.setText("Loading...");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 808, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 572, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(loadingText)
                        .addGap(63, 63, 63))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(loadingText)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainSplitPane.setLeftComponent(jPanel7);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        jMenuBar1.setBackground(new java.awt.Color(255, 255, 255));

        editMenu.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Save");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        editMenu.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Clear");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        editMenu.add(jMenuItem2);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Print");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        editMenu.add(jMenuItem4);

        jMenuBar1.add(editMenu);

        displayMenu.setText("Edit");

        jMenu1.setText("Smooth");

        jMenu2.setText("Filter");

        algorithmMenu.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("Savitzky-Golay");
        jMenu2.add(jRadioButtonMenuItem1);

        algorithmMenu.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText("Sliding Window");
        jMenu2.add(jRadioButtonMenuItem2);

        algorithmMenu.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setText("Triangular Algorithm");
        jMenu2.add(jRadioButtonMenuItem3);

        jMenu1.add(jMenu2);

        jMenu3.setText("Points");

        pointsMenu.add(jRadioButtonMenuItem4);
        jRadioButtonMenuItem4.setSelected(true);
        jRadioButtonMenuItem4.setText("3 - points");
        jMenu3.add(jRadioButtonMenuItem4);

        pointsMenu.add(jRadioButtonMenuItem5);
        jRadioButtonMenuItem5.setText("5 - points");
        jMenu3.add(jRadioButtonMenuItem5);

        pointsMenu.add(jRadioButtonMenuItem6);
        jRadioButtonMenuItem6.setText("7 - points");
        jMenu3.add(jRadioButtonMenuItem6);

        pointsMenu.add(jRadioButtonMenuItem7);
        jRadioButtonMenuItem7.setText("9 - points");
        jMenu3.add(jRadioButtonMenuItem7);

        jMenu1.add(jMenu3);

        displayMenu.add(jMenu1);

        jMenu4.setText("Baseline");

        jMenu5.setText("Connect by");

        blConnectionMenu.add(jRadioButtonMenuItem8);
        jRadioButtonMenuItem8.setSelected(true);
        jRadioButtonMenuItem8.setText("Interpolation");
        jMenu5.add(jRadioButtonMenuItem8);

        blConnectionMenu.add(jRadioButtonMenuItem9);
        jRadioButtonMenuItem9.setText("Regression");
        jMenu5.add(jRadioButtonMenuItem9);

        jMenu4.add(jMenu5);

        jMenu6.setText("Method");

        blMethodMenu.add(jRadioButtonMenuItem10);
        jRadioButtonMenuItem10.setSelected(true);
        jRadioButtonMenuItem10.setText("Line");
        jMenu6.add(jRadioButtonMenuItem10);

        blMethodMenu.add(jRadioButtonMenuItem11);
        jRadioButtonMenuItem11.setText("Cubic Spline");
        jMenu6.add(jRadioButtonMenuItem11);

        jMenu4.add(jMenu6);

        displayMenu.add(jMenu4);

        jMenuBar1.add(displayMenu);

        optionsMenu.setText("Library");

        searchMenu.setText("Search");
        searchMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchMenuActionPerformed(evt);
            }
        });
        optionsMenu.add(searchMenu);

        jMenuItem5.setText("Save to library");
        optionsMenu.add(jMenuItem5);

        jMenuBar1.add(optionsMenu);

        toolsMenu.setText("Tools");
        toolsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toolsMenuMouseClicked(evt);
            }
        });

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Original Spectrum");
        toolsMenu.add(jMenuItem3);

        jMenuBar1.add(toolsMenu);

        helpMenu.setText("Help");
        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
//        uploadFile();
////1. select file and validate 
//        fileChooser();
//
//        if (validateFileType()) {
//
//            try {
//
//                String[] choices = {"Transmittance", "Absorbance"};
//                String input = (String) JOptionPane.showInputDialog(null, "Select input type",
//                        "Input type", JOptionPane.QUESTION_MESSAGE, null, // Use
//                        // default
//                        // icon
//                        choices, // Array of choices
//                        choices[0]); // Initial choice
//
//                if (input.equals("Transmittance")) {
//
//                    //2.read file
//                    readFile();
//
//                    //create spectrum
//                    generate_spectrum(specPanel, "input_data"); //original spectrum
////                    generate_spectrum_seperateFrame("input_data");
//                }
//
//                if (input.equals("Absorbance")) {
//
//                    readAbsFile();
//                    AbsToTrans ab = new AbsToTrans();
//                    //create spectrum
//                    generate_spectrum(specPanel, "input_data"); //original spectrum
//                }
//
//                //3.run default smoothing
//                {
//                    sg = new SavitzkyGolayFilter();
//                    sg.applyFilter_3points();
//
//                    combined2Charts(createInputDataset(), createSmoothedDataset(), rsPanel);
//
//                }
//                //4.draw default baseline
//                {
//                    v1 = new ValleysLocator("avg_data");
//                    v1.cal_1storder_derivative(v1.getSmoothedPointList());
//                    v1.cal_2ndorder_derivative(v1.getSmoothedPointList());
//                    v1.findCandidateSet();
//                    v1.evaluateNeighbourhood();
////                    v1.discardBelowThresh(2, lowerBoundT, upperBoundT);
////                    numBandsLabel.setText(String.valueOf(v1.getCandidates().size()));
//
//                    SortedMap<BigDecimal, BigDecimal> mapi = null;
//                    intpol = new InterpolatedBL();
//
//                    mapi = intpol.linearInterp(createValleyDataset(v1.getPeaktops()), v1.getPeaktops().size());
//
//                    combined2Charts(createDataset(mapi, "Interpolated data"), createSmoothedDataset(), baseline_panel);
////                        combined2Charts(createDataset(intpol.getDifferencewithLine(), "Baseline Corrected"), createSmoothedDataset(), comPanel);
//                    combined2Charts(createDataset(intpol.getDifferencewithLine(), "Baseline Corrected"), input_dataset, comPanel);
//                }
//
//                //Valleys graph plot
//                {
//
//                    showValleys("avg_data");
//                }
//
//                //get the baseline equation
//                getBaselineEquation();
//
//            } catch (IOException ex) {
//                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (SQLException ex) {
//                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        } else {
//
//            JOptionPane.showMessageDialog(null, "Invalid file format!", "Error", JOptionPane.ERROR_MESSAGE);
//
//        }
//        //create one dataset for input_table
//        try {
//            input_dataset = createInputDataset();
//        } catch (SQLException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//        }//end
//        showDefaultTrendLine();
//        thresholdSlider.setMaximum(upperBoundT);
//     
//        thresholdSlider.setMinimum(lowerBoundT);
//        thresholdSlider.setPaintTicks(true);
//        thresholdSlider.setPaintLabels(true);

    }//GEN-LAST:event_openButtonActionPerformed

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
////                    ls = SlidingAvgSmoothSingleton.getInstance();
//                    createDuel(ml.createDataset(), createSmoothedDataset(), comPanel);
//                    break;
//                case 3:
////                    tri = TriangularSmoothSingleton.getInstance();
//                    createDuel(ml.createDataset(), createSmoothedDataset(), comPanel);
//                    break;
//                case 4:
////                    sgf = SavitzkyGolayFilterSingleton.getInstance();
//                    createDuel(ml.createDataset(), createSmoothedDataset(), comPanel);
//                    break;
//
//            }
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_peakButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        int confirmed = JOptionPane.showConfirmDialog(null,
                "Clear all?", "Clear",
                JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION) {
            {
                rs = null;
                pst = null;
                fileName = "";
                errorLine = new ArrayList<>();
                dataArray = new Object[1000][2];
                ls = null;
                tri = null;
                ds = null;
                sl = null;
                ts = null;
                sgf = null;
                intpol = null;
                pr = null;
                sg = null;
                pointer = null;
                pointer2 = null;
                bc = null;
                newInstance = false;
                sliderValuesList = new ArrayList<Integer>();
                prev = 0;
                inputvalidity = true;
                p = new Properties();
                points = 0;
                plot = null;
                thresh = 0;
                lowerBoundX = 0;
                upperBoundX = 0;
                lowerBoundT = 0;
                upperBoundT = 0;
                spec = null;
                chart = null;
                duelchart = null;
                smoothedSpec = null;
                smoothed_chart = null;
                charts3 = null;
                input_dataset = null;
                baseline_dataset = null;
                smoothed_dataset = null;
                sliderPreviousValue = 0;
                sliderCurrentValue = 0;
                h_current = 0;
                h_old = 0;
                threshCurrent = 2;
                threshPrevious = 0; //threshold values by sliders for valley detection
                noiseThreshPrevious = 0; //threshold values by sliders for valley detection
                bltab = false;
                peakset = null;
                xyplotT = null;
                bandstab = false;
                chartPanel_com = null;

            }
            clearAll();
        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }


    }//GEN-LAST:event_clearButtonActionPerformed

    private void smootheSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smootheSelectionActionPerformed
        smoothPanel.removeAll();
        smoothPanel.revalidate();
        smoothPanel.repaint();
//        createSmoothed_spectrum(ls.rowDataList, ls.avgPointList); //could be used to show graph of selected section only
//        ls.updateSmoothedValue();
        switch (MainWindow.getAlgorithm()) {
            case 1:
                DefaultSmooth df = new DefaultSmooth();
                df.smooth_selected_section();
                generate_spectrum(smoothPanel, "avg_data");
                break;
            case 2:
//                SlidingAvgSmooth_Selection sl = new SlidingAvgSmooth_Selection();
                sl = SlidingAvgSmooth_Selection.getInstance();
                sl.smooth_selected_section();
                generate_spectrum(smoothPanel, "avg_data");
                break;
            case 3:
//                TriangularSmooth_Selection ts = new TriangularSmooth_Selection();
                ts = TriangularSmooth_Selection.getInstance();
                ts.smooth_selected_section();
                generate_spectrum(smoothPanel, "avg_data");
                //
                break;

            case 4:
//                TriangularSmooth_Selection ts = new TriangularSmooth_Selection();
                sgf = SavitzkyGolayFilterSingleton.getInstance();
//                sgf.smooth_selected_section();
                generate_spectrum(smoothPanel, "avg_data");
                //
                break;
        }
    }//GEN-LAST:event_smootheSelectionActionPerformed
    private boolean checkValidity() {

        if ((smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Select..."))) {
            inputvalidity = false;
//            val_label1.setText("* Invaild!");
        }
        if (!threepoints.isSelected() && !fivepoints.isSelected() && !sevenpoints.isSelected() && !ninepoints.isSelected()) {
            inputvalidity = false;
//            val_label2.setText("* Invalid!");
        }
        return inputvalidity;
    }
    private void smAlgoComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smAlgoComboActionPerformed

        Object obj = evt.getSource();
        Enumeration<AbstractButton> enumeration = pointsbuttonGroup.getElements();
        smoothningSlider.setValue(1);
        if (obj == smAlgoCombo) {

            if (smAlgoCombo.getSelectedItem().equals("Select...")) {

                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement().setEnabled(false);
                }
                pointsbuttonGroup.clearSelection();

            }
            if (smAlgoCombo.getSelectedItem().equals("Triangular Smoothing")) {
                algorithm = 3;
                pointsbuttonGroup.clearSelection();
                threepoints.setEnabled(false);
//                fivepoints.setSelected(true);
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
                pointsbuttonGroup.clearSelection();

                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement().setEnabled(true);

                }
            } else if (smAlgoCombo.getSelectedItem().equals("Savitzky-Golay Filter")) {
                algorithm = 4;
//                threepoints.setSelected(true);
                pointsbuttonGroup.clearSelection();
                threepoints.setEnabled(true);
                fivepoints.setEnabled(true);
                sevenpoints.setEnabled(true);
                ninepoints.setEnabled(true);
            } else if (smAlgoCombo.getSelectedItem().equals("None")) {
                algorithm = 5;
//                threepoints.setSelected(false);
                pointsbuttonGroup.clearSelection();
                threepoints.setEnabled(false);
                fivepoints.setEnabled(false);
                sevenpoints.setEnabled(false);
                ninepoints.setEnabled(false);

                String sql2 = "Delete FROM avg_data";
                String sql1 = "INSERT INTO `avg_data`(`ID`, `WAVENUMBER`, `TRANSMITTANCE`) SELECT  * FROM input_data";
                PreparedStatement pst1 = null, pst2 = null;
                try {
                    pst2 = conn.prepareStatement(sql2);
                    pst2.executeUpdate();

                    pst1 = conn.prepareStatement(sql1);
                    pst1.executeUpdate();

                } catch (Exception e) {
                    System.err.println(e);
                } finally {
                    try {

                        pst2.close();
                        pst1.close();
                    } catch (Exception e) {

                    }
                }

                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                algorithm = 0;
                pointsbuttonGroup.clearSelection();
                while (enumeration.hasMoreElements()) {
                    enumeration.nextElement().setEnabled(true);
                    enumeration.nextElement().setSelected(false);

                }
            }
        }

    }//GEN-LAST:event_smAlgoComboActionPerformed

    private void resetSmoothButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSmoothButtonActionPerformed
//        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
//            filterPassLabel.setText(null);
//            ls = SlidingAvgSmoothSingleton.getInstance();
//            ls.reverse();
//            createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);
//            smoothningSlider.setValue(1);

        filterPassLabel.setText(null);
        smoothningSlider.setValue(1);
        sliderValuesList.clear();
        sliderValuesList.add(0);

        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
            ls = SlidingAvgSmoothSingleton.getInstance();
            ls.reverse();
            createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);
        }
        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Triangular Smoothing")) {
            tri = TriangularSmoothSingleton.getInstance();
            tri.reverse();
            createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
        }
        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Default")) {
            ds = DefaultSmooth.getInstance();
            ds.reverse();
        }


    }//GEN-LAST:event_resetSmoothButtonActionPerformed

    private void searchDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchDatabaseButtonActionPerformed
//        try {
//
//            System.out.println(" intpol.getY()  " + intpol.getY());
//            //select only zero transmittance data
//            String query1 = "select WAVENUMBER, TRANSMITTANCE from baseline_data where transmittance = " + intpol.getY();
//            JDBCXYDataset dataset = new JDBCXYDataset(conn, query1);
//
//            RegressionBL bc1 = new RegressionBL();
//            bc1.drawRegressionLine(duelchart, dataset, lowerBoundX, upperBoundX);
////            System.out.println("Flat Line   y = " + bc1.getM1() + "*x + " + bc1.getC1());
//
//        } catch (Exception e) {
//            System.err.println(e);
//        }

//        v2.cal_d_w_lenghts();
//        reduceMinfromAllY();
//        measurechange();
//        combined2Charts(createDataset(bc.getDifferencewithLine(), "Baseline Corrected"), createSmoothedDataset(), comPanel);
        LibraryFtir lb = new LibraryFtir();
        lb.setVisible(true);
        lb.setLocationRelativeTo(null);
    }//GEN-LAST:event_searchDatabaseButtonActionPerformed

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
                    blmethodButtonGroup.clearSelection();
                    lineCheckBox.setEnabled(true);
                    splineCheckBox.setEnabled(true);
                    cubicSplineCheckBox.setEnabled(false);
                } else if (baselineMethodCombo.getSelectedItem().equals("Interpolation")) {
                    blmethodButtonGroup.clearSelection();
                    lineCheckBox.setEnabled(true);
                    splineCheckBox.setEnabled(false);
                    cubicSplineCheckBox.setEnabled(true);
                }
            }
        }
    }//GEN-LAST:event_baselineMethodComboActionPerformed

    private void smoothningSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_smoothningSliderMouseReleased
        specTabbedPane.setSelectedIndex(1);

        sliderCurrentValue = smoothningSlider.getValue();

        if (sliderCurrentValue < sliderPreviousValue) {
//            System.out.println("Decreasing.");
            reverseSmooth();

        } else if (sliderCurrentValue > sliderPreviousValue) {
//            System.out.println("Increaseing.");
            performSmooth();
        }

        sliderPreviousValue = sliderCurrentValue;

    }//GEN-LAST:event_smoothningSliderMouseReleased

    private void smoothningSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_smoothningSliderMousePressed
        sliderPreviousValue = smoothningSlider.getValue();
    }//GEN-LAST:event_smoothningSliderMousePressed

    private void threshSlider1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_threshSlider1MouseReleased
        threshCurrent = threshSlider1.getValue();
//        ragneMarker();

        if (threshCurrent < threshPrevious) {

            reverseThresh();

        } else if (threshCurrent > threshPrevious) {

            performThresh();
        }

        threshPrevious = threshCurrent;
    }//GEN-LAST:event_threshSlider1MouseReleased

    private void nextButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButton1ActionPerformed

        settingsTabbedPane.setSelectedIndex(1);
        specTabbedPane.setSelectedIndex(2);
        setBaseline();


    }//GEN-LAST:event_nextButton1ActionPerformed

    private void nextButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButton2ActionPerformed
        setBaseline();
        performThresh();
        bandstab = true;

        if (bltab) {
            settingsTabbedPane.setSelectedIndex(2);
        } else {
            JOptionPane.showMessageDialog(null, "Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_nextButton2ActionPerformed

    private void predictButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_predictButtonActionPerformed
        pr = new Predict();
        pr.getResults();
        pr.updateResultsTable();//database

        updateResult_table();//interface result table
        if (bandstab) {
            settingsTabbedPane.setSelectedIndex(3);
        } else {
            JOptionPane.showMessageDialog(null, "Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_predictButtonActionPerformed

    private void fivepointsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fivepointsMouseClicked

        specTabbedPane.setSelectedIndex(1);
        performSingeTimeSmooth();
    }//GEN-LAST:event_fivepointsMouseClicked

    private void threepointsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_threepointsMouseClicked

        specTabbedPane.setSelectedIndex(1);

        performSingeTimeSmooth();
    }//GEN-LAST:event_threepointsMouseClicked

    private void sevenpointsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sevenpointsMouseClicked

        specTabbedPane.setSelectedIndex(1);
        performSingeTimeSmooth();
    }//GEN-LAST:event_sevenpointsMouseClicked

    private void ninepointsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ninepointsMouseClicked

        specTabbedPane.setSelectedIndex(1);
        performSingeTimeSmooth();
    }//GEN-LAST:event_ninepointsMouseClicked

    private void resultTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultTableMouseClicked
        performThresh();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        int row = resultTable.getSelectedRow();

        double w = Double.parseDouble(String.valueOf(resultTable.getValueAt(row, 0)));
        int wv = (int) Math.round(w);

        XYSeriesCollection dataSet0 = (XYSeriesCollection) xyplotT.getDataset(0);
        XYSeries series0 = dataSet0.getSeries(0);
        XYItemRenderer renderer1 = new MyXYBarRenderer();

        chartPanel_com.removeOverlay(crosshairOverlay);
        xyplotT.removeAnnotation(pointer);
        xyplotT.removeAnnotation(pointer2);

        crosshairOverlay = new CrosshairOverlay();
        xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair.setLabelVisible(false);
        yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair.setLabelVisible(false);
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);
        chartPanel_com.addOverlay(crosshairOverlay);

        pointer = new XYPointerAnnotation("", 0, 0, 7.0 * Math.PI / 4.0);
        pointer.setTipRadius(3.0);
        pointer.setBaseRadius(15.0);
        pointer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pointer.setPaint(Color.blue);
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        pointer.setBackgroundPaint(Color.yellow);

        // Shapes only
        for (Object i : series0.getItems()) {
            XYDataItem item = (XYDataItem) i;
            double x = item.getXValue();
            double y = item.getYValue();

            if (Math.abs(x - wv) < 1) {

                if (y > 0) {
                    pointer.setX(x);
                    pointer.setY(y);
                    pointer.setText("X = " + df.format(x) + " , Y = " + df.format(y));
                    xyplotT.addAnnotation(pointer);
                }
                xCrosshair.setValue(x);
                yCrosshair.setValue(y);

            }
        }


    }//GEN-LAST:event_resultTableMouseClicked

    private void openButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openButtonMouseClicked

        //1. select file to upload/ removed this from upload
        fileChooser();

        class MyWorker extends SwingWorker<String, Void> {

            protected String doInBackground() {
                progressBar.setVisible(true);
                loadingText.setVisible(true);
//                progressBar.setIndeterminate(true);
                uploadFile();
                return "Done";
            }

            protected void done() {
                progressBar.setVisible(false);
                loadingText.setVisible(false);
            }
        }

        new MyWorker().execute();
    }//GEN-LAST:event_openButtonMouseClicked

    private void lineCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lineCheckBoxMouseClicked
        setBaseline();
    }//GEN-LAST:event_lineCheckBoxMouseClicked

    private void cubicSplineCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cubicSplineCheckBoxMouseClicked
        setBaseline();
    }//GEN-LAST:event_cubicSplineCheckBoxMouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void splineCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_splineCheckBoxMouseClicked
        setBaseline();
    }//GEN-LAST:event_splineCheckBoxMouseClicked

    private void toolsMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toolsMenuMouseClicked
        generate_spectrum(specPanel, "input_data");
    }//GEN-LAST:event_toolsMenuMouseClicked

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

//        jMenuItem2.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
//                executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
//                executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(true);
//                ScheduledFuture<?> future = executor.schedule(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            Process p = Runtime.getRuntime().exec("cmd /c start java -jar D:\\MovieLibrary.jar");
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }, 2, TimeUnit.SECONDS);
//                executor.shutdown();
//                try {
//                    executor.awaitTermination(10, TimeUnit.SECONDS);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//
//                System.exit(0);
//            }
//
//            ScheduledExecutorService schedulerExecutor = Executors.newScheduledThreadPool(2);
//            Callable<Process> callable = new Callable<Process>() {
//
//                @Override
//                public Process call() throws Exception {
//                    Process p = Runtime.getRuntime().exec("cmd /c start /b java -jar D:\\MovieLibrary.jar");
//                    return p;
//                }
//            };
//
//            FutureTask<Process> futureTask = new FutureTask<Process>(callable);
//
////            schedulerExecutor.submit(futureTask);
////
////            schedulerExecutor.shutdown();
////
////            System.exit(0);
//    });

    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void resultTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultTableMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_resultTableMouseMoved

    private void searchMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchMenuActionPerformed
        LibraryFtir lb = new LibraryFtir();
        lb.setVisible(true);
        lb.setLocationRelativeTo(null);
    }//GEN-LAST:event_searchMenuActionPerformed

    private void resultsFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsFilterActionPerformed
        String sql = "";

        Object obj2 = evt.getSource();

        if (obj2 == resultsFilter) {
            String fil = resultsFilter.getSelectedItem().toString();
            if (fil.equalsIgnoreCase("All")) {

                sql = "select `WAVENUMBER` AS 'Wavenumber', `BOND` AS 'Bond', `FUNCTIONAL_GROUP` AS 'Functional Group' from result";
            } else {
                sql = "select `WAVENUMBER` AS 'Wavenumber', `BOND` AS 'Bond', `FUNCTIONAL_GROUP` AS 'Functional Group' from result where (COMPOUND_CATEGORY like \"%" + fil + "%\" OR COMPOUND_TYPE like \"%" + fil + "%\" OR FUNCTIONAL_GROUP like \"%" + fil + "%\" )";
            }
            try {

                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next() == false) {

                    JOptionPane.showMessageDialog(null, "No results found!");
                } else {

                    do {
                        resultTable.setModel(DbUtils.resultSetToTableModel(rs));
                    } while (rs.next());

                }

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

    }//GEN-LAST:event_resultsFilterActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        try {
            MessageFormat header = new MessageFormat("Report");
            MessageFormat footer = new MessageFormat("Page{0,number,integer}");

            resultTable.print(JTable.PrintMode.NORMAL, header, footer);
        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(null, "Print failed!" + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void deselectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectButtonActionPerformed
        jCheckBox1.setSelected(false);
        jCheckBox2.setSelected(false);
        jCheckBox3.setSelected(false);
        jCheckBox4.setSelected(false);
        jCheckBox5.setSelected(false);
        jCheckBox6.setSelected(false);
        jCheckBox7.setSelected(false);
        jCheckBox8.setSelected(false);
        jCheckBox9.setSelected(false);
        jCheckBox10.setSelected(false);

    }//GEN-LAST:event_deselectButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        jCheckBox1.setSelected(true);
        jCheckBox2.setSelected(true);
        jCheckBox3.setSelected(true);
        jCheckBox4.setSelected(true);
        jCheckBox5.setSelected(true);
        jCheckBox6.setSelected(true);
        jCheckBox7.setSelected(true);
        jCheckBox8.setSelected(true);
        jCheckBox9.setSelected(true);
        jCheckBox10.setSelected(true);


    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        List<JCheckBox> buttons = new ArrayList<>();
        buttons.add(jCheckBox1);
        buttons.add(jCheckBox2);
        buttons.add(jCheckBox3);
        buttons.add(jCheckBox4);
        buttons.add(jCheckBox5);
        buttons.add(jCheckBox6);
        buttons.add(jCheckBox7);
        buttons.add(jCheckBox8);
        buttons.add(jCheckBox9);
        buttons.add(jCheckBox10);
        
        String selectedItem="";
        for (JCheckBox checkbox : buttons) {
            if (checkbox.isSelected()) {
                selectedItem += "\""+ checkbox.getText() + "\",";
            }
        }
        
        selectedItem = selectedItem.substring(0, selectedItem.length() - 1);
        System.out.println(selectedItem);
        
        
        try {
                String sql = "SELECT round(`WAVENUMBER`,0) AS 'Wavenumber', `BOND` AS 'Bond', `FUNCTIONAL_GROUP` AS 'Functional Group' FROM `result` WHERE COMPOUND_CATEGORY IN (" +selectedItem+")";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next() == false) {

                    JOptionPane.showMessageDialog(null, "No results found!");
                } else {

                    do {
                        resultTable.setModel(DbUtils.resultSetToTableModel(rs));
                    } while (rs.next());

                }

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
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
//            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
//            UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
//             UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//          

            UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
            ProgressPainter painter = new ProgressPainter(Color.WHITE, Color.GREEN);
            UIManager.getLookAndFeelDefaults().put("ProgressBar[Enabled+Finished].foregroundPainter", painter);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    private void update_table() {

        try {
            String sql = "select ID AS \"Index\", WAVENUMBER AS 'Wavenumber' , TRANSMITTANCE AS 'Transmittance' from input_data";

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

            JDBCXYDataset dataset = new JDBCXYDataset(conn, query1);

//            JFreeChart spec = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);
            spec = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);
            spec.setBorderVisible(false);
            spec.setBackgroundPaint(Color.white);
            spec.getXYPlot().setDomainGridlinesVisible(true);

            ChartPanel chartPanel = new ChartPanel(spec);
//            chartPanel_com.setPreferredSize(new Dimension(654, 350));
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

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void generate_spectrum_seperateFrame(String tableName) {
        try {

            String query1 = "select WAVENUMBER, TRANSMITTANCE from " + tableName;
            JDBCXYDataset dataset = new JDBCXYDataset(conn, query1);

            JFreeChart chart = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset, PlotOrientation.VERTICAL, false, true, true);
            BarRenderer renderer = null;
            XYPlot plot = null;
            renderer = new BarRenderer();
            ChartFrame frame = new ChartFrame("", chart);
            frame.setVisible(true);
            frame.setSize(650, 400);
            frame.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = frame.getSize();
            frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

            plot = chart.getXYPlot();
            NumberAxis domain = (NumberAxis) plot.getDomainAxis();
            domain.setAutoRange(true);
            domain.setInverted(true);

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
            if (dataFile != null) {
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
            }

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
                boolean m = regrexMatch.matches();

                if (!regrexMatch.matches()) {
                    ++invalid_input;
                    errorLine.add(lineNumber + 1);

                } else {
                    ++valid_input;

                }

                ++lineNumber;

            }
            br.close();

            System.out.println("Invalid inputs found!");
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

                if (line.startsWith(commentChar) | line.equals("") | line.contains("[a-zA-Z]+")) {
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
//                            System.out.println("Import rows " + i);
                        }
                        conn.commit();
                        pst.close();
                        conn.close();
                        input.close();
//                        System.out.println("Successfully imported excel to mysql table");
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

    private void uploadTxt() {

        try {

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = "";
            String[] value = null;
            String fullarrays = "";
            Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*");

            while ((line = br.readLine()) != null) {

                Matcher matcher = pattern.matcher(line);

                if (matcher.matches() || line.equals("")) {
                    //skip lines with letters and empty lines
                    continue;
                } else {

                    switch (fileType) {
                        case CSV:
                            value = line.split(",");//for cvs
                            break;
                        case TXT:
                            value = line.split("\\s+"); //white space regex
                            break;
                        case DPT:
                            value = line.split("\t"); //for DPT
                    }

                    String twoarrays = "(" + value[0].trim() + " , " + value[1].trim() + ")";
                    fullarrays = fullarrays + twoarrays + ",";

                }

            }

            fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

            String sql = "insert into input_data (WAVENUMBER , TRANSMITTANCE) values " + fullarrays;
            System.out.println(sql);

            PreparedStatement pst = null;
            pst = conn.prepareStatement(sql);
            pst.executeUpdate();

            br.close();

        } catch (Exception e) {
            System.err.println(e);
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
            smoothPanel.removeAll();
            smoothPanel.revalidate();
            smoothPanel.repaint();
//            String query2 = "select WAVENUMBER, TRANSMITTANCE from avg_data";
//            JDBCXYDataset dataset = new JDBCXYDataset(conn, query2);
            XYDataset dataset1 = createDataset(rowDataList, averagedList);

            smoothedSpec = ChartFactory.createXYLineChart("", "Wavenumber (cm-1)", "Transmittance %", dataset1, PlotOrientation.VERTICAL, false, true, true);

            smoothedSpec.setBorderVisible(false);
            smoothedSpec.getXYPlot().setDomainGridlinesVisible(true);

            ChartPanel chartPanel = new ChartPanel(smoothedSpec);

//            chartPanel_com.setPreferredSize(new Dimension(654, 350));
            chartPanel.setDomainZoomable(true);
            chartPanel.setHorizontalAxisTrace(true);
            chartPanel.setVerticalAxisTrace(true);

            BarRenderer renderer = null;
            XYPlot plot = smoothedSpec.getXYPlot();
            NumberAxis range = (NumberAxis) plot.getRangeAxis();
            range.setAutoRange(true);
            renderer = new BarRenderer();

            smoothPanel.setLayout(new java.awt.BorderLayout());
            smoothPanel.add(chartPanel, BorderLayout.CENTER);
            smoothPanel.validate();
            smoothPanel.setPreferredSize(new Dimension(654, 350));
            smoothPanel.setVisible(true);

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

                smoothPanel.removeAll();
                smoothPanel.revalidate();
                smoothPanel.repaint();

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
        changeValueText.setText(null);

        threshSlider1.setValue(2);
        numBandsText.setText(null);

        filterPassLabel.setText(null);
        smoothningSlider.setValue(1);

        specTabbedPane.setSelectedIndex(0);
        settingsTabbedPane.setSelectedIndex(0);

        //clear tables
        String sql1 = "delete from input_data";
        String sql2 = "delete from avg_data";
        String sql3 = "delete from abs_data";
        String sql4 = "delete from baseline_data";
        String sql5 = "delete from band";
        String sql6 = "delete from candidates";
        String sql7 = "delete from result";

        try {
            pst = conn.prepareStatement(sql1);
            pst.execute();

            pst2 = conn.prepareStatement(sql2);
            pst2.execute();

            pst3 = conn.prepareStatement(sql3);
            pst3.execute();

            pst4 = conn.prepareStatement(sql4);
            pst4.execute();

            pst5 = conn.prepareStatement(sql5);
            pst5.execute();

            pst6 = conn.prepareStatement(sql6);
            pst6.execute();

            pst7 = conn.prepareStatement(sql7);
            pst7.execute();

            //revalidate panels    
            specPanel.removeAll();
            specPanel.revalidate();
            specPanel.repaint();

            smoothPanel.removeAll();
            smoothPanel.revalidate();
            smoothPanel.repaint();

            baselinePanel.removeAll();
            baselinePanel.revalidate();
            baselinePanel.repaint();

            comPanel.removeAll();
            comPanel.revalidate();
            comPanel.repaint();

//            specTabbedPane.removeAll();
//            specTabbedPane.revalidate();
//            specTabbedPane.repaint();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                pst.close();
                pst2.close();
                pst3.close();
                pst4.close();
                pst5.close();
                pst6.close();
                pst7.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
//        System.out.println("All cleared");
        update_table();
        updateResult_table();

    }

    public void readFile() throws FileNotFoundException, IOException, SQLException {
        try {

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = "";
            String[] value = null;
            String fullarrays = "";
            Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*");

            while ((line = br.readLine()) != null) {

                Matcher matcher = pattern.matcher(line);

                if (matcher.matches() || line.equals("")) {
                    //skip lines with letters and empty lines
                    continue;
                } else {

                    switch (fileType) {
                        case CSV:
                            value = line.split(",");//for cvs
                            break;
                        case TXT:
                            value = line.split("\\s+"); //white space regex
                            break;
                        case DPT:
                            value = line.split("\t"); //for DPT
                    }

                    String twoarrays = "(" + value[0].trim() + " , " + value[1].trim() + ")";
                    fullarrays = fullarrays + twoarrays + ",";

                }

            }

            fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

            String sql = "insert into input_data (WAVENUMBER , TRANSMITTANCE) values " + fullarrays;

            PreparedStatement pst = null;
            pst = conn.prepareStatement(sql);
            pst.executeUpdate();

            br.close();

        } catch (Exception e) {
            System.err.println(e);
        }

        update_table();

        /* String value = " ";
        validateFileType();

        try {

            switch (fileType) {
                case CSV:
                    value = ",";//for cvs
                    break;
                case TXT:

//                    uploadTxt();
//                    value = " ";//for txt    
//                    value = "\\s+";//for txt
//                    value = "[:space:]";
                    break;
                case DPT:
                    value = "\t"; //for DPT
                }

            String qry = "LOAD DATA LOCAL INFILE '" + fileName + "' INTO TABLE input_data FIELDS TERMINATED BY '" + value + "' LINES TERMINATED BY '\\r\\n' (wavenumber, transmittance)";

            pst = conn.prepareStatement(qry);
            pst.executeUpdate();

            update_table();
        } catch (Exception e) {
            System.out.println(e);
        } finally {

            pst.close();
        }

        //Delete out of range data 0
        try {
            String qrydel = "DELETE FROM input_data WHERE wavenumber < 399 ";
            pst = conn.prepareStatement(qrydel);
            pst.executeUpdate();
            update_table();
        } catch (Exception e) {
            System.err.println(e);
        } finally {

            pst.close();
        }*/
    }

    public void readAbsFile() throws FileNotFoundException, IOException, SQLException {

        try {

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = "";
            String[] value = null;
            String fullarrays = "";
            Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*");

            while ((line = br.readLine()) != null) {

                Matcher matcher = pattern.matcher(line);

                if (matcher.matches() || line.equals("")) {
                    //skip lines with letters and empty lines
                    continue;
                } else {

                    switch (fileType) {
                        case CSV:
                            value = line.split(",");//for cvs
                            break;
                        case TXT:
                            value = line.split("\\s+"); //white space regex
                            break;
                        case DPT:
                            value = line.split("\t"); //for DPT
                    }

                    String twoarrays = "(" + value[0].trim() + " , " + value[1].trim() + ")";
                    fullarrays = fullarrays + twoarrays + ",";

                }

            }

            fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

            String sql = "insert into abs_data (WAVENUMBER , TRANSMITTANCE) values " + fullarrays;

            String qrydel = "DELETE FROM input_data WHERE wavenumber = 0.00000000";
            pst = conn.prepareStatement(qrydel);
            pst.executeUpdate();

            PreparedStatement pst = null;
            pst = conn.prepareStatement(sql);
            pst.executeUpdate();

            br.close();

        } catch (Exception e) {
            System.err.println(e);
        }

        /*String value = " ";
//        validateFileType();

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

            String qry = "LOAD DATA LOCAL INFILE '" + fileName + "' INTO TABLE abs_data FIELDS TERMINATED BY '" + value + "' LINES TERMINATED BY '\\r\\n' (wavenumber, transmittance)";

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
        }*/
    }

    public void createDuel(XYDataset set1, XYDataset set2, JPanel panel) {

        panel.removeAll();
        panel.revalidate();
        panel.repaint();

        XYPlot plot = new XYPlot();

        XYDataset collection1 = set1;
        peakset = set1;
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
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);	// Lines only
        renderer2.setSeriesPaint(0, Color.blue);

        ValueAxis domain2 = new NumberAxis("");
        ValueAxis range2 = new NumberAxis("");

        plot.setDataset(1, collection2);
        plot.setRenderer(1, renderer2);
        plot.setDomainAxis(1, domain2);
        plot.setRangeAxis(1, range2);
        xyplotT = plot;
//        plot.mapDatasetToDomainAxis(0, 1);
//        plot.mapDatasetToRangeAxis(0, 1);
        domain2.setAutoRange(true);
        domain2.setInverted(true);
        domain2.setVisible(false);
        range2.setVisible(false);

//        plot.mapDatasetToDomainAxis(1, 1);
//        plot.mapDatasetToRangeAxis(1, 1);
        duelchart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chartPanel_com = new ChartPanel(duelchart);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(chartPanel_com, BorderLayout.CENTER);
        panel.validate();
        panel.setPreferredSize(new Dimension(654, 350));
        panel.setVisible(true);

//        duelchart.setAutoRangeIncludesZero(false);
        //to retain zoom
//        chartPanel_com.setMouseWheelEnabled(true);
//        chartPanel_com.setHorizontalAxisTrace(true);
//        chartPanel_com.setVerticalAxisTrace(true);
        crosshairOverlay = new CrosshairOverlay();
        xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        xCrosshair.setLabelVisible(false);
        yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair.setLabelVisible(false);
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);

        pointer = new XYPointerAnnotation("", 0, 0, 7.0 * Math.PI / 4.0);
        pointer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pointer.setTipRadius(3.0);
        pointer.setBaseRadius(15.0);
        pointer.setPaint(Color.blue);
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        pointer.setBackgroundPaint(Color.yellow);

        pointer2 = new XYPointerAnnotation("", 0, 0, 7.0 * Math.PI / 4.0);
        pointer2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pointer2.setTipRadius(3.0);
        pointer2.setBaseRadius(15.0);
        pointer2.setPaint(Color.blue);
        pointer2.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        pointer2.setBackgroundPaint(new Color(255, 255, 255));
//        pointer.setBackgroundPaint(new Color(180, 180, 180, 180));

//        chartPanel_com.addChartMouseListener(new ThisMouseListener() {
//        });
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);

        chartPanel_com.addChartMouseListener(new ChartMouseListener() {

//            
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
//                ListSelectionModel model = resultTable.getSelectionModel();
                int f, l;
                ChartEntity entity = event.getEntity();

                resultTable.getSelectionModel().clearSelection();

                if (entity != null && entity instanceof XYItemEntity) {
                    XYItemEntity ent = (XYItemEntity) entity;

                    int sindex = ent.getSeriesIndex();
                    int iindex = ent.getItem();

                    int x = (int) Math.round(set1.getXValue(sindex, iindex));

//                    System.out.println("x = " + x);
//                    System.out.println("y = " + set1.getYValue(sindex, iindex));
                    ListSelectionModel model = resultTable.getSelectionModel();
                    model.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                    ArrayList<Integer> indices = new ArrayList<Integer>();
                    try {

                        for (int i = 0; i < resultTable.getRowCount(); i++) {

                            double d = Double.parseDouble(String.valueOf(resultTable.getValueAt(i, 0)));

                            if (Math.abs(d - x) < 0.0000001) {

                                indices.add(i);

                            }

                        }
                        f = Collections.min(indices);
                        l = Collections.max(indices);
                        model.setSelectionInterval(f, l);
                        resultTable.scrollRectToVisible(resultTable.getCellRect(f, 0, true));

                    } catch (NoSuchElementException e) {
                        System.err.println(e);
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

//                System.out.println(x0 + "   " + y0);
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

//                    chartPanel_com.setMouseWheelEnabled(true);
//                    chartPanel_com.setHorizontalAxisTrace(true);
//                    chartPanel_com.setVerticalAxisTrace(true);
                }

            }

        }
        );

        resultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

    public void create3charts(XYDataset set1, XYDataset set2, JPanel panel) {

        panel.removeAll();
        panel.revalidate();
        panel.repaint();

        XYPlot plot = new XYPlot();

        XYDataset collection1 = set1;
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);	// line only
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
        charts3 = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
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

        XYPlot xyplot = new XYPlot();

        XYDataset collection1 = set1;
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);	// Shapes only
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer1.setSeriesPaint(0, Color.magenta);
        ValueAxis domain1 = new NumberAxis("Wavenumber (cm-1)");
        ValueAxis range1 = new NumberAxis("Transmittance %");
        domain1.setAutoRange(true);
        domain1.setInverted(true);
        range1.setAutoRange(true);

        xyplot.setDataset(0, collection1);
        xyplot.setRenderer(0, renderer1);
        xyplot.setRenderer(0, xylineandshaperenderer1);
        xyplot.setDomainAxis(0, domain1);
        xyplot.setRangeAxis(0, range1);

        XYDataset collection2 = set2;
        XYItemRenderer renderer2 = new XYLineAndShapeRenderer(true, false);	// Lines only
        ValueAxis domain2 = new NumberAxis("");
        ValueAxis range2 = new NumberAxis("");
        XYLineAndShapeRenderer xylineandshaperenderer2 = new XYLineAndShapeRenderer(true, false);
        xylineandshaperenderer2.setSeriesPaint(0, Color.blue);

        xyplot.setDataset(1, collection2);
        xyplot.setRenderer(1, renderer2);
        xyplot.setRenderer(1, xylineandshaperenderer2);
        xyplot.setDomainAxis(1, domain2);
        xyplot.setRangeAxis(1, range2);

//        plot.mapDatasetToDomainAxis(0, 1);
//        plot.mapDatasetToRangeAxis(0, 1);
        domain2.setAutoRange(true);
        domain2.setInverted(true);
        domain2.setVisible(false);
        range2.setVisible(false);

//        plot.mapDatasetToDomainAxis(1, 1);
//        plot.mapDatasetToRangeAxis(1, 1);
        smoothed_chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
        ChartPanel chartPanel = new ChartPanel(smoothed_chart);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.validate();
        panel.setPreferredSize(new Dimension(654, 350));
        panel.setVisible(true);

//        ChartRenderingInfo chartInfo = new ChartRenderingInfo();
//        chart.createBufferedImage(chartConfig.width, chartConfig.height, chartInfo);
//        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        //to get clicked point
//        chartPanel_com.addChartMouseListener(new ChartMouseListener() {
//            @Override
//            public void chartMouseClicked(ChartMouseEvent event) {
//                int mouseX = event.getTrigger().getX();
//                int mouseY = event.getTrigger().getY();
//                System.out.println("x = " + mouseX + ", y = " + mouseY);
//                Point2D p = chartPanel_com.translateScreenToJava2D(new Point(mouseX, mouseY));
//                XYPlot plot = (XYPlot) chart.getPlot();
//                Rectangle2D plotArea = chartPanel_com.getScreenDataArea();
//                ValueAxis domainAxis = plot.getDomainAxis();
//                RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
//                ValueAxis rangeAxis = plot.getRangeAxis();
//                RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
//                double chartX = domainAxis.java2DToValue(p.getX(), plotArea, domainAxisEdge);
//                double chartY = rangeAxis.java2DToValue(p.getY(), plotArea, rangeAxisEdge);
//                System.out.println("Chart: x = " + chartX + ", y = " + chartY);
//            }
//        chartPanel_com.addChartMouseListener(new ChartMouseListener() {
//            @Override
//            public void chartMouseClicked(ChartMouseEvent e) {
//
//                xyplot.setDomainCrosshairVisible(true);
//                xyplot.setDomainCrosshairLockedOnData(true);
//                xyplot.setRangeCrosshairVisible(true);
//                xyplot.setRangeCrosshairLockedOnData(true);
//                System.out.println(" Clicked on " + xyplot.getDomainCrosshairValue() + "   ,   " + xyplot.getRangeCrosshairValue());
//                xyplot.setDomainCrosshairVisible(false);
//                xyplot.setDomainCrosshairLockedOnData(false);
//                xyplot.setRangeCrosshairVisible(false);
//                xyplot.setRangeCrosshairLockedOnData(false);
//            }
//
//            @Override
//            public void chartMouseMoved(ChartMouseEvent cme) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        });
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

    private XYDataset createInputDataset() throws SQLException {
        String query1 = "select WAVENUMBER, TRANSMITTANCE AS 'Original Spectrum' from input_data";
        input_dataset = new JDBCXYDataset(conn, query1);

        return input_dataset;
    }

    private XYDataset createSmoothedDataset() throws SQLException {

        String query1 = "select WAVENUMBER, TRANSMITTANCE AS 'Smoothed Spectrum' from avg_data";
        smoothed_dataset = new JDBCXYDataset(conn, query1);
        return smoothed_dataset;

    }

    private XYDataset createBaselineDataset() throws SQLException {

        String query1 = "select WAVENUMBER, TRANSMITTANCE AS 'Baseline Corrected Spectrum' from baseline_data";
        baseline_dataset = new JDBCXYDataset(conn, query1);
        return baseline_dataset;

    }

    public void showDefaultTrendLine() {
        bc = new RegressionBL();
        bc.drawRegressionLine(spec, input_dataset, lowerBoundX, upperBoundX);//good to get default trend line

    }

    public void showValleys(String table) {

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

            v2 = new ValleysLocator(table);
            v2.qBLdata();
            v2.cal_1storder_derivative(v1.getBaselineCorrectedPointList());
            v2.cal_2ndorder_derivative(v1.getBaselineCorrectedPointList());
            v2.findCandidateSet();
            v2.evaluateNeighbourhood();

            createDuel(createValleyDataset(v2.getCandidates()), createBaselineDataset(), comPanel);
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void performSmooth() {
        int sliderValue = smoothningSlider.getValue();

        if (checkValidity() && sliderValue != 0) {

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Select...")) {
                JOptionPane.showMessageDialog(null, "Algorithmn Invalid!", "Error!", JOptionPane.WARNING_MESSAGE);
            }

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
                algorithm = 2;
                ls = SlidingAvgSmoothSingleton.getInstance();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        ls.cal_5point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                filterPassLabel.setText(Integer.toString(ls.count));
            }
            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Triangular Smoothing")) {
                algorithm = 3;
                tri = TriangularSmoothSingleton.getInstance();
                for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                    tri.cal_5point_avg();
//                                createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
                    try {
                        combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                filterPassLabel.setText(Integer.toString(ls.count));
            }
            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Savitzky-Golay Filter")) {
                algorithm = 4;
                sgf = SavitzkyGolayFilterSingleton.getInstance();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        sgf.applyFilter_3points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (sevenpoints.isSelected()) {
                    points = 7;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        sgf.applyFilter_7points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else if (ninepoints.isSelected()) {
                    points = 9;
                    for (int i = 0; i < sliderValue - sliderPreviousValue; i++) {
                        sgf.applyFilter_9points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                filterPassLabel.setText(Integer.toString(sgf.count));
            }
            showValleys("avg_data");
        }

    }

    private void reverseSmooth() {

        int sliderValue = smoothningSlider.getValue();

        if (checkValidity() && sliderValue != 0) {

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Default")) {
                algorithm = 1;
                DefaultSmooth ds1 = new DefaultSmooth();

                for (int i = 0; i < sliderValue; i++) {
                    ds1.general_avg_algorithm_3point(sliderValue);
                }
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                filterPassLabel.setText(Integer.toString(sliderValue));
            }

            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
                algorithm = 2;
                SlidingAvgSmoothSingleton s1 = new SlidingAvgSmoothSingleton();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue; i++) {
                        s1.cal_3point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                tri = TriangularSmoothSingleton.getInstance();
                for (int i = 0; i < sliderValue; i++) {
                    tri.cal_5point_avg();
//                                createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
                    try {
                        combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                filterPassLabel.setText(Integer.toString(ls.count));
            }
            if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Savitzky-Golay Filter")) {
                algorithm = 4;
                sgf.reset();
                sgf = SavitzkyGolayFilterSingleton.getInstance();
                if (threepoints.isSelected()) {
                    points = 3;
                    for (int i = 0; i < sliderValue; i++) {
                        sgf.applyFilter_3points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                        try {
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
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
                            combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                filterPassLabel.setText(Integer.toString(sgf.count));
            }
            showValleys("avg_data");
        }
    }

    public void performSingeTimeSmooth() {

        chartPanel_com.restoreAutoBounds();
//        System.out.println("Auto bounds restored");

        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Select...")) {
            JOptionPane.showMessageDialog(null, "Algorithmn Invalid!", "Error!", JOptionPane.WARNING_MESSAGE);
        }

        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Unweighted Sliding Average ")) {
            algorithm = 2;
            SlidingAvgSmooth ls = new SlidingAvgSmooth();
            if (threepoints.isSelected()) {
                points = 3;

                ls.cal_3point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (fivepoints.isSelected()) {
                points = 5;

                ls.cal_5point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (sevenpoints.isSelected()) {
                points = 7;

                ls.cal_7point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (ninepoints.isSelected()) {
                points = 9;

                ls.cal_9point_avg();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Triangular Smoothing")) {
            algorithm = 3;
            TriangularSmooth tri = new TriangularSmooth();

            tri.cal_5point_avg();
//                                createSmoothed_spectrum(tri.originalPoints, tri.smoothedPoints);
            try {
                combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
            } catch (SQLException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (smAlgoCombo.getSelectedItem().toString().equalsIgnoreCase("Savitzky-Golay Filter")) {
            algorithm = 4;
            SavitzkyGolayFilter sgf = new SavitzkyGolayFilter();
            if (threepoints.isSelected()) {
                points = 3;

                sgf.applyFilter_3points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (fivepoints.isSelected()) {
                points = 5;

                sgf.applyFilter_5points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (sevenpoints.isSelected()) {
                points = 7;

                sgf.applyFilter_7points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (ninepoints.isSelected()) {
                points = 9;

                sgf.applyFilter_9points();
//                                    createSmoothed_spectrum(ls.originalPoints, ls.smoothedPoints);//old
                try {
                    combined2Charts(input_dataset, createSmoothedDataset(), smoothPanel);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        measurechange();
        showValleys("avg_data");

    }

    private void reverseThresh() {

        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1 = new ValleysLocator("baseline_data");
            v1.qBLdata();
            v1.cal_1storder_derivative(v1.getBaselineCorrectedPointList());
            v1.cal_2ndorder_derivative(v1.getBaselineCorrectedPointList());
            v1.findCandidateSet();
            v1.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
            numBandsText.setText(String.valueOf(v1.getCandidates().size()));
            createDuel(createValleyDataset(v1.getCandidates()), createBaselineDataset(), comPanel);
            updateCandidateTable(v1.getCandidates());

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end

    }

    private void performThresh() {
        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
//            v2.qBLdata();
//            v2.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
//            createDuel(createValleyDataset(v2.getCandidates()), createBaselineDataset(), comPanel);

            v2 = new ValleysLocator("baseline_data");
            v2.qBLdata();
            v2.cal_1storder_derivative(v1.getBaselineCorrectedPointList());
            v2.cal_2ndorder_derivative(v1.getBaselineCorrectedPointList());
            v2.findCandidateSet();
            v2.evaluateNeighbourhood();
            v2.discardBelowThresh(threshCurrent, lowerBoundT, upperBoundT);
            numBandsText.setText(String.valueOf(v2.getCandidates().size()));

            createDuel(createValleyDataset(v2.getCandidates()), createBaselineDataset(), comPanel);
            updateCandidateTable(v2.getCandidates());

        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }//end
    }

    private void reverseNoiseFilter() {

        comPanel.removeAll();
        comPanel.revalidate();
        comPanel.repaint();

        try {
            v1 = new ValleysLocator("avg_data");
            v1.cal_1storder_derivative(v1.getSmoothedPointList());
            v1.cal_2ndorder_derivative(v1.getSmoothedPointList());
            v1.findCandidateSet();
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
            v1 = new ValleysLocator("avg_data");
            v1.cal_1storder_derivative(v1.getSmoothedPointList());
            v1.cal_2ndorder_derivative(v1.getSmoothedPointList());
            v1.findCandidateSet();
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

    //m,c of the baseline equation
    public static double c = 0;
    public static double m = 0;

    public static double getC() {
        return c;
    }

    public static void setC(double c) {
        MainWindow.c = c;
    }

    public static double getM() {
        return m;
    }

    public static void setM(double m) {
        MainWindow.m = m;
    }

    private void getBaselineEquation() {
        try {

//            System.out.println(" intpol.getY()  " + intpol.getY());
            //select only mode value of transmittance data
            String query1 = "select WAVENUMBER, TRANSMITTANCE from baseline_data where transmittance = " + intpol.getY();
            JDBCXYDataset dataset = new JDBCXYDataset(conn, query1);

            RegressionBL bc1 = new RegressionBL();
            System.out.println("===== Baseline Equation =====");
            bc1.drawRegressionLine(duelchart, dataset, lowerBoundX, upperBoundX);
            System.out.println("=============================");
            c = bc1.getC1();
            m = bc1.getM1();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //calculate perpendicular distance from peak point to the line  
    private void ragneMarker() {
        ValueMarker marker = null;
        plot = duelchart.getXYPlot();
        plot.clearRangeMarkers();

        double thresholdValue = 100 - threshSlider1.getValue();

        //horizontal line drawer
        marker = new ValueMarker(thresholdValue);
        marker.setLabel("Threshold Level");
        marker.setLabelAnchor(RectangleAnchor.CENTER);
        marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
        marker.setPaint(Color.YELLOW);
        plot.addRangeMarker(marker);
    }

    public void setBaseline() {
        bltab = true;
        specTabbedPane.setSelectedIndex(2);
        try {
            if (baselineMethodCombo.getSelectedItem().toString().equalsIgnoreCase("Select...")) {
                JOptionPane.showMessageDialog(null, "Invalid point connecting method!", "Error!", JOptionPane.WARNING_MESSAGE);
            }
            if (baselineMethodCombo.getSelectedItem().toString().equalsIgnoreCase("Regression")) {
                bc = new RegressionBL();
                create3charts(createSmoothedDataset(), createValleyDataset(v1.getPeaktops()), baselinePanel);

                if (lineCheckBox.isSelected()) {

                    bc.drawRegressionLine(charts3, createValleyDataset(v1.getPeaktops()), lowerBoundX, upperBoundX);
                }
                if (splineCheckBox.isSelected()) {

                    bc.drawPolynomialFit(charts3, createValleyDataset(v1.getPeaktops()), lowerBoundX, upperBoundX);
                }
                combined2Charts(createDataset(bc.getLinePoints(), "Baseline"), createSmoothedDataset(), baselinePanel);
                //result
                combined2Charts(createDataset(bc.getDifferencewithLine(), "Baseline Corrected"), createSmoothedDataset(), comPanel);
                showValleys("baseline_data");
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

                combined2Charts(createDataset(mapi, "Baseline"), createSmoothedDataset(), baselinePanel);
                //result
                combined2Charts(createDataset(intpol.getDifferencewithLine(), "Baseline Corrected"), createSmoothedDataset(), comPanel);
                showValleys("baseline_data");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

//        baselineButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                nextButton2.setEnabled(true);
//            }
//        });
    }

    public void updateCandidateTable(NavigableMap<BigDecimal, BigDecimal> list) {
        clearCandidateTable();
        String fullarrays = "";
        for (BigDecimal wavelength : list.keySet()) {

            BigDecimal key = wavelength;
            BigDecimal value = list.get(wavelength);
            String twoarrays = "(" + key + " , " + value + ")";
            fullarrays = fullarrays + twoarrays + ",";

        }
        fullarrays = fullarrays.substring(0, fullarrays.length() - 1);

        String sql = "INSERT INTO candidates (wavenumber,transmittance)  VALUES " + fullarrays;
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

    private void clearCandidateTable() {

        String sql1 = "delete from candidates";
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

    private void updateResult_table() {

//        TableFromDatabase();
        try {

//            String sql = "select `WAVENUMBER` AS 'Wavenumber', `BOND` AS 'Bond', `FUNCTIONAL_GROUP` AS 'Functional Group', `COMPOUND_TYPE` AS 'Compound Type' , `COMPOUND_CATEGORY` AS 'Compound Category' from result";
            String sql = "select round(`WAVENUMBER`,0) AS 'Wavenumber', `BOND` AS 'Bond', `FUNCTIONAL_GROUP` AS 'Functional Group' from result";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            resultTable.setModel(DbUtils.resultSetToTableModel(rs));

//            CheckBoxWrapperTableModel wrapperModel = new CheckBoxWrapperTableModel(DbUtils.resultSetToTableModel(rs), "Select");
//            resultTable.setModel( wrapperModel );
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                rs.close();
                pst.close();
            } catch (Exception e) {
                System.err.println(e);
            }
        }

    }

    public void TableFromDatabase() {
        Vector<Object> columnNames = new Vector<Object>();
        Vector<Object> data = new Vector<Object>();

        try {

            //  Read data from a table
            String sql = "select `WAVENUMBER` AS 'Wavenumber', `BOND` AS 'Bond', `FUNCTIONAL_GROUP` AS 'Functional Group' from result";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            //  Get column names
            for (int i = 1; i <= columns; i++) {
                columnNames.addElement(md.getColumnName(i));
            }
            columnNames.addElement(" ");

            //  Get row data
            while (rs.next()) {
                Vector row = new Vector(columns + 1);
                for (int i = 1; i <= columns; i++) {
                    row.addElement(rs.getObject(i));
                }
                row.addElement(new JCheckBox("", false));
                data.addElement(row);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        //  Create table with database data
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public Class getColumnClass(int column) {
                for (int row = 0; row < getRowCount(); row++) {
                    Object o = getValueAt(row, column);

                    if (o != null) {
                        return o.getClass();
                    }
                }

                return Object.class;
            }
        };

        resultTable.setModel(model);
//        JScrollPane scrollPane = new JScrollPane( resultTable );
//        getContentPane().add( scrollPane );
//
//        JPanel buttonPanel = new JPanel();
//        getContentPane().add( buttonPanel, BorderLayout.SOUTH );
    }

    private void uploadFile() {
        //1. select file and validate 
//        fileChooser();

        if (validateFileType()) {

            try {

                String[] choices = {"Transmittance", "Absorbance"};
                String input = (String) JOptionPane.showInputDialog(null, "Select input type",
                        "Input type", JOptionPane.QUESTION_MESSAGE, null, // Use
                        // default
                        // icon
                        choices, // Array of choices
                        choices[0]); // Initial choice

                if (input.equals("Transmittance")) {

                    //2.read file
                    readFile();
                    reduceMinfromAllY();

                    //create spectrum
                    generate_spectrum(specPanel, "input_data"); //original spectrum
//                    generate_spectrum_seperateFrame("input_data");
                }

                if (input.equals("Absorbance")) {

                    readAbsFile();
                    AbsToTrans ab = new AbsToTrans();
                    reduceMinfromAllY();
                    update_table();
                    //create spectrum
                    generate_spectrum(specPanel, "input_data"); //original spectrum
                }

                //3.run default smoothing
                {
                    sg = new SavitzkyGolayFilter();
                    sg.applyFilter_3points();
                    measurechange(); //change from original value = change/no.of points

                    combined2Charts(createInputDataset(), createSmoothedDataset(), smoothPanel);

                }
                //4.draw default baseline
                {
                    v1 = new ValleysLocator("avg_data");
                    v1.cal_1storder_derivative(v1.getSmoothedPointList());
                    v1.cal_2ndorder_derivative(v1.getSmoothedPointList());
                    v1.findCandidateSet();
                    v1.evaluateNeighbourhood();
//                    v1.discardBelowThresh(2, lowerBoundT, upperBoundT);
//                    numBandsLabel.setText(String.valueOf(v1.getCandidates().size()));

                    SortedMap<BigDecimal, BigDecimal> mapi = null;
                    intpol = new InterpolatedBL();

                    mapi = intpol.linearInterp(createValleyDataset(v1.getPeaktops()), v1.getPeaktops().size());

                    combined2Charts(createDataset(mapi, "Baseline"), createSmoothedDataset(), baselinePanel);
//                        combined2Charts(createDataset(intpol.getDifferencewithLine(), "Baseline Corrected"), createSmoothedDataset(), comPanel);
                    combined2Charts(createDataset(intpol.getDifferencewithLine(), "Baseline Corrected"), input_dataset, comPanel);
                }

                //Valleys graph plot
                {

                    showValleys("avg_data");
                }

                //get the baseline equation
                getBaselineEquation();

            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {

            JOptionPane.showMessageDialog(null, "Invalid file format!", "Error", JOptionPane.ERROR_MESSAGE);

        }

    }

    //min value is reduced from all Y values to fix scale
    private void reduceMinfromAllY() {
        BigDecimal min = null;
        ResultSet rs = null;
        PreparedStatement pst1 = null;

        String sql1 = "select min(transmittance) as min from input_data";
        try {
            pst = conn.prepareStatement(sql1);
            rs = pst.executeQuery(sql1);

            if (rs.next()) {
                min = rs.getBigDecimal("min");
            }

            String sql2 = "UPDATE `input_data` SET TRANSMITTANCE = TRANSMITTANCE  - " + min;
            pst1 = conn.prepareStatement(sql2);
            pst1.executeUpdate();

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                pst.close();
                pst1.close();

            } catch (Exception e) {
                System.err.println(e);
            }
        }

    }

    //the change of original value after smoothing 
    private void measurechange() {
        BigDecimal result = null;
        ResultSet rs = null;

        String sql1 = "SELECT sum(abs(input_data.TRANSMITTANCE - avg_data.TRANSMITTANCE))/count(input_data.TRANSMITTANCE - avg_data.TRANSMITTANCE) as result FROM input_data INNER JOIN avg_data ON input_data.WAVENUMBER = avg_data.WAVENUMBER ";
        try {
            pst = conn.prepareStatement(sql1);
            rs = pst.executeQuery(sql1);

            if (rs.next()) {
                result = rs.getBigDecimal("result");
            }
            double c = result.doubleValue() * 100;
//            System.out.println(c);

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(4);
//            System.out.println(df.format(c));

            changeValueText.setText(String.valueOf(df.format(c)));

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                pst.close();

            } catch (Exception e) {
                System.err.println(e);
            }
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup algorithmMenu;
    private javax.swing.JComboBox<String> baselineMethodCombo;
    private javax.swing.JPanel baselinePanel;
    private javax.swing.ButtonGroup blConnectionMenu;
    private javax.swing.ButtonGroup blMethodMenu;
    private javax.swing.ButtonGroup blmethodButtonGroup;
    private javax.swing.JTextField changeValueText;
    private javax.swing.JButton clearButton;
    private javax.swing.JPanel comPanel;
    private javax.swing.JCheckBox cubicSplineCheckBox;
    public static javax.swing.JTable dataTable;
    private javax.swing.JButton deselectButton;
    private javax.swing.JMenu displayMenu;
    private javax.swing.JMenu editMenu;
    private javax.swing.JTextField filePathText;
    private javax.swing.JLabel filterPassLabel;
    private javax.swing.JRadioButton fivepoints;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem10;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem11;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem4;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem5;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem6;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem7;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem8;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JCheckBox lineCheckBox;
    private javax.swing.JLabel loadingText;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JButton nextButton1;
    private javax.swing.JButton nextButton2;
    private javax.swing.JRadioButton ninepoints;
    private javax.swing.JTextField numBandsText;
    private javax.swing.JButton okButton;
    private javax.swing.JButton openButton;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JButton peakButton;
    private javax.swing.ButtonGroup pointsMenu;
    private javax.swing.ButtonGroup pointsbuttonGroup;
    private javax.swing.JButton predictButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton resetSmoothButton;
    public javax.swing.JTable resultTable;
    private javax.swing.JComboBox<String> resultsFilter;
    private javax.swing.JPanel resultsPanel;
    private javax.swing.JButton searchDatabaseButton;
    private javax.swing.JMenuItem searchMenu;
    private javax.swing.JSplitPane sectionSplitPane;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JTabbedPane settingsTabbedPane;
    private javax.swing.JRadioButton sevenpoints;
    private javax.swing.JComboBox<String> smAlgoCombo;
    public javax.swing.JPanel smoothPanel;
    private javax.swing.JButton smootheSelection;
    private javax.swing.JSlider smoothningSlider;
    private javax.swing.JPanel specPanel;
    private javax.swing.JSplitPane specSplitPane;
    private javax.swing.JTabbedPane specTabbedPane;
    private javax.swing.JCheckBox splineCheckBox;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JRadioButton threepoints;
    private javax.swing.JSlider threshSlider1;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables

}
