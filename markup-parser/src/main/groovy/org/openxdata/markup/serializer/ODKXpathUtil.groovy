package org.openxdata.markup.serializer

import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.Form
import org.openxdata.markup.MultiSelectQuestion
import org.openxdata.markup.XPathUtil

import static org.openxdata.xpath.XPathParser.*

/**
 * Created by kay on 7/16/14.
 */
public class ODKXpathUtil {

    static String getOXDJRCountOnRepeatValidation(String reg) {
        XPathUtil xp = new XPathUtil(reg)

        def children = xp.tree.children

        if (children?.size() > 0) {
            CommonTree left = children[0] as CommonTree
            CommonTree right = children[1] as CommonTree

            def leftSide = left.emitTailString()

            if (leftSide != 'length.')
                return null

            if (right.isPath()) {
                def path = right.emitTailString()
                path = path.startsWith('/') ? path : "/$path"
                return path
            }

            //ODK Currently does not support this
//            if (right.token.type == NUMBER)
//                return right.emitTailString()

        }
        return null
    }

    /**
     * This method mainly deals with multi-selects.
     * @param form
     * @param xpath
     * @return
     */
    //todo Consider using Regex instead of ANTLR
    static String makeODKCompatibleXPath(Form form, String xpath, boolean numberedBindings) {
        XPathUtil xp = new XPathUtil(xpath)


        def transFormer = { StringBuilder builder, CommonTree tree, int offset ->
            def compatibleExpr = makeSelectionACompatibleExpression(tree, xpath)
            if (compatibleExpr) {

                //make sure u get the position of the first toke in the expression
                def start = tree.parent.children[0].charPositionInLine + offset
                //todo y add +1: weird will figure this out later add a one to compensate of ' or "
                def end = XPathUtil.getLastIndex(tree.parent) + offset + 1
                builder.replace(start, end, compatibleExpr)
            }
        }

        def filter = {
            if (!it.isPath())
                return false
            def qnBinding = XPathUtil.getNodeName(it.emitTailString())
            if (numberedBindings)
                qnBinding = removeIndex(qnBinding)
            def qn = form.getQuestion(qnBinding)
            return qn != null && (qn instanceof MultiSelectQuestion || qn.type?.equalsIgnoreCase('boolean'))
        }
        return xp.tranformXPath(filter, transFormer)

    }

    private static String removeIndex(String binding) {
        return binding.replaceFirst(/(^_[0-9]+)/, '')
    }

    static String makeSelectionACompatibleExpression(CommonTree tree, String xpath) {
        def parent = tree.getParent() as CommonTree

        //make sure we have a path and literal
        List<CommonTree> children = parent.children
        def isPathAndLiteral = children.any { it.isPath() } &&
                children.any { it.token.type == LITERAL }

        def isPathAndBoolean = children.any { it.isPath() } &&
                children.any {
                    def lastChild = XPathUtil.getLastChild(it)
                    it.children.size() == 1 && (lastChild.type == TRUE || lastChild.type == FALSE)
                }

        if (!(isPathAndBoolean || isPathAndLiteral)) return null

        //we only convert != and =
        if (parent.token.type == EQ || parent.token.type == NEQ) {
            //make sure to get the first character in the expression
            def start = tree.parent.children[0].charPositionInLine
            def end = XPathUtil.getLastIndex(parent)
            def multiSelectEqExpr = xpath.substring(start, end + 1)
            return transformToProperSelection(multiSelectEqExpr)
        }
        return null
    }

    /**
     Transforms expressions like /f/q1 = true -> /f/q1 = 'true'
     or /f/q1 = 'male' -> selected(/f/q1,'male')
     or /f/q1 != 'male' -> not(selected(/f/q1,'male'))
     */
    static String transformToProperSelection(String expr) {
        //this is based on a assumption that '!' or '=' can never be in bindings
        def parts = expr.split(/[!]*=/)

        def left = parts[0].trim()
        def right = parts[1].trim()

        // make sure right is a literal otherwise Swap
        if (left.startsWith("'") || left.startsWith('"') || left.equals('true') || left.equals('false')) {
            (left, right) = [right, left]
        }


        def isBooleanSelect = right == 'true' || right == 'false'

        if (isBooleanSelect) {
            return expr.contains('!') ? "$left != '$right'" : "$left = '$right'"
        } else {
            def rightParts = right.replaceAll(/('|")/, '').trim().split(',')
            StringBuilder builder = new StringBuilder()

            int maxParts = rightParts.size()
            rightParts.eachWithIndex { String part, int i ->
                builder.append("selected($left, '$part')")
                if (i < maxParts - 1)
                    builder.append(' and ')
            }

            if (expr.contains('!'))
                return "not(${builder.toString()})"

            return builder.toString()
        }
    }


}
