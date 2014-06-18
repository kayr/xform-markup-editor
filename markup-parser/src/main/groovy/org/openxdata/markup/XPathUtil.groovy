package org.openxdata.markup

import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree

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

    private def parse() {
        def parser = Util.createXpathParser(xpath)
        tree = parser.eval().tree as CommonTree
    }

    String shorten(Form form) {
        collect {}
    }

    List<CommonTree> collect(Closure filter) {
        tree.collect(filter)
    }

    List<CommonTree> findAll(Closure filter) {
        tree.findAll(filter)
    }

    List<CommonTree> collectDeep(Closure filter) {

        collectImpl(tree) { CommonTree tree ->
            tree.children.size() == 1
        }

    }


    static {
        CommonTree.metaClass.collect = { Closure clos ->
            XPathUtil.collectImpl(delegate, clos)
        }

        CommonTree.metaClass.findAll = { Closure clos ->
            XPathUtil.findAllImpl(delegate, clos)
        }

        Tree.metaClass.emitString{
            XPathUtil.emitString(delegate)
        }
    }

    static List<CommonTree> collectImpl(Tree tree, Closure filter) {
        if (!(tree instanceof CommonTree)) return []
        List<CommonTree> trees = []
        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            def result = filter(child)
            if (result) {
                trees << result
            } else {
                trees.addAll(collectImpl(child, filter))
            }
        }
        return trees
    }

    static List<CommonTree> findAllImpl(Tree tree, Closure filter) {
        if (!(tree instanceof CommonTree)) return []
        List<CommonTree> trees = []
        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            def result = filter(child)
            if (result) {
                trees << child
            } else {
                trees.addAll(findAllImpl(child, filter))
            }
        }
        return trees
    }

    /** Print out a whole tree not just a node */
    public static String emitString(Tree tree) {
        if (tree.getChildCount() == 0) {
            return tree.toString();
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < tree.getChildCount(); i++) {
            Tree t = (Tree) tree.getChild(i);
            buf.append(emitString(t));
        }
        return buf.toString();
    }

    @SuppressWarnings("unchecked")
    private String[] combine(Object... strings) {
        List<String> result = new ArrayList<String>();
        for (Object o : strings) {
            if (o instanceof String[])
                for (String s : (String[]) o)
                    result.add(s);
            else if (o instanceof String)
                result.add((String) o);
            else if (o instanceof List<?>)
                for (String s : (List<String>) o)
                    result.add(s);
            else
                throw new RuntimeException("can't combine, unexpected type: " + o.getClass());
        }
        return result as String[];
    }


}
