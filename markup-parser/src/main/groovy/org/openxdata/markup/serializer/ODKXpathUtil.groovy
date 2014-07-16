package org.openxdata.markup.serializer

import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.Form
import org.openxdata.markup.MultiSelectQuestion
import org.openxdata.markup.XPathUtil
import org.openxdata.xpath.XPathParser

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

            if (right.token.type == XPathParser.NUMBER)
                return right.emitTailString()

        }
        return null
    }

    /**
     * This method mainly deals with multi-selects
     * @param form
     * @param xpath
     * @return
     */
    static String makeODKCompatibleXPath(Form form, String xpath) {
        XPathUtil xp = new XPathUtil(xpath)


        def transFormer = { StringBuilder builder, CommonTree tree, int offset ->
            def compatibleExpr = makeMultiSelectCompatibleExpression(tree, xpath)
            if (compatibleExpr) {

                def start = tree.charPositionInLine + offset
                //todo y add +1: weird will figure this out later add a one to compensate of ' or "
                def end = XPathUtil.getLastIndex(tree.parent) + offset + 1

                println "**Inserting $compatibleExpr into ($start,$end)"
                builder.replace(start, end, compatibleExpr)
            }
        }

        def filter = {
            if (!it.isPath())
                return false
            def qn = form.getQuestion(XPathUtil.getNodeName(it.emitTailString()))
            return qn != null && qn instanceof MultiSelectQuestion
        }
        return xp.tranformXPath(filter, transFormer)

    }

    static String makeMultiSelectCompatibleExpression(CommonTree tree, String xpath) {
        def parent = tree.getParent() as CommonTree

        //make sure we have a path and literal
        List<CommonTree> children = parent.children
        def isPathAndLiteral = children.any { it.isPath() } &&
                children.any { it.token.type == XPathParser.LITERAL }

        if (!isPathAndLiteral) return null

        //we only convert != and =
        if (parent.token.type == XPathParser.EQ || parent.token.type == XPathParser.NEQ) {
            def start = tree.charPositionInLine
            def end = XPathUtil.getLastIndex(parent)
            def multiSelectEqExpr = xpath.substring(start, end + 1)
            return convertToSelectedOrNotSelected(multiSelectEqExpr)
        }
        return null
    }

    static String convertToSelectedOrNotSelected(String expr) {
        //this is based on a assumption that '!' or '=' can never be in bindings
        def parts = expr.split(/[!]*=/)

        def left = parts[0].trim()
        def right = parts[1]

        // make sure right is a literal otherwise Swap
        if (left.startsWith("'") || left.startsWith('"')) {
            (left, right) = [right, left]
        }

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
