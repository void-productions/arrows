/*
	Klasse aller Icons die im ChoosePlayerPropertyMenu gelistet werden
*/

package menu.components.icons;

import game.PlayerProperty;
import graphics.ImageFile;
import graphics.ImageID;
import menu.components.Icon;
import menu.ComponentContainer;
import misc.math.Rect;
import misc.Debug;

public class PlayerPropertyIcon extends Icon
{
	public static final int WIDTH = 40;
	public static final int HEIGHT = 40;
	private PlayerProperty playerProperty;

	public PlayerPropertyIcon(ComponentContainer parent, Rect rect, PlayerProperty playerProperty)
	{
		super(parent, rect);
		this.playerProperty = playerProperty;
	}

	// Setter
	public void setPlayerProperty(PlayerProperty playerProperty)
	{
		this.playerProperty = playerProperty;
	}
	// Getter
	public PlayerProperty getPlayerProperty() { return playerProperty; }
	@Override public ImageID getImageID()
	{
		// Falls noch kein 
		if (getPlayerProperty() == null)
		{
			Debug.once("PlayerPropertyIcon.getImageID(): getPlayerProperty() == null -> returning ImageFile.VOID_ICON.getImageID()");
			return ImageFile.VOID_ICON.getImageID();
		}
		return getPlayerProperty().getIconImageID();
	}
}
