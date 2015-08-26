package core.menu.menues;

import core.Main;
import core.menu.Menu;
import core.menu.components.*;
import misc.math.*;

public class CreateServerMenu extends Menu
{
	public CreateServerMenu()
	{
		getComponents().add(new Label(this, new Rect(400, 10, 200, 40), "Create Server"));
		getComponents().add(new Button(this, new Rect(300, 300, 100, 100), "Create Server")
		{
			@Override public void onClick(int mouseButton)
			{
				Main.getMenues().add(new ServerLobbyMenu());
			}
		});
		getComponents().add(new BackButton(this, new Position(10, 500)));
	}

	@Override public boolean isFullscreen() { return true; }
}
