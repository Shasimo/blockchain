package com.example.blockchain;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public final class PopUp extends DialogFragment {
    private AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
    public static void showPopUp(String title, String message, AppCompatActivity activity){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}
