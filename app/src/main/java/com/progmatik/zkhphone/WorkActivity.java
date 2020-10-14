package com.progmatik.zkhphone;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class WorkActivity extends AppCompatActivity {

    TextView debtLabel, debtSummLabel, mcLabel;
    Spinner addressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        //Отключаем Actionbar вверху страницы т.к на первой странице о нам не нужен
        //по сути делаем Window.FullSize
        getSupportActionBar().hide();

        debtLabel = (TextView) findViewById(R.id.label_debt); //Фраза "Задолженность\nна 31.10.17"
        debtSummLabel = (TextView) findViewById(R.id.label_debt_summ); //Сумма задолженности
        mcLabel = (TextView) findViewById(R.id.label_mc); //Название УК

        addressSpinner = (Spinner) findViewById(R.id.spinner_address); //Выпадающий список с адресами
        addressSpinner.getBackground().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP); //красим стрелочку выпадающего списка в синий цвет

        mcLabel.setText(Html.fromHtml("<u>УКЭЖ &quot;Сибирская Инициатива&quot;</u>")); //Вот так указываем текст с html-тегами
        // адаптер
        String[] data = {"ул. Сибиряков-Гвардейцев, 236а кв. 128", "управление адресами..."};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        addressSpinner.setAdapter(adapter);
        // заголовок
        //addressSpinner.setPrompt("Сибиряков-Гвардейцев, 236а кв. 128");
        // выделяем элемент
        addressSpinner.setSelection(0);
        //
    }
}
