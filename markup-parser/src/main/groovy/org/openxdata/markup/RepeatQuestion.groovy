package org.openxdata.markup

import org.openxdata.markup.exception.DuplicateQuestionException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class RepeatQuestion extends AbstractQuestion implements HasQuestions {

    List<IQuestion> questions = []

    RepeatQuestion() {}

    RepeatQuestion(String question) {
        super(question)
    }

    @Override
    List<IQuestion> getQuestions() {
        return questions
    }

    @Override
    void addQuestion(IQuestion question) {

        def dupeQuestion = Form.findQuestionWithBinding(question.binding, hasQuestions)

        if (dupeQuestion) {
            throw new DuplicateQuestionException(question1: question, question2: dupeQuestion)
        }

        question.setParent(this)
        questions << question
    }



    @Override
    String getType() {
        return 'repeat'
    }


}
