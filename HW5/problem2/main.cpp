#include <iostream>
#include <string>
#include <utility>
#include <set>
#include <vector>
#include <tuple>
#include <algorithm>
#include <cmath>
#include <fstream>

/* =======START OF PRIME-RELATED HELPERS======= */
/*
 * The code snippet below AS A WHOLE does the primality
 * test and integer factorization. Feel free to move the
 * code to somewhere more appropriate to get your codes
 * more structured.
 *
 * You don't have to understand the implementation of it.
 * But if you're curious, refer to the sieve of Eratosthenes
 *
 * If you want to just use it, use the following 2 functions.
 *
 * 1) bool is_prime(int num):
 *     * `num` should satisfy 1 <= num <= 999999
 *     - returns true if `num` is a prime number
 *     - returns false otherwise (1 is not a prime number)
 *
 * 2) std::multiset<int> factorize(int num):
 *     * `num` should satisfy 1 <= num <= 999999
 *     - returns the result of factorization of `num`
 *         ex ) num = 24 --> result = { 2, 2, 2, 3 }
 *     - if `num` is 1, it returns { 1 }
 */

const int PRIME_TEST_LIMIT = 999999;
int sieve_of_eratosthenes[PRIME_TEST_LIMIT + 1];
bool sieve_calculated = false;
void print_multiset(const std::multiset<int>& m);
void make_sieve() {
    sieve_of_eratosthenes[0] = -1;
    sieve_of_eratosthenes[1] = -1;
    for(int i=2; i<=PRIME_TEST_LIMIT; i++) {
        sieve_of_eratosthenes[i] = i;
    }
    for(int i=2; i*i<=PRIME_TEST_LIMIT; i++) {
        if(sieve_of_eratosthenes[i] == i) {
            for(int j=i*i; j<=PRIME_TEST_LIMIT; j+=i) {
                sieve_of_eratosthenes[j] = i;
            }
        }
    }
    sieve_calculated = true;
}

bool is_prime(int num) {
    if (!sieve_calculated) {
        make_sieve();
    }
    return sieve_of_eratosthenes[num] == num;
}

std::multiset<int> factorize(int num) {
    if (!sieve_calculated) {
        make_sieve();
    }
    std::multiset<int> result;
    while(num > 1) {
        result.insert(sieve_of_eratosthenes[num]);
        num /= sieve_of_eratosthenes[num];
    }
    if(result.empty()) {
        result.insert(1);
    }
    return result;
}

/* =======END OF PRIME-RELATED HELPERS======= */

/* =======START OF std::string LITERALS======= */
/* Use this code snippet if you want */

const std::string MAXIMIZE_GAIN = "Maximize-Gain";
const std::string MINIMIZE_LOSS = "Minimize-Loss";
const std::string MINIMIZE_REGRET = "Minimize-Regret";

/* =======END OF std::string LITERALS======= */


/* =======START OF TODOs======= */

std::pair<int, int> number_fight(int a, int b) {
    // TODO 2-1
    std::multiset<int> factorize_a = factorize(a);
    std::multiset<int> factorize_b = factorize(b);
    std::set<int> factorize_set_a(factorize_a.begin(), factorize_a.end());
    std::set<int> factorize_set_b(factorize_b.begin(), factorize_b.end());
    std::set<int> intersect;
    std::set_intersection(factorize_set_a.begin(),factorize_set_a.end(),
                          factorize_set_b.begin(),factorize_set_b.end(),
                          std::inserter(intersect,intersect.begin()));
    std::set<int>::iterator it;
    int product_of_distinct_prime = 1;
    for (it = intersect.begin(); it != intersect.end(); ++it) {
        int prime = *it;
        product_of_distinct_prime*= prime;
    }
    return std::pair<int, int>(a/product_of_distinct_prime,b/product_of_distinct_prime);
}
//when A attacks B
std::pair<int, int> number_attack(int a, int b){
    std::pair<int,int> number_after_fight = number_fight(a,b);
    std::pair<int,int> number_after_attack;
    int D = b - number_after_fight.second; // damage of B
    std::multiset<int> factorize_b = factorize(b);
    if(factorize_b.find(7)==factorize_b.end()){ // no 7 in factorization of b
        number_after_attack.first = a;
        number_after_attack.second = b - D;
    }
    else{
        int D2 = floor(D/2);
        number_after_attack.first = a - D2<1 ? 1:a-D2;
        number_after_attack.second = b - D2<1 ? 1:b-D2;
    }
    return number_after_attack;
}
bool will_fight(int a, int b){
    std::pair<int,int> number_after_fight = number_fight(b,a);
    std::pair<int,int> number_after_b_attacks_a = number_attack(b,a);
    std::pair<int,int> number_after_a_attacks_b = number_attack(a,b);
    bool conditional_decision_to_fight_1;
    bool conditional_decision_to_fight_2;
    //if B chooses to fight, make conditional decision 1
    conditional_decision_to_fight_1 = (number_after_fight.second>=number_after_b_attacks_a.second);
    //if B chooses not to fight, make conditional decision 2
    conditional_decision_to_fight_2 = (number_after_a_attacks_b.first>=a);
    if(conditional_decision_to_fight_1 == conditional_decision_to_fight_2) return conditional_decision_to_fight_1;
    else{
        return a<b;
    }
}
void swap_pair_element(std::pair<int,int>* ptr_some_pair){
    int temp;
    temp = ptr_some_pair->first;
    ptr_some_pair->first= ptr_some_pair->second;
    ptr_some_pair->second = temp;
}
std::pair<int, int> number_vs_number(int a, int b) {
    // TODO 2-2
    bool will_fight_a = will_fight(a,b);
    bool will_fight_b = will_fight(b,a);
    if(will_fight_a && will_fight_b) return number_fight(a,b);
    else if(will_fight_a && !will_fight_b) return number_attack(a,b);
    else if(!will_fight_a && will_fight_b){
        std::pair<int,int> result = number_attack(b,a);
        swap_pair_element(&result);
        return result;
    }
    else return std::pair<int,int>(a,b);
}
bool compare_pair(std::pair<int,int> a, std::pair<int,int> b){
    return a.first>b.first;
}
std::pair<int,int> pick_one(std::string type,std::pair<int,int>* best, std::pair<int,int>* worst,int length){
    std::pair<int,int> pick;
    if(type == MAXIMIZE_GAIN){
        pick.first = INT32_MIN; // find the max of best value
        for(int i=0;i<length;i++){
            if(best[i].first>pick.first) {
                pick = best[i];
            }
            else if(best[i].first==pick.first&&best[i].second<pick.second){
                pick = best[i];
            }
        }
    }
    else if(type == MINIMIZE_LOSS){
        pick.first = INT32_MIN; // find the max of worst values
        for(int i=0;i<length;i++){
            if(worst[i].first>pick.first){
                pick = worst[i];
            }
            else if(worst[i].first==pick.first && worst[i].second<pick.second){
                pick = worst[i];
            }
        }
    }
    else{ // regret
        if(length==1){
            pick.second = worst[0].second;
            return pick;
        }
        pick.first = INT32_MAX;
        sort(best, best + length, compare_pair);
        for (int i = 0; i < length; i++) {
            int regret = worst[i].second != best[0].second ?
                         best[0].first - worst[i].first :
                         best[1].first - worst[i].first;
            if (pick.first > regret) {
                pick.first = regret;
                pick.second = worst[i].second;
            }
            else if(pick.first==regret && worst[i].second<pick.second){
                pick.first = regret;
                pick.second = worst[i].second;
            }
        }
    }
    return pick;
}

std::pair<std::multiset<int>, std::multiset<int>> player_battle(
    std::string type_a, std::multiset<int> a, std::string type_b, std::multiset<int> b
) {
    // TODO 2-3
    std::pair<int,int>* best_a = new std::pair<int,int>[a.size()];
    std::pair<int,int>* worst_a = new std::pair<int,int>[a.size()];
    std::pair<int,int>* best_b = new std::pair<int,int>[b.size()];
    std::pair<int,int>* worst_b = new std::pair<int,int>[b.size()];
    int a_loc=0,b_loc;
    for(int i=0;i<b.size();i++) {
        best_b[i].first = INT32_MIN; // initialization for marking unchanged values
        worst_b[i].first = INT32_MAX;
    }
    for(int i=0;i<a.size();i++) {
        best_a[i].first = INT32_MIN;
        worst_a[i].first = INT32_MAX;
    }
    for(int a_num:a){
        b_loc = 0;
        for(int b_num:b){
            std::pair<int, int> result = number_vs_number(a_num, b_num);
            //std::cout<<result.first<<" "<<result.second<<std::endl;
            int delta_a = result.first - a_num;
            int delta_b = result.second - b_num;
            //std::cout<<"anum : "<<a_num<<" b_num : "<<b_num<<" delta : "<<delta_a<<" "<<delta_b<<std::endl;
            if(best_a[a_loc].first<delta_a) {
                best_a[a_loc].first = delta_a;
                best_a[a_loc].second = a_num;
            }
            if(worst_a[a_loc].first>delta_a) {
                worst_a[a_loc].first = delta_a;
                worst_a[a_loc].second = a_num;
            }
            if(best_b[b_loc].first<delta_b) {
                best_b[b_loc].first = delta_b;
                best_b[b_loc].second = b_num;
            }
            if(worst_b[b_loc].first>delta_b) {
                worst_b[b_loc].first = delta_b;
                worst_b[b_loc].second = b_num;
            }
            b_loc++;
        }
        a_loc++;
    }
    /*
    for(int i=0;i<a.size();i++){
        std::cout<<"a : "<<i<<" "<<best_a[i].first<<" "<<best_a[i].second<<std::endl;
        std::cout<<worst_a[i].first<<" "<<worst_a[i].second<<std::endl;
    }
    for(int i=0;i<b.size();i++){
        std::cout<<"b : "<<i<<" "<<best_b[i].first<<" "<<best_b[i].second<<std::endl;
        std::cout<<worst_b[i].first<<" "<<worst_b[i].second<<std::endl;
    }*/
    std::pair<int,int> pick_a = pick_one(type_a,best_a,worst_a,a.size());
    std::pair<int,int> pick_b = pick_one(type_b,best_b,worst_b,b.size());
    //std::cout<< "result "<<pick_a.second<<" "<<pick_b.second<<std::endl;
    std::pair<int, int> result = number_vs_number(pick_a.second, pick_b.second);
    //std::cout<<"alsdjf"<<std::endl;
    a.erase(a.find(pick_a.second));
    b.erase(b.find(pick_b.second));
    a.insert(result.first);
    b.insert(result.second);
    //std::cout<<"alsdjf"<<std::endl;
    return std::pair<std::multiset<int>, std::multiset<int>>(a,b);
}
bool areSame(std::multiset<int> a, std::multiset<int> b){
    return a==b;
}
std::pair<std::multiset<int>, std::multiset<int>> player_vs_player(
    std::string type_a, std::multiset<int> a, std::string type_b, std::multiset<int> b
) {
    // TODO 2-4
    std::multiset<int> prev_a = a;
    std::multiset<int> prev_b = b;
    while(true){
        std::pair<std::multiset<int>, std::multiset<int>> result =
                player_battle(type_a,a,type_b,b);
        a = result.first;
        b = result.second;
        //std::cout<<"a: ";
        //print_multiset(a);
        //std::cout<<"b: ";
        //print_multiset(b);
        if(areSame(prev_a,a) && areSame(prev_b,b)) break;
        prev_a = a;
        prev_b = b;
    }
    return std::pair<std::multiset<int>, std::multiset<int>>(a,b);
}
int multiset_sum(std::multiset<int> a){
    int sum = 0;
    for(auto i:a){
        sum+=i;
    }
    return sum;
}
int tournament(std::vector<std::pair<std::string, std::multiset<int>>> players) {
    // TODO 2-5
    //std::multiset<int> remove_index;
    std::vector<int> players_id;
    std::vector<int> remaining_id;
    for(int i=0;i<players.size();i++){
        remaining_id.push_back(i);
    }
    std::vector<std::pair<std::string, std::multiset<int>>> remaining;

    while(remaining_id.size()!=1){
        std::vector<int> remaining_id_after_this_round;
        for(int i=0;i<remaining_id.size()-1;i+=2){
            int first_player_id = remaining_id.at(i);
            int second_player_id = remaining_id.at(i+1);
            std::pair<std::multiset<int>, std::multiset<int>> result =
                    player_vs_player(players.at(first_player_id).first, players.at(first_player_id).second,
                                     players.at(second_player_id).first,players.at(second_player_id).second);
            if(multiset_sum(result.first) >= multiset_sum(result.second)){
                remaining_id_after_this_round.push_back(first_player_id);
            }
            else{
                remaining_id_after_this_round.push_back(second_player_id);
            }
            if(remaining_id.size()%2==1){
                remaining_id_after_this_round.push_back(remaining_id.at(remaining_id.size()-1));
            }
        }
        remaining_id = remaining_id_after_this_round;
    }
    return remaining_id.at(0);
}

int steady_winner(std::vector<std::pair<std::string, std::multiset<int>>> players) {
    // TODO 2-6
    int size = players.size();
    int winner_count[size];
    for(int i=0;i<size;i++) winner_count[i]=0;
    for(int i=0;i<size;i++){
        winner_count[(i+tournament(players))%size]++;
        std::pair<std::string, std::multiset<int>> first = players.at(0);
        players.erase(players.begin());
        players.push_back(first);
        //std::cout<<players.size()<<std::endl;
//        for(int j=0;j<players.size();j++){
//            std::cout<<players.at(j).second.size()<<std::endl;
//        }
    }
    int max_value = INT32_MIN;
    int max_loc = 0;
    for(int i=0;i<size;i++){
        //std::cout<<winner_count[i]<<std::endl;
        if(max_value<winner_count[i]){
            max_value = winner_count[i];
            max_loc = i;
        }
    }
    return max_loc;
}

/* =======END OF TODOs======= */

/* =======START OF THE MAIN CODE======= */
/* Please do not modify the code below */

typedef std::pair<std::string, std::multiset<int>> player;

player scan_player() {
    std::multiset<int> numbers;
    std::string player_type; int size;
    std::cin >> player_type >> size;
    for(int i=0;i<size;i++) {
        int t; std::cin >> t; numbers.insert(t);
    }
    return make_pair(player_type, numbers);
}

void print_multiset(const std::multiset<int>& m) {
    for(int number : m) {
        std::cout << number << " ";
    }
    std::cout << std::endl;
}
/*
int main() {
    int n_q = 0;
    while (true) {
        int question_number;
        std::cin >> question_number;
        n_q++;
        if (question_number == 1) {
            int a, b;
            std::cin >> a >> b;

            std::tie(a, b) = number_fight(a, b);
            std::cout << a << " " << b << std::endl;
        } else if (question_number == 2) {
            int a, b;
            std::cin >> a >> b;
            std::tie(a, b) = number_vs_number(a, b);
            std::cout << a << " " << b << std::endl;
        } else if (question_number == 3 || question_number == 4) {
            auto a = scan_player();
            auto b = scan_player();
            std::multiset<int> a_, b_;
            //if(n_q==4226){
            //    print_multiset(a.second);
            //}
            n_q++;
            if (question_number == 3) {
                tie(a_, b_) = player_battle(
                        a.first, a.second, b.first, b.second
                );
            } else {
                tie(a_, b_) = player_vs_player(
                        a.first, a.second, b.first, b.second
                );
            }
            print_multiset(a_);
            print_multiset(b_);
            //std::cout<<multiset_sum(a_)<<" "<<multiset_sum(b_)<<std::endl;
        } else if (question_number == 5 || question_number == 6) {
            int num_players;
            std::cin >> num_players;
            std::vector<player> players;
            for (int i = 0; i < num_players; i++) {
                players.push_back(scan_player());
            }
            //std::cout<<"case : "<<++n_q<<" question : "<<question_number<<" ";
            if(n_q == 11047) print_multiset(players.at(0).second);
            int winner_id;
            if (question_number == 5) {
                winner_id = tournament(players);
            } else {
                winner_id = steady_winner(players);
            }
            std::cout << winner_id << std::endl;
        } else {
            return 0;
        }
    }
    return 0;
}*/

int main() {
    int question_number; std::cin >> question_number;
    if (question_number == 1) {
        int a, b; std::cin >> a >> b;
        std::tie(a, b) = number_fight(a, b);
        std::cout << a << " " << b << std::endl;
    } else if (question_number == 2) {
        int a, b; std::cin >> a >> b;
        std::tie(a, b) = number_vs_number(a, b);
        std::cout << a << " " << b << std::endl;
    } else if (question_number == 3 || question_number == 4) {
        auto a = scan_player();
        auto b = scan_player();
        std::multiset<int> a_, b_;
        if (question_number == 3) {
            tie(a_, b_) = player_battle(
                a.first, a.second, b.first, b.second
            );
        } else {
            tie(a_, b_) = player_vs_player(
                a.first, a.second, b.first, b.second
            );
        }
        print_multiset(a_);
        print_multiset(b_);
    } else if (question_number == 5 || question_number == 6) {
        int num_players; std::cin >> num_players;
        std::vector<player> players;
        for(int i=0;i<num_players;i++) {
            players.push_back(scan_player());
        }
        int winner_id;
        if (question_number == 5) {
            winner_id = tournament(players);
        } else {
            winner_id = steady_winner(players);
        }
        std::cout << winner_id << std::endl;
    }
    return 0;
}
/* =======END OF MAIN CODE======= */