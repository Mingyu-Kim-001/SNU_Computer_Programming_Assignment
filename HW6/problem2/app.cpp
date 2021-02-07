#include "app.h"
#include "Post.h"
#include <iostream>
#include <fstream>
#include "app.h"
#include "config.h"
#include <filesystem>
#include <ctime>
#include "time.h"
#include <vector>

char App::POST_TIME_FORMAT[50] = "%Y/%m/%d %H:%M:%S";
App::App(std::istream& is, std::ostream& os): is(is), os(os) {
    // TODO
}
void App::run() {
    // TODO
    //os<<"------ Authentication ------"<<std::endl<<"id="<<std::endl;
    os<<"------ Authentication ------"<<std::endl<<"id=";
    std::string id;
    getline(is,id);
    //os<<"passwd="<<std::endl;
    os<<"passwd=";
    if(!authenticate(is,os,id)) return;
    std::string command;
    while(true) {
        printFunctionIntroduction(os, id);
        os << "-----------------------------------" << std::endl;
        //os << "Command="<<std::endl;
        os << "Command=";
        getline(is, command);
        if (command == "post") {
            std::string filePathToWrite = getNewPostPath(id);
            writePost(is, os, filePathToWrite);
        } else if (command == "recommend") {
            recommend(is, os, id);
        } else if(command.length()>=6 && command.substr(0,6)=="search"){
            if(command.length()==6 || command.at(6) == ' ') {
                //std::cout<<command<<std::endl;
                search2(is,os,command);
            }
        } else if(command=="exit"){
            return;
        }

    }
}

bool App::checkPasswd(std::string id, std::string passwd){
    std::ifstream passwdFile;
    passwdFile.open(SERVER_STORAGE_DIR + id + "/password.txt");
    std::string truePasswd;
    return getline(passwdFile,truePasswd) && passwd==truePasswd;

}
void App::printFunctionIntroduction(std::ostream& os, std::string id){
    os<<"-----------------------------------"<<std::endl;
    os<<id<<"@sns.com"<<std::endl;
    os<<"post : Post contents"<<std::endl;
    os<<"recommend : recommend interesting posts"<<std::endl;
    os<<"search <keyword> : List post entries whose contents contain <keyword>"<<std::endl;
    os<<"exit : Terminate this program"<<std::endl;
}
bool App::authenticate(std::istream& is, std::ostream& os,std::string id){
    std::string passwd;
    getline(is,passwd);
    if(checkPasswd(id,passwd)){
        return true;
    }
    else{
        os<<"Failed Authentication."<<std::endl;
        return false;
    }
}
std::string App::getNewPostPath(std::string id){
    std::string path = SERVER_STORAGE_DIR;
    int maxFileNum = -1;
    for(const auto & userDir : std::filesystem::directory_iterator(path)) {
        std::string postDirPath = userDir.path().string() + "/post";
        for (const auto &postEntry : std::filesystem::directory_iterator(postDirPath)) {
            std::string filename = postEntry.path().filename().string();
            int fileNum = std::stoi(filename.substr(0,filename.find(".")));
            if(maxFileNum<fileNum) {
                maxFileNum = fileNum;
            }
        }
    }
    std::string resultPath = path + id + "/post/" + std::to_string(maxFileNum+1) + ".txt";
    return resultPath;
}
void App::writePost(std::istream& is, std::ostream& os,std::string filePathToWrite){
    std::string title,content="",contentLine;
    os<<"-----------------------------------"<<std::endl;
    os<<"New Post"<<std::endl;
    os<<"* Title="<<std::endl;
    getline(is,title);
    os<<"* Content"<<std::endl;
    while(true){
        os<<">"<<std::endl;
        getline(is,contentLine);
        if(contentLine=="") break;
        content+= contentLine + "\n";
    }
    std::ofstream postFile;
    postFile.open(filePathToWrite);
    std::time_t t = std::time(0);   // get time now
    std::tm* now = std::localtime(&t);
    char timeWithFormat[256];
    std::strftime(timeWithFormat,256,POST_TIME_FORMAT,now);
    postFile<<timeWithFormat<<std::endl;
    postFile<<title<<std::endl<<std::endl<<content;
    postFile.close();
}
void App::printRecommendedPost(std::ostream& os, Post* post){
    os<<"id: "<<post->id<<std::endl;
    os<<"created at: "<<post->date<<std::endl;
    os<<"title: "<<post->title<<std::endl;
    os<<"content:"<<std::endl;
    os<<post->content;
    if(post->content=="") os<<std::endl;
}
bool App::comparePostPtr(Post*a, Post*b) {
    return a->date_num > b->date_num;
}
void App::recommend(std::istream& is, std::ostream& os,std::string id){
    std::string friendListPath = SERVER_STORAGE_DIR + id + "/friend.txt";
    std::ifstream friendListFile;
    friendListFile.open(friendListPath);
    std::string friendName;
    std::vector<Post*> friendPost;
    while(getline(friendListFile,friendName)){
        readPostOfSomeone(friendName,friendPost);
    }
    sort(friendPost.begin(),friendPost.end(),comparePostPtr);
    for(int i=0;i<friendPost.size();i++){
        if(i>=10) break;
        os<<"-----------------------------------"<<std::endl;
        printRecommendedPost(os,friendPost[i]);
    }
    //os<<"-----------------------------------"<<std::endl;
    for(auto postPtr:friendPost){
        delete postPtr;
    }
}
Post* App::readOnePost(std::ifstream& postFile,int postId){
    std::string line, title, content,date;
    int index = 0;
    while(getline(postFile,line)){
        if(index==0){
            date = line;
        } else if(index==1){
            title = line;
        } else if(index>2){ // if index==2, do nothing(there is only a linebreak)
            content+= line + "\n";
        }
        index++;
    }
    Post* post = new Post(postId,title,content,date);
    return post;
}
void App::readPostOfSomeone(std::string name,std::vector<Post*>& friendPost){
    std::string path = SERVER_STORAGE_DIR + name + "/post";
    for(const auto & entry : std::filesystem::directory_iterator(path)){
        std::string filename = entry.path().filename().string();
        int postId = std::stoi(filename.substr(0,filename.find(".")));
        std::ifstream postFile;
        postFile.open(entry.path());
        Post* post = readOnePost(postFile,postId);
        friendPost.push_back(post);
    }
}
int App::countOccurrence(std::string& s, std::vector<std::string>& keywords){
    std::vector<std::string>* splitted = strsplit(s," ");
    int occurrence = 0;
    for(auto word:(*splitted)){
        //std::cout<<word<<" ";
        for(auto keyword:keywords){
            if(word==keyword) occurrence++;
        }
    }
    delete splitted;
    return occurrence;
}
bool App::compareSearchedPost(PostForCompareSearchResult& a, PostForCompareSearchResult&b) {
    if(a.occurrence==b.occurrence){
        return comparePostPtr(a.post, b.post);
    }
    return a.occurrence>b.occurrence;
}
void App::printSearchedPost(std::ostream& os, Post* post){
    os<<"id: "<<post->id<<", created at: "<<post->date<<", title: "<<post->title<<std::endl;
}
std::vector<std::string>* App::strsplit(std::string& s,std::string delim){
    std::vector<std::string>* splitted = new std::vector<std::string>;
    size_t previous = 0, current;
    current = s.find(delim,previous); // to not include the first word "search"
    std::string substring;
    while (current != std::string::npos){
        substring = s.substr(previous, current - previous);
        if(substring.length()>0 && substring!=" ") splitted->push_back(substring);
        previous = current + 1;
        current = s.find(delim,previous);
    }
    substring = s.substr(previous,current-previous);
    if(substring.length()>0 && substring!=" ") splitted->push_back(substring);
    return splitted;
}
void App::search2(std::istream& is, std::ostream& os, std::string command){
    os<<"-----------------------------------"<<std::endl;
    std::vector<std::string> keywords;
    std::vector<std::string>* splitted = strsplit(command," ");
    bool isFirst = true;
    for(auto word:(*splitted)){
        if(isFirst){ // to not include the first word "search"
            isFirst = false;
            continue;
        }
        if(std::find(keywords.begin(), keywords.end(), word) == keywords.end()){ // to avoid duplicate
            keywords.push_back(word);
        }
    }
    delete splitted;
    std::vector<Post*>* allPosts = new std::vector<Post*>;
    std::string path = SERVER_STORAGE_DIR;
    for(const auto & userDir : std::filesystem::directory_iterator(path)) {
        std::string userName= userDir.path().filename().string();
        readPostOfSomeone(userName,(*allPosts));
    }


    std::vector<PostForCompareSearchResult> searchedPostList;
    for(Post* post:(*allPosts)){
        int occurrence = countOccurrence(post->title,keywords);
        std::istringstream iss_content(post->content);
        std::string content_line;
        while(getline(iss_content,content_line)) {
            occurrence+= countOccurrence(content_line, keywords);
        }
        if(occurrence>0) {
            struct PostForCompareSearchResult p;
            p.post = post;
            p.occurrence = occurrence;
            searchedPostList.push_back(p);
        }
    }
    //std::cout<<"check"<<std::endl;
    sort(searchedPostList.begin(),searchedPostList.end(),compareSearchedPost);
    for(int i=0;i<searchedPostList.size();i++){
        if(i>=10) break;
        printSearchedPost(os,searchedPostList[i].post);
    }
    for(auto p:(*allPosts)){
        delete p;
    }
    delete allPosts;
}
void App::search(std::istream& is, std::ostream& os, std::string command){
    os<<"-----------------------------------"<<std::endl;
    std::vector<std::string> keywords;
    std::vector<std::string>* splitted = strsplit(command," ");
    bool isFirst = true;
    for(auto word:(*splitted)){
        if(isFirst){ // to not include the first word "search"
            isFirst = false;
            continue;
        }
        if(std::find(keywords.begin(), keywords.end(), word) == keywords.end()){ // to avoid duplicate
            keywords.push_back(word);
        }
    }
    delete splitted;

    std::string path = SERVER_STORAGE_DIR;
    std::vector<PostForCompareSearchResult> searchedPostList;
    for(const auto & userDir : std::filesystem::directory_iterator(path)){
        std::string postDirPath = userDir.path().string() + "/post";
        for(const auto & postEntry : std::filesystem::directory_iterator(postDirPath)){
            std::string filename = postEntry.path().filename().string();
            int postId = std::stoi(filename.substr(0,filename.find(".")));
            std::ifstream postFile;
            postFile.open(postEntry.path());
            std::string line;
            std::string wholeLine = "";
            getline(postFile,line); // the first line(date) is useless
            while(getline(postFile,line)){
                wholeLine+= line + " ";
            }
            int occurrence = countOccurrence(wholeLine,keywords);

            if(occurrence>0){
                postFile.close();
                //std::ifstream postFile2;
                postFile.open(postEntry.path());
                Post* post = readOnePost(postFile,postId);
                struct PostForCompareSearchResult p;
                p.post = post;
                p.occurrence = occurrence;
                searchedPostList.push_back(p);
            }
        }
    }
    sort(searchedPostList.begin(),searchedPostList.end(),compareSearchedPost);
    for(int i=0;i<searchedPostList.size();i++){
        if(i>=10) break;
        printSearchedPost(os,searchedPostList[i].post);
        //std::cout<<searchedPostList[i].occurrence<<std::endl;
    }

    for(auto searchedPost:searchedPostList){
        delete searchedPost.post;
    }
}