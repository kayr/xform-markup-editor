package org.openxdata.markup

import com.sun.org.apache.xerces.internal.util.XMLChar
import groovy.transform.CompileStatic
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CharStream
import org.antlr.runtime.CommonTokenStream
import org.openxdata.markup.exception.InvalidAttributeException
import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 11/26/12
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
class Util {


    public static final int CACHE_SIZE = 50

    public static String getBindName(String question) { memoizedGetBindName(question) }

    private
    static memoizedGetBindName = { String question -> return getBindStatic(question) }.memoizeBetween(CACHE_SIZE, CACHE_SIZE)

    @CompileStatic
    private static String getBindStatic(String question) {

        // if len(s) < 1, return '_blank'
        if (question == null || question.length() < 1)
            return "_blank";

        def s = getTextWithoutDecTemplate(question)
        // return s.trim().replaceAll(/\s+/, "_").replaceAll(/\W/, "").toLowerCase()

        // Converts a string into a valid XML token (tag name)
        // No spaces, start with a letter or underscore, not 'xml*'

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

    @CompileStatic
    public static String getTextWithoutDecTemplate(String text) {
        if (text.contains('${')) {
            if (text.indexOf('}$') < text.length() - 2)
                text = text.substring(0, text.indexOf('${')) + text.substring(text.indexOf('}$') + 2);
            else
                text = text.substring(0, text.indexOf('${'));
        }
        return text;
    }

    @CompileStatic
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

    def static time(String name = "", Closure worker) {
        println "BenchmarkStart: $name"
        def start = System.currentTimeMillis()
        def rt = worker.call()
        def stop = System.currentTimeMillis()
        def time = stop - start
        def readableTime = TimeFormat.valueOf(time, TimeFormat.ROUND_TO_MILLISECOND)
        println "Completed in ${readableTime}"
        return [value: rt, time: time]
    }

    /**
     * This was used to try and guess datatype for a specific project I was working on
     * it is far from accurate. Just left it here just in case
     * @param bind
     * @return
     */
    static String getType(String bind) {
        boolean isBool = booleanKeys.any { bind.startsWith(it + '_') }
        if (isBool)
            return "boolean"

        boolean isDate = dates.any { bind.startsWith(it + '_') }
        if (isDate)
            return "date"

        return "string"
    }

    static <T> T wrapValidationExceptionHandler(int line, Closure<T> c) {
        try {
            return c()
        } catch (ValidationException ex) {
            ex.line = line
            throw ex
        }
    }

    static Map<String, String> parseBind(String option, int line) {
        wrapValidationExceptionHandler(line) { memoizedParseBind.call(option) }
    }

    private
    static memoizedParseBind = { String option -> parseBindStatic(option) }.memoizeBetween(CACHE_SIZE, CACHE_SIZE)

//    @CompileStatic
    private static Map<String, String> parseBindStatic(String option) {
        def bind

        if (!option) {
            bind = getBindName(option)
        } else if (option[0] == '$') {
            def tmpBind = option.find(/[$][^\s]*\s/)?.trim()
            //make sure bind is at the beginning
            validateId(tmpBind?.replaceFirst(/\$/, ''), 0)
            if (tmpBind == null || option.indexOf(tmpBind) > 0)
                throw new ValidationException("""Option [$option] has an invalid id.
 An Id should start with lower case characters follow by low case characters, numbers or underscores""")
            option = option.replaceFirst(/[$][^\s]*/, '').trim()
            bind = tmpBind.trim() - '$'
        } else {
            bind = getBindName(option)
        }
        return [option: option, bind: bind]
    }

    public static void validateId(String id, int line) {
        wrapValidationExceptionHandler(line) {
            if (Study.validateWithXML.get())
                memoizedValidateGeneral.call(id)
            else
                memoizedValidateId.call(id)
        }
    }
    private static memoizedValidateId = { String id ->
        if (!(id ==~ /[a-z_][a-z0-9_]*/))
            throw new InvalidAttributeException("You have an invalid variable [$id] .\n" +
                    "Attributes should start with a small letter followed by small letters and underscores")
    }.memoizeBetween(CACHE_SIZE, CACHE_SIZE)

    private static memoizedValidateGeneral = { String id ->
        if (!(XMLChar.isValidName(id)))
            throw new InvalidAttributeException("You have an invalid variable [$id] .\n" +
                    "Attributes should start with a small letter followed by small letters and underscores")
    }.memoizeBetween(CACHE_SIZE, CACHE_SIZE)


    def static booleanKeys = """is
                        has
                        do
                        any
                        does
                        are
                   """.split(/\s+/)

    def static dates = """period
                  date
        """.split(/\s+/)


}
