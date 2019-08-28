/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.print;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 *
 * @author Pramuditha Buddhini
 */
public class PrintableWrapper implements Printable {

    private Printable delegate;
    private int offset;

    public PrintableWrapper(Printable delegate, int offset) {
        this.offset = offset;
        this.delegate = delegate;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        return delegate.print(graphics, pageFormat, pageIndex - offset);
    }

   
}
