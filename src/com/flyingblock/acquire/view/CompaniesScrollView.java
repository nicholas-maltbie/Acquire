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
     * @param stockFont Font of stock counts.
     * @param stockStyle Style of stock counts.
     * @param background Background color of the panel.
     */
    public CompaniesScrollView(List<Corporation> corporations, String nameFont,
            String stockFont, int stockStyle, Color background)
    {
        super();
        views = new ArrayList<>();
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        for(Corporation c : corporations)
        {
            CorporationView view = new CorporationView(c, nameFont,
                    stockFont, stockStyle,background);
            view.setBorder(BorderFactory.createLineBorder(c.getColor(), 5));
            pane.add(view);
            views.add(view);
        }
        this.setViewportView(pane);
        this.setBackground(background);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
    
    /**
     * Updates the view for current company data.
     */
    public void update()
    {
        for(CorporationView view : views)
        {
            view.update();
        }
    }
    
    /**
     * Sets the preferred size for the corporation views.
     * @param dims Dimensions of the panels
     */
    public void setPreferredCompanySize(Dimension dims)
    {
        for(CorporationView view : views)
            view.setPreferredSize(dims);
    }
}
