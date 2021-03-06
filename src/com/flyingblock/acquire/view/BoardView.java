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

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Board;
import com.flyingblock.acquire.model.Location;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Graphical representation of an AcquireBoard.
 * @author Nicholas Maltbie
 */
public class BoardView extends JPanel implements MouseListener, MouseMotionListener
{
    /**
     * Parent this is drawn on and will listen to.
     */
    private Container parent;
    /**
     * Listeners to this BoardView.
     */
    private final List<BoardListener> listeners;
    /**
     * Color of empty cells.
     */
    private Color emptyColor;
    /**
     * Color for outlining grid.
     */
    private Color gridColor;
    /**
     * List of buttons within the grid.
     */
    private Board<HotelView> hotelButtons;
    /**
     * Board that this will graphically represent.
     */
    private AcquireBoard board;
    /**
     * Font type for the board graphics.
     */
    private String font;
    
    /**
     * Constructs a board view with specified parameters.
     * @param board Board to graphically represent.
     * @param background Background color for the board.
     * @param emptyColor Text color for empty cells.
     * @param gridColor Color for cell border lines.
     * @param font Font type for the board graphics.
     */
    public BoardView(AcquireBoard board, Color background, Color emptyColor, Color gridColor, String font)
    {
        this.setBackground(background);
        this.board = board;
        this.font = font;
        this.gridColor = gridColor;
        this.emptyColor = emptyColor;
        this.listeners = new ArrayList<>();
        this.setLayout(new GridLayout(board.getNumRows(), board.getNumCols()));
        hotelButtons = new Board<>(board.getNumRows(), board.getNumCols());
        
        for(int row = 0; row < board.getNumRows(); row++)
            for(int col = 0; col < board.getNumCols(); col++)
            {
                HotelView hotel;
                if(board.isEmpty(row, col))
                    hotel = new HotelView(new Location(row, col), Color.WHITE, emptyColor, font);                    
                else
                    hotel = new HotelView(board.get(row, col), background, font);
                hotel.setSizeString("A" + (int)(Math.pow(10, Math.log10(board.getNumCols()))-1));
                hotelButtons.set(row, col, hotel);
                this.add(hotel);
            }
    }
    
    
    /**
     * Makes this component listen to parent for events.
     * @param parent Component to listen to.
     */
    public void setupListener(Container parent)
    {
        parent.addMouseListener(this);
        parent.addMouseMotionListener(this);
        this.parent = parent;
    }
    
    /**
     * Removes this component as a listener to the parent.
     * @param parent Must first be listening to this.
     */
    public void closeListener(Container parent)
    {
        parent.removeMouseListener(this);
        parent.removeMouseMotionListener(this);
        this.parent = this;
    }
    
    /**
     * This method will update the screen graphics based on the board that this
     * view was created from.
     */
    public void update()
    {
        for(int row = 0; row < board.getNumRows(); row++)
            for(int col = 0; col < board.getNumCols(); col++)
                if(!board.isEmpty(row, col) && board.get(row, col) != null)
                    hotelButtons.get(row, col).setHotel(board.get(row, col));
                else
                    hotelButtons.get(row, col).setHotel(new Location(row, col));
        this.repaint();
    }
    
    /**
     * Adds a listener to the board listeners. Board listeners listen to mouse
     * actions that are located within the board.
     * @param listener Listener to add.
     */
    public void addBoardListener(BoardListener listener)
    {
        synchronized(listeners)
        {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a listener to the board listeners. Board listeners listen to mouse
     * actions that are located within the board.
     * @param listener Listener to remove.
     * @return If the listener was successfully removed from the list.
     */
    public boolean removeBoardListener(BoardListener listener)
    {
        synchronized(listeners)
        {
            return listeners.remove(listener);
        }
    }
    
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getBackground());
        g2.fill(new Rectangle(0,0,this.getWidth(),this.getHeight()));
        int width = this.getWidth()/board.getNumCols();
        int height = this.getHeight()/board.getNumRows();
        
        if(lastLocation != null && lastLocation.getRow() >= 0 && lastLocation.getRow() < board.getNumRows()
                && lastLocation.getCol() >= 0 && lastLocation.getCol() < board.getNumCols())
        {
            g2.setColor(new Color(255-getBackground().getRed(),
                    255-getBackground().getGreen(),255-getBackground().getBlue(), 50));
            g2.fill(new Rectangle2D.Float(lastLocation.getCol()*width, 
                    lastLocation.getRow()*height, width, height));
        }
        
        g2.setColor(gridColor);
        for(int row = 0; row <= board.getNumRows(); row++)
        {
            for(int col = 0; col <= board.getNumCols(); col++)
                g2.draw(new Line2D.Float(width*col,0,width*col,height*board.getNumRows()));
            g2.draw(new Line2D.Float(0,height*row,width*board.getNumCols(),height*row));
        }
    }
    
    /**
     * Gets the grid location in which point p is located.
     * @param p Point to check.
     * @return Returns location of the the grid cell which contains the point.
     */
    private Location getGridLocation(Point p)
    {
        int width = this.getWidth()/board.getNumCols();
        int height = this.getHeight()/board.getNumRows();
        return new Location((int)p.getY()/height, (int)p.getX()/width);
    }
    
        /**
     * Gets where this point is found on the screen or null if the point is not 
     * within the JPanel.
     * @param onScreen Point on the screen.
     * @return Returns Point within the JPanel or null if it is not within the
     * JPanel.
     */
    private Point getRelativeToThis(Point onScreen)
    {
        Point screen = this.getLocationOnScreen();
        Rectangle bounds = new Rectangle(screen.x, screen.y,
                this.getWidth(), this.getHeight());
        if(!bounds.contains(onScreen))
            return null;
        return new Point(onScreen.x - screen.x, onScreen.y - screen.y);
    }

    @Override
    public void mouseClicked(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            Location loc = getGridLocation(relative);
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) -> {
                    listener.buttonClicked(loc, e);
                });
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            Location loc = getGridLocation(relative);
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) -> {
                    listener.buttonPressed(loc, e);
                });
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            Location loc = getGridLocation(relative);
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) -> {
                    listener.buttonRelease(loc, e);
                });
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {
        
    }

    /**
     * This is the last saved location of the mouse, it's used to identify if
     * the mouse has moved within the grid. Additionally, to highlight the
     * current mouse location.
     */
    private Location lastLocation;
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            Location loc = getGridLocation(relative);
            if(loc != lastLocation)
            {
                lastLocation = loc;
                parent.repaint();
            }
            else if(lastLocation != null && !loc.equals(lastLocation))
            {
                lastLocation = loc;
                parent.repaint();
            }
        }
        else
        {
            lastLocation = null;
            parent.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            Location loc = getGridLocation(relative);
            if(loc != lastLocation)
            {
                lastLocation = loc;
                parent.repaint();
            }
            else if(lastLocation != null && !loc.equals(lastLocation))
            {
                lastLocation = loc;
                parent.repaint();
            }
        }
        else
        {
            lastLocation = null;
            parent.repaint();
        }
    }
    
    /**
     * Gets the size of any piece within the board.
     * @return Returns the width and height of any hotel view within the board.
     */
    public Dimension getPieceBounds()
    {
        return new Dimension(this.getWidth()/board.getNumCols(), 
                this.getHeight()/board.getNumRows());
    }
}
