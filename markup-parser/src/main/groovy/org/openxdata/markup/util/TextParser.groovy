package org.openxdata.markup.util

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

import java.util.regex.Pattern

/**
 * Created by user on 7/3/2017.
 */
@CompileStatic
class TextParser {

    static Pattern PATTERN = Pattern.compile(/\{\{.*?}}/)

    static Pattern OXD_PATTERN = Pattern.compile(/\$\{.*?}\$/)

    static List<TextToken> parseText(String text, String start, String end) {
        if (start == '${' && end == '}$') {
            parseText(text, OXD_PATTERN, start, end)
        } else {
            parseText(text, Pattern.compile("${start}.*?${end}"), start, end)
        }
    }

    static List<TextToken> parseOxdToken(String text) {
        parseText(text, OXD_PATTERN, '${', '}$')
    }


    static List<TextToken> parseText(String text) { parseText(text, PATTERN, '{{', '}}') }

    static List<TextToken> parseText(String text, Pattern pattern, String startWord, String endWord) {

        if (text == null) return Collections.EMPTY_LIST

        def matcher = pattern.matcher(text)


        Map<Integer, Integer> startEnd = [:]
        int lastItem = -1
        while (matcher.find()) {
            startEnd[matcher.start()] = lastItem = matcher.end()
        }

        if (startEnd.isEmpty()) return [TextToken.create(0, text.size(), TextToken.Type.TEXT, text, startWord, endWord)]

        List<TextToken> tokens = []



        startEnd.inject(0) { int start, Map.Entry<Integer, Integer> se ->

            //create previous token if necessary
            if (start != se.key) {
                tokens << TextToken.create(start, se.key, TextToken.Type.TEXT, text, startWord, endWord)
            }

            //create token for this entry
            tokens << TextToken.create(se.key, se.value, TextToken.Type.EXPRESSION, text, startWord, endWord)


            return se.value
        }

        def lastIndex = text.size()

        if (lastItem != lastIndex) {
            tokens << TextToken.create(lastItem, lastIndex, TextToken.Type.TEXT, text, startWord, endWord)
        }


        return tokens

    }


    @EqualsAndHashCode
    @CompileStatic
    static class TextToken {

        enum Type {
            TEXT, EXPRESSION
        }

        int    start, end
        String text, startWord, endWord
        Type   type


        static TextToken create(int start, int end, Type type1, String wholeText, String startWord, String endWord) {
            def part = wholeText.substring(start, end)

            new TextToken(text: part, type: type1, start: start, end: end, startWord: startWord, endWord: endWord)


        }

        String getInnerText() {

            if (type == Type.EXPRESSION) {
                return text.replace(startWord, '').replace(endWord, '')
            }

            return text

        }
    }
}


