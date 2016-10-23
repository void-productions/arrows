#include "LobbyTileMap.hpp"

#include <misc/Converter.hpp>

#include <SFML/Graphics.hpp>

LobbyTileMap::LobbyTileMap()
{}

LobbyTileMap::LobbyTileMap(const std::vector<std::vector<int>>& ints)
{
	this->ints = ints;
}

void LobbyTileMap::updateMap(const std::vector<std::vector<int>>& ints)
{
	this->ints = ints;
}

std::vector<std::vector<int>> LobbyTileMap::getIntsByFile(const std::string& path)
{
	sf::Image image;
	image.loadFromFile(path);
	std::vector<std::vector<int>> ints;

	ints.reserve(image.getSize().x);
	for (unsigned int x = 0; x < image.getSize().x; x++)
	{
		ints.push_back(std::vector<int>()); // Spalten werden in den vector geschrieben
		ints.back().reserve(image.getSize().y);
		for (unsigned int y = 0; y < image.getSize().y; y++)
		{
			ints[x].push_back(Converter::colorToInt(image.getPixel(x, y))); // Einzelne Werte werden in die jeweilige Spalte geschrieben
		}
	}

	return ints;
}

std::vector<std::vector<int>> LobbyTileMap::getInts() const
{
	return ints;
}

bool LobbyTileMap::isValid() const
{
	return getInts().size() > 0;
}