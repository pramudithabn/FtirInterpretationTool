/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Pramuditha Buddhini
 */
public class TableModel extends AbstractTableModel {

    private List ColumnHeader;
    private List tableData;
    private List rowData;

    private int totalcolumn;

    public TableModel(ResultSet rs) {

        try {

            ResultSetMetaData meta = rs.getMetaData();

            totalcolumn = meta.getColumnCount();

            ColumnHeader = new ArrayList(totalcolumn);

            tableData = new ArrayList();

            for (int i = 1; i <= totalcolumn; i++) {
                ColumnHeader.add(meta.getColumnName(i));

            }
        } catch (Exception e) {

            System.out.println(e);
        }

        SwingWorker<Boolean, List<Object>> worker = new SwingWorker<Boolean, List<Object>>() {

            @Override
            protected Boolean doInBackground() throws Exception {

                while (rs.next()) {

                    rowData = new ArrayList(totalcolumn);
                    for (int i = 1; i <= totalcolumn; i++) {
                        rowData.add(rs.getObject(i));
                    }
                    publish(rowData);

                }

                return true;

            }

            @Override
            protected void process(List chunks) {
                tableData.add(chunks);

            }

            @Override
            protected void done() {
                try {
                    Boolean status = get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        };

        worker.execute();
    }// constructor end

    @Override
    public int getColumnCount() {

        return ColumnHeader.size();
    }

    public String getColumnName(int columnIndex) {
        return (String) ColumnHeader.get(columnIndex);

    }

    @Override
    public int getRowCount() {

        return tableData.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        List rowData2 = (List) tableData.get(rowIndex);

        return rowData2.get(columnIndex);
    }

}
