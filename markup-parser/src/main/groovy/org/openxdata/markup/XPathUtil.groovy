package org.openxdata.markup

import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CharStream
import org.antlr.runtime.CommonToken
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree
import org.codehaus.groovy.runtime.StackTraceUtils
import org.openxdata.markup.exception.ValidationException

import static org.openxdata.markup.ParserUtils.find

/**
 * Created by kay on 6/16/14.
 */

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
        List<CommonTree> paths = tree.findAll { CommonTree tree ->
            tree.isPath()
        }
        convertToSimpleListOfPathMap(paths)
    }

    List<Map> getAllPathVariables() {
        List<CommonTree> paths = tree.findAll { CommonTree tree ->
            tree.isPath() || isMarkUpPath(tree)
        }
        convertToSimpleListOfPathMap(paths)
    }

    private static List<LinkedHashMap<String, Object>> convertToSimpleListOfPathMap(List<CommonTree> paths) {
        return paths.collect {
            String path = emitTailString(it)
            String lastChild = getLastChild(it)?.toString()
            return [path: path, name: getNodeName(path), start: it.charPositionInLine, end: getLastIndex(it) + 1, varName: lastChild]
        }
    }


    String removeMarkupSyntax(IFormElement question, Map config = [:]) {
        def logicType = config.logicType ?: 'XPATH'

        boolean allowOxdLikeRelativePaths = config.allowRelativePath == null ? true : config.allowRelativePath

        boolean indexed = config.indexed ?: false

        def visited = []

        def filter = { CommonTree tree -> isMarkUpPath(tree) }

        def transFormer = { StringBuilder finalXPath, CommonTree tree, int offset ->

//            if (visited.contains(tree)) return

//            def children = findAllDeep(tree) { true } as List
//            visited.addAll([tree] + children)

//            def markUpPath = emitTailString(tree).replaceFirst(/\$:?/, '')


            def qNameTree = find(tree) { CommonTree c -> c.type == XPathParser.QNAME || c.type == XPathParser.DOT }
            def actualNameTree = qNameTree.type == XPathParser.DOT ? qNameTree : qNameTree.getChild(0) as CommonTree
            def markUpPath = actualNameTree.text

            def isCurrentPathShortCut = markUpPath == '.'

            def finalVar = markUpPath

            def qn = isCurrentPathShortCut ? question : question.parentForm.getElement(finalVar)

            if (!qn) throw new ValidationException("$logicType Logic for [$question.text] has an unknown variable [$finalVar]", question.line)

            boolean isRelative = tree.type == XPathParser.SHORT_OXD_ABSPATH && allowOxdLikeRelativePaths

            def convertedPath = createFinalBinding(qn, finalVar, indexed, isRelative, config)

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
            def name = v.varName
            if (name == '.' || v.path == 'null') continue
            def qn = question.parentForm.getElement(name)
            if (!qn) throw new ValidationException("Error parsing XPATH[$xpath] $logicType logic for has an unknown variable [$v]", question.line)
        }
    }

    static
    private String createFinalBinding(IFormElement qn, String variable, boolean indexed, boolean relative, Map config) {
        if (variable == '.') return qn.getAbsoluteBinding(indexed, relative)
        def variableQn = qn.parentForm.getElement(variable)
        return variableQn.getAbsoluteBinding(indexed, relative)
    }


    static int getLastIndex(Tree tree) {
        if (tree.childCount == 0)
            return (tree.token as CommonToken).stopIndex

        List trees = tree.findAll { Tree t ->
            t.childCount == 0
        }
        return trees.get(trees.size() - 1).token.stop
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

    String extractExpr(CommonTree _tree) {
        return extractExpr(_tree, xpath)
    }

    static Tree getLastChild(Tree tree) {
        List trees = tree.findAll { Tree t ->
            t.childCount == 0
        }
        return trees.get(trees.size() - 1)
    }

    static String getNodeName(String path) {
        new File(path).name
    }

    List<Tree> findResults(Closure filter) {
        findResultsImpl(tree, filter)
    }

    List<CommonTree> findAll(Closure filter) {
        findAll tree, filter
    }

    static List<CommonTree> findResultsImpl(Tree tree, Closure filter) {
        findResultsImpl(tree, filter, true, false)
    }

    static List<CommonTree> findAllImpl(Tree tree, Closure filter) {
        findResultsImpl(tree, filter, false, false)
    }

    static List<CommonTree> findResultsImpl(Tree tree, Closure filter, boolean transform, boolean deep, boolean includeParent = true) {
        List<CommonTree> trees = []

        if (includeParent && filter(tree)) { //first check this item on the tree
            trees << tree
            if (!deep) return trees
        }

        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            def result = filter(child)
            if (result) {
                trees << (transform ? result : child)
            }

            if (result && !deep) continue

            trees.addAll(findResultsImpl(child, filter, transform, deep, false))
        }
        return trees
    }

    /** Print out only the tails */
    public static String emitTailString(Tree tree) {
        if (tree.getChildCount() == 0) {
            return tree.toString()
        }
        StringBuffer buf = new StringBuffer()

        for (int i = 0; i < tree.getChildCount(); i++) {
            Tree t = (Tree) tree.getChild(i)
            buf.append(emitTailString(t))
        }
        return buf.toString()
    }

    String transformXPath(Closure filter, Closure transformer, boolean swallowException = false) {
        if (!xpath) return null

        try {
            def builder = new StringBuilder(xpath)
            def trees = findAllDeep(tree, filter)

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

    static findAllDeep(CommonTree tree, Closure filter) {
        findResultsImpl(tree, filter, false, true)
    }

    static findAll(CommonTree tree, Closure filter) {
        findAllImpl(tree, filter)
    }

    static isPath(CommonTree tree) {
        tree.token?.type == XPathParser.ABSPATH || tree.token?.type == XPathParser.RELPATH
    }


    static {
        CommonTree.metaClass.findResults = { Closure clos ->
            XPathUtil.findResultsImpl(delegate, clos)
        }

        CommonTree.metaClass.findAll = { Closure clos ->
            XPathUtil.findAll(delegate, clos)
        }

        CommonTree.metaClass.findAllDeep = { Closure clos ->
            XPathUtil.findAllDeep(delegate, clos)
        }

        Tree.metaClass.emitTailString {
            XPathUtil.emitTailString(delegate)
        }

        Tree.metaClass.isCommonTree {
            delegate instanceof CommonTree
        }

        CommonTree.metaClass.isPath {
            XPathUtil.isPath(delegate)
        }
    }
}
