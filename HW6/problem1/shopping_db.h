#ifndef PROBLEM1_SHOPPING_DB_H
#define PROBLEM1_SHOPPING_DB_H

#include <string>
#include <vector>
#include <map>
#include "user.h"
#include "product.h"

class ShoppingDB {
public:
    ShoppingDB();
    void addProduct(std::string name, int price);
    int modifyProduct(std::string name, int price);
    void addUser(std::string name, std::string password,bool premium);
    //bool isLogin(std::string username);
    User* login(std::string name, std::string password);
    Product* findProduct(std::string product_name);
    void add_to_cart(User* user,Product* product);
    bool logout();
    std::string* list_products();
    std::string* list_cart_products(User* user);
    int buy_all_in_cart(User* user);
    void addPurchaseInfo(User* user, Product* product);
    std::string* recommend_products(User* user);
    enum{
        MODIFY_SUCCESS,
        NO_NAME_MATCH,
        PRICE_INVALID,
    };
private:
    std::vector<User*> users;
    std::vector<Product*> products;
    std::map<User*,std::vector<Product*>> cartOfAllUsers;
    std::map<User*,std::vector<Product*>> purchaseInfoOfAllUsers;
    std::string ProductToString(User* user, Product* product);
};

#endif //PROBLEM1_SHOPPING_DB_H
