package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
interface IOption {

    String getText()

    String getBind()

    ISelectionQuestion getParent()

    void setParent(ISelectionQuestion qn)

    int getLine()

    void setLine(int i)

    String getMarkUpText()
}
