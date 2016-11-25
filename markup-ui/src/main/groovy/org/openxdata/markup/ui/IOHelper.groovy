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

    static httpPost(String url, Map<String, Object> params) {
        def url1 = new URL(url)
        def connection = url1.openConnection()
        connection.requestMethod = "POST"
        def urlParameters = params.collect { k, v ->
            URLEncoder.encode(k, 'UTF-8') + '=' + URLEncoder.encode(v.toString(), "UTF-8")
        }.join('&')
        //send post request
        connection.doOutput = true
        connection.outputStream << urlParameters
        //get http response
        String response = connection.inputStream.text
        return response
    }

    static save(File file, String text) {
        println("Saving file: ${file.absolutePath}")
        file.setText(text, 'UTF-8')
    }

    static String loadText(File file) {
        println("Reading file: ${file.absolutePath}")
        file.getText('UTF-8')
    }

    static boolean needsSaving(File file, String textContent) {

        def textNullOrEmpty = textContent == null || textContent.isEmpty()
        def fileNull = file == null || !file.exists()

        if (fileNull && textNullOrEmpty) { return false }

        if (fileNull && !textNullOrEmpty) { return true }

        if (!fileNull && textNullOrEmpty) { return true }

        def fileText = loadText(file)

        if (fileText != textContent) { return true }

        return false
    }

}
