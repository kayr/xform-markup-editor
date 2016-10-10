package org.openxdata.markup.ui

import org.openxdata.markup.Form
import org.openxdata.markup.HasQuestions
import org.openxdata.markup.IFormElement
import org.openxdata.markup.Study
import org.openxdata.markup.deserializer.MarkupDeserializer

import javax.swing.*
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import java.awt.*

import static javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION

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
        tree.with {
            addTreeSelectionListener(owner)
            selectionModel.selectionMode = SINGLE_TREE_SELECTION
            showsRootHandles = false
            font = new Font(font.name, Font.PLAIN, 11)
            cellRenderer = new ToolTipTreeRenderer()
        }

        ToolTipManager.sharedInstance().registerComponent(tree)
        add(new JScrollPane(tree));
    }

    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    def updateTree(Study study, Closure listener) {
        this.listener = listener
        clear()

        rootNode.setUserObject(study)
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

    private def renderQuestions(DefaultMutableTreeNode rootNode2, HasQuestions hasQuestions) {
        for (qn in hasQuestions.elements) {
            DefaultMutableTreeNode childNode = addObject(rootNode2, qn)
            if (qn instanceof HasQuestions)
                renderQuestions(childNode, qn)
        }
    }

    void showError(String error) {
        clear()
        rootNode.setUserObject(error)
        tree.setEnabled(false)
    }


    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
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
        tree.scrollRowToVisible(level)
    }

    public void expandTo(IFormElement level) {

        def parent = level
        def hierarchy = []
        while (parent) {
            hierarchy << parent
            parent = parent.parent
        }
        hierarchy << rootNode.userObject

        int currentRow = 0
        for (formElem in hierarchy.reverse()) {

            for (currentRow = 0; currentRow < tree.getRowCount(); currentRow++) {
                def node = tree.getPathForRow(currentRow).lastPathComponent.asType(DefaultMutableTreeNode);
                if (node.getUserObject().is(formElem)) {
                    tree.expandRow(currentRow)
                    break
                }
            }

        }

        tree.setSelectionRow(currentRow)
        tree.scrollRowToVisible(currentRow)
    }

    private boolean keyboardUpdateTriggered = false, mouseUpdateTriggered = false


    @Override
    void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode component = (DefaultMutableTreeNode) tree.lastSelectedPathComponent
        if (!keyboardUpdateTriggered && component?.userObject instanceof IFormElement) {
            mouseUpdateTriggered = true
            listener.call(component.userObject)
        }
    }

    void selectNodeForLine(int caretLine) {
        if (mouseUpdateTriggered) {
            mouseUpdateTriggered = false
            return
        }

        def object = rootNode.getUserObject()
        if (!(object instanceof Study)) return
        def study = object as Study
        def element = study.getElementClosestToLine(caretLine)
        keyboardUpdateTriggered = true
        expandTo(element)
        keyboardUpdateTriggered = false
    }


    static main(args) {
        StudyTreeBuilder b = new StudyTreeBuilder()

        JFrame f = new JFrame()
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        f.add(b)
        f.setSize(400, 500)
        f.setVisible(true)

        def parser = new MarkupDeserializer(Resources.oxdSampleForm)
        def study = parser.study()

        SwingUtilities.invokeLater {
            b.updateTree(study) { println it }
        }
        SwingUtilities.invokeLater {
            b.expandTo(study.forms.first()['height'])
        }
    }
}
