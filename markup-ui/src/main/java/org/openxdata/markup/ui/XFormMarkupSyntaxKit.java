package org.openxdata.markup.ui;

import jsyntaxpane.DefaultSyntaxKit;

/**
 * @author kay
 */
public class XFormMarkupSyntaxKit extends DefaultSyntaxKit {

    public XFormMarkupSyntaxKit() {
        super(new org.openxdata.markup.ui.XFormMarkupLexer());
    }

}
