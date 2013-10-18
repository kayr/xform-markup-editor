package org.openxdata.markup.ui

import org.openxdata.markup.*

import javax.swing.*
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel
import java.awt.*

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 10/17/13
 * Time: 8:02 AM
 * To change this template use File | Settings | File Templates.
 */
class StudyTreeBuilder extends JPanel implements TreeSelectionListener {

    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private Closure listener


    StudyTreeBuilder() {
        super(new GridLayout(1, 0));
        init()
    }

    def init() {
        rootNode = new DefaultMutableTreeNode("Root Node");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this)
        tree.setShowsRootHandles(false);
        JScrollPane scrollPane = new JScrollPane(tree);
        tree.setFont(new Font(tree.font.name, Font.PLAIN, 11))
        tree.setCellRenderer(new ToolTipTreeRenderer())
        ToolTipManager.sharedInstance().registerComponent(tree)
        add(scrollPane);

    }

    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    def updateTree(Study study, Closure listener) {
        this.listener = listener
        clear()

        rootNode.setUserObject(study.name)
        renderForms(study.forms)

        treeModel.reload()
        tree.setEnabled(true)
    }

    private void renderForms(java.util.List<Form> forms) {
        for (form in forms) {

            def formNode = addObject(rootNode, form)

            for (page in form.pages) {
                def pageNode = addObject(formNode, page)
                renderQuestions(pageNode, page)
            }

        }
    }

    def renderQuestions(DefaultMutableTreeNode rootNode2, HasQuestions hasQuestions) {
        for (qn in hasQuestions.questions) {
            DefaultMutableTreeNode childNode = addObject(rootNode2, qn)
            if (qn instanceof HasQuestions)
                renderQuestions(childNode, qn)
        }
    }

    void showError(String error){
        clear()
        rootNode.setUserObject(error)
        tree.setEnabled(false)
    }


    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        if (parent == null) {
            parent = rootNode;
        }
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
        return childNode;
    }

    public void expand(int level) {
        for (int i = 0; i < level && i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    @Override
    void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode component = tree.lastSelectedPathComponent
        if (component?.userObject instanceof IQuestion)
            listener.call(component.userObject)

    }

    static main(args) {
        StudyTreeBuilder b = new StudyTreeBuilder()

        JFrame f = new JFrame()
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        f.add(b)
        f.setSize(400, 500)
        f.setVisible(true)

        def parser = Util.createParser(Resources.oxdSampleForm)
        SwingUtilities.invokeLater { b.updateTree(parser.study()) { println it } }
    }
}
