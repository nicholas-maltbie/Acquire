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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
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
        
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    /**
     * This method will update the screen graphics based on the board that this
     * view was created from.
     */
    public void update()
    {
        for(int row = 0; row < board.getNumRows(); row++)
            for(int col = 0; col < board.getNumCols(); col++)
                if(board.isEmpty(row, col) != hotelButtons.isEmpty(row, col))
                    hotelButtons.get(row, col).setHotel(board.get(row, col));
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
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(gridColor);
        int width = this.getWidth()/board.getNumCols();
        int height = this.getHeight()/board.getNumRows();
        
        for(int row = 1; row < board.getNumRows(); row++)
        {
            for(int col = 1; col < board.getNumCols(); col++)
                g2.draw(new Line2D.Float(width*col,0,width*col,this.getHeight()));
            g2.draw(new Line2D.Float(0,height*row,this.getWidth(),height*row));
        }
        
        if(lastLocation != null && lastLocation.getRow() >= 0 && lastLocation.getRow() < board.getNumRows()
                && lastLocation.getCol() >= 0 && lastLocation.getCol() < board.getNumCols())
        {
            g2.setColor(new Color(255,255,255, 50));
            g2.fill(new Rectangle2D.Float(lastLocation.getCol()*width, 
                    lastLocation.getRow()*height, width, height));
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

    @Override
    public void mouseClicked(MouseEvent e) 
    {
        Location loc = getGridLocation(e.getPoint());
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.buttonClicked(loc, e);
            });
        }
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        Location loc = getGridLocation(e.getPoint());
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.buttonPressed(loc, e);
            });
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        Location loc = getGridLocation(e.getPoint());
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.buttonRelease(loc, e);
            });
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.enterBoard(e);
            });
        }
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {
        lastLocation = null;
        this.repaint();
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.exitBoard(e);
            });
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        //Nothing happens here so there is no code for it.
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
        Location loc = getGridLocation(e.getPoint());
        if(loc != lastLocation)
        {
            lastLocation = loc;
            this.repaint();
        }
        else if(lastLocation != null && !loc.equals(lastLocation))
        {
            lastLocation = loc;
            this.repaint();
        }
    }
    
}
