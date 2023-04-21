package com.example.database;
import com.example.database.QueryHelper.*;
import com.example.database.DBConst.*;
import static com.example.blockchain.PopUp.*;

import androidx.appcompat.app.AppCompatActivity;

public final class DBErrorManager {
    public static final int PLAYER_NOT_EXIST = 1;
    public static final int KEY_NOT_EXIST = 2;
    public static final int INSERTION_ERROR = 3;
    public static final int UPDATE_ERROR = 4;
    public static final int DELETE_ERROR = 5;
    public static final int SELECT_ERROR = 6;
    public static final int TABLE_NOT_EXIST = 7;
    public static String dbErrorChecker(int errorType, AppCompatActivity activity, String optionalMessage){
        String errorToShow = null;
        switch (errorType){
            case PLAYER_NOT_EXIST:
                errorToShow = "Player does not exist";
                break;
            case KEY_NOT_EXIST:
                errorToShow = "Key does not exist";
                break;
            case INSERTION_ERROR:
                errorToShow = "Insertion error";
                break;
            case UPDATE_ERROR:
                errorToShow = "Update error";
                break;
            case DELETE_ERROR:
                errorToShow =  "Delete error";
                break;
            case SELECT_ERROR:
                errorToShow = "Select error";
                break;
            case TABLE_NOT_EXIST:
                errorToShow = "Table does not exist";
                break;
            default:
                errorToShow = "Unknown error";
                break;
        }
        if (optionalMessage != null){
            errorToShow += "\n" + optionalMessage;
        }
        showPopUp("Error", errorToShow, activity);
        return null;
    }


}
