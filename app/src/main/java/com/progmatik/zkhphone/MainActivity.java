package com.progmatik.zkhphone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.progmatik.zkhphone.classes.AdvertisingIdClient;
import com.progmatik.zkhphone.interfaces.RequestFinishListeners;
import com.progmatik.zkhphone.utils.myDialogs;
import com.progmatik.zkhphone.utils.myHttpsRequest;
import com.progmatik.zkhphone.utils.myLoader;
import com.progmatik.zkhphone.utils.myUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity implements RequestFinishListeners {

    String advInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Отключаем Actionbar вверху страницы т.к на первой странице о нам не нужен
        //по сути делаем Window.FullSize
        getSupportActionBar().hide();

        init();
    }

    //Инициализация
    public void init()  {

        //Отображаем спиннер
        ProgressBar spinner = (ProgressBar) findViewById(R.id.loading_indicator);
        spinner.setVisibility(View.VISIBLE);

        //Отображаем сообщение "Инициализация"
        final TextView label = (TextView) findViewById(R.id.loading_label);
        label.setText(R.string.label_initialization);
        label.setVisibility(View.VISIBLE);

        //Пытаемся получить advertising_id
        AdvertisingIdClient.getAdvertisingId(this, new AdvertisingIdClient.Listener() {

            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {
                // Callback when process is over
                advInfo = adInfo.getId();
                //Проверяем, что на файловой системе есть клиентский сертификат
                String filename = MainActivity.this.getFilesDir().getAbsolutePath() + "/client.p12";
                File file = new File(filename);

                //Если не существует
                if ( !file.exists() ) {
                    //Меняем текст на "Первоначальная настройка..."
                    label.setText(R.string.label_initial_setup);
                    //Отправляем запрос к серверу на регистрацию
                    try {
                        requestSignup();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //Uninstall рилоржения удаляет все его файлы?
                    //file.delete();
                }
            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {
                // Callback when process fails
                new myDialogs().showErrorDialog(MainActivity.this,
                        getResources().getString(R.string.dialog_error),
                        getResources().getString(R.string.error_initialization_malfunction),
                        getResources().getString(R.string.button_close),
                        null,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                MainActivity.this.finish();
                            }
                        },
                        null
                );

            }
        });
    }

    public void requestSignup() throws JSONException {
        //Отправляем запрос на регистрацию
        JSONObject json = new JSONObject();
        json.put("singupid", this.advInfo.toString().replace("-",""));
        String signature = myUtils.openssl_sign(myUtils.getProgmatikPrivateKey(MainActivity.this), json.toString());
        myLoader loader = new myLoader(MainActivity.this);
        loader.setRequestFinishListeners(MainActivity.this);
        loader.setSendMethod(myHttpsRequest.sendMethod.smPOST);
        loader.setSubURL("/signup");
        loader.setPostData( json );

        loader.execute();

    }

    //Перехватчик завершения https-Запроса
    @Override
    public void requestFinished(myHttpsRequest httpsRequest) {
        if ( httpsRequest.getState() == myHttpsRequest.requestState.rqsError ) {
            //if ( overlay != null && overlay.isShowing() ) { overlay.dismiss(); }
            new myDialogs().showErrorDialog(MainActivity.this,
                    getResources().getString(R.string.dialog_error),
                    getResources().getString(R.string.error_network),
                    getResources().getString(R.string.button_close),
                    getResources().getString(R.string.button_settings),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            MainActivity.this.finish();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                            startActivity(intent);
                        }
                    });
        } else if ( httpsRequest.getState() == myHttpsRequest.requestState.rqsComplete ) {
            /*
            myResponse response;
            response = myUtils.parseXML( httpsRequest.getXML() );
            if ( response == null || response.getResult() == null || response.getResult().getCode() != 1 ) {
                if ( overlay != null && overlay.isShowing() ) { overlay.dismiss(); }
                if ( response.getResult().getCode() != -0xf2 ) {
                    new myDialogs().showErrorDialog(ConfirmActivity.this,
                            getResources().getString(R.string.dialog_error),
                            ( response == null ?  getResources().getString(R.string.error_wrong_server_answer) : String.format(getResources().getString(R.string.error_server_answer),response.getResult().getDesc(),"0x"+Integer.toHexString(response.getResult().getCode()*-1)) ),
                            "",
                            getResources().getString(R.string.button_close),
                            null,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //dialog.cancel();
                                    goBack();
                                }
                            });

                } else {
                    myUtils.displayToastUnderView( ConfirmActivity.this, confirmCodeInput, response.getResult().getDesc());
                    //включаем железную кнопку "Назад"
                    hardwareBackButtonEnabled = true;
                    if ( !confirmCodeInput.hasFocus() ) {
                        confirmCodeInput.requestFocus();
                        showKeyboard( confirmCodeInput );
                    }
                }
            } else {
                //Скрываем слой перекрывающий все окно с надписью "пожалуйста подождите"
                if ( overlay != null && overlay.isShowing() ) { overlay.dismiss(); }
                String pmode = httpsRequest.getParams().getItem("pmode").getValue();
                //Получили ответ на запрос повторной отправки смс с кодом подтверждения
                if( pmode.equals("resend") ) {
                    //Запускаем таймер обратно отчета повторного запроса кода подтверждения
                    resendTimerStart();
                    myUtils.displayToastUnderView( ConfirmActivity.this, confirmCodeInput, getString( R.string.message_confirmcode_successfully_requested ));

                } else {
                    //Иначе получили ответ на попытку подтвердить телефон
                    //Скрываем слой перекрывающий все окно с надписью "пожалуйста подождите"
                    myUtils.displayToastUnderView( ConfirmActivity.this, confirmCodeInput, getString( R.string.message_phone_successfully_confirmed ));

                    // пишем данные
                    String line = "<?xml version=\"1.0\" encoding=\"utf-8\"?><response><result><code>1</code><desc>OK</desc><list type=\"setting\"><item phone=\""+pphone+"\" sim=\""+psim+"\" hash=\""+myUtils.getHash(pphone, psim)+"\"/></list></result></response>";

                    try {
                        File myFile = new File(ConfirmActivity.this.getFilesDir().getAbsolutePath(), SETTINGS_FILENAME);
                        myFile.createNewFile();
                        FileWriter fWriter = new FileWriter(myFile);
                        fWriter.write(line);
                        fWriter.flush();
                        fWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("canExit", Boolean.TRUE);
                    setResult(RESULT_OK, intent);
                    goBack();
                }
                //включаем железную кнопку "Назад"
                hardwareBackButtonEnabled = true;

            }
            */
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
