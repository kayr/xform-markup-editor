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
        List trees = tree.findAll { Tree t ->
            t.childCount == 0
        }
        return trees.get(trees.size() - 1).token.stop
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
        findResultsImpl(tree, filter, true)
    }

    static List<CommonTree> findAllImpl(Tree tree, Closure filter) {
        findResultsImpl(tree, filter, false)
    }

    static List<CommonTree> findResultsImpl(Tree tree, Closure filter, boolean transform) {
        List<CommonTree> trees = []
        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            def result = filter(child)
            if (result) {
                trees << (transform ? result : child)
            } else {
                trees.addAll(findResultsImpl(child, filter, transform))
            }
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

    static {
        CommonTree.metaClass.findResults = { Closure clos ->
            XPathUtil.findResultsImpl(delegate, clos)
        }

        CommonTree.metaClass.findAll = { Closure clos ->
            XPathUtil.findAllImpl(delegate, clos)
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
