package playerproperty.item;

import playerproperty.PlayerProperty;
import playerproperty.item.items.*;
import graphics.ImageID;
import misc.Debug;

public abstract class Item extends PlayerProperty
{
	private byte id;
	private static Item[] items;

	static
	{
		items = new Item[]
		{
			new HealthRing()
		};

		for (byte i = 0; i < items.length; i++)
		{
			items[i].id = i;
		}
	}

	// for sub
	public abstract int getMassStat();

	public static Item[] getAllItems() { return items; }

	// Getter
	public static Item getByID(byte id)
	{
		if (id >= 0 && id < items.length)
		{
			return items[id];
		}
		return null;
	}

	@Override public final byte getID() { return id; }
}