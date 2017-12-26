package org.openxdata.markup.serializer

import groovy.transform.CompileStatic
import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.*

import static org.openxdata.markup.XPathParser.*
import static org.openxdata.markup.XPathUtil.*

/**
 * This class is mainly used to transform OXD XPATH Expressions to something ODK Understands
 */
class ODKXpathUtil {


    String _xpath
    Form form
    boolean numberedBindings
    private List<CommonTree> visitedTrees = []

    ODKXpathUtil() {}

    ODKXpathUtil(Form form, String _xpath, boolean numberedBindings) {
        this.form = form
        this._xpath = _xpath
        this.numberedBindings = numberedBindings
    }

    String makeCompatible() {
        return _makeODKCompatibleXPath(_xpath)
    }

    /**
     * Transform the expression associated to the path into a compatible odk path
     * @param finalXPath The container that stores the final xpath
     * @param tree The tree path of the expression
     * @param offset length by how much the expression has changed
     * @return
     */
    private def clsTransformer = { StringBuilder finalXPath, CommonTree tree, int offset ->
        if (visitedTrees.contains(tree)) return
        visitedTrees.add(tree)

        def compatibleExpr = makeCompatibleExpression(tree)
        if (!compatibleExpr) return

        //make sure u get the position of the first token in the expression
        def start = (tree.parent as CommonTree).children[0].charPositionInLine + offset
        def end = getLastIndex(tree.parent) + offset + 1
        finalXPath.replace(start, end, compatibleExpr)
    }

    /**
     * Filters out all the tree tokens
     */
    private def clsFilter = { CommonTree tree ->
        if (!isPath(tree)) return false
        def qn = findQuestion(tree, form, numberedBindings)
        return (qn instanceof MultiSelectQuestion || qn?.type?.equalsIgnoreCase('boolean'))
    }

    private String _makeODKCompatibleXPath(String xpath) {
        XPathUtil xp = new XPathUtil(xpath)
        return xp.transformXPath(this.clsFilter, this.clsTransformer)
    }


    private static IQuestion findQuestion(CommonTree tree, Form form, boolean numberedBindings) {
        def qnBinding = XPathUtil.getNodeName(ParserUtils.emitTailString(tree))
        if (numberedBindings) qnBinding = removeIndex(qnBinding)
        return form.getElement(qnBinding)
    }

    private static String removeIndex(String binding) {
        return binding.replaceFirst(/(^_[0-9]+)/, '')
    }

    @CompileStatic
    private String makeCompatibleExpression(CommonTree qnTree) {
        def parent = qnTree.getParent() as CommonTree

        //we only convert != and =
        if (!parent || (parent.token.type != EQ && parent.token.type != NEQ)) return null

        IQuestion qn = findQuestion(qnTree, form, numberedBindings)
        def isMultiSelect = qn instanceof MultiSelectQuestion
        def isBoolean = qn?.type == 'boolean'

        if (isBoolean)
            return transformToBooleanExpr(qnTree, qn)

        if (isMultiSelect)
            return transformToMultiSelectExpr(qnTree, qn)

        return null
    }


    @CompileStatic
    private String transformToBooleanExpr(CommonTree qnTree, IQuestion qn) {

        def eqTree = qnTree.parent as CommonTree
        List<CommonTree> children = eqTree.children

        if (isPath(children[1]) && isPath(children[0])) return null

        CommonTree leftTree = qnTree
        CommonTree rightTree = children.find { it != qnTree }

        def leftString = ParserUtils.emitTailString(leftTree)
        def rightString = ParserUtils.emitTailString(rightTree).replaceFirst(/\(\)/, '')
        def eq = eqTree.type == EQ ? '=' : '!='

        if (rightString == 'true' || rightString == 'false')
            return "$leftString $eq '$rightString'"
        if (rightString.startsWith("'") || rightString.startsWith('"'))
            return "$leftString $eq $rightString"

        //recursively make the rightString Compatible
        rightString = makeCompatibleSubXpath(rightTree)
        return "$leftString = string($rightString)"

    }

    @CompileStatic
    private String transformToMultiSelectExpr(CommonTree qnTree, IQuestion qn) {

        CommonTree parent = qnTree.parent as CommonTree
        List<CommonTree> children = parent.children

        CommonTree leftTree = qnTree
        CommonTree rightTree = children.find { it != qnTree }

        if (children[1] == qnTree && isPath(children[0])) {
            def rQn = findQuestion(children[0], qn.parentForm, numberedBindings)
            if (rQn instanceof MultiSelectQuestion) return null
        }

        def leftString = ParserUtils.emitTailString(leftTree)
        def rightString = extractExpr(rightTree, _xpath)

        def finalExpr
        if (rightString.startsWith('"') || rightString.startsWith("'")) {
            def rightParts = rightString
                    .replaceAll(/('|")/, '').trim().split(',')

            finalExpr = rightParts.collect { "selected($leftString, '$it')" }.join(' or ')

        } else {
            rightString = makeCompatibleSubXpath(rightTree)
            finalExpr = "selected($leftString, $rightString)"
        }

        if (parent.type == NEQ) finalExpr = "not($finalExpr)"

        return finalExpr
    }

    private String makeCompatibleSubXpath(CommonTree rightTree) {
        def rightString = extractExpr(rightTree, _xpath)
        List<CommonTree> trees = ParserUtils.findAllDeepSelf(rightTree) { true }
        visitedTrees.addAll(trees)
        rightString = makeODKCompatibleXPath(form, rightString, numberedBindings)
        return rightString
    }

    //**************************************************//
    //                  STATIC METHODS                  //
    //**************************************************//

    static String makeODKCompatibleXPath(Form form, String xpath, boolean numberedBindings) {
        return new ODKXpathUtil(form, xpath, numberedBindings).makeCompatible()
    }

    static String getOXDJRCountOnRepeatValidation(String reg) {
        XPathUtil xp = new XPathUtil(reg)

        def children = xp.tree.children
        if (children?.size() > 0) {
            CommonTree left = children[0] as CommonTree
            CommonTree right = children[1] as CommonTree

            def leftSide = ParserUtils.emitTailString(left)
            if (leftSide != 'length(.)') return null

            def tail = ParserUtils.emitTailString(right)

            if (right.type == ABSPATH) return tail

            if (right.type == RELPATH) return "/$tail"

            //if (right.token.type == NUMBER)  return right.emitTailString() //noODK Support
        }
        return null
    }
}
