package cosmetic;

import entity.entities.dynamic.spinnable.MinimizedSpinnableEntity;
import graphics.ImageID;
import misc.Debug;
import misc.math.game.GamePosition;

public class MinimizedCosmetic extends MinimizedSpinnableEntity
{
	public MinimizedCosmetic(GamePosition position, ImageID imageID, float rotation, boolean[] effectIDs)
	{
		super(position, imageID, rotation, effectIDs);
	}
}
