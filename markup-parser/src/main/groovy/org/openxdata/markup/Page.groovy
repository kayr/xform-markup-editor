package org.openxdata.markup
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class Page implements HasQuestions {


    Page() {}

    Page(String name) {
        this.name = name
    }


    String toString() {
        name
    }

    String getAbsoluteBinding() {
        return "$parent.absoluteBinding"
    }

    String getBinding() {
        return null
    }

}
