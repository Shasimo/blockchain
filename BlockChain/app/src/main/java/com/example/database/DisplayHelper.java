package com.example.database;

import java.util.ArrayList;
import java.util.List;
import static com.example.database.QueryHelper.*;

import com.example.blockchain.User;

// This is just an idea to maybe minimize the amount of new strings in some files maybe directly put in User.java ?
public final class DisplayHelper {
    public static List<String> infoDisplay(User user){

        List<String> info = new ArrayList<>();
        info.add("Bienvenue " + user.getPseudo());
        info.add("Elo: " + user.getEloValue());
        info.add("Public Key: " + keyConcatenationQuery(user.getPublicKey()));
        info.add("Score d'arbitrage: TODELETE");
        return info;
    }
}
