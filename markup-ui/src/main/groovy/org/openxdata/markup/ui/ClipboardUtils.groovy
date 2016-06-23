package org.openxdata.markup.ui

import java.awt.datatransfer.StringSelection
import java.awt.Toolkit
import java.awt.datatransfer.*

/**
 * Created by Martin on 23/06/2016.
 */
class ClipboardUtils {
    static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()

    static void setClipboardContents(final String contents){
        clipboard.setContents(new StringSelection(contents), null)
    }

    static String getClipboardContents(){
        return clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor)
    }
}
