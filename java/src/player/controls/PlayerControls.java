package player.controls;

import player.property.skill.Skill;
import misc.Debug;
import misc.math.game.GamePosition;
import network.game.packets.PlayerControlsUpdatePacket;

public class PlayerControls
{
	public static final byte SIZE_OF_CONTROLS = 7;

	public static final byte SKILL0_ID = 0;
	public static final byte SKILL1_ID = 1;
	public static final byte SKILL2_ID = 2;
	public static final byte SKILL3_ID = 3;
	public static final byte JUMP_ID = 4;
	public static final byte LEFT_ID = 5;
	public static final byte RIGHT_ID = 6;

	public boolean[] controls = new boolean[SIZE_OF_CONTROLS];
	public GamePosition mousePosition = new GamePosition();

	public void applyPlayerControlsUpdatePacket(PlayerControlsUpdatePacket packet)
	{
		Debug.warnIf(packet == null, "PlayerControls.applyPlayerControlsUpdatePacket(): packet == null");

		if (packet.mousePosition != null) // wenn die maus im screen ist
		{
			this.mousePosition = packet.mousePosition;
		}

		byte[] b = packet.controls;
		for (int i = 0; i < b.length; i++)
		{
			if (b[i] >= 100)
			{
				controls[b[i]-100] = true;
			}
			else
			{
				controls[b[i]] = false;
			}
		}
	}

	public boolean[] getControls() { return controls; }

	public boolean isJump() { return controls[JUMP_ID]; }
	public boolean isLeft() { return controls[LEFT_ID]; }
	public boolean isRight() { return controls[RIGHT_ID]; }
	public boolean[] getSkills()
	{
		boolean[] skills = new boolean[Skill.SKILLS_SIZE];
		for (int i = 0; i < Skill.SKILLS_SIZE; i++)
		{
			skills[i] = controls[i];
		}
		return skills;
	}

	public void setJump(boolean b) { getControls()[JUMP_ID] = b; }
	public void setLeft(boolean b) { getControls()[LEFT_ID] = b; }
	public void setRight(boolean b) { getControls()[RIGHT_ID] = b; }
	public void setSkill(boolean b, byte i)
	{
		Debug.warnIf((i < 0) && (i >= Skill.SKILLS_SIZE), "PlayerControls.setSkills(): invalid index = " + i);
		getControls()[i] = b;
	}
}
