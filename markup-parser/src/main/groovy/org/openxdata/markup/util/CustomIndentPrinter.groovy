package org.openxdata.markup.util

import groovy.transform.CompileStatic

@CompileStatic
class CustomIndentPrinter extends IndentPrinter {

    private boolean newLinesEnabled = true
    private int     linesPrinted    = 0

    CustomIndentPrinter(Writer out) {
        super(out)
    }


    @Override
    void println() {
        linesPrinted++
        mayBeEnable()
        if (newLinesEnabled)
            super.println()
    }

    @Override
    void incrementIndent() {
        if (newLinesEnabled)
            super.incrementIndent()
    }

    @Override
    void decrementIndent() {
        if (newLinesEnabled)
            super.decrementIndent()

    }

    @Override
    void printIndent() {
        if (newLinesEnabled)
            super.printIndent()
    }

    boolean getNewLinesEnabled() {
        return newLinesEnabled
    }

    private boolean mayBeEnable() {
        if (linesPrinted == 2)
            this.@newLinesEnabled = true
    }

    void setNewLinesEnabled(boolean newLinesEnabled) {
        if (newLinesEnabled) {
            linesPrinted = 0
        } else {
            this.@newLinesEnabled = false
            linesPrinted = 0
        }
    }
}
