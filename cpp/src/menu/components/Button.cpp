#include "Button.hpp"

#include <misc/Debug.hpp>
#include <core/Screen.hpp>

Button::Button(ComponentContainer* parent, const PixelRect& rect, const std::string& s) : MenuComponent(parent, rect), text(s)
{}

void Button::render() const
{
	if (isEnabled())
	{
		Screen::fillRect(getAbsoluteRect(), sf::Color::Red);
	}
	else
	{
		Screen::fillRect(getAbsoluteRect(), sf::Color(120, 100, 100));
	}
	Screen::drawText(getText(), getAbsoluteRect().getPosition(), sf::Color::Black);
}

void Button::onClick(int mouseButton)
{
	if (isEnabled())
	{
		onPress();
	}
}

std::string Button::getText() const
{
	return text;
}

void Button::setText(const std::string& newText)
{
	text = newText;
}
