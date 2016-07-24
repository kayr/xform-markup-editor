package org.openxdata.markup

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams as CP
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.Tree

import static groovy.transform.stc.FirstParam.FirstGenericType

/**
 * Created by kay on 5/27/2016.
 */
@CompileStatic
class ParserUtils {

    static def <T extends Tree> List findResults(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, true, false)
    }

    static def <T extends Tree> List findResultsDeep(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, true, true)
    }

    static def <T extends Tree> List<T> findAllDeep(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, false, true)
    }

    static def <T extends Tree> List<T> findAllDeepSelf(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, false, true, false, true)
    }

    static def <T extends Tree> List<T> findAll(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, false, false)
    }

    static def <T extends Tree> List<T> findAllBreadth1st(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplBreadth(tree, filter, false, false)
    }

    static def <T extends Tree> T find(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, false, false, true)?.get(0) as T
    }

    static def <T extends Tree> T findSelf(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, false, false, true, true)?.get(0) as T
    }

    static def <T extends Tree> T findDeep(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, true, true)?.get(0) as T
    }

    static def <T extends Tree> T findDeepSelf(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImplDeep(tree, filter, false, true, true, true)?.get(0) as T
    }

    static def <T extends Tree> void each(T tree, @CP(FirstGenericType) Closure filter) {
        eachImpl(tree, filter, false)
    }

    /**
     * Walks down(depth first) a tree till it finds an element the closure matches then finds similar
     * elements at that same depth
     */
    static def <T extends Tree> List findResultsImplDeep(T tree,
                                                         @CP(FirstGenericType) Closure filter,
                                                         boolean transform,
                                                         boolean deep,
                                                         boolean breakOnFirst = false,
                                                         boolean includeParent = false) {


        List trees = []

        if (includeParent && filter(tree)) { //first check this item on the tree
            trees << tree
            if (!deep || breakOnFirst) return trees
        }

        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            def result = filter(child)
            if (result) {
                trees << (transform ? result : child)
                if (breakOnFirst) break
            }

            //if you have found a result and you are not in deep mode...stop
            if (trees && !deep) continue

            trees.addAll(findResultsImplDeep(child, filter, transform, deep))
        }
        return trees
    }

    /**
     * Walks down a tree till it finds an element the closure matches then finds similar
     * elements at that same level
     */
    static def <T extends Tree> List findResultsImplBreadth(T tree,
                                                         @CP(FirstGenericType) Closure filter,
                                                         boolean transform,
                                                         boolean deep,
                                                         boolean breakOnFirst = false,
                                                         boolean includeParent = false) {


        List trees = []

        if (includeParent && filter(tree)) { //first check this item on the tree
            trees << tree
            if (!deep || breakOnFirst) return trees
        }

        List<CommonTree> treesWithChildren = []

        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            def child = tree.getChild(i) as CommonTree
            def result = filter(child)
            if (result) {
                trees << (transform ? result : child)
                if (breakOnFirst) break
            }

            //if you have found a result and you are not in deep mode...stop
            if (trees && !deep) continue

            if(child.getChildCount() > 0){
                treesWithChildren << child
            }
        }

        for (CommonTree child in treesWithChildren) {
            trees.addAll(findResultsImplDeep(child, filter, transform, deep))
        }

        return trees
    }


    static def <T extends Tree> void eachImpl(T tree,
                                              @CP(FirstGenericType) Closure visitor,
                                              boolean deep) {

        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            visitor(child)
            if (deep) {
                eachImpl(child, visitor, deep)
            }
        }
    }


    static void printTree(CommonTree ast, PrintStream w = System.out) {
        print(ast, 0, w);
    }

    private static void print(CommonTree tree, int level, PrintStream w) {
        //indent level
        for (int i = 0; i < level; i++) print("--")

        //print node description: type code followed by token text
        w.println("| " + tree.getType() + " " + tree.getText())

        //print all children
        if (tree.getChildren() != null)
            for (Object ie : tree.getChildren()) {
                print((CommonTree) ie, level + 1, w)
            }
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
}


