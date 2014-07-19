package org.openxdata.markup

import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree
import org.openxdata.xpath.XPathParser

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
        def parser = Util.createXpathParser(xpath)
        tree = parser.eval().tree as CommonTree
        XPathParser.NEQ
        return this
    }

    List<Map> getPathVariables() {
        List<CommonTree> paths = tree.findAll { CommonTree tree ->
            tree.isPath()
        }

        paths.collect {
            String path = it.emitTailString()
            return [path: path, name: getNodeName(path), start: it.charPositionInLine, end: getLastIndex(it) + 1]
        }
    }

    static int getLastIndex(Tree tree) {
        if (tree.childCount == 0)
            tree = tree.token.stop

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
        tree.findResults(filter)
    }

    List<CommonTree> findAll(Closure filter) {
        tree.findAll(filter)
    }

    static List<CommonTree> findResultsImpl(Tree tree, Closure filter) {
        findResultsImpl(tree, filter, true, false)
    }

    static List<CommonTree> findAllImpl(Tree tree, Closure filter) {
        findResultsImpl(tree, filter, false, false)
    }

    static List<CommonTree> findResultsImpl(Tree tree, Closure filter, boolean transform, boolean deep) {
        List<CommonTree> trees = []
        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            def result = filter(child)
            if (result) {
                trees << (transform ? result : child)
            }

            if (result && !deep) continue

            trees.addAll(findResultsImpl(child, filter, transform, deep))
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

    String transformXPath(Closure filter, Closure transformer) {
        if (!xpath) return null

        try {
            def builder = new StringBuilder(xpath)
            def trees = this.findAll(filter)

            //todo do some caching to improve performance
            trees.inject(0) { Integer offset, def tree ->
                int oldSize = builder.size()
                transformer(builder, tree, offset)
                return builder.size() - oldSize + offset
            }
            return builder.toString()
        } catch (Exception x) {
            System.err.println("!!!!: Failed to process xpath: [$xpath]: [$x]")
            x.printStackTrace()
            return xpath
        }
    }

    static {
        CommonTree.metaClass.findResults = { Closure clos ->
            XPathUtil.findResultsImpl(delegate, clos)
        }

        CommonTree.metaClass.findAll = { Closure clos ->
            XPathUtil.findAllImpl(delegate, clos)
        }

        CommonTree.metaClass.findAllDeep = { Closure clos ->
            XPathUtil.findResultsImpl(delegate, clos, false, true)
        }

        Tree.metaClass.emitTailString {
            XPathUtil.emitTailString(delegate)
        }

        Tree.metaClass.isCommonTree {
            delegate instanceof CommonTree
        }

        CommonTree.metaClass.isPath {
            delegate.token.type == XPathParser.ABSPATH || delegate.token.type == XPathParser.RELPATH
        }
    }
}
