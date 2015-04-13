package com.ericski.Battlestations.ui;

import com.ericski.Battlestations.BattlestationColors;
import com.ericski.Battlestations.Module;
import com.ericski.Battlestations.ModuleFactory;
import com.l2fprod.common.swing.JOutlookBar;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class ModuleSelectorBar extends JOutlookBar
{

    private static final long serialVersionUID = 1L;
    private static final int MODULESIZE = 64;

    public ModuleSelectorBar()
    {
        super(JTabbedPane.RIGHT);
        JPanel allPanel = new JPanel();
        allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS));

        for (Module m : ModuleFactory.INSTANCE.getAllModules())
        {
            if (!"obsolete".equals(m.getProfession()))
            {
                allPanel.add(Box.createVerticalStrut(3));
                ModuleSelectionPanel msp = new ModuleSelectionPanel(-1, -1, false);                
                msp.setModule(m);
                msp.setWidth(MODULESIZE);
                msp.setHeight(MODULESIZE);
                msp.setShowToolTip(true);
                allPanel.add(msp);
            }
        }
        allPanel.add(Box.createVerticalStrut(3));
        JScrollPane allScroller = new JScrollPane(allPanel);
        allScroller.setPreferredSize(new Dimension(84, 256));
        allScroller.setMinimumSize(new Dimension(84, 128));
        addTab("All", allScroller);

        JPanel generalPanel = new JGradPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        ModuleSelectionPanel msp;
        for (Module m : ModuleFactory.INSTANCE.getAllModulesForProfession("general"))
        {
            generalPanel.add(Box.createVerticalStrut(3));
            msp = new ModuleSelectionPanel(-1, -1, false);
            msp.setModule(m);
            msp.setWidth(MODULESIZE);
            msp.setHeight(MODULESIZE);
            msp.setShowToolTip(true);
            generalPanel.add(msp);
        }
        generalPanel.add(Box.createVerticalStrut(3));
        JScrollPane generalScroller = new JScrollPane(generalPanel);
        generalScroller.setPreferredSize(new Dimension(84, 256));
        generalScroller.setMinimumSize(new Dimension(84, 128));
        addTab("General", generalScroller);

        JPanel pilotPanel = new JGradPanel();
        pilotPanel.setLayout(new BoxLayout(pilotPanel, BoxLayout.Y_AXIS));
        pilotPanel.setBackground(BattlestationColors.Piloting.getColor());
        for (Module m : ModuleFactory.INSTANCE.getAllModulesForProfession("pilotting"))
        {
            pilotPanel.add(Box.createVerticalStrut(3));
            msp = new ModuleSelectionPanel(-1, -1, false);
            msp.setModule(m);
            msp.setWidth(MODULESIZE);
            msp.setHeight(MODULESIZE);
            msp.setShowToolTip(true);
            pilotPanel.add(msp);
        }
        pilotPanel.add(Box.createVerticalStrut(3));
        JScrollPane pilotScroller = new JScrollPane(pilotPanel);
        pilotScroller.setPreferredSize(new Dimension(84, 256));
        pilotScroller.setMinimumSize(new Dimension(84, 128));
        addTab("Pilotting", pilotScroller);

        JPanel sciencePanel = new JGradPanel();
        sciencePanel.setLayout(new BoxLayout(sciencePanel, BoxLayout.Y_AXIS));
        sciencePanel.setBackground(BattlestationColors.Science.getColor());
        for (Module m : ModuleFactory.INSTANCE.getAllModulesForProfession("science"))
        {
            sciencePanel.add(Box.createVerticalStrut(3));
            msp = new ModuleSelectionPanel(-1, -1, false);
            msp.setModule(m);
            msp.setWidth(MODULESIZE);
            msp.setHeight(MODULESIZE);
            msp.setShowToolTip(true);
            sciencePanel.add(msp);
        }
        sciencePanel.add(Box.createVerticalStrut(3));
        JScrollPane scienceScroller = new JScrollPane(sciencePanel);
        scienceScroller.setPreferredSize(new Dimension(84, 256));
        scienceScroller.setMinimumSize(new Dimension(84, 128));
        addTab("Science", scienceScroller);

        JPanel engineerPanel = new JGradPanel();
        engineerPanel.setLayout(new BoxLayout(engineerPanel, BoxLayout.Y_AXIS));
        engineerPanel.setBackground(BattlestationColors.Engineering.getColor());
        
        for (Module m : ModuleFactory.INSTANCE.getAllModulesForProfession("engineering"))
        {
            engineerPanel.add(Box.createVerticalStrut(3));
            msp = new ModuleSelectionPanel(-1, -1, false);
            msp.setModule(m);
            msp.setWidth(MODULESIZE);
            msp.setHeight(MODULESIZE);
            msp.setShowToolTip(true);
            engineerPanel.add(msp);
        }
        engineerPanel.add(Box.createVerticalStrut(3));
        JScrollPane engineerScroller = new JScrollPane(engineerPanel);
        engineerScroller.setPreferredSize(new Dimension(84, 256));
        engineerScroller.setMinimumSize(new Dimension(84, 128));
        addTab("Engineering", engineerScroller);

        JPanel combatPanel = new JGradPanel();
        combatPanel.setLayout(new BoxLayout(combatPanel, BoxLayout.Y_AXIS));
        combatPanel.setBackground(BattlestationColors.Combat.getColor());
        combatPanel.add(Box.createVerticalStrut(3));
        for (Module m : ModuleFactory.INSTANCE.getAllModulesForProfession("combat"))       
        {
            combatPanel.add(Box.createVerticalStrut(3));
            msp = new ModuleSelectionPanel(-1, -1, false);       
            msp.setModule(m);
            msp.setWidth(MODULESIZE);
            msp.setHeight(MODULESIZE);
            msp.setShowToolTip(true);
            combatPanel.add(msp);
        }
        combatPanel.add(Box.createVerticalStrut(3));
        JScrollPane combatScroller = new JScrollPane(combatPanel);
        combatScroller.setPreferredSize(new Dimension(84, 256));
        combatScroller.setMinimumSize(new Dimension(84, 128));
        addTab("Combat", combatScroller);

        JPanel obsoletePanel = new JGradPanel();
        obsoletePanel.setLayout(new BoxLayout(obsoletePanel, BoxLayout.Y_AXIS));
        obsoletePanel.setBackground(new Color(50, 40, 10));
        obsoletePanel.add(Box.createVerticalStrut(3));
        for (Module m : ModuleFactory.INSTANCE.getAllModulesForProfession("obsolete"))
        {
            obsoletePanel.add(Box.createVerticalStrut(3));
            msp = new ModuleSelectionPanel(-1, -1, false);            
            msp.setModule(m);
            msp.setWidth(MODULESIZE);
            msp.setHeight(MODULESIZE);
            msp.setShowToolTip(true);
            obsoletePanel.add(msp);
        }
        obsoletePanel.add(Box.createVerticalStrut(3));
        JScrollPane obsoleteScroller = new JScrollPane(obsoletePanel);
        obsoleteScroller.setPreferredSize(new Dimension(84, 256));
        obsoleteScroller.setMinimumSize(new Dimension(84, 128));
        addTab("Obsolete", obsoleteScroller);

        setSelectedIndex(1);
    }
}
