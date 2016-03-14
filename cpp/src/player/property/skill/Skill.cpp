#include "Skill.hpp"

#include "../../../misc/Converter.hpp"
#include "../../../misc/Debug.hpp"
#include "skills/ArrowShotSkill.hpp"

extern const char ARROWSHOTSKILL_SID = 0;

std::vector<Skill*> Skill::skills;

void Skill::init()
{
	skills.push_back(new ArrowShotSkill());
}

void Skill::uninit()
{
	skills.clear();
}

Skill* Skill::get(int id)
{
	Debug::warnIf(id < 0 || id >= getAmount(), "Skill::get(): id(" + Converter::intToString(id) + ") out of range");
	return skills[id];
}

int Skill::getAmount()
{
	return skills.size();
}

const std::vector<Skill*> Skill::getAllSkillsByAvatarID(char avatarID)
{
	std::vector<Skill*> tmp;
	for (int i = 0; i < skills.size(); i++)
	{
		if (skills[i]->getAvatarID() == avatarID)
		{
			tmp.push_back(skills[i]);
		}
	}
	return tmp;
}