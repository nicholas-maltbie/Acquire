/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.view;

import com.flyingblock.acquire.model.Corporation;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * View of the companies.
 * @author Nicholas Maltbie
 */
public class CompaniesScrollView extends JScrollPane
{
    /**
     * CorporationView of the companies.
     */
    private List<CorporationView> views;
    
    /**
     * Constructs a scroll pane for multiple companies.
     * @param corporations Companies to display in a game.
     * @param nameFont Font of company names.
     * @param nameStyle Style of company names.
     * @param stockFont Font of stock counts.
     * @param stockStyle Style of stock counts.
     * @param background Background color of the panel.
     * @param preferedCompanySize Size of the panels.
     */
    public CompaniesScrollView(List<Corporation> corporations,
            String nameFont, int nameStyle, String stockFont, int stockStyle,
            Color background, Dimension preferedCompanySize)
    {
        super();
        views = new ArrayList<>();
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        for(Corporation c : corporations)
        {
            CorporationView view = new CorporationView(c, nameFont, nameStyle,
                    stockFont, stockStyle,background);
            view.setPreferredSize(preferedCompanySize);
            view.setBorder(BorderFactory.createLineBorder(c.getColor(), 5));
            pane.add(view);
            views.add(view);
        }
        this.setViewportView(pane);
        this.setBackground(background);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setBounds(0,0,preferedCompanySize.width, preferedCompanySize.height);
    }
    
    /**
     * Adds a button listener to the panel.
     * @param listener Listener to add.
     */
    public void addButtonListener(CompanyPanelListener listener)
    {
        for(CorporationView view : views)
        {
            view.addButtonListener(listener);
        }
    }
    
    /**
     * Removes a button listener to the panel.
     * @param listener Listener to remove.
     */
    public void removeButtonListener(CompanyPanelListener listener)
    {
        for(CorporationView view : views)
        {
            view.removeButtonListener(listener);
        }
    }
}