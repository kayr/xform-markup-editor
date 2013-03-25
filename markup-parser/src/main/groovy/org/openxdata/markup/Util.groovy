package org.openxdata.markup

import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CharStream
import org.antlr.runtime.CommonTokenStream
import org.openxdata.xpath.XPathLexer
import org.openxdata.xpath.XPathParser

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 11/26/12
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
class Util {
    public static String getBindName(String question) {
        def s = getTextWithoutDecTemplate(question)
        // return s.trim().replaceAll(/\s+/, "_").replaceAll(/\W/, "").toLowerCase()

        // Converts a string into a valid XML token (tag name)
        // No spaces, start with a letter or underscore, not 'xml*'

        // if len(s) < 1, return '_blank'
        if (s == null || s.length() < 1)
            return "_blank";

        // xml tokens must start with a letter
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";

        // after the leading letter, xml tokens may have
        // digits, period, or hyphen
        String nameChars = letters + "0123456789.-";

        // special characters that should be replaced with valid text
        // all other invalid characters will be removed
        HashMap<String, String> swapChars = new HashMap<String, String>();
        swapChars.put("!", "bang");
        swapChars.put("#", "pound");
        swapChars.put("\\*", "star");
        swapChars.put("'", "apos");
        swapChars.put("\"", "quote");
        swapChars.put("%", "percent");
        swapChars.put("<", "lt");
        swapChars.put(">", "gt");
        swapChars.put("=", "eq");
        swapChars.put("/", "slash");
        swapChars.put("\\\\", "backslash");
        swapChars.put("\\.", "dot");
        swapChars.put("-", "hyphen");

        s = s.replace("'", "");

        // start by cleaning whitespace and converting to lowercase
        s = s.replaceAll("^\\s+", "").replaceAll(/\s+$/, "").replaceAll("\\s+", "_").toLowerCase();

        // swap characters
        Set<Map.Entry<String, String>> swaps = swapChars.entrySet();
        for (Map.Entry<String, String> entry : swaps) {
            if (entry.getValue() != null)
                s = s.replaceAll(entry.getKey(), "_" + entry.getValue() + "_");
            else
                s = s.replaceAll(String.valueOf(entry.getKey()), "");
        }

        // ensure that invalid characters and consecutive underscores are
        // removed
        String token = "";
        boolean underscoreFlag = false;
        for (int i = 0; i < s.length(); i++) {
            if (nameChars.indexOf((int) s.charAt(i)) != -1) {
                if (s.charAt(i) != '_' || !underscoreFlag) {
                    token += s.charAt(i);
                    underscoreFlag = (s.charAt(i) == '_');
                }
            }
        }

        // remove extraneous underscores before returning token
        token = token.replaceAll("_+", "_");
        token = token.replaceAll(/_+$/, "");

        // make sure token starts with valid letter
        try {
            if (letters.indexOf((int) token.charAt(0)) == -1 || token.startsWith("xml"))
                token = "_" + token;
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
        // return token
        return token;

    }

    public static String getTextWithoutDecTemplate(String text) {
        if (text.contains('${')) {
            if (text.indexOf('}$') < text.length() - 2)
                text = text.substring(0, text.indexOf('${')) + text.substring(text.indexOf('}$') + 2);
            else
                text = text.substring(0, text.indexOf('${'));
        }
        return text;
    }

    public static void writeToFile(String fileName, String contents) {
        File file = new File(fileName)
        while (file.exists()) {
            println "Deleting file $file.absolutePath"
            try {
                file.delete()
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        file.text = contents

    }

    def static time(String name = "",Closure worker) {
        println "BenchmarkStart: $name"
        def start = System.currentTimeMillis()
        def rt = worker.call()
        def stop = System.currentTimeMillis()
        def time = stop - start
        def readableTime = TimeFormat.valueOf(time, TimeFormat.ROUND_TO_MILLISECOND)
        println "Completed in ${ readableTime}"
        return [value:rt,time:time]
    }

    /**
     * This was used to try and guess datatype for a specific project I was working on
     * it is far from accurate. Just left it here just in case
     * @param bind
     * @return
     */
    static String getType(String bind) {
        boolean isBool = booleanKeys.any {bind.startsWith(it+'_')}
        if (isBool)
            return "boolean"

        boolean isNumber = numbers.any {bind.contains(it)}
        if (isNumber)
            return "int"

        boolean isDecimal = decimals.any {bind.contains(it)}
        if (isDecimal)
            return "decimal"

        boolean isDate = dates.any {bind.contains(it)}
        if (isDate)
            return "date"
        return "string"
    }

    public static XformParser createParser(String testString) throws IOException {
        CharStream stream = new ANTLRStringStream(testString);
        XformLexer lexer = new XformLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XformParser parser = new XformParser(tokens);

        return parser;
    }

    public static XPathParser createXpathParser(String testString) throws IOException {
        CharStream stream = new ANTLRStringStream(testString);
        XPathLexer lexer = new XPathLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XPathParser parser = new XPathParser(tokens);
        return parser;
    }


    def static booleanKeys = """is
                        has
                        do
                        any
                        does
                        are
                   """.split(/\s+/)

    def static numbers = """number
                        _enrollment
                        total
                        boys_in
                        girls_in
                        days
                        no.
                        stances
        """.split(/\s+/)

    def static decimals = """_number
                        _kg
                        _size_
                        _estimation
                        estimate
                        _acre
                        average
                        _size
                        _enrollment
                        _rate
                        capacity
                        distance
                        total
                        boys_in
                        girls_in
                        percentage
                        percent
                        volume
                        days
        """.split(/\s+/)

    def static dates = """period
                  date
        """.split(/\s+/)


}
