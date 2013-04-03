package org.openxdata.markup.ui;

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 3/27/13
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

public class MsgConsole implements Runnable {
    JTextArea displayPane;
    BufferedReader reader;

    private MsgConsole(JTextArea displayPane, PipedOutputStream pos) {
        this.displayPane = displayPane;

        try {
            PipedInputStream pis = new PipedInputStream(pos);
            reader = new BufferedReader(new InputStreamReader(pis));
        } catch (IOException e) {
        }
    }

    public void run() {
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                displayPane.append(line + "\n");
                displayPane.setCaretPosition(displayPane.getDocument().getLength());
            }

            System.err.println("im here");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null,
                    "Error redirecting output : " + ioe.getMessage());
        }
    }

    public static void redirectOutput(JTextArea displayPane) {
        MsgConsole.redirectOut(displayPane);
        MsgConsole.redirectErr(displayPane);
    }

    public static void redirectOut(JTextArea displayPane) {
        PipedOutputStream pos = new PipedOutputStream();
        System.setOut(new PrintStream(pos, true));

        MsgConsole console = new MsgConsole(displayPane, pos);
        new Thread(console).start();
    }

    public static void redirectErr(JTextArea displayPane) {
        PipedOutputStream pos = new PipedOutputStream();
        System.setErr(new PrintStream(pos, true));

        MsgConsole console = new MsgConsole(displayPane, pos);
        new Thread(console).start();
    }

    public static void init(Component comp) {
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        JFrame frame = new JFrame("Output");

        frame.setLocation(comp.getX(),comp.getY()+comp.getHeight());
        frame.getContentPane().add(scrollPane);
        frame.setSize(comp.getWidth(), 100);
        frame.setVisible(true);

        MsgConsole.redirectOutput(textArea);

    }
}