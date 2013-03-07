package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/30/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
class SingleSelectQuestion extends AbstractSelectionQuestion implements ISelectionQuestion {

    SingleSelectQuestion() {}

    SingleSelectQuestion(String question) {
        super(question)
    }


    @Override
    String getType() {
        return 'string'
    }
}
