package network.lobby.packets.user;

import misc.Debug;
import misc.compress.*;
import network.lobby.packets.UserPacket;
import network.lobby.packets.PlayerPropertyUserPacket;
import player.property.skill.Skill;
import player.property.PlayerProperty;

public class SkillUserPacket extends UserPacket implements PlayerPropertyUserPacket
{
	private byte[] skillIDs;

	public SkillUserPacket(byte[] skillIDs)
	{
		this.skillIDs = new byte[Skill.SKILLS_SIZE];
		for (byte i = 0; i < skillIDs.length; i++)
		{
			this.skillIDs[i] = skillIDs[i];
		}
	}

	public SkillUserPacket(SkillUserPacket packet)
	{
		this(packet.getSkillIDs());
	}

	public void assign(SkillUserPacket skillPacket)
	{
		Debug.warnIf(skillPacket == null, "SkillUserPacket.assign(null)");
		skillIDs = skillPacket.skillIDs;
	}


	// Setter
	@Override public void setIDs(byte[] ids)
	{
		for (int i = 0; i < ids.length; i++)
		{
			skillIDs[i] = ids[i];
		}
	}

	// getter
	public Skill[] getSkills()
	{
		Skill[] skills = new Skill[Skill.SKILLS_SIZE];
		for (byte i = 0; i < Skill.SKILLS_SIZE; i++)
		{
			skills[i] = Skill.getByID(skillIDs[i]);	
		}
		return skills;
	}

	public byte[] getSkillIDs() { return skillIDs; }

	public SkillUserPacket(CompressBuffer buffer)
	{
		skillIDs = buffer.cutByteArray();
	}

	@Override public byte getCID() { return Compressor.SKILL_USER_PACKET_CID; }
	@Override public byte[] compress()
	{
		return Compressor.compressByteArray(skillIDs);
	}

	@Override public PlayerProperty[] getPlayerProperty() { return getSkills(); }
}
