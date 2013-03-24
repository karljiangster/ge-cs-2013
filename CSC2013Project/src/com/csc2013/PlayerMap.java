package com.csc2013;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

public class PlayerMap
{
	private MapPoint player;
	private Map<MapPoint, BoxType> grid;
	private final Object lock = new Object();
	
	/*
	 * For use in debugger, allows the JFrame to properly display the map.
	 * Otherwise, these variables serve no purpose.
	 */
	int minX;
	int minY;
	int maxX;
	int maxY;
	
	private SchoolPlayerDebugger debugger;
	
	public PlayerMap()
	{
		reset();
	}
	
	public void setDebugger(SchoolPlayerDebugger debug)
	{
		this.debugger = debug;
	}
	
	public SchoolPlayerDebugger getDebugger()
	{
		return this.debugger;
	}
	
	public void reset()
	{
		this.player = new MapPoint(0, 0);
		this.grid = new HashMap<>();
	}
	
	public void setPlayerPosition(int x, int y)
	{
		setPlayerPosition(new MapPoint(x, y));
	}
	
	public void setPlayerPosition(MapPoint point)
	{
		synchronized(this.lock)
		{
			this.player = point;
		}
	}
	
	/*
	 * For use in debugger, allows the JFrame to properly display the map.
	 */
	private void cacheBounds(MapPoint point)
	{
		int x = point.x;
		int y = point.y;
		if(x < this.minX)
		{
			this.minX = x;
		}
		if(x > this.maxX)
		{
			this.maxX = x;
		}
		if(y < this.minY)
		{
			this.minY = y;
		}
		if(y > this.maxY)
		{
			this.maxY = y;
		}
	}
	
	public void set(BoxType type, int x, int y)
	{
		set(type, new MapPoint(x, y));
	}
	
	public void set(BoxType type, MapPoint point)
	{
		synchronized(this.lock)
		{
			cacheBounds(point);
			this.grid.put(point, type);
		}
	}
	
	public BoxType get(int x, int y)
	{
		return get(new MapPoint(x, y));
	}
	
	public BoxType get(MapPoint point)
	{
		synchronized(this.lock)
		{
			return this.grid.get(point);
		}
	}
	
	public BoxType getPlayer()
	{
		return get(getPlayerPoint());
	}
	
	public MapPoint getPlayerPoint()
	{
		return this.player;
	}
	
	public boolean contains(BoxType type)
	{
		return this.grid.containsValue(type);
	}
	
	/* Important stuff */
	
	private MapPoint findSingle(BoxType type)
	{
		if(type == null)
			return null;
		
		MapPoint found = null;
		for(Entry<MapPoint, BoxType> entry : this.grid.entrySet())
		{
			if(entry.getValue() == type)
			{
				if(found != null)
					return null;
				found = entry.getKey();
			}
		}
		return found;
	}
	
	public Action actionTo(BoxType type)
	{
		MapPoint aStarDest = findSingle(type);
		
		Set<MapPath> paths = aStarDest == null
				? BFSearch.search(this, getPlayerPoint(), type)
				: AStarSearch.search(this, getPlayerPoint(), aStarDest);
		
		if(paths.isEmpty())
			return null;
		
		for(MapPath path : paths)
		{
			if(path.length() == 1)
				return Action.Pickup;
			Action move = getPathAction(path);
			if(canExplore(move))
				return move;
		}
		
		return getPathAction(paths.iterator().next());
	}
	
	private Action getPathAction(MapPath path)
	{
		final MapPoint player = getPlayerPoint();
		final MapPoint point = path.getStepPath().getLastPoint();
		if(player.west().equals(point))
			return Action.West;
		else if(player.east().equals(point))
			return Action.East;
		else if(player.north().equals(point))
			return Action.North;
		else if(player.south().equals(point))
			return Action.South;
		throw new AssertionError();
	}
	
	private Action getActionToNeighbor(MapPoint point)
	{
		final MapPoint player = getPlayerPoint();
		if(player.west().equals(point))
			return Action.West;
		else if(player.east().equals(point))
			return Action.East;
		else if(player.north().equals(point))
			return Action.North;
		else if(player.south().equals(point))
			return Action.South;
		throw new AssertionError();
		
		//		int deltaX = point.x - player.x;
		//		int deltaY = point.y - player.y;
		//		assert (deltaX == 0) != (deltaY == 0);
		//		if(deltaX == 0)
		//		{
		//			if(deltaY > 0)
		//				return Action.South;
		//			else
		//				return Action.North;
		//		}
		//		else if(deltaX > 0)
		//			return Action.East;
		//		else
		//			return Action.West;
	}
	
	public Action discoveryChannel(Action lastMove, int keyCount)
	{
		if(canExplore(lastMove))
			return lastMove;
		return actionTo(null);
	}
	
	private boolean canExplore(Action move)
	{
		if(move == null)
			return false;
		MapPoint cur = getPlayerPoint();
		while(get(cur) == BoxType.Open)
		{
			MapPoint next = cur.execute(move);
			if(next.equals(cur))
				return false;
			cur = next;
		}
		return get(cur) == null;
	}
}