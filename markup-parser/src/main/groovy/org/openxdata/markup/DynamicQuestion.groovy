package org.openxdata.markup

import org.openxdata.markup.exception.InvalidAttributeException
import org.openxdata.markup.exception.ValidationException

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

    DynamicQuestion() {}

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
        return parentForm.dynamicOptions."${dynamicInstanceId}"
    }

    public boolean validate() {
        if (!parentForm.dynamicOptions."$dynamicInstanceId")
            throw new ValidationException("DynamicQuestion[$text] Instance ID[$dynamicInstanceId] does not exit in the form")

        if (!parentQuestionId)
            throw new ValidationException("DynamicQuestion[$text] parent question has not been set. Please set the parent using the [@parent] attribute")

        def parentQn = Form.findQuestionWithBinding(parentQuestionId, parentForm)

        if (!parentQn)
            throw new InvalidAttributeException("DynamicQuestion[$text] has an invalid parent question id[$parentQuestionId]")
    }

    String getIndexedParentQuestionId() {
        return Form.findQuestionWithBinding(parentQuestionId, parentForm).indexedBinding
    }

    String getIndexedAbsParentBinding() {
        return Form.findQuestionWithBinding(parentQuestionId, parentForm).indexedAbsoluteBinding
    }

    String getAbsParentBinding() {
        return Form.findQuestionWithBinding(parentQuestionId, parentForm).absoluteBinding
    }
}
