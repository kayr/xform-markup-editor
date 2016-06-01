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
        findResultsImpl(tree, filter, true, false)
    }

    static def <T extends Tree> List<T> findAllDeep(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImpl(tree, filter, false, true)
    }

    static def <T extends Tree> List<T> findAll(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImpl(tree, filter, false, false)
    }

    static def <T extends Tree> T find(T tree, @CP(FirstGenericType) Closure filter) {
        findResultsImpl(tree, filter, false, true)?.get(0) as T
    }

    static def <T extends Tree> void each(T tree, @CP(FirstGenericType) Closure filter) {
        eachImpl(tree, filter, false)
    }

    /**
     * Walks down a tree till it finds an element the closure matches then finds similar
     * elements at that same level
     */
    static def <T extends Tree> List findResultsImpl(T tree,
                                                     @CP(FirstGenericType) Closure filter,
                                                     boolean transform,
                                                     boolean deep,
                                                     boolean breakOnFirst = false) {
        List trees = []
        int count = tree.getChildCount()
        for (int i = 0; i < count; i++) {
            Tree child = tree.getChild(i)
            def result = filter(child)
            if (result) {
                trees << (transform ? result : child)
                if (breakOnFirst) break
            }

            //if you have already found a result and you are not in deep mode...stop
            if (result && !deep) continue

            trees.addAll(findResultsImpl(child, filter, transform, deep))
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
        w.println(" " + tree.getType() + " " + tree.getText())

        //print all children
        if (tree.getChildren() != null)
            for (Object ie : tree.getChildren()) {
                print((CommonTree) ie, level + 1, w)
            }
    }
}
