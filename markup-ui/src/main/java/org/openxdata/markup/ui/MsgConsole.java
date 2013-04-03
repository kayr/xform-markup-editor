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
import javax.swing.*;

public class MsgConsole implements Runnable {
    private JTextArea displayPane;
    private BufferedReader reader;
    private PrintStream oldOut;

    private MsgConsole(JTextArea displayPane, PipedOutputStream pos, PrintStream oldOut) {
        this.displayPane = displayPane;
        this.oldOut = oldOut;

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
                if (!displayPane.isShowing())
                    displayPane.setVisible(true);
                displayPane.append(line + "\n");
                displayPane.setCaretPosition(displayPane.getDocument().getLength());

                oldOut.println(line);

                Window win = SwingUtilities.windowForComponent(displayPane);
                if (!win.isVisible()) {
                    win.setVisible(true);
                }
                win.toFront();
            }
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
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(pos, true));

        MsgConsole console = new MsgConsole(displayPane, pos, oldOut);
        new Thread(console).start();
    }

    public static void redirectErr(JTextArea displayPane) {
        PipedOutputStream pos = new PipedOutputStream();
        PrintStream oldOut = System.out;
        System.setErr(new PrintStream(pos, true));

        MsgConsole console = new MsgConsole(displayPane, pos, oldOut);
        new Thread(console).start();
    }

    public static void init(Component parentComponent) {
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        JFrame frame = new JFrame("Output");
        frame.setLocation(parentComponent.getX(), parentComponent.getY() + parentComponent.getHeight());
        frame.getContentPane().add(scrollPane);
        frame.setSize(parentComponent.getWidth(), 150);

        MsgConsole.redirectOutput(textArea);

    }
}