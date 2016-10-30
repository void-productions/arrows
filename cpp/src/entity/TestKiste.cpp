#include "TestKiste.hpp"

#include <collision/RectBody.hpp>
#include <graphics/GraphicsManager.hpp>

TestKiste::TestKiste(const GameVector& position)
	: Idler(new RectBody(position, GameVector(0.7f, 0.5f)))
{}

TestKiste::TestKiste(CompressBuffer* buffer)
	: Idler(buffer)
{}

CompressID TestKiste::getCompressID() const
{
	return TEST_KISTE_CID;
}

std::string TestKiste::getCompressString() const
{
	return Idler::getCompressString();
}

Sponge TestKiste::getSponge() const
{
	return Sponge{1, 0.3f};
}

sf::Texture* TestKiste::getTexture() const
{
	return GraphicsManager::getTexture(TESTKISTE_GID);
}

std::string TestKiste::toString() const
{
	return "TestKiste";
}

float TestKiste::getMass() const
{
	return 10.f;
}
