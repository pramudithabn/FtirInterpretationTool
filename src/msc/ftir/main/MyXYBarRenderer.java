/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.main;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Pramuditha Buddhini
 */
class MyXYBarRenderer extends XYLineAndShapeRenderer {

    public MyXYBarRenderer() {
        super();
        
    }

    public Paint getItemPaint(int row, int column) {
        // here we assume we're working with the primary dataset
            
        
            
            return Color.yellow;
        
    }
}
