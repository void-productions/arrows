package game.avatar.avatars;

import game.avatar.Avatar;
import graphics.ImageID;
import graphics.ImageFile;

public class Archer extends Avatar
{
	@Override public ImageID getIconImageID() // works
	{
		return ImageFile.ARCHER_ICON.getImageID();
	}

	@Override public String getName() { return "Legolas ^^"; }
	@Override public String getDescription() { return "a funny fernkämpfer"; }
}
