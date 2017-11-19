package org.openxdata.markup

import groovy.transform.CompileStatic
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CharStream
import org.antlr.runtime.CommonToken
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree
import org.codehaus.groovy.runtime.StackTraceUtils
import org.openxdata.markup.exception.ValidationException

import static org.openxdata.markup.ParserUtils.*

/**
 * Created by kay on 6/16/14.
 */
@CompileStatic
class XPathUtil {

    String xpath
    CommonTree tree

    XPathUtil(String xpath) {
        this.xpath = xpath
        parse()
    }

    private XPathUtil parse() {
        tree = createAST(xpath)
        return this
    }

    public static XPathParser createXpathParser(String testString) throws IOException {
        CharStream stream = new ANTLRStringStream(testString);
        XPathLexer lexer = new XPathLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XPathParser parser = new XPathParser(tokens);
        return parser;
    }

    public static CommonTree createAST(String string) throws IOException {
        def parser = createXpathParser(string)
        return parser.eval().tree as CommonTree
    }


    List<Map> getXPathPathVariables() {
        List<CommonTree> paths = findAllDeepSelf(tree) { CommonTree tree ->
            isPath(tree)
        }
        convertToSimpleListOfPathMap(paths)
    }

    List<Map> getAllPathVariables() {
        List<CommonTree> paths = findAllDeepSelf(tree) { CommonTree tree ->
            isPath(tree) || isMarkUpPath(tree)
        }
        convertToSimpleListOfPathMap(paths)
    }

    private static List<LinkedHashMap<String, Object>> convertToSimpleListOfPathMap(List<CommonTree> paths) {
        return paths.collect {
            String path = emitTailString(it)
            String lastChild = getLastNameOnTree(it)?.toString()
            return [path: path, name: getNodeName(path), start: it.charPositionInLine, end: getLastIndex(it) + 1, varName: lastChild]
        }
    }


    String removeMarkupSyntax(IFormElement question, Map config = [:]) {
        def logicType = config.logicType ?: 'XPATH'

        boolean allowOxdLikeRelativePaths = config.allowRelativePath == null ? true : config.allowRelativePath

        boolean indexed = config.indexed ?: false

        def filter = { CommonTree tree -> isMarkUpPath(tree) }

        def transFormer = { StringBuilder finalXPath, CommonTree tree, int offset ->

            def qNameTree = find(tree) { CommonTree c ->
                c.type == XPathParser.QNAME || c.type == XPathParser.DOT
            }

            def actualNameTree = qNameTree.type == XPathParser.DOT ? qNameTree : qNameTree.getChild(0) as CommonTree

            def variableReference = actualNameTree.text

            def isCurrentPathShortCut = variableReference == '.'

            def qn = isCurrentPathShortCut ? question : question.parentForm.getElement(variableReference)

            if (!qn) throw new ValidationException("$logicType Logic for [$question.text] has an unknown variable [$variableReference]", question.line)

            boolean isRelative = tree.type == XPathParser.SHORT_OXD_ABSPATH && allowOxdLikeRelativePaths

            def convertedPath = createFinalBinding(qn, variableReference, indexed, isRelative, config)

            def start = tree.charPositionInLine + offset
            def end = (actualNameTree.token as CommonToken).stopIndex + offset + 1

            finalXPath.replace(start, end, convertedPath)

        }

        def path = transformXPath(filter, transFormer)

        return path
    }

    static String validateXpath(String xpath, IFormElement question, String logicType) {
        def xp = new XPathUtil(xpath)
        def variables = xp.getAllPathVariables()
        for (v in variables) {
            def name = v.varName as String
            def path = v.path as String

            //attributes also come as part of variables and paths so exclude them among the things to be validated
            if (path == '.' || path == '$.' || path == '..' || path == 'null' || path?.startsWith('@')) continue

            def qn = question.parentForm.getElement(name)
            if (!qn) throw new ValidationException("Error parsing XPATH[$xpath] $logicType logic for[${question.debugString}] because it has an unknown variable [$v]", question.line)
        }
    }

    static
    private String createFinalBinding(IFormElement qn, String variable, boolean indexed, boolean relative, Map config) {
        if (variable == '.') return qn.getAbsoluteBinding(indexed, relative)
        def variableQn = qn.parentForm.getElement(variable)
        return variableQn.getAbsoluteBinding(indexed, relative)
    }

    static int getLastIndex(CommonTree tree) {
        if (tree.childCount == 0)
            return (tree.token as CommonToken).stopIndex

        def trees = findAllDeep(tree) { Tree t ->
            t.childCount == 0
        }
        return trees.last().token.asType(CommonToken).stopIndex
    }

    static String extractExpr(CommonTree _tree, String xpath) {
        def start = _tree.charPositionInLine
        List<CommonTree> children = _tree.children
        if (children) {
            start = children[0].charPositionInLine
        }

        def end = getLastIndex(_tree)
        return xpath.substring(start, end + 1)
    }

    private static CommonTree getLastNameOnTree(CommonTree tree) {
        List<CommonTree> trees = findAllBreadth1st(tree) { CommonTree t ->
            t.type == XPathParser.QNAME
        }

        if (trees)
            return trees.last().children.first() as CommonTree
        return null
    }

    static String getNodeName(String path) {
        if (path == null) return null
        new File(path).name
    }


    String transformXPath(Closure filter, Closure transformer, boolean swallowException = false) {
        if (!xpath) return null

        try {
            def builder = new StringBuilder(xpath)
            def trees = findAllDeepSelf(tree, filter)

            //todo do some caching to improve performance
            trees.inject(0) { Integer offset, def tree ->
                int oldSize = builder.size()
                transformer(builder, tree, offset)
                return builder.size() - oldSize + offset
            }
            return builder.toString()
        } catch (Exception x) {
            if (!swallowException) {
                throw x
            }
            System.err.println("!!!!: Failed to process xpath: [$xpath]: [$x]")
            StackTraceUtils.printSanitizedStackTrace(x)
            return xpath
        }
    }

    static isMarkUpPath(CommonTree tree) {
        tree.token?.type == XPathParser.SHORT_ABSPATH || tree.token?.type == XPathParser.SHORT_OXD_ABSPATH
    }

    static isPath(CommonTree tree) {
        tree.token?.type == XPathParser.ABSPATH || tree.token?.type == XPathParser.RELPATH
    }


}
