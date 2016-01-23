/*
	Wird für Client-LocalPlayer vom Server zum Client geschickt und dort gespeichert.
*/

package player;

import java.util.LinkedList;

import effect.MinimizedEffect;
import misc.compress.Compressable;
import player.PlayerStats;

public class LocalClientGamePlayerFrameUpdate implements Compressable
{
	private LinkedList<MinimizedEffect> effects = new LinkedList<MinimizedEffect>();
	private PlayerStats playerStats;
	private float[] charges;

	public LocalClientGamePlayerFrameUpdate(LinkedList<MinimizedEffect> effects, PlayerStats playerStats, float[] charges)
	{
		this.effects = new LinkedList<MinimizedEffect>(effects);
		this.playerStats = new PlayerStats(playerStats);
		this.charges = new float[charges.length];
		for (int i = 0; i < charges.length; i++)
		{
			this.charges[i] = charges[i];
		}
	}

	public LinkedList<MinimizedEffect> getMinimizedEffects() { return effects; }
	public PlayerStats getPlayerStats() { return playerStats; }
	public float[] getCharges() { return charges; }
}
