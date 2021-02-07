#include "product.h"

Product::Product(std::string name, int price): name(name), price(price) { }
std::ostream& operator << ( std::ostream& os, const Product & p )
{
    return os<<(std::string)"("
    <<p.name
    <<(std::string)", "
    <<std::to_string(p.price)
    <<(std::string)")";
}
