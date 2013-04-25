package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
interface HasQuestions {

    List<IQuestion> getQuestions()

    void addQuestion(IQuestion question)

    String getBinding()

    String getAbsoluteBinding()

    List<IQuestion> getAllQuestions()

    Form getParentForm()

}
