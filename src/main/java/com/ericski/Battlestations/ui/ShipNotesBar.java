package com.ericski.Battlestations.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;

public class ShipNotesBar extends JPanel
{
    private static final String TOGGLEACTION = "TOGGLEACTION";

    private static final long serialVersionUID = 1L;

    ShipCreatorPanel parentPanel;
    JButton hiderButton;

    JXCollapsiblePane modulePane;
    JPanel hider;

    boolean collapsed = true;

    @Override
    public Dimension getPreferredSize()
    {
        if (collapsed)
        {
            return hider.getPreferredSize();
        }
        else
        {
            Dimension dim1 = hider.getPreferredSize();
            Dimension dim2 = modulePane.getPreferredSize();

            return new Dimension(dim1.width, dim1.height + dim2.height);
        }
    }

    public ShipNotesBar(ShipCreatorPanel parentPanel)
    {
        super(new BorderLayout());
        this.parentPanel = parentPanel;
        hider = new JGradPanel(Color.BLACK.brighter().brighter(), Color.WHITE);
        hider.setLayout(new BoxLayout(hider, BoxLayout.X_AXIS));
        hiderButton = new JButton("/\\");
        hiderButton.setFont(hiderButton.getFont().deriveFont(6));
        hiderButton.setMargin(new Insets(0, 0, 0, 0));
        hider.add(hiderButton);
        hider.add(Box.createHorizontalStrut(5));
        JLabel label = new JLabel("Ship Notes");
        label.setForeground(Color.WHITE);
        hider.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent evt)
            {
                if (!evt.isPopupTrigger())
                {
                    showHideShipBar();
                }
            }
        });
        hider.add(label);
        add(hider, BorderLayout.WEST);
        hiderButton.setActionCommand(TOGGLEACTION);
        hiderButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                String key = e.getActionCommand();
                if (TOGGLEACTION.equals(key))
                {
                    showHideShipBar();
                }
            }
        });

        //modulePane = new JPanel(new BorderLayout());
        modulePane = new JXCollapsiblePane(Direction.DOWN);

        modulePane.setLayout(new BorderLayout());
        modulePane.setCollapsed(true);
        modulePane.add(new JLabel("FOO"));
        modulePane.addPropertyChangeListener("collapsed", new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                if ((boolean) evt.getNewValue())
                {
                    hiderButton.setText("/\\");
                    collapsed = true;
                }
                else// if ("expanded".equals((String)evt.getNewValue()) ) 
                {
                    hiderButton.setText("\\/");
                    collapsed = false;
                }
            }
        });
        modulePane.setMaximumSize(new Dimension(400, 400));
        add(modulePane, BorderLayout.CENTER);
    }

    private void showHideShipBar()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                if (modulePane.isCollapsed())
                {
                    collapsed = false;
                    hiderButton.setText("<>");
                    modulePane.setCollapsed(false);
                }
                else
                {
                    hiderButton.setText("<>");
                    modulePane.setCollapsed(true);
                }
            }
        });
    }
}
