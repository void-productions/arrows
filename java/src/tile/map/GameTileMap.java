package tile.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.TreeMap;
import java.util.LinkedList;

import static core.Main.TILESIZE;
import game.Game;
import entity.Entity;
import tile.ExtendedTile;
import tile.tiles.SpawnTeamTile;
import graphics.ImageFile;
import misc.Debug;
import player.property.Team;
import misc.math.game.GamePosition;
import misc.math.game.GameRect;
import misc.math.pixel.PixelPosition;

public class GameTileMap
{
	private ExtendedTile[][] tiles;
	private TreeMap<Team, GamePosition> spawnPositions = new TreeMap<Team, GamePosition>();
	private BufferedImage staticImage;
	
	public GameTileMap(LobbyTileMap lobbyTileMap)
	{
		tiles = new ExtendedTile[lobbyTileMap.getInts().length][lobbyTileMap.getInts()[0].length];

		for (int x = 0; x < lobbyTileMap.getInts().length; x++)
		{
			for (int y = 0; y < lobbyTileMap.getInts()[0].length; y++)
			{
				tiles[x][y] = ExtendedTile.getByColorID(lobbyTileMap.getInts()[x][y], x+0.5f, y+0.5f);
			}
		}

		for (int x = 0; x < tiles.length; x++)
		{
			for (int y = 0; y < tiles[0].length; y++)
			{
				if (tiles[x][y] instanceof SpawnTeamTile)
					spawnPositions.put(((SpawnTeamTile) tiles[x][y]).getTeam(), new GamePosition(x, y));
			}
		}
		staticImage = new BufferedImage(tiles.length * TILESIZE, tiles[0].length * TILESIZE, BufferedImage.TYPE_INT_ARGB);
		initStaticImage();
	}

	public GamePosition getSpawnTilePositionByTeam(Team team)
	{
		for (int x = 0; x < tiles.length; x++)
		{
			for (int y = 0; y < tiles[0].length; y++)
			{
				if (tiles[x][y] instanceof SpawnTeamTile && ((SpawnTeamTile) tiles[x][y]).getTeam() == team)
					return new GamePosition(x+0.5f, y+0.5f);
			}
		}
		Debug.warn("GameTileMap.getSpawnTilePositionByTeam(): no SpawnTile found for team " + team);
		return null;
	}	

	// returnt Bild von der Map (nur StaticBlocks)
	// Das Bild passt sich NICHT dem Zoom an
	private void initStaticImage()
	{
		Graphics g = getStaticImage().getGraphics();
		for (int x = 0; x < tiles.length; x++)
		{
			for (int y = 0; y < tiles[0].length; y++)
			{
				Debug.warnIf(tiles[x][y] == null, "GameTileMap.initStaticImage(): tiles[" + x + "][" + y + "] == null");
				Debug.warnIf(tiles[x][y].getImageID() == null, "GameTileMap.initStaticImage(): tiles[" + x + "][" + y + "].getImageID() == null");
				g.drawImage(ImageFile.getImageByImageID(tiles[x][y].getImageID()), x * TILESIZE, y * TILESIZE, TILESIZE, TILESIZE, null);
				if (tiles[x][y] instanceof SpawnTeamTile)
				{
					Color c = ((SpawnTeamTile)tiles[x][y]).getTeam().getColor();
					c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 50);
					g.setColor(c);
					g.fillRect(x*TILESIZE, y*TILESIZE, TILESIZE, TILESIZE);
				}
			}
		}

	}

	public LinkedList<ExtendedTile> getPossibleColliderTiles(Entity e)
	{
		LinkedList<ExtendedTile> colliders = new LinkedList<ExtendedTile>();
		int minX, minY, maxX, maxY;
		//float r = e.getSize().getMagnitude() * 0.5f;
		minX = (int) (e.getLeft());
		maxX = (int) (e.getRight());
		minY = (int) (e.getTop());
		maxY = (int) (e.getBot());

		if (minX < 0)
		{
			minX = 0;
			Debug.warn("GameTileMap.getPossibleColliderTiles(): Entity " + e + " seems to be out of Map");
		}

		if (minY < 0)
		{
			minY = 0;
			Debug.warn("GameTileMap.getPossibleColliderTiles(): Entity " + e + " seems to be out of Map");
		}

		if (maxX >= tiles.length)
		{
			maxX = tiles.length-1;
			Debug.warn("GameTileMap.getPossibleColliderTiles(): Entity " + e + " seems to be out of Map");
		}

		if (maxY >= tiles[0].length)
		{
			maxY = tiles[0].length-1;
			Debug.warn("GameTileMap.getPossibleColliderTiles(): Entity " + e + " seems to be out of Map");
		}

		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
			{
				if (tiles[x][y].isObstacle())
					colliders.add(tiles[x][y]);
			}
		return colliders;
	}

	public boolean isObstacleAt(int x, int y)
	{
		return isValidGridPosition(x, y) && (tiles[x][y]).isObstacle();
	}

	public boolean isValidGridPosition(int x, int y)
	{
		return (x >= 0) && (x < tiles.length) && (y >= 0) && (y < tiles[0].length);
	}

	public boolean isInMap(GamePosition position)
	{
		return (position.getX() >= 0) && (position.getY() >= 0) && (position.getX() < tiles.length+1) && (position.getY() < tiles[0].length+1);
	}

	public boolean isInMap(GameRect rect)
	{
		return isInMap(rect.getPosition().minus(rect.getSize().divide(2.f)))
		    && isInMap(rect.getPosition().plus(rect.getSize().divide(2.f)));
	}

	public boolean couldGoHere(Entity e, GamePosition pos)
	{
		int minX = (int)(e.getLeft() + pos.getX() - e.getPosition().getX());
		int maxX = (int)(e.getRight() + pos.getX() - e.getPosition().getX())+1;
		int minY = (int)(e.getTop() + pos.getY() - e.getPosition().getY());
		int maxY = (int)(e.getBot() + pos.getY() - e.getPosition().getY())+1;
		for (int x = minX; x < maxX; x++)
			for (int y = minY; y < maxY; y++)
				if (!isValidGridPosition(x, y) || isObstacleAt(x, y))
					return false;
		return true;
	}

	public BufferedImage getStaticImage()
	{
		Debug.warnIf(staticImage == null, "GameTileMap.getStaticImage(): returns null");
		return staticImage;
	}

	public void tick()
	{
		for (int x = 0; x < tiles.length; x++)
		{
			for (int y = 0; y < tiles[0].length; y++)
			{
				tiles[x][y].tick();
			}
		}
	}

	public static GameTileMap get()
	{
		Debug.warnIf(Game.get() == null, "GameTileMap.get(): game == null");
		Debug.warnIf(Game.get().getGameTileMap() == null, "GameTileMap.get(): gameTileMap == null");
		return Game.get().getGameTileMap();
	}
}
