package com.ericski.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

public class JPopupTextArea extends JTextArea
{
    private static final long serialVersionUID = 1L;
    private static HashMap<String, Action> actions;

    public JPopupTextArea()
    {
        super();
        addPopupMenu();
    }

    public JPopupTextArea(int rows, int cols)
    {
        super(rows, cols);
        addPopupMenu();
    }

    private void addPopupMenu()
    {
        createActionTable();

        JPopupMenu menu = new JPopupMenu();
        menu.add(getActionByName(DefaultEditorKit.copyAction, "Copy"));
        menu.add(getActionByName(DefaultEditorKit.cutAction, "Cut"));
        menu.add(getActionByName(DefaultEditorKit.pasteAction, "Paste"));
        menu.add(new JSeparator());
        menu.add(getActionByName(DefaultEditorKit.selectAllAction, "Select All"));
        add(menu);

        addMouseListener(
            new PopupTriggerMouseListener(
                menu,
                this));

		//no need to hold the references in the map,
        // we have used the ones we need.
        actions.clear();
    }

    private Action getActionByName(String name, String description)
    {
        Action a = actions.get(name);
        if (a == null)
        {
            a = actions.get(description);
        }
        a.putValue(Action.NAME, description);
        return a;
    }

    private void createActionTable()
    {
        actions = new HashMap<>();
        Action[] actionsArray = getActions();
        for (Action a : actionsArray)
        {
            actions.put((String) a.getValue(Action.NAME), a);
        }
    }

    public static class PopupTriggerMouseListener extends MouseAdapter
    {
        private final JPopupMenu popup;
        private final JComponent component;

        public PopupTriggerMouseListener(JPopupMenu popup, JComponent component)
        {
            this.popup = popup;
            this.component = component;
        }

        //some systems trigger popup on mouse press, others on mouse release, we want to cater for both
        private void showMenuIfPopupTrigger(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                popup.show(component, e.getX() + 3, e.getY() + 3);
            }
        }

		//according to the javadocs on isPopupTrigger, checking for popup trigger on mousePressed and mouseReleased
        //should be all  that is required
        //public void mouseClicked(MouseEvent e) 
        //{
        //    showMenuIfPopupTrigger(e);
        //}
        @Override
        public void mousePressed(MouseEvent e)
        {
            showMenuIfPopupTrigger(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            showMenuIfPopupTrigger(e);
        }
    }
}
