package org.openxdata.markup

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 10:19 PM
 * To change this template use File | Settings | File Templates.
 */
interface ISelectionQuestion extends IQuestion {

    List<IOption> getOptions()

    void addOption(IOption option)

}
