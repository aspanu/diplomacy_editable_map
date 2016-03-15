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

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Code for Virtual Mr. Spud Head, v1.0                                       //
// by Chris Rywalt, 1995                                                      //
// crywalt@westnet.com                                                        //
// http://www.westnet.com/~crywalt                                            //
//                                                                            //
// For updates, check my homepage.                                            //
//                                                                            //
// This game and its source code are hereby placed in the public domain.      //
//                                                                            //
// Special thanks to Maria Winslow, winslow@cs.unc.edu, for her Electro       //
// Magnetic Poetry code, without which this would not have been possible.     //
// Go play her game, too:  http://prominence.com/java/poetry/                 //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.awt.image.ImageObserver;

public class Piece
{
  public int X = 0;
  public int Y = 0;
  public int oldX = 0;
  public int oldY = 0;
  public String Name;
  public Image img;

  public int dx = 0;
  public int dy = 0;

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// mouse_within() returns true if the given x,y coordinates are within the    //
//                rectangle of the image.                                     //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

  public boolean mouse_within(int x_coord, int y_coord)
  {
    boolean within = false;

    if(x_coord >= X &&
       (x_coord <= (X+getWidth())) &&
       y_coord >= Y &&
       (y_coord <= (Y+getHeight())))
      within = true;

    // dx and dy tell us where within the rectangle the click occurred, so
    // the piece doesn't jump when you click on it.
    dx=(x_coord-X);
    dy=(y_coord-Y);

    return(within);
  }

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// getWidth() returns the width of the image.                                 //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

  public int getWidth()
  {
    return(img.getWidth(null));
  }

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// getHeight() returns the Height of the image.                               //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

  public int getHeight()
  {
    return(img.getHeight(null));
  }

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// drawPiece() draws the piece in the given graphics area.                    //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

  public void drawPiece(Graphics g, ImageObserver observer)
  {
    oldX = X;
    oldY = Y;

    g.drawImage(img,X,Y,observer);
  }
}
