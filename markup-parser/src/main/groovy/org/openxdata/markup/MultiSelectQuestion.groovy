package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 9:27 PM
 * To change this template use File | Settings | File Templates.
 */
class MultiSelectQuestion  implements ISelectionQuestion {

    MultiSelectQuestion() {}

    MultiSelectQuestion(String question) {
        setText(question)
    }

    @Override
    String getType() {
        return 'string'
    }
}
