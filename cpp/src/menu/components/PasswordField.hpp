#ifndef __PASSWORDFIELD_CLASS__
#define __PASSWORDFIELD_CLASS__

class ComponentContainer;
class PixelRect;

#include <menu/MenuComponent.hpp>
#include <string>

class PasswordField : public MenuComponent
{
	public:
		PasswordField(ComponentContainer*, const PixelRect&, const std::string&);
		PasswordField(ComponentContainer*, const PixelRect&);
		void render() const override;
		void onTextEntered(char c) override;
		const std::string& getText() const;
	private:
		std::string text;
	
};

#endif
