#ifndef PROBLEM2_APP_H
#define PROBLEM2_APP_H
#include "Post.h"
#include <iostream>
#include <fstream>
#include "config.h"
#include <filesystem>
#include <ctime>
#include "time.h"
#include <vector>
#include <algorithm>

class App {
public:
    App(std::istream& is, std::ostream& os);
    void run();
    static char POST_TIME_FORMAT[50];

private:
    std::istream& is;
    std::ostream& os;
    bool authenticate(std::istream& is, std::ostream& os,std::string id);
    bool checkPasswd(std::string id, std::string passwd);
    std::string getNewPostPath(std::string id);
    void writePost(std::istream& is, std::ostream& os,std::string filePathToWrite);
    Post* readOnePost(std::ifstream& postFile,int postId);
    void readPostOfSomeone(std::string name,std::vector<Post*>& friendPost);
    void recommend(std::istream& is, std::ostream& os,std::string id);
    static bool comparePostPtr(Post*a, Post*b);
    void printRecommendedPost(std::ostream& os, Post* post);
    void printFunctionIntroduction(std::ostream& os, std::string id);
    struct PostForCompareSearchResult{
        Post* post;
        int occurrence;
    };
    std::vector<std::string>* strsplit(std::string& s,std::string delim);
    int countOccurrence(std::string& s, std::vector<std::string>& keywords);
    static bool compareSearchedPost(PostForCompareSearchResult& a, PostForCompareSearchResult&b);
    void printSearchedPost(std::ostream& os, Post* post);
    void search(std::istream& is, std::ostream& os, std::string command);
    void search2(std::istream& is, std::ostream& os, std::string command);

};

#endif //PROBLEM2_APP_H
