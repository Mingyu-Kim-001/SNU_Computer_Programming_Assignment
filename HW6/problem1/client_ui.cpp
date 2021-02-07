#include <vector>
#include <cmath>
#include "client_ui.h"
#include "product.h"
#include "user.h"

ClientUI::ClientUI(ShoppingDB &db, std::ostream& os) : UI(db, os), current_user() { }

ClientUI::~ClientUI() {
    delete current_user;
}

void ClientUI::signup(std::string username, std::string password, bool premium) {
    // TODO: For problem 1-2
    db.addUser(username,password,premium);
    os<<"CLIENT_UI: "<<username<<" is signed up."<<std::endl;
}

void ClientUI::login(std::string username, std::string password) {
    // TODO: For problem 1-2
    if(current_user){
        os<<"CLIENT_UI: Please logout first."<<std::endl;
    }
    else{
        User* user = db.login(username,password);
        if(user){
            os<<"CLIENT_UI: "<<username<<" is logged in."<<std::endl;
            current_user = user;
        }
        else{
            os<<"CLIENT_UI: "<<"Invalid username or password."<<std::endl;
        }
    }
}

void ClientUI::logout() {
    // TODO: For problem 1-2
    if(current_user){
        os<<"CLIENT_UI: "<<current_user->name<<" is logged out."<<std::endl;
        current_user = nullptr;
    }
    else{
        os<<"CLIENT_UI: There is no logged-in user."<<std::endl;
    }
}
bool ClientUI::isLogin(){
    if(current_user) return true;
    os<<"CLIENT_UI: Please login first."<<std::endl;
    return false;
}
void ClientUI::add_to_cart(std::string product_name) {
    // TODO: For problem 1-2
    if(!isLogin()) return;
    Product* product = db.findProduct(product_name);
    if(product){
        db.add_to_cart(current_user,product);
        os<<"CLIENT_UI: "<<product_name<<" is added to the cart."<<std::endl;
    }
    else{
        os<<"CLIENT_UI: Invalid product name."<<std::endl;
    }
}

void ClientUI::list_cart_products() {
    // TODO: For problem 1-2.
    if(!isLogin()) return;
    std::string* result = db.list_cart_products(current_user);
    os<<"CLIENT_UI: Cart: ["<<*result<<"]"<<std::endl;
    delete result;
}

void ClientUI::buy_all_in_cart() {
    // TODO: For problem 1-2
    if(!isLogin()) return;
    int total_price = db.buy_all_in_cart(current_user);
    os<<"CLIENT_UI: Cart purchase completed. Total price: "<<std::to_string(total_price)<<"."<<std::endl;

}

void ClientUI::buy(std::string product_name) {
    // TODO: For problem 1-2
    if(!isLogin()) return;
    Product* product = db.findProduct(product_name);
    if(product){
        db.addPurchaseInfo(current_user,product);
        os<<"CLIENT_UI: Purchase completed. Price: "
        <<((current_user->premium) ? round((product->price)*0.9) : product->price)
        <<"."
        <<std::endl;
    }
    else{
        os<<"CLIENT_UI: Invalid product name."<<std::endl;
    }
}

void ClientUI::recommend_products() {
    // TODO: For problem 1-3.
    if(!isLogin()) return;
    std::string* result = db.recommend_products(current_user);
    os<<"CLIENT_UI: Recommended products: ["<<*result<<"]"<<std::endl;
    delete result;
}
