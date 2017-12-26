package org.openxdata.markup.exception

import groovy.transform.CompileStatic
import org.openxdata.markup.IFormElement

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/6/13
 * Time: 5:41 PM
 * To change this template use File | Settings | File Templates.
 */
@CompileStatic
class DuplicateElementException extends Exception {

    DuplicateElementException(List<IFormElement> elements) {
        this.elements = elements
    }


    @Override
    String getMessage() {
        return """The Elements below generate the same binding. Please assign different Ids
                  |${elements*.debugString.join(',\n')}""".stripMargin()
    }

    List<IFormElement> elements
}
