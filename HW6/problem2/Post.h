
#ifndef PROBLEM2_POST_H
#define PROBLEM2_POST_H
#include <iostream>
#include <vector>
class Post {
public:
    int id;
    std::string title;
    std::string content;
    std::string date;
    Post(int id, std::string title, std::string content, std::string date);
    bool operator>(const Post& r) const;
    long long date_num;
private:

};


#endif //PROBLEM2_POST_H
