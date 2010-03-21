package edu.usu.cs.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * JPanel in which a Graph is drawn in.
 * Creation date: (3/21/2002 2:00:51 PM)
 * @author Randy Secrist
 */
public class GraphPanel extends JPanel {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -986752829664879314L;
	
	public Surface surf;

	/**
	 * Graph Drawing Surface.  This is where we paint.
	 */
	public class Surface extends JPanel implements Runnable {
		/**
		 * Serial Version UID
		 */
		private static final long serialVersionUID = 7353057918034218185L;
		
		public int islandId;
		public Log log;

		public Graphics2D offgraphics;
		public Dimension offscreensize;

		public NodeWrapper pick;		
		public boolean pickfixed;
		public String nodeLabel = new String();
		public String edgeLabel = new String();
		public boolean directed;

		public final Stroke drawStroke = new BasicStroke(1.0f);
		public final Stroke edgeStroke = new BasicStroke(2.0f);
		
		public BufferedImage offscreen;
		public boolean random;

		public Thread relaxer;
		
		/**
		 * Creates a new surface to draw a graph upon.
		 * @param id
		 * @param log
		 */
		public Surface(int id, Log log) {
			this.islandId = id;
			this.log = log;

			this.setBackground(Color.white);
			
			// Mouse Listener for Surface:
			this.addMouseListener(new SurfaceMouseAdapter(this, log));
		}
		/**
		 * Paints the current surface.
		 */
		public void paintComponent(Graphics g) {
			update(g);
		}
		/**
		 * Runs the surface animation.
		 */
		public void run() {
			Thread me = Thread.currentThread();
			while (relaxer == me) {
				relax();
				if (random && (Math.random() < 0.03)) {
					int temp = (int) (Math.random() * Globals.getInstance().getNumNodes());
					NodeWrapper n = Globals.getInstance().getNode(temp);
					while (n.islandId != islandId) {
						temp++;
						if (temp == Globals.getInstance().getNumNodes()) temp = 0;
						n = Globals.getInstance().getNode(temp);
					}
					if (!n.fixed) {
						n.x += 100 * Math.random() - 50;
						n.y += 100 * Math.random() - 50;
					}
				}
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					break;
				}
			}
		}
		/**
		 * Starts this surfaces balancing algorithm.
		 */
		public void start() {
			relaxer = new Thread(this);
			relaxer.setPriority(Thread.MIN_PRIORITY);
			relaxer.setName("Island Thread " + islandId);
			relaxer.start();
		}
		
		/**
		 * Draws a node on the surface.
		 * @param g2d
		 * @param n
		 * @param fm
		 */
		public void paintNode(Graphics2D g2d, NodeWrapper n, FontMetrics fm) {
			if (n == null) return;
			int x = (int)n.x;
			int y = (int)n.y;

			// Set Height & Width of Node
			int w = (int) n.getFillRect().getWidth();
			int h = (int) n.getFillRect().getHeight();
			
			// Draw Fill - With either traversed color or node color
			if (n.isTraversed)
				g2d.setColor(Globals.getColor(Globals.TRAVERSED_NODE));
			else if(n.equals(Globals.getInstance().getSelectedNode()) && !n.isPressed)
				g2d.setColor(Globals.getColor(Globals.FIXED_COLOR));
			else if (n.isPressed)
				g2d.setColor(Globals.getColor(Globals.SELECT_COLOR));
			else
				g2d.setColor(Globals.getColor(Globals.NODE_COLOR));
			//else g2d.setColor(n.nodeColor);
			g2d.fill(n.getFillRect());

			// Draw Border			
			g2d.setColor(Color.black);
			g2d.draw(n.getBorderRect());
			
			// Draw Display Name
			if (nodeLabel.equalsIgnoreCase(MBI.NODE_ID)) {
				g2d.drawString(""+n.getNode().getId(), x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
			}
			else {
				g2d.drawString(n.getNode().getData().getDisplayName(), x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
			}
		}
		/**
		 * Starts this surfaces balancing algorithm.
		 */
		public synchronized void stop() {
			relaxer = null;
			notifyAll();
		}
		/**
		 * Used to relax the nodes in the surface to make them appear to balance.
		 */
		public synchronized void relax() {
			for (int i = 0; i < Globals.getInstance().getNumEdges(); i++) {
				EdgeWrapper e = Globals.getInstance().getEdge(i);
				if (this.islandId != e.islandId) continue;

				// Safety Bound Check
			    //boolean lowerBound = (e_from >= 0) && (e_to >=0);
				//boolean upperBound = (e_from < Globals.getInstance().getNumNodes()) && (e_to < Globals.getInstance().getNumNodes());

				//if (! (lowerBound && upperBound) ) return;

				NodeWrapper to = Globals.getInstance().getNodeById(e.to);
				NodeWrapper from = Globals.getInstance().getNodeById(e.from);

				if (to == null || from == null) return;

				double vx = to.x - from.x;
				double vy = to.y - from.y;

				double len = Math.sqrt(vx * vx + vy * vy);
				len = (len == 0) ? .0001 : len;
				double f = ( (Globals.getInstance().getEdge(i)).len - len) / (len * 3);
				double dx = f * vx;
				double dy = f * vy;
			
				to.dx += dx;
				to.dy += dy;
				from.dx += -dx;
				from.dy += -dy;
			}
			
			for (int i = 0; i < Globals.getInstance().getNumNodes(); i++) {
				NodeWrapper n1 = Globals.getInstance().getNode(i);
				if (this.islandId != n1.islandId) continue;
				
				double dx = 0;
				double dy = 0;

				for (int j = 0; j < Globals.getInstance().getNumNodes(); j++) {
					if (i == j) {
						continue;
					}
					NodeWrapper n2 = Globals.getInstance().getNode(j);
					if (this.islandId != n2.islandId) continue;
					
					double vx = n1.x - n2.x;
					double vy = n1.y - n2.y;
					double len = vx * vx + vy * vy;
					if (len == 0) {
						dx += Math.random();
						dy += Math.random();
					}
					else if (len < 100 * 100) {
						dx += vx / len;
						dy += vy / len;
					}
				}
				double dlen = dx * dx + dy * dy;
				if (dlen > 0) {
					dlen = Math.sqrt(dlen) / 2;
					n1.dx += dx / dlen;
					n1.dy += dy / dlen;
				}
			}

			Dimension d = getSize();
			for (int i = 0; i < Globals.getInstance().getNumNodes(); i++) {
				NodeWrapper n = Globals.getInstance().getNode(i);
				if (this.islandId != n.islandId) continue;
				
				if (!n.fixed) {
					n.x += Math.max(-5, Math.min(5, n.dx));
					n.y += Math.max(-5, Math.min(5, n.dy));
				}
				if (n.x < 0) {
					n.x = 0;
				}
				else if (n.x > d.width) {
					n.x = d.width;
				}
				if (n.y < 0) {
					n.y = 0;
				}
				else if (n.y > d.height) {
					n.y = d.height;
				}
				n.dx /= 2;
				n.dy /= 2;
			}
			repaint();
		}
		
		/**
		 * Draws an arrow using with the head projects determined upon the angle the line
		 * makes respective to the horizontal x axis.
		 * 
		 * @param g The graphics to draw with.
		 * @param x1 The start x point.
		 * @param y1 The start y point.
		 * @param x2 The end x point.
		 * @param y2 The end y point.
		 */
		public void drawArrow(Graphics2D g, double x1, double y1, double x2, double y2) {
			int ARROWSIZE = 10;
			double ARROWANGLE = Math.PI / 6;

			Point AL, AR;
			double px, py;
			double alpha, alphaL, alphaR;
			double Alx, Aly, Arx, Ary;

			px = x2 - x1;
			py = y1 - y2;
			// Calculating alpha, alphaL, alphaR...
			if (px == 0)
				alpha = (py < 0) ? Math.PI / 2 : -Math.PI / 2;
			else
				alpha = Math.atan(py / px);
			// Calculating alphaL, alphaR...
			alphaL = alpha - ARROWANGLE;
			alphaR = alpha + ARROWANGLE;
			// Calculating arrow projections...
			Alx = Math.cos(alphaL) * ARROWSIZE;
			Aly = Math.sin(alphaL) * ARROWSIZE;
			Arx = Math.cos(alphaR) * ARROWSIZE;
			Ary = Math.sin(alphaR) * ARROWSIZE;
			// Calculating arrow points...
			if (px > 0) {
				AL = new Point((int) x2 - (int) (Alx), (int) y2 + (int) (Aly));
				AR = new Point((int) x2 - (int) (Arx), (int) y2 + (int) (Ary));
			}
			else {
				AL = new Point((int) x2 + (int) (Alx), (int) y2 - (int) (Aly));
				AR = new Point((int) x2 + (int) (Arx), (int) y2 - (int) (Ary));
			}
			// Drawing...
			g.draw(new Line2D.Double(x1, y1, x2, y2));
			g.draw(new Line2D.Double(AL.x, AL.y, x2, y2));
			g.draw(new Line2D.Double(AR.x, AR.y, x2, y2));
		}
		
		/**
		 * Updates the size of the node using the specified font metrics.
		 * @param n The node to update.
		 * @param fm The font metrics to use.
		 */
		public void setNodeSize(NodeWrapper n, FontMetrics fm) {
			if (n == null) return;
			int x = (int)n.x;
			int y = (int)n.y;
			
			// Set Height & Width of Node
			int w, h;
			if (nodeLabel.equalsIgnoreCase(MBI.NODE_ID)) {
				w = fm.stringWidth(""+n.getNode().getId()) + 10;
				h = fm.getHeight() + 4;
			}
			else {
				w = fm.stringWidth(n.getNode().getData().getDisplayName()) + 10;
				h = fm.getHeight() + 4;
			}
			
			// Fill Rectangle
			Rectangle fill = new Rectangle(x - w/2, y - h / 2, w, h);
			n.setFillRect(fill);

			// Border Rectangle
			Rectangle border = new Rectangle(x - w/2, y - h / 2, w-1, h-1);
			n.setBorderRect(border);
		}
		
		/**
		 * Updates the surface of this panel.
		 */
		public void update(Graphics g) {
			super.paintComponent(g);

			Dimension d = this.getSize();
		
			if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
				offscreen = new BufferedImage (d.width, d.height, BufferedImage.TYPE_INT_RGB);
				offscreensize = d;
				if (offgraphics != null) {
					offgraphics.dispose();
				}
				offgraphics = offscreen.createGraphics();
				offgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				offgraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
				offgraphics.setFont(new Font("Arial", Font.PLAIN, 13));
			}
			
			// Draw The Background Color
			offgraphics.setBackground(getBackground());
			offgraphics.clearRect(0,0,d.width,d.height);

			// Draw All
			this.paintSurface();			

			// Send everything to the screen.
			g.drawImage(offscreen, 0, 0, null);
		}
		
		/**
		 * Draws the surface of this panel.
		 */
		public void paintSurface() {
			// Set Node Size
			for (int i = Globals.getInstance().getNumNodes()-1; i >= 0; i--) {
				NodeWrapper n = Globals.getInstance().getNode(i);
				if ( n != null && this.islandId != n.islandId ) continue;
				setNodeSize(n, offgraphics.getFontMetrics());
				
				// When all graph bugs are worked out, the below lines can be removed.
				//offgraphics.setStroke(drawStroke);
				//paintNode(offgraphics, n, offgraphics.getFontMetrics());
			}

			// Draw Edges
			for (int i = 0 ; i < Globals.getInstance().getNumEdges() ; i++) {
				EdgeWrapper e = getEdge(i);
				if ( this.islandId != e.islandId) continue;
				
				// Fetch Center to Center point coordinates.
			    int e_from = e.from;
			    int e_to = e.to;

			    // Safety Bound Check
				NodeWrapper from = getNodeById(e_from);
				NodeWrapper to = getNodeById(e_to);
				if (from == null || to == null) return;

				double x1 = from.x;
			    double y1 = from.y;
			    double x2 = to.x;
			    double y2 = to.y;

			    double fx1 = 0, fy1 = 0, fx2 = 0, fy2 = 0; // used for directed graphs			    
				if (directed) {
				    // Calculate incident begin and end points
				    // (These are the edges of the rectangles where the line begins and ends.)
				    Rectangle2D r1 = from.getBorderRect();
				    Rectangle2D r2 = to.getBorderRect();

					double wx1 = Math.abs(r1.getWidth() / 2);
					double wy1 = Math.abs(r1.getHeight() / 2);
					double wx2 = Math.abs(r2.getWidth() / 2);
					double wy2 = Math.abs(r2.getHeight() / 2);

					double cy1, cx1;
					if ((x2 - x1) == 0) {
						cy1 = 0;
					}
					else {
						cy1 = ((y2 - y1) * wx1) / (x2 - x1);
					}
					if (Math.abs(cy1) > wy1) {
						// off a side
						if ((y2 - y1) == 0) {
							cx1 = 0;
						}
						else {
							cx1 = ((x2 - x1) * wy1) / (y2 - y1);
						}
						cy1 = wy1;
					}
					else {
						cx1 = wx1;
					}

					double cy2, cx2;
					if ((x2 - x1) == 0) {
						cy2 = 0;
					}
					else {
						cy2 = ((y2 - y1) * wx2) / (x2 - x1);
					}
					if (Math.abs(cy2) > wy2) {
						// off a side
						if ((y2 - y1) == 0) {
							cx2 = 0;
						}
						else {
							cx2 = ((x2 - x1) * wy2) / (y2 - y1);
						}
						cy2 = wy2;
					}
					else {
						cx2 = wx2;
					}

					if ((x2 - x1) > 0) {
						// n1 left of n2
						fx1 = x1 + Math.abs(cx1) + 2;
						fx2 = x2 - Math.abs(cx2) - 2;
					}
					else {
						// n1 right of n2
						fx1 = x1 - Math.abs(cx1) - 2;
						fx2 = x2 + Math.abs(cx2) + 2;
					}

					if ((y2 - y1) > 0) {
						// n1 above of n2
						fy1 = y1 + Math.abs(cy1) + 2;
						fy2 = y2 - Math.abs(cy2) - 2;
					}
					else {
						// n1 below of n2
						fy1 = y1 - Math.abs(cy1) - 2;
						fy2 = y2 + Math.abs(cy2) + 2;
					}
				}

				// Set Logical length and set draw properties
			    int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) - e.len);
			    if (e.isTraversed) offgraphics.setColor(Globals.getColor(Globals.TRAVERSED_EDGE));
			    else offgraphics.setColor((len < 10) ? Globals.getColor(Globals.ARC_COLOR_1) : (len < 20 ? Globals.getColor(Globals.ARC_COLOR_2) : Globals.getColor(Globals.ARC_COLOR_3)));
			    offgraphics.setStroke(edgeStroke);

   				// Draw either a directed edge or non directed edge.
   				if (directed) this.drawArrow(offgraphics, fx1, fy1, fx2, fy2);
   				else this.offgraphics.draw(new Line2D.Double(x1,y1,x2,y2));
			     
			    if (edgeLabel.equalsIgnoreCase(MBI.EDGE_WEIGHTS)) { // Paints Edge Weight
				    String lbl = String.valueOf(e.getEdge().getWeight());
					offgraphics.setColor(Globals.getColor(Globals.STRESS_COLOR));
					offgraphics.drawString(lbl, (int) (x1 + (x2-x1)/2), (int) (y1 + (y2-y1)/2));
			    }
			    else if (edgeLabel.equalsIgnoreCase(MBI.EDGE_STRESS)) { // Paints Edge Stress (Balanced Length of Edge)
					String lbl = String.valueOf(len);
					offgraphics.setColor(Globals.getColor(Globals.STRESS_COLOR));
					offgraphics.drawString(lbl, (int) (x1 + (x2-x1)/2), (int) (y1 + (y2-y1)/2));
			    }
			    offgraphics.setColor(Color.black);
			}

			// Draw Nodes
			offgraphics.setStroke(drawStroke);
			for (int i = Globals.getInstance().getNumNodes()-1; i >= 0; i--) {
				NodeWrapper n = Globals.getInstance().getNode(i);
				if ( n != null && this.islandId != n.islandId ) continue;
				paintNode(offgraphics, Globals.getInstance().getNode(i), offgraphics.getFontMetrics());
			}
		}
		public EdgeWrapper getEdge(int i) {
			return Globals.getInstance().getEdge(i);
		}
		public NodeWrapper getNodeById(int edgeComponent) {
			NodeWrapper n = Globals.getInstance().getNodeById(edgeComponent);				
			return n;
		}

	}

	/**
	 * GraphPanel constructor comment.
	 */
	public GraphPanel(int id) {
		super();
	
		Dimension d = GEdit.getInstance().getSurfaceSize();
		Log log = GEdit.getInstance().getLog();
	
		this.setPreferredSize(d);
		
		// Ensure surface get's initalized
		if(!this.initWrappers(id, log)) return;
	
		// Add the surface to the panel.
		this.setLayout(new BorderLayout());
			
		// Setup scroll pane & add to dialog.
		this.setBorder(new TitledBorder(new EtchedBorder(), "Connected Component " + (id+1)));
		this.add(surf, BorderLayout.CENTER);
	}

	/**
	 * Returns true if the surface has been properly initalized.
	 * false otherwise.
	 */
	public boolean initWrappers(int id, Log log) {
		try {
			if (log == null) {
				throw new LogNotInstantiatedException("initSurface::Surface (GraphPanel) - Log must be properly instantiated before a surface can be drawn.");
			}
			surf = new Surface(id, log);
			surf.setPreferredSize(this.getPreferredSize());
			
			return true;
		}
		catch (java.lang.Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
}