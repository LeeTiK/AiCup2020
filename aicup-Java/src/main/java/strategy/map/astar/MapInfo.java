package strategy.map.astar;

import strategy.MyEntity;

public class MapInfo
{
	public int[][] maps; //
	public int width; //
	public int hight; //
	public Node start; //
	public Node end; //
	
	public MapInfo(int[][] maps, Node start, Node end)
	{
		this.maps = maps;
		this.width = maps.length;
		this.hight = maps[0].length;
		this.start = start;
		this.end = end;
	}

	public MapInfo(MyEntity[][] maps, Node start, Node end)
	{
		this.maps = transformatMap(maps);
		this.width = maps.length;
		this.hight = maps[0].length;
		this.start = start;
		this.end = end;
	}

	protected int[][] transformatMap(MyEntity[][] mapEntity)
	{
		maps = new int[mapEntity.length][mapEntity[0].length];

		for (int i=0; i<mapEntity.length; i++)
		{
			for (int j=0; j<mapEntity[i].length; j++)
			{
				switch (mapEntity[i][j].getEntityType())
				{
					case WALL:
					case HOUSE:
					case BUILDER_BASE:
					case BUILDER_UNIT:
					case MELEE_BASE:
					case TURRET:
					case MELEE_UNIT:
					case RANGED_BASE:
					case RANGED_UNIT:
						maps[i][j] = AStar.BAR;
						break;
					case RESOURCE:
						maps[i][j] = AStar.BAR;
						break;
					case Empty:
						maps[i][j] = 0;
						break;
				}
			}
		}

		return maps;
	}


}