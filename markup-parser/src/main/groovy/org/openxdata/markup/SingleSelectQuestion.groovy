package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/30/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
class SingleSelectQuestion implements ISelectionQuestion {

    SingleSelectQuestion() {init()}

    SingleSelectQuestion(String question) {
        setText(question)
        init()
    }

    private def init(){ this.xformType = XformType.SELECT1 }


    @Override
    String getType() {
        return 'string'
    }
}
