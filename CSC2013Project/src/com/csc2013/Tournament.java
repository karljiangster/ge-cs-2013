package com.csc2013;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.csc2013.DungeonMaze.PlayerType;

/**
 * 
 * This class starts a tournament consisting of Dungeon Maze games
 * 
 * @author GE
 * 
 */
public class Tournament
{
	/*
	 * This variable controls how many steps a player can take before starting
	 * the next game
	 */
	public final static int maxSteps = 200;
	/*
	 * Possible maps to use are: map01.tmx map02.tmx map03.tmx
	 */
	public final static String maps[] = {"test01.tmx"};
	/*
	 * This is how many spaces ahead players can see. Acceptable values are 1
	 * through 15.
	 */
	public final static int vision[] = {10};
	/*
	 * Possible players are: PlayerType.AI PlayerType.School PlayerType.Human
	 */
	public final static PlayerType players[] = {PlayerType.School};
	
	public static final AppGameContainer container;
	static
	{
		DungeonMaze dm = new DungeonMaze();
		try
		{
			container = new AppGameContainer(dm, 640, 480, false);
			container.setVerbose(false); // suppress Slick2Ds output
		}
		catch(SlickException e)
		{
			System.out.println("There is an internal issue with the game framework.  Please send the following error code to CSC.CaseStudy@ge.com.");
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static void main(String[] argv)
	{
		try
		{
			container.start();
		}
		catch(SlickException e)
		{
			System.out.println("There is an internal issue with the game framework.  Please send the following error code to CSC.CaseStudy@ge.com.");
			e.printStackTrace();
		}
		System.exit(0);
	}
}
