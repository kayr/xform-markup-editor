package org.openxdata.markup.serializer
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 11/15/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
class MarkupAligner {


    public static final String INDENT = '    '
    String markUpTxt

    static String align(String form) {
        new MarkupAligner(form).align()
    }


    MarkupAligner(String txt) {
        this.markUpTxt = txt
    }

    String align() {
        if (markUpTxt == null)
            return ""

        StringWriter writer = new StringWriter()
        IndentPrinter printer = new IndentPrinter(writer, INDENT)


        String prevLine = null
        boolean inDynamic = false
        boolean inMultiline = false

        def lines = markUpTxt.trim().readLines()

        def size = lines.size()
        for (int i = 0; i < size; i++) {


            def currentLine = lines.get(i).trim()

            if (inMultiline && !currentLine.endsWith("'''")) {
                printer.println(lines.get(i))
                continue
            }

            if (currentLine.isEmpty()) continue

            def nextLine = i + 1 < size ? lines[i + 1].trim() : null


            currentLine = currentLine.replaceAll(/\s+/, ' ')

            if (currentLine.startsWith('>')) {
                printer.printIndent()
                //remove extra whitespace from options
                currentLine = currentLine.replaceFirst(/>>?\s+/) { it.toString().trim() }
                printer.println "  $currentLine"
                prevLine = currentLine

                if (isMultiLine(currentLine)) {
                    inMultiline = true
                }

                continue
            }

            boolean justQuitMultiQn = false
            if (currentLine == '}') {
                printer.decrementIndent()
                if (inDynamic) {
                    inDynamic = false
                }

            } else if (currentLine.endsWith("'''")) {
                if (inMultiline) {
                    inMultiline = false
                    justQuitMultiQn = true
                }


            }
            //print a new line if its a new question , page or form
            else if ((isQuestionOrOption(prevLine) && isQuestion(currentLine)
                    || isPager(prevLine) && isQuestionOrOption(currentLine)
                    || isQuestionOrOption(prevLine) && isKeyLine(currentLine)
                    || isPager(prevLine) && isKeyLine(currentLine)
                    || (isPager(currentLine)))
                    && (!inDynamic && currentLine != '}') // don't print in dynamic qn brace
                    && (!inMultiline && !justQuitMultiQn) // don't print in multiline
                    && (!isAttribute(prevLine) || !isPager(currentLine)) //attributes for pagers
                    && !isComment(currentLine)) {//no new lines for all comments
                printer.println()
                printer.println()
            }

            if (isMultiLine(currentLine) && !justQuitMultiQn) {
                inMultiline = true
                printer.println()
                printer.println()
            }

            //comment handling
            if (isQuestion(prevLine) && isComment(currentLine)
                    || isOption(prevLine) && isComment(currentLine) && !isOption(nextLine)
            ) {
                printer.println()
                printer.println()
            }

            printer.printIndent()
            printer.println currentLine


            if (currentLine.matches(/((repeat)|(group))\s*\{.*/)) {
                printer.incrementIndent()
                printer.println()
            } else if (currentLine.matches(/dynamic\s*\{/) || currentLine.matches(/dynamic_instance\s*\{/)) {
//increase indent
                printer.incrementIndent()
                inDynamic = true
            }

            //set previous line
            prevLine = currentLine
        }
        printer.flush()
        return writer.toString()
    }

    private static boolean isMultiLine(String currentLine) {
        return currentLine.matches(/((''')|(>>?\s*''')).*/)
    }

    private static boolean isKeyLine(String s) {
        if (!s) return false
        return s.startsWith('@') ||
                s.startsWith('#') ||
                s.startsWith('//') ||
                s.matches(/((repeat)|(group))\s*\{.*/) ||
                s.matches(/dynamic_instance\s*\{/) ||
                s.matches(/dynamic\s*\{/)

    }

    private static boolean isAttribute(String s) {
        if (!s) return false
        return s.startsWith('@')
    }

    private static boolean isComment(String s) {
        if (!s) return false
        return s.startsWith('//')
    }

    private static boolean isOption(String s) {
        if (!s) return false
        return s.startsWith('>') || s.startsWith('$>')
    }


    private static boolean isPager(String s) {
        if (!s) return false
        return s.startsWith('#')
    }

    private static boolean isQuestionOrOption(String s) {
        if (!s) return false
        return !isKeyLine(s)
    }

    private static boolean isQuestion(String s) {
        if (!s) return false
        return (isQuestionOrOption(s) && !isOption(s))
    }
}
