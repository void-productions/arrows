#include "ArrowSalveSkill.hpp"

#include <misc/Debug.hpp>
#include <entity/bullets/SimpleArrow.hpp>
#include <entity/Mob.hpp>

ArrowSalveSkill::ArrowSalveSkill(const SkillGivethrough& gt)
	: TriggerSkill(gt)
{}

void ArrowSalveSkill::onTrigger()
{
	constexpr int ARROW_COUNT = 4;
	if (getOwner()->getSpeed().x > 0.f)
	{
		for (int i = 0; i < ARROW_COUNT; i++)
		{
			addBullet(new SimpleArrow(getOwner()->getPosition() + GameVector(i, 0), getOwner()->getSpeed() + GameVector(1.4f, 0.f), getOwner()));
		}
	}
	else
	{
		for (int i = 0; i < ARROW_COUNT; i++)
		{
			addBullet(new SimpleArrow(getOwner()->getPosition() + GameVector(i, 0), getOwner()->getSpeed() + GameVector(-1.4f, 0.f), getOwner()));
		}
	}
}
