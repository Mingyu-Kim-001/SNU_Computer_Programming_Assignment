
#include "Post.h"
Post::Post(int id, std::string title, std::string content, std::string date): id(id),title(title),content(content),date(date){
//    char year[10],month[10],day[10],hour[10],minute[10],second[10];
//    std::sscanf(date.c_str(),App::POST_TIME_FORMAT_TO_SCAN,year,month,day,hour,minute,second);
//    std::string year_s(year);
//    std::string month_s(month),day_s(day),hour_s(hour),minute_s(minute),second_s(second);
//    std::cout<<month_s<<std::endl;
//    date_num = std::stoll(year_s+month_s+day_s+hour_s+minute_s+second_s);
//    std::cout<<date<<std::endl<<date_num<<std::endl;
    std::string s = date;

    std::vector<std::string> delimiterList = {"/","/"," ",":",":"};
    std::string parseDate = "";
    for(auto delimiter:delimiterList){
        size_t pos = s.find(delimiter);
        parseDate+= s.substr(0,pos);
        s.erase(0, pos + delimiter.length());
    }
    parseDate+= s;
    date_num = stoll(parseDate);
}

bool Post::operator>(const Post& r) const
{
    return date_num > r.date_num;
}