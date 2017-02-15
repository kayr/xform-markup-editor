package org.openxdata.markup.ui;

import javax.swing.*;
import javax.swing.text.StyledDocument;

public class JTextWrapPane extends JTextPane {

    private boolean wrapState = false;

    JTextWrapPane() {
        super();
    }

    public JTextWrapPane(StyledDocument document) {
        super(document);
    }


    public boolean getScrollableTracksViewportWidth() {
        return wrapState;
    }


    public void setLineWrap(boolean wrap) {
        wrapState = wrap;
    }


    public boolean getLineWrap(boolean wrap) {
        return wrapState;
    }
}