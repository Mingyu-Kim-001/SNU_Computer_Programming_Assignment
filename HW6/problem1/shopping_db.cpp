#include <cmath>
#include <set>
#include <algorithm>
#include "shopping_db.h"
#include <iostream>

ShoppingDB::ShoppingDB() {

}
std::string ShoppingDB::ProductToString(User* user, Product* product){
    std::string result;
    bool premium = user?user->premium:false;
    int price = premium? round((product->price)*0.9): product->price;
    result+= "(" + product->name + ", " + std::to_string(price) + ")";
    return result;
}
void ShoppingDB::addProduct(std::string name, int price) {
    products.push_back(new Product(name,price));
}
int ShoppingDB::modifyProduct(std::string name, int price) {
    for(Product* product:products){
        if(product->name==name){
            if(price<=0) return PRICE_INVALID;
            product->price = price;
            return MODIFY_SUCCESS;
        }
    }
    return NO_NAME_MATCH;
}
std::string* ShoppingDB::list_products() {
    std::string* result = new std::string("");
    int n = products.size();
    for(int i=0;i<n;i++){
        Product* product = products[i];
        (*result)+= ProductToString(nullptr,product);
        if(i<n-1) (*result)+=", ";
    }
    return result;
}
Product* ShoppingDB::findProduct(std::string product_name){

    for(Product* product:products){
        if(product->name==product_name){
            return product;
        }
    }
    return nullptr;
}

void ShoppingDB::addUser(std::string username, std::string password, bool premium){
    //users.push_back(premium?((User*)new PremiumUser(username,password)):((User*)new NormalUser(username,password)));
    users.push_back(new User(username,password,premium));
}

User* ShoppingDB::login(std::string username, std::string password){
    for(User* user:users){
        if(user->name==username){
            if(user->checkPassword(password)){
                //loginUsers.push_back(user);
                return user;
            }
            else{
                return nullptr;
            }
        }
    }
    return nullptr;
}
void ShoppingDB::add_to_cart(User* user,Product* product){
    auto iter = cartOfAllUsers.find(user);
    if(iter==cartOfAllUsers.end()){
        std::vector<Product*> newCart;
        newCart.push_back(product);
        cartOfAllUsers.insert(std::pair<User*,std::vector<Product*>>(user,newCart));
    }
    else{
        iter->second.push_back(product);
    }
}
std::string* ShoppingDB::list_cart_products(User* user){
    auto iter = cartOfAllUsers.find(user);
    std::string* result = new std::string("");
    if(iter==cartOfAllUsers.end()){
        return result;
    }
    else{
        auto cart = iter->second;
        int n = cart.size();
        for(int i=0;i<n;i++){
            Product* product = cart.at(i);
            (*result)+= ProductToString(user,product);
            if(i<n-1) (*result)+=", ";
        }
        return result;
    }
}
void ShoppingDB::addPurchaseInfo(User* user, Product* product){
    auto iter = purchaseInfoOfAllUsers.find(user);
    if(iter==purchaseInfoOfAllUsers.end()){
        std::vector<Product*> purchaseList;
        purchaseList.push_back(product);
        purchaseInfoOfAllUsers.insert(std::pair<User*,std::vector<Product*>>(user,purchaseList));
    }
    else{
        purchaseInfoOfAllUsers.find(user)->second.push_back(product);
    }
}

int ShoppingDB::buy_all_in_cart(User* user){
    auto iter = cartOfAllUsers.find(user);
    if(iter==cartOfAllUsers.end()) return 0;
    auto& cart = iter->second;
    int total_price = 0;
    for(Product* product:cart){
        total_price+= product->price;
        addPurchaseInfo(user,product);
    }
    if(user->premium) total_price = round(total_price*0.9);
    cart.clear();
    return total_price;
}
bool pairCompare(const std::pair<int,int>& a, const std::pair<int,int>& b){
    if(a.first == b.first){
        return a.second<b.second;
    }
    return a.first>b.first;
}

std::string* ShoppingDB::recommend_products(User* user){
    std::string* result = new std::string("");
    std::vector<Product*> recommendationList;
    if(!user->premium) {//for normal user
        auto iter = purchaseInfoOfAllUsers.find(user);
        if (iter == purchaseInfoOfAllUsers.end()) return result;
        auto purchaseList = iter->second;
        for(auto i=purchaseList.rbegin();i!=purchaseList.rend();++i){
            if(std::find(recommendationList.begin(),recommendationList.end(),*i)==recommendationList.end()){
                recommendationList.push_back(*i);
                if(recommendationList.size()==3) break;
            }
        }
    }
    else{//for premium user
        std::vector<std::pair<int,int>> simAndOrderOfEachUser;
        auto iterOfThisUser = purchaseInfoOfAllUsers.find(user);
        if(iterOfThisUser==purchaseInfoOfAllUsers.end()){//There is no purchase info of this user.
            for(int i=0;i<users.size();i++){
                simAndOrderOfEachUser.push_back(std::pair<int,int>(0,i));
            }
        }
        else {
            std::set<Product *> uniquePurchaseOfThisUser(iterOfThisUser->second.begin(), iterOfThisUser->second.end());
            for (int i = 0; i < users.size(); i++) {
                if (users[i] == user) continue;
                auto iter = purchaseInfoOfAllUsers.find(users[i]);
                int numOfCommonPurchased = 0;
                if (iter != purchaseInfoOfAllUsers.end()) {
                    std::set<Product *> uniquePurchase(iter->second.begin(), iter->second.end());
                    std::set<Product*> intersection;
                    std::set_intersection(uniquePurchaseOfThisUser.begin(),uniquePurchaseOfThisUser.end(),
                                          uniquePurchase.begin(),uniquePurchase.end(),
                                          std::inserter(intersection,intersection.begin()));
                    numOfCommonPurchased = intersection.size();
                }
                simAndOrderOfEachUser.push_back(std::pair<int, int>(numOfCommonPurchased, i));
            }
        }
        sort(simAndOrderOfEachUser.begin(),simAndOrderOfEachUser.end(),pairCompare);
        for(auto & i : simAndOrderOfEachUser) {
            auto iter = purchaseInfoOfAllUsers.find(users[i.second]);
            if (iter == purchaseInfoOfAllUsers.end()) continue;
            if (std::find(recommendationList.begin(), recommendationList.end(), iter->second.back()) ==
                recommendationList.end()) {
                recommendationList.push_back(iter->second.back());
                if (recommendationList.size() == 3) break;
            }
        }
    }
    int n = recommendationList.size();
    for(int i=0;i<n;i++){
        //std::cout<<"test : "<<recommendationList[i]->name<<std::endl;
        (*result)+= ProductToString(user,recommendationList[i]);
        if(i<n-1) (*result)+=", ";
    }
    return result;
}