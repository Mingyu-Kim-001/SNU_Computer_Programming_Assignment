#include "admin_ui.h"

AdminUI::AdminUI(ShoppingDB &db, std::ostream& os): UI(db, os) { }

void AdminUI::add_product(std::string name, int price) {
    // TODO: For problem 1-1
    if(price>0){
        os<<"ADMIN_UI: "<<name<<" is added to the database."<<std::endl;
        db.addProduct(name,price);
    }
    else{
        os<<"ADMIN_UI: Invalid price."<<std::endl;
    }
}

void AdminUI::edit_product(std::string name, int price) {
    // TODO: For problem 1-1
    int errorCode = db.modifyProduct(name,price);
    if(errorCode==ShoppingDB::MODIFY_SUCCESS){
        os<<"ADMIN_UI: "<<name<<" is modified from the database."<<std::endl;
    }
    else if(errorCode==ShoppingDB::NO_NAME_MATCH){
        os<<"ADMIN_UI: Invalid product name."<<std::endl;
    }
    else if(errorCode==ShoppingDB::PRICE_INVALID){
        os<<"ADMIN_UI: Invalid price."<<std::endl;
    }
}

void AdminUI::list_products() {
    // TODO: For problem 1-1
    std::string* result = db.list_products();
    os<<"ADMIN_UI: Products: ["<<*result<<"]"<<std::endl;
    delete result;
}
