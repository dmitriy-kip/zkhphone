package com.progmatik.zkhphone.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.progmatik.zkhphone.R;
import com.progmatik.zkhphone.classes.myKeyValueItem;
import com.progmatik.zkhphone.classes.myKeyValuesList;
import com.progmatik.zkhphone.classes.myResponse;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class myUtils implements myConsts {

    public void hideIndicator(ProgressBar indicator) {
        indicator.setVisibility(View.GONE);
    }

    public void showIndicator(ProgressBar indicator) {
        indicator.setVisibility(View.VISIBLE);
    }

    public String md5(String string) {
        String result = "";
        if (!TextUtils.isEmpty(string)) {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
                byte[] bytes = md5.digest(string.getBytes());
                for (byte b : bytes) {
                    String temp = Integer.toHexString(b & 0xff);
                    result += (temp.length() == 1 ? "0" + temp : temp);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public Boolean writeTextFile(Context context, String filename, String content) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE)));
            // пишем данные
            bw.write(content);
            // закрываем поток
            bw.close();
            return false;
        } catch (FileNotFoundException e) {
            return false;
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public boolean fileExist(Context context, String filename){
        File file = context.getFileStreamPath(filename);
        return file.exists();
    }

    public String readTextFile(Context context, String filename) {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    context.openFileInput(filename)));
            String result = "";
            // читаем содержимое
            String str = "";
            while ((str = br.readLine()) != null) {
                result = result + str;
            }
            br.close();
            return result;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Boolean writeTextFileSD(Context context, String directory, String filename, String content) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            //"SD-карта не доступна: " + Environment.getExternalStorageState());
            return false;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + directory);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, filename);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(content);
            // закрываем поток
            bw.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public myResponse parseXML( String xmlText ) {
        myResponse response = null;
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        String text = "";
        String lastTag = "";
        String parentTag = "";
        myKeyValuesList Item = null;
        myKeyValueItem subItem = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput( new StringReader(xmlText) );

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagname = parser.getName();
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("Response")) {
                            response = new myResponse(); //<response></response>
                            parentTag = tagname;
                        } else if (tagname.equalsIgnoreCase("Result") && response != null ) {
                            response.createResult();//<response><result></result></response>
                            parentTag = tagname;
                        } else if (tagname.equalsIgnoreCase("List") && response != null && response.getResult() != null ) {
                            response.getResult().createList();//<response><result><list type="..."></list></result></response>
                            parentTag = tagname;
                            Integer attrsCount = parser.getAttributeCount();
                            if ( attrsCount > 0 ) {
                                for ( Integer i = 0; i < attrsCount; i++ ) {
                                    String attrName = parser.getAttributeName( i );
                                    String attrValue = parser.getAttributeValue(null, attrName );
                                    myKeyValueItem attribute = new myKeyValueItem( attrName, attrValue );
                                    response.getResult().getList().getAttributes().addItem( attribute );
                                }                            }
                        } else if (tagname.equalsIgnoreCase("item") && response != null && response.getResult() != null && response.getResult().getList() != null ) {
                            Item = new myKeyValuesList();
                            Integer attrsCount = parser.getAttributeCount();
                            if ( attrsCount > 0 ) {
                                for ( Integer i = 0; i < attrsCount; i++ ) {
                                    String attrName = parser.getAttributeName( i );
                                    String attrValue = parser.getAttributeValue(null, attrName );
                                    subItem = new myKeyValueItem(attrName, attrValue);
                                    Item.addItem( subItem );
                                }
                            }
                        }
                        lastTag = tagname;

                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText().trim();
                        if ( !text.equals("") ) {
                            if ( lastTag.equalsIgnoreCase("Code") && parentTag.equalsIgnoreCase("Result") ) {
                                response.getResult().setCode( Integer.parseInt(text) );
                            } else if ( lastTag.equalsIgnoreCase("Desc") && parentTag.equalsIgnoreCase("Result") ) {
                                response.getResult().setDesc( text );
                            } else if ( lastTag.equalsIgnoreCase("Type") && parentTag.equalsIgnoreCase("List") ) {
                                response.getResult().getList().setType( text );
                            } else if ( parentTag.equalsIgnoreCase("List") && Item != null ) {
                                subItem = new myKeyValueItem(lastTag, text);
                                Item.addItem( subItem );
                            }
                        }
                        //text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("Item"))
                        {
                            response.getResult().getList().addItem( Item );
                            Item = null;
                        }
                        break;

                    default:  break;
                }


                eventType = parser.next();

            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String readTextFileSD(String directory, String filename) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            //"SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + directory);
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, filename);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String result = "";
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                result = result + str;
            }
            br.close();
            return result;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public void showKeyboard(AutoCompleteTextView textView) {
        textView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, 0, 0, 0));
        textView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 0, 0, 0));
    }

    public void showKeyboard(EditText editText) {
        editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, 0, 0, 0));
        editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, 0, 0, 0));
    }

    public void disableAutoCompleteTextView(AutoCompleteTextView textView) {
        textView.setFocusableInTouchMode(false);
        textView.setEnabled(false);
        textView.setCursorVisible(false);
    }
    public void enableAutoCompleteTextView(AutoCompleteTextView textView) {
        textView.setFocusableInTouchMode(true);
        textView.setCursorVisible(true);
        textView.setEnabled(true);
    }

    public void disableEditText(EditText editText) {
        editText.setFocusableInTouchMode(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    public void enableEditText(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }

    public void disableButton(Button btn) {
        btn.setFocusable(false);
        btn.setEnabled(false);
    }

    public void enableButton(Button btn) {
        btn.setFocusable(true);
        btn.setEnabled(true);
        //btn.setCursorVisible(false);
        //textView.setKeyListener(null);
        //textView.setBackgroundColor(Color.TRANSPARENT);
    }

    public Boolean isInteger(String value) {
        try {
            Integer val = Integer.valueOf(value);
            if (val != null)
                return true;
            else
                return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void displayToastUnderView(Activity activity, View view, String text) {
        int location[]=new int[2];
        view.getLocationOnScreen(location);
        Toast toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        toast.setGravity( Gravity.TOP|Gravity.CENTER, 0, location[1]+30);
        toast.show();
    }

    public static PrivateKey getPrivateKeyFromP12File(String FileNameP12, String P12Password) {
        String keyAlias = null;
        KeyStore keystore = null;
        PrivateKey key = null;
        Enumeration<String> aliases = null;

        File file = new File(FileNameP12);
        if ( file.exists() ) {
            FileInputStream is = null;
            try {
                is = new FileInputStream( FileNameP12);
                keystore = KeyStore.getInstance("PKCS12");
            } catch (FileNotFoundException | KeyStoreException e) {
                //e.printStackTrace();
                return null;
            }
            try {
                assert keystore != null;
                keystore.load(is, P12Password.toCharArray());
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                //e.printStackTrace();
                return null;
            }
            try {
                aliases = keystore.aliases();
            } catch (KeyStoreException e) {
                //e.printStackTrace();
                return null;
            }
            assert aliases != null;
            while (aliases.hasMoreElements()) {
                keyAlias = (String) aliases.nextElement();
            }
            try {
                key = (PrivateKey) keystore.getKey(keyAlias, P12Password.toCharArray());
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e ) {
                //e.printStackTrace();
                return null;
            }
        }
        return key;
    }

    public static PrivateKey getProgmatikPrivateKey(Context myContext) {
        String keyAlias = null;
        KeyStore keystore = null;
        PrivateKey key = null;
        Enumeration<String> aliases = null;

            InputStream is;
            try {
                is = myContext.getResources().openRawResource(R.raw.progmatik);
                keystore = KeyStore.getInstance("PKCS12");
            } catch ( KeyStoreException e) {
                //e.printStackTrace();
                return null;
            }
            try {
                assert keystore != null;
                keystore.load(is, PKCS_PASSWD.toCharArray());
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                //e.printStackTrace();
                return null;
            }
            try {
                aliases = keystore.aliases();
            } catch (KeyStoreException e) {
                //e.printStackTrace();
                return null;
            }
            assert aliases != null;
            while (aliases.hasMoreElements()) {
                keyAlias = (String) aliases.nextElement();
            }
            try {
                key = (PrivateKey) keystore.getKey(keyAlias, PKCS_PASSWD.toCharArray());
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e ) {
                //e.printStackTrace();
                return null;
            }
        return key;
    }

    public static String openssl_sign(PrivateKey privateKey, String plainText) {
    byte[] signature = null;
    try {
      Signature sig = Signature.getInstance("SHA256withRSA");
      sig.initSign(privateKey);
      sig.update(plainText.getBytes(StandardCharsets.UTF_8));
      signature = sig.sign();
    } catch(Exception ex) {
      ex.printStackTrace();
    }
    return Base64.encodeToString(signature, Base64.DEFAULT);
  }

}
