/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msc.ftir.library;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Pramuditha Buddhini
 */
public class Test {

    public Test() {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JTextField f = new JTextField(10);

        AutoSuggestor autoSuggestor = new AutoSuggestor(f, frame, null, Color.WHITE.brighter(), Color.BLUE, Color.RED, 0.75f) {
            @Override
            boolean wordTyped(String typedWord) {

                //create list for dictionary this in your case might be done via calling a method which queries db and returns results as arraylist
                ArrayList<String> words = new ArrayList<>();
                words.add("hello");
                words.add("heritage");
                words.add("happiness");
                words.add("goodbye");
                words.add("cruel");
                words.add("car");
                words.add("war");
                words.add("will");
                words.add("world");
                words.add("wall");


                setDictionary(words);
                //addToDictionary("bye");//adds a single word

                return super.wordTyped(typedWord);//now call super to check for any matches against newest dictionary
            }
        };

        JPanel p = new JPanel();

        p.add(f);

        frame.add(p);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test();
            }
        });
    }
}
