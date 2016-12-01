package org.openxdata.markup
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class Page implements HasQuestions {


    Page() { init() }

    Page(String name) {
        init()
        this.name = name
    }

    private def init() {
        this.xformType = XformType.GROUP
    }

    String toString() {
        def newText = name ?: 'Group'
        if (id) return "${questionIdx}. $newText"
        return newText
    }

    //pages should not trying to generate ids
    String getBinding() {
        return this.id
    }
}
