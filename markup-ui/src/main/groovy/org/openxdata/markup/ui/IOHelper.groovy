package org.openxdata.markup.ui

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

import javax.swing.*
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Created by kay on 10/7/2016.
 */
class IOHelper {

    def static <T extends JFileChooser> File chooseFile(File lastAccessedFile,
                                                        FileFilter fileFilter,
                                                        @ClosureParams(FirstParam.FirstGenericType) Closure<Integer> fnRenderDialog) {
        JFileChooser jc = new JFileChooser()

        jc.fileFilter = fileFilter

        def selectedFile = lastAccessedFile
        if (selectedFile) {
            if (selectedFile.isDirectory())
                jc.currentDirectory = selectedFile
            else
                jc.selectedFile = selectedFile
        }


        def option = fnRenderDialog(jc)

        if (option != JFileChooser.APPROVE_OPTION) return null

        return jc.selectedFile
    }

    static FileNameExtensionFilter filter(String desc, String... extensions) {
        return new FileNameExtensionFilter(desc, extensions)
    }
}
