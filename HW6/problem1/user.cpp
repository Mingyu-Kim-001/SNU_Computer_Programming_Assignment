#include "user.h"

User::User(std::string name, std::string password, bool premium): name(name), password(password),premium(premium) {

}
bool User::checkPassword(std::string password) {
    return this->password==password;
}