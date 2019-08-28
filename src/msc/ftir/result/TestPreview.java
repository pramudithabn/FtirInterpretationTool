/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.result;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.*;
import java.awt.print.*;
import java.text.MessageFormat;
import javax.print.attribute.*;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import javax.swing.JTable.PrintMode;

/**
 *
 * @author Pramuditha Buddhini
 */
class TestPreview extends JFrame implements ActionListener {
    PrinterJob printerJob = PrinterJob.getPrinterJob();
    HashPrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
    JTable table = null;
 
    public TestPreview() {
        super("Test of Print Preview");
        getContentPane().add(createTable());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setVisible(true);
    }
 
    private JPanel createTable() {
       String val[][] = {
            {"A", "Test 01", "A", "Test 01"}, 
            
        };
 
        String headers[] = {"A", "B", "C", "D"};
        table = new JTable(val, headers);
        JButton previewButton = new JButton("Preview Table");
        previewButton.addActionListener(this);
        JPanel panel = new JPanel(new BorderLayout()), top = new JPanel(new FlowLayout());
        top.add(previewButton);
        panel.add(top, "North"); 
        panel.add(new JScrollPane(table), "Center");
        return panel;
    }
 
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Preview Table"))
            attributes.add(new MediaPrintableArea(6, 6, 198, 278, MediaPrintableArea.MM)); // A4: 210x297mm
            attributes.add(Sides.DUPLEX);
            attributes.add(OrientationRequested.LANDSCAPE);
            new PrintPreview(
                table.getPrintable(PrintMode.FIT_WIDTH, null, new MessageFormat("Page {0}") ), 
                printerJob.getPageFormat(attributes)
            );
    }
 
    public static void main(String arg[]) {
        new TestPreview();
    }
}
