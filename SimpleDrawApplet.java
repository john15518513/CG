/*
   In this simple drawing applet, the user can draw lines by pressing
   the mouse button and moving the mouse before releasing the button.
   A line is drawn from the point where the mouse button is pressed to the
   point where it is released.  A choice of colors is offered.
*/


import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;

public class SimpleDrawApplet extends Applet implements ItemListener {

    static final Color[] colorList = {
        // List of available colors; having this in an array makes
        // it much easier to write the itemStateChanged() routine.
        Color.black, Color.gray, Color.red, Color.green, Color.blue,
        new Color(200,0,0), new Color(0,180,0), new Color(0,0,180),
        Color.cyan, Color.magenta, Color.yellow, new Color(100,90,20)
    };

    Choice colorChoice; // A menu of available colors.

    SimpleDrawCanvas canvas;  // This is where the drawing is actually done.
                             // This applet displayes this canvas with some
                             // controls below it.

    public void init() {
        // Create components and lay out the applet

        canvas = new SimpleDrawCanvas();

        Panel bottom = new Panel();
        Button b1 = new Button("Clear");
        b1.addActionListener(canvas);    // Note that canvas will handle button events.
        bottom.add(b1);
        Button b2 = new Button("Close");
        b2.addActionListener(canvas);
        bottom.add(b2);

        colorChoice = new Choice();
        colorChoice.add("Black");
        colorChoice.add("Gray");
        colorChoice.add("Red");
        colorChoice.add("Green");
        colorChoice.add("Blue");
        colorChoice.add("Dark Red");
        colorChoice.add("Dark Green");
        colorChoice.add("Dark Blue");
        colorChoice.add("Cyan");
        colorChoice.add("Magenta");
        colorChoice.add("Yellow");
        colorChoice.add("Brown");
        colorChoice.addItemListener(this);
        bottom.add(colorChoice);

        setBackground(Color.lightGray);
        setLayout(new BorderLayout(3,3));
        add("Center",canvas);
        add("South",bottom);

    } // end init()

   public Insets getInsets() {
         // Say how much of a border to leave around the edges of the applet.
      return new Insets(3,3,3,3);
   }

   public void itemStateChanged(ItemEvent evt) {
         // This is called when the user selects an item from the colorChoice
         // menu.  The index of the selected item specifies the color to use
         // for drawing.  The canvas is notified of the new drawing color
         // by calling its setDrawing Color method.
      int colorIndex = colorChoice.getSelectedIndex();
      canvas.setDrawingColor(colorList[colorIndex]);
   }

} // end class SimpleDrawApplet


class Point {
    int x, y;

    Point (int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Line {  // an object of this class represents a colored line segment
    int x1, y1;   // One endpoint of the line segment.
    int x2, y2;   // The other endpoint of the line segment.
    Color color;  // The color of the line segment.
}

class Circle {
    // int x1, x2; // center for the circle
    Point center;
    int radius;
}

class SimpleDrawCanvas extends Canvas implements ActionListener, MouseListener {
    // A canvas where the use can draw lines in various colors.


    Color currentColor;  // Color that is currently being used for drawing new lines.
    java.util.List<Point> pointSet = new ArrayList<Point>();
    Point mousePoint;
    Point top, bottomleft, bottomright;
    boolean selfClose;
    boolean mouseClick;
    SimpleDrawCanvas() {
        // Construct the canvas, and set it to listen for mouse events.
        setBackground(Color.white);
        currentColor = Color.black;
    	addMouseListener(this);
        selfClose = false;
        mouseClick = false;
        // doClear();
    }

    int orientation(Point p, Point q, Point r) {
    	int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
    	return (val > 0)? 1: 2;
    }

    boolean inside(Point r) {
        // return true;
    	if (orientation(top, bottomleft, r) == 1 &&
    	    orientation(bottomleft, bottomright, r) == 1 &&
    	    orientation(bottomright, top, r) == 1) 
    	    return true;
    	
    	return false;		
    }

    boolean intersect(Point p1, Point q1, Point p2, Point q2) {
	// Find the four orientations needed for general and
        // special cases
    	int o1 = orientation(p1, q1, p2);
    	int o2 = orientation(p1, q1, q2);
    	int o3 = orientation(p2, q2, p1);
    	int o4 = orientation(p2, q2, q1);
 
    	// General case
    	if (o1 != o2 && o3 != o4)
            return true;
	
    	return false;	
    }

    boolean selfcross(Point r) {
    	int size = pointSet.size();
    	int i = 0;
    	// click 'close'
    	if (r.x == pointSet.get(0).x && r.y == pointSet.get(0).y) { 
    	    i = 1;
    	}
    	for (; i<size-2; i++ ) {
    	    //return true if added point r incur intersection
    	    if (intersect(pointSet.get(i), pointSet.get(i+1), pointSet.get(size-1), r))
    		return true;
    	}
    	return false;
    }

    void setDrawingColor(Color c) { 
        // Set current color to c.
        currentColor = c;
    }

    void doClear() {
        // Clear all the lines from the picture.
        pointSet.clear();
        mouseClick = false;
        repaint();
    }

    void doClose() { 
        // Remove most recently added line from the picture.
        selfClose = true;
        repaint();
    }

    public void actionPerformed(ActionEvent evt) {
    // A button has been clicked; do the appropriate command.
    String command = evt.getActionCommand();
    if (command.equals("Clear"))
        doClear();
    else if (command.equals("Close"))
        doClose();
    }

    public void mousePressed(MouseEvent evt) {
    }

    public void mouseReleased(MouseEvent evt) {
    } // end mouseReleased
    public void mouseClicked(MouseEvent evt) { 
        mousePoint = new Point(evt.getX(), evt.getY());
        mouseClick = true;
        repaint();
    }  // Other methods in the MouseListener interface
    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}

    public void paint(Graphics g) {
        // Drw triangulation
        top = new Point(400, 50);
        bottomleft = new Point(200, 350);
        bottomright = new Point(600, 350);
        g.setColor(currentColor);
        // Point boundry
        g.fillOval(top.x-3, top.y-3, 6, 6); 
        g.fillOval(bottomleft.x-3, bottomleft.y-3, 6, 6); 
        g.fillOval(bottomright.x-3, bottomright.y-3, 6, 6); 
        // Line boundry
        g.drawLine(top.x, top.y, bottomleft.x, bottomleft.y);
        g.drawLine(bottomleft.x, bottomleft.y, bottomright.x, bottomright.y);
        g.drawLine(bottomright.x, bottomright.y, top.x, top.y); 


        Point p = mousePoint;
        int size = pointSet.size();
        if (size < 3 && inside(p) && mouseClick) {
            pointSet.add(p);
        } else if (!selfcross(p) && inside(p) && mouseClick) {
            pointSet.add(p);
        }
        mouseClick = false;
        size = pointSet.size();
        for (int i=0; i < size; i++) {
            Point dp = pointSet.get(i);
            g.fillOval(dp.x-3, dp.y-3, 6, 6);
            System.out.print("draw");
        }
        for (int i=1; i < size; i++) {
            Point dp = pointSet.get(i);
            Point pdp = pointSet.get(i-1);
            g.drawLine(dp.x, dp.y, pdp.x, pdp.y);
        }
        if (selfClose && size > 2 ) {
            Point sp = pointSet.get(0);
            Point ep = pointSet.get(size-1);
            if (!selfcross(sp)) {
                g.drawLine(sp.x, sp.y, ep.x, ep.y);
                selfClose = false;
            }
        }
        g.dispose();
    }

} // end class SimpleDrawCanvas
