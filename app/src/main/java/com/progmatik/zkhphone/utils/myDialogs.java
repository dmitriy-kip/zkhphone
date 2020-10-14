package com.progmatik.zkhphone.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class myDialogs {

    /*
    *
    * Вывод диалогового окна с сообещением об ошибке
    *
    */
    public void showErrorDialog(Context context, String wndCaption,  String wndText, String btnOkCaption, String btnCancelCaption, OnClickListener btnOkOnClick, OnClickListener btnCancelOnClick ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle(wndCaption);
        builder.setMessage(wndText);
                //.setIcon(R.drawable.ic_android_cat)
        builder.setCancelable(false);
        builder.setPositiveButton(btnOkCaption,btnOkOnClick);
        if ( btnCancelOnClick != null ) {
            builder.setNegativeButton(btnCancelCaption, btnCancelOnClick);
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

}
