package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/2/13
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
class DynamicQuestion extends AbstractQuestion implements ISelectionQuestion {

    String dynamicInstanceId
    String parentQuestionId

    DynamicQuestion(String question) {
        super(question)
    }

    @Override
    String getType() {
        return 'string'
    }

    @Override
    void addOption(IOption option) {
    }

    List<IOption> getOptions() {
       return parentForm.dynamicOptions."${binding}"
        //return options
    }
}
