package org.openxdata.markup.ui

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Created by kay on 1/4/2016.
 */
class HistoryKeeper {

    final static int MAX_HISTORY = 20

    static def registerHistory(String filePath) {

        def history = removeHistory(filePath)

        if (history.size() >= MAX_HISTORY) history.pop()

        history.add(0, filePath)

        writeHistory(history)

        return history

    }

    static List<String> removeHistory(String filePath) {
        def history = getHistory()
        history.remove(filePath)
        writeHistory(history)
        return history
    }

    static List<String> getHistory() {
        def lines = []
        try {
            lines = loadHistoryFile().readLines()
        } catch (Exception x) {
            System.err.println("Error reading history file $x")
            loadHistoryFile().delete()
        }
        return lines
    }

    private static File loadHistoryFile() {
        def historyFile = new File("$userHome/.oxdmarkup/", 'history')
        if (!historyFile.exists()) {
            historyFile.parentFile.mkdirs()
            historyFile.createNewFile()
        }
        return historyFile
    }

    private static void writeHistory(List<String> history) {
        loadHistoryFile().text =  history.unique().join(System.lineSeparator())
    }

    private static File getUserHome() {
        System.getProperty('user.home') as File
    }

    static File getLastAccessedFile(){
        return history.head()?.asType(File)
    }

}
