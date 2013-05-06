package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/30/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractSelectionQuestion extends AbstractQuestion implements ISelectionQuestion {

    def options = []

    AbstractSelectionQuestion() {}

    AbstractSelectionQuestion(String question) {
        super(question)
    }

    List<IOption> getOptions() {
        options
    }

    @Override
    void addOption(IOption option) {
        option.setParent(this)
        options << option
    }
}
