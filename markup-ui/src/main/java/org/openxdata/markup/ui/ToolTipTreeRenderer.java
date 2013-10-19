package org.openxdata.markup.ui;

import org.openxdata.markup.IQuestion;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ToolTipTreeRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        final Component cmp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            Object userObject = node.getUserObject();
            if (userObject instanceof IQuestion) {
                IQuestion qn = (IQuestion) userObject;
                renderQuestion(cmp, qn);
            } else {
                this.setToolTipText(userObject + "");
            }
        }

        return cmp;
    }

    private void renderQuestion(Component cmp, IQuestion qn) {

        if (qn.getIndexedBinding().length() > 64)
            cmp.setForeground(Color.red);

        String tooltip = "#" + qn.getQuestionIdx() + ' ' + qn.getBinding() + '-'+qn.getText();

        this.setToolTipText(tooltip);
    }
}