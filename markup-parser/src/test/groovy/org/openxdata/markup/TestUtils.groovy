package org.openxdata.markup

/**
 * Created by kay on 1/5/2017.
 */
class TestUtils {

    static String loadResourceText(String path){
        return TestUtils.classLoader.getResourceAsStream(path).text
    }

    static String trimAllLines(String text){
        return text.readLines().collect { it.trim() }.join('\n')
    }
}
