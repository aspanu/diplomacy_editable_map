/**
 *
 * This is not my source code, I downloaded it from http://realpolitik.sourceforge.net/JavaDip/index.html
 * on a long forgotten part of the internet. It seemed sad that it was abandoned, so I'm trying to resurrect it
 * for anyone else that might be interested. To resurrect it, I had to make a number of modifications to
 * bring it up 20 years. These can be found in the commits on this github.
 *
 * Adrian Spanu
 * 2016
 *
**/

//----------------------------------------------------------------------
//
// Diplomacy.java
//
// written by Scott Williams and Jim Van Verth
// based on code by Chris Rywalt and Maria Winslow
//
// This game and its source code are hereby placed in the public domain
// with the exception of all artwork Copyright (C) 1996 James M. Van Verth.
//
//----------------------------------------------------------------------

import java.awt.*;
import java.awt.event.MouseEvent;

public class Diplomacy extends Frame implements Runnable
{
	Thread thread;

	// window width and height
	final int winWidth = 693;
	final int winHeight = 512;

	// frame size
	final int frameWidth = winWidth;
	final int frameHeight = winHeight + 50;

	Panel innerPanel;

	// map
	final int mapWidth = 693;
	final int mapHeight = 512;

	// game pieces
	final int pieceWidth = 16;
	final int pieceHeight = 20;

	// trash can
	int trashcanWidth = 50;
	int trashcanHeight = 70;
	int trashcanX;
	int trashcanY;

	// help button
	int helpWidth = 60;
	int helpHeight = 30;
	int helpX = 0;
	int helpY = winHeight;
	int helpPosition;
	boolean helpText = false;

	// about button
	int aboutWidth = 60;
	int aboutHeight = 30;
	int aboutX = winWidth-aboutWidth;
	int aboutY = winHeight;
	int aboutPosition;
	boolean aboutText = false;

	// message button
	int messageWidth = winWidth-helpWidth-aboutWidth;
	int messageHeight = 30;
	int messageX = helpX+helpWidth;
	int messageY = winHeight;
	int messagePosition;
	public String message;

	// height of font for string placement and such
	int wordHeight;

	// for double buffering to prevent flicker
	Image offScreenImage;
	Graphics offScreen;

	// non-variable images
	public Image mapimage;
	public Image trashcanimage;

	// is there an active piece?
	boolean activePiece = false; 
	boolean indexPiece = false;

	// coordinates for the clipping rectangle
	int clipX = 0;
	int clipY = 0;
	int clipWidth = frameWidth;
	int clipHeight = frameHeight;

	// number of pieces
	int numberPieces;
	int numberIndexPieces;

	// font stuff
	Font wordFont;
	FontMetrics wordMetrics;

	// arrays of variable images
	public Piece index_pieces[];
	public Piece pieces[];


	public Diplomacy()
	{
	}

	public Diplomacy(String s)
	{
		super(s);
		innerPanel = new Panel();
		this.add(innerPanel);

	}

	public void init()
	{
		setLayout(new BorderLayout());

		// list of pieces
		String pieceList[] = 
		{ 
			"RedFleet", 
			"RedArmy", 
			"BlueFleet", 
			"BlueArmy", 
			"CyanFleet", 
			"CyanArmy", 
			"BlackFleet", 
			"BlackArmy", 
			"GreenFleet", 
			"GreenArmy",
			"WhiteFleet", 
			"WhiteArmy", 
			"YellowFleet", 
			"YellowArmy"
		};

		numberPieces = -1;
		numberIndexPieces = pieceList.length;

		pieces = new Piece[256];
		index_pieces = new Piece[numberIndexPieces];

		index_pieces[0] = new Piece();
		index_pieces[0].X = 5;
		index_pieces[0].Y = 5;
		index_pieces[0].img  = getToolkit().getImage("images/"+pieceList[0]+".gif");

		for(int i=1;i<numberIndexPieces;i++)
		{
		  index_pieces[i] = new Piece();
		  index_pieces[i].X = 5+index_pieces[i-1].X+ pieceWidth;
		  index_pieces[i].Y = index_pieces[0].Y;
		  index_pieces[i].img  = getToolkit().getImage("images/"+pieceList[i]+".gif");
		}

		// Set up some font stuff and the default message for the center (message)
		// button.
		wordFont = new Font("Helvetica", Font.BOLD, 12);
		if (wordFont == null)
		  wordFont = getFont();

		wordMetrics = getFontMetrics (wordFont);
		message = "Java Diplomacy";
		messagePosition = messageX+(messageWidth-wordMetrics.stringWidth(message))/2;

		// Set up the help and about buttons.
		helpPosition = helpX+((helpWidth-wordMetrics.stringWidth("HELP"))/2);
		aboutPosition = aboutX+((aboutWidth-wordMetrics.stringWidth("ABOUT"))/2);

		// Load in and initialize the non-variable images.
		mapimage  = getToolkit().getImage("images/map.gif");
		trashcanimage  = getToolkit().getImage("images/trash.gif");
		trashcanX = 5;
		trashcanY = winHeight-trashcanHeight-5;

		resize(frameWidth, frameHeight);
	}

	public void update(Graphics g) 
	{
		if((activePiece)&&(!helpText)&&(!aboutText)) 
		{
		  clipX = Math.min(pieces[0].oldX, pieces[0].X);
		  clipY = Math.min(pieces[0].oldY, pieces[0].Y);
		  clipWidth = pieces[0].getWidth()+Math.abs(pieces[0].oldX - pieces[0].X);
		  clipHeight = pieces[0].getHeight()+Math.abs(pieces[0].oldY - pieces[0].Y);
		}
		else 
		{
		  clipX = 0;
		  clipY = 0;
		  clipWidth = frameWidth;
		  clipHeight = frameHeight;
		}

		g.clipRect(clipX,clipY,clipWidth,clipHeight);

		paint(g);

		clipX = 0;
		clipY = 0;
		clipWidth = frameWidth;
		clipHeight = frameHeight;

		g.clipRect(clipX,clipY,clipWidth,clipHeight);
	}

	public void paintFrame(Graphics g) 
	{
		// set up the proper button bar text, depending on context.
		if((!helpText)&&(!aboutText))
		{
		  message = "Java Diplomacy";
		}
		else
		{
		  message = "Back to Java Diplomacy";
		}
		messagePosition = messageX+(messageWidth-wordMetrics.stringWidth(message))/2;

		// set up the playing rectangle
		g.setColor (new Color (255,255,255));
		g.fill3DRect (0, 0, winWidth, winHeight, true);

		// set the font in the graphics context
		g.setFont (wordFont);

		// set the message on the bottom
		g.setColor (new Color (175, 128, 128));
		g.fill3DRect (messageX, messageY, messageWidth, messageHeight, true);

		// set the help button
		g.setColor (new Color (125, 170, 170));
		g.fill3DRect (helpX, helpY, helpWidth, helpHeight, true);
		g.setColor (new Color (60,60,60));
		g.drawString ("HELP",helpPosition,winHeight+20);

		// set the about button
		g.setColor (new Color (125, 170, 170));
		g.fill3DRect (aboutX, aboutY, aboutWidth, aboutHeight, true);
		g.setColor (new Color (60,60,60));
		g.drawString ("ABOUT",aboutPosition,winHeight+20);

		// color for the text on the bottom
		g.setColor (new Color (60,60,60));
		g.drawString (message, messagePosition, winHeight + 20);

		// if we're in play mode, draw in static and moveable pieces.
		if((!helpText)&&(!aboutText))
		{
		  g.drawImage(mapimage, 0, 0, this.innerPanel);

		  g.drawImage(trashcanimage,trashcanX,trashcanY,this.innerPanel);

		  for(int i=(numberIndexPieces-1);i>=0;i--)
		  {
			index_pieces[i].drawPiece(g,this.innerPanel);
		  }
		  for(int i=numberPieces;i>=0;i--)
		  {
			pieces[i].drawPiece(g,this.innerPanel);
		  }
		}
		// otherwise, display the static pieces and whatever text is appropriate
		// to the context (either help text or about text).
		else
		{
			wordHeight = wordMetrics.getHeight();
			for(int i=(numberIndexPieces-1);i>=0;i--)
			{
				index_pieces[i].drawPiece(g,this.innerPanel);
			}
			g.drawImage(trashcanimage,trashcanX,trashcanY,this.innerPanel);
			if(helpText)
			{
				g.drawString("Clicking and dragging the mouse on any of the pieces",
							 15,150+(2*wordHeight));
				g.drawString("along the top will give you a piece you can place",
							 15,150+(3*wordHeight));
				g.drawString("anywhere.",
							 15,150+(4*wordHeight));
				g.drawString("Dropping a piece in the trashcan in the lower left",
							 15,150+(6*wordHeight));
				g.drawString("corner will get rid of it.",
							 15,150+(7*wordHeight));
			}
			else
			{
			g.drawString("Java Diplomacy",
							 15,150+(2*wordHeight));
				g.drawString("by Scott Williams and Jim Van Verth",
							 15,150+(3*wordHeight));
				g.drawString("Artwork Copyright (C) 1996 James M. Van Verth",
							 15,150+(4*wordHeight));
				g.drawString("Portions by Chris Rywalt and Maria Winslow",
							 15,150+(5*wordHeight));
				}
		}
	}
  
	public void paint(Graphics g) 
	{
		if (offScreen != null) 
		{
		  // double-buffering available
		  paintFrame (offScreen);
		  g.drawImage (offScreenImage, 0, 0, this);
		}
		else 
		{
		  // no double-buffering
		  paintFrame (g);
		}
	}

	// *************** Event handling *******************

	public void processMouseEvent(MouseEvent mouseEvent)
		//java.awt.Event event, int x, int y)
	{
		Point eventLocation = mouseEvent.getPoint();
		int x = eventLocation.x;
		int y = eventLocation.y;
		int currentPiece = 0;

		// temp is used for swapping.
		Piece temp;
		if(helpText||aboutText)
		{
		  return;
		}

		// else, determine if the mouseDown was on an index piece.
		for(int i=numberIndexPieces-1;i>=0;i--)
		{
		  if(index_pieces[i].mouse_within(x,y)) // if the mouseDown was within
		  {                                     // an index piece, set that as
			currentPiece = i;                   // the current piece, which we'll
			activePiece = true;                 // clone later.
			indexPiece = true;
		  }
		}

		// if the mouseDown was on an index piece, make a copy of it and put it
		// in the pieces[] array as element 0, which is the active piece.
		if(indexPiece)
		{
		  numberPieces++;

		  pieces[numberPieces] = new Piece();
		  pieces[numberPieces].X = index_pieces[currentPiece].X;
		  pieces[numberPieces].Y = index_pieces[currentPiece].Y;
		  pieces[numberPieces].img = index_pieces[currentPiece].img;

		  temp = pieces[numberPieces];
		  for(int j=numberPieces;j>0;j--)
		  {
			pieces[j] = pieces[j-1];
		  }
		  pieces[0] = temp;
		}

		// find out if the mouseDown was within a moveable piece.  If it was,
		// set that as the current piece.
		if(numberPieces>=0)
		{
		  for(int i=numberPieces;i>=0;i--)
		  {
			if(pieces[i].mouse_within(x,y))
			{
			  currentPiece = i;
			  activePiece = true;
			}
		  }
		}

		// move the current piece to the active piece position, pieces[0].
		if(activePiece)
		{
		  temp = pieces[currentPiece];
		  for(int j=currentPiece;j>0;j--)
		  {
			pieces[j] = pieces[j-1];
		  }
		  pieces[0] = temp;
		}

		// now that things have changed, repaint().
		repaint();

		return;
	}

	public boolean mouseDrag(java.awt.Event event, int x, int y)
	{
		if((activePiece)&&(numberPieces>=0))
		{
		  pieces[0].X = Math.max(1,Math.min(x-pieces[0].dx,winWidth-1-pieces[0].getWidth()));
		  pieces[0].Y = Math.max(1,Math.min(y-pieces[0].dy,winHeight-1-pieces[0].getHeight()));
		  repaint();
		}
		return true;
		}

		public boolean mouseUp(java.awt.Event event, int x, int y)
		{
		// if the mouseUp was within the message button
		if((x>=messageX)&&
		   (x<=messageX+messageWidth)&&
		   (y>=messageY)&&
		   (y<=messageY+messageHeight))
		{
		  helpText = false;
		  aboutText = false;
		}
		// if the mouseUp was within the about button
		if((x>=aboutX)&&
		   (x<=aboutX+aboutWidth)&&
		   (y>=aboutY)&&
		   (y<=aboutY+aboutHeight))
		{
		  helpText = false;
		  aboutText = true;
		}
		// if the mouseUp was within the help button
		if((x>=helpX)&&
		   (x<=helpX+helpWidth)&&
		   (y>=helpY)&&
		   (y<=helpY+helpHeight))
		{
		  helpText = true;
		  aboutText = false;
		}
		// if the mouseUp was within the trashcan
		if((x>=trashcanX)&&
		   (x<=trashcanX+trashcanWidth)&&
		   (y>=trashcanY)&&
		   (y<=trashcanY+trashcanHeight)&&
		   activePiece)
		{
		  for(int i=0;i<=numberPieces;i++)
		  {
			pieces[i] = pieces[i+1];
		  }
		  numberPieces--;
		}

		// After any mouseUp, these are all certainly false.
		indexPiece = false;
		activePiece = false;

		repaint();
		return true;
	}

	public boolean handleEvent(Event evt) 
	{
		if( evt.id == Event.WINDOW_DESTROY)
		{
			System.exit(0);
		}
		return super.handleEvent(evt);
	}


	// *************** Implentation of "Runnable" ***************

	public void run()
	{
		try 
		{
		  offScreenImage = createImage (frameWidth, frameHeight);
		  offScreen = offScreenImage.getGraphics ();

		} 
		catch (Exception e) 
		{
		  // double-buffering not available
		  offScreen = null;
		}

		while (true)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
	}

	public void start()
	{
		thread = new Thread(this);
		thread.start();
	}

	public void stop()
	{
		thread.stop();
	}

	// ************ MAIN ******************

	public static void main(String args[])
	{
		Diplomacy frame = new Diplomacy("Java Diplomacy");
		frame.init();
		frame.show();
		frame.start();
	}
}

