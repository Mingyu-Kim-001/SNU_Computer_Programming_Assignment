

import java.util.*;

public class User {
    private String username;
    public User(String username) { this.username = username; }
    @Override
    public String toString() {
        return username;
    }
    @Override
    public boolean equals(Object user2){
        if(!(user2 instanceof User)) return false;
        return user2.toString().equals(this.toString());
    }
}
