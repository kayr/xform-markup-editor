package org.openxdata.markup
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */

@SuppressWarnings("ClashingTraitMethods")
class RepeatQuestion implements HasQuestions,IQuestion{


    RepeatQuestion() {}

    RepeatQuestion(String question) {
        setText(question)
    }


    @Override
    String getType() {
        return 'repeat'
    }

    @Override
    void setValue(Object value) {
//        throw new UnsupportedOperationException('You cannot set a value on a repeat question')
    }

}
