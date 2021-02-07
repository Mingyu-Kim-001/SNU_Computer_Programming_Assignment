#ifndef PROBLEM1_USER_H
#define PROBLEM1_USER_H

#include <string>
#include <vector>
#include "product.h"

class User {
public:
    User(std::string name, std::string password, bool premium);
    const std::string name;
    bool premium;
    bool checkPassword(std::string password);
private:
    std::string password;
};

//class NormalUser : public User {
//public:
//    //NormalUser(std::string name, std::string password);
//    using User::User;
//    bool premium = false;
//private:
//    std::string password;
//};
//
//class PremiumUser : public User {
//public:
//    //PremiumUser(std::string name, std::string password);
//    using User::User;
//    bool premium = true;
//private:
//    std::string password;
//};

#endif //PROBLEM1_USER_H
