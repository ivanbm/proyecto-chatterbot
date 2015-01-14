package com.izv.android.proyectochatterbot;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends Activity {

    TextToSpeech mitts;
    private EditText etconver;
    private final int CHECK_VOICE_DATA_PASS = 1;
    private Button btHablar;
    private ChatterBotSession bot1session;
    private String respuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btHablar = (Button)findViewById(R.id.btHablar);

        // ---------------  METODO LEER -----------------
        etconver = (EditText)findViewById(R.id.etConver);

        mitts=new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            Locale loc = new Locale ("spa", "ESP");
                            //mitts.setLanguage(Locale.UK);
                            mitts.setLanguage(loc);

                        }else{
                            System.out.println("ERROOOOOOOOOOOOOR");
                        }
                    }
                });

        //------------- PROBANDO-----------------

        try{
            ChatterBotFactory factory = new ChatterBotFactory();
            ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT);
            bot1session = bot1.createSession();
        }catch (Exception e){
            System.out.println("ERROR DE CLEVERBOT");
        }




        //----------------------------------------------------------------


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CHECK_VOICE_DATA_PASS: {
                if (resultCode == RESULT_OK && null != data) {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    final String s = result.get(0);

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                respuesta = bot1session.think(s);
                                System.out.println("RESPUESTA "+ respuesta);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            hablaMovil(respuesta);
                            btHablar.setEnabled(true);
                        }
                    };

                    thread.start();
                    escribir(s, respuesta);

//-------------------------------------------------------------------------
                    //new hacerConexion().execute();
                   /* String respuesta = "";
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //etconver.setText(result.get(0));

                    try{
                        String s = result.get(0);
                        etconver.setText(s);
                        //respuesta = bot1session.think(s);
                        //respuesta = new hacerConexion().execute();
                        etconver.setText(etconver.getText()+"\n"+s+"\n"+respuesta);
                        System.out.println("RESPUESTAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+respuesta);


                    }catch(Exception e){
                        System.out.println("EXCEPTIOOOOOOOOOOOOOOON "+e);
                    }

                    hablaMovil(respuesta);*/
                }
                break;
            }

        }
    }



    public void hablaMovil(String resp){

        //String toSpeak = etconver.getText().toString();
        mitts.speak(resp, TextToSpeech.QUEUE_FLUSH, null);

    }



    public void hablaUsuario(View v) {

        btHablar.setEnabled(false);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.pulsa));
        try {
            startActivityForResult(intent, CHECK_VOICE_DATA_PASS);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "fracaso absoluto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void escribir(String s, String resp){
        if(respuesta == null){
            respuesta = "";
        }
        etconver.setText(etconver.getText()+"\n"+ respuesta+"\n"+s);
    }

}
