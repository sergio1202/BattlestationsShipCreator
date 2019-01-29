package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.MoveableItem;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

public class ModuleZoomDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4156424361923668880L;
	private final SelectionPanel msp;

	public ModuleZoomDialog(MoveableItem module)
	{
		super();
		setTitle(module.getDescription());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ImageIcon icon = new ImageIcon(getClass().getResource("/img/tentac_ship_icon.png"));
		setIconImage(icon.getImage());
		this.msp = new SelectionPanel(false);
		msp.setWidth(250);
		msp.setHeight(250);
		msp.setMoveableItem(module);
		this.add(msp);
//
//        Would be nice to do
//
//        addKeyListener(new KeyAdapter()
//        {
//            @Override
//            public void keyReleased(KeyEvent e)
//            {
//                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER
//                    || e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE)
//                {
//                    // we're done for now....
//                    dispose();
//                }
//            }
//        });
		pack();
	}
}
