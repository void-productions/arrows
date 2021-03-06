/*
 * speichert die map währed des LobbyMenues
 */

package tile.map;

import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import misc.Debug;

public class LobbyTileMap
{
	private int[][] map;

	public LobbyTileMap(int[][] map)
	{
		this.map = map;
	}

	// returnt eine neue LobbyTileMap aus dem übergebenen Pfad
	public static LobbyTileMap getByFile(File path)
	{
		if (!path.exists())
		{
			Debug.warn("LobbyTileMap.getByFile(): Try to load Map \"" + path.getPath() + "\" but it does not exists");
			return null;
		}

		if (!path.isFile())
		{
			Debug.warn("LobbyTileMap.getByFile(): Try to load Map \"" + path.getPath() + "\" but it is not a File");
			return null;
		}

		BufferedImage image = null;
		try
		{
			image = ImageIO.read(path);
		} catch (Exception e)
		{
			Debug.warn("LobbyTileMap.getByFile(): can't open File \"" + path.getPath() + "\". Maybe wrong format.");
			return null;
		}

		Debug.warnIf(image == null, "LobbyTileMap.getByFile(): returns null");

		return getByBufferedImage(image);
	}

	// returnt eine neue LobbyTileMap aus dem übergebenen image
	public static LobbyTileMap getByBufferedImage(BufferedImage image)
	{
		int[][] map = new int[image.getWidth()][image.getHeight()];

		for (int x = 0; x < image.getWidth(); x++)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				Color c = new Color(image.getRGB(x, y));
				map[x][y] = Integer.parseInt(String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()), 16);
			}
		}

		return new LobbyTileMap(map);
	}

	public int[][] getInts()
	{
		if (map == null)
		{
			Debug.warn("LobbyTileMap.getInts(): returns null");
		}
		return map;
	}
}
