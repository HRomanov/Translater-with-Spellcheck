package romanov.translate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.GsonConverterFactory;

import retrofit.Response;
import retrofit.Retrofit;
import retrofit.Callback;


public class TranslateActivity extends AppCompatActivity {

    public final String URL = "https://translate.yandex.net";
    public final String KEY = "trnsl.1.1.20161223T172242Z.4b70bdbf39cc4ef5.6e1b74b2cb1c73885f3924b9d3b7389d5adf3723";
    private TextView tvTranslate;
    private EditText etInputText;
    private Gson gson;
    private Retrofit retrofit;
    private Link service;
    private Map<String, String> mapJson, mapGetResponse;
    private Button btnInputLanguage;
    private Button translateBtn;
    private Button checkBtn;
    private Button clearBtn;
    private Button btnOutPutLanguage;
    public int tmpKeyIn;
    public int tmpKeyOut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        String[] LanguageList = getResources().getStringArray(R.array.languageList);
        String[] LanguageListFirst = getResources().getStringArray(R.array.languageListFirst);
        btnInputLanguage = (Button) findViewById(R.id.btnInputLanguage);
        btnOutPutLanguage = (Button) findViewById(R.id.btnOutputLanguage);
        translateBtn = (Button) findViewById(R.id.translateBtn);
        checkBtn = (Button) findViewById(R.id.spell_check);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        tvTranslate = (TextView) findViewById(R.id.translate_text);
        etInputText = (EditText) findViewById(R.id.input_text);

        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URL)
                .build();

        service = retrofit.create(Link.class);

        Intent intent = getIntent();
        int InputLanguage = intent.getIntExtra("InputLanguage", tmpKeyIn);
        btnInputLanguage.setText(LanguageListFirst[InputLanguage]);

        int OutPutLanguage = intent.getIntExtra("OutPutLanguage", tmpKeyOut);
        btnOutPutLanguage.setText(LanguageList[OutPutLanguage]);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONTask().execute("https://api.textgears.com/check.php?text=" + etInputText.getText().toString() + "!&key=DEMO_KEY").toString();
            }
        });


    }


    public void onClickInputLanguage(View view) {
        Intent intent = new Intent(this, LanguageListActivityFrom.class);
        //передать текущую хуйню
        startActivity(intent);

    }


    public void onClickOutputLanguage(View view) {
        Intent intent2 = new Intent(this, LanguageListActivityTo.class);
        startActivity(intent2);
    }

    public void onClickTranslate(View view) {
        String[] LanguageListKey = getResources().getStringArray(R.array.languageListKey);
        String[] LanguageListKeyFirst = getResources().getStringArray(R.array.languageListKeyFirst);
        Intent intent = getIntent();
        int InputLanguageKey = intent.getIntExtra("InputLanguageKey", tmpKeyIn);
        int OutPutLanguageKey = intent.getIntExtra("OutPutLanguageKey", tmpKeyOut);
        Log.i("ТУТ КОРОЧЕ -->", LanguageListKeyFirst[InputLanguageKey] + " - " + LanguageListKey[OutPutLanguageKey]);

        mapJson = new HashMap<>();
        mapJson.put("key", KEY);
        mapJson.put("lang", (LanguageListKeyFirst[InputLanguageKey] + "-" + LanguageListKey[OutPutLanguageKey]));
        mapJson.put("text", etInputText.getText().toString());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(translateBtn.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        service.translate(mapJson).enqueue(new Callback<TranslateData>() {
            @Override
            public void onResponse(Response<TranslateData> response, Retrofit retrofit) {
                TranslateData data = response.body();
                ArrayList<String> text = data.getText();
                String translateText = text.toString();
                translateText = translateText.substring(1, translateText.length() - 1);
                tvTranslate.setText(translateText);
                //   Log.i(String.valueOf(data), "   Это мое сообщение для записи в журнале" + data.getLang());

            }

            @Override
            public void onFailure(Throwable t) {

            }


        });
    }

    String badwords_str;
    String better;

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String returnString = "";
            String returnBetterString = "";

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                java.net.URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("errors");

                int arrSize = parentArray.length();


                for (int i = 0; i < arrSize; i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    String bad = finalObject.getString("bad");
                    if(bad != null) returnString += bad + " / ";

                    JSONArray betterArray = finalObject.getJSONArray("better");
                    int betterArrSize = betterArray.length();
                   // String[] betterArr = new String[betterArrSize];
                    for (int j = 0; j < betterArrSize; j++) {
                        String betterWords = betterArray.getString(j);
                   //     betterArr[j]= betterWords;
                        if(betterWords!=null) returnBetterString += betterWords + "\n";
                    }
                    if(returnBetterString != null) returnBetterString += " / "+" ";
                }

                returnString = returnString + "-" + returnBetterString;
                return returnString;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            onClickCheck(result);
            //onClickClear(part2);
        }

    }

    int better_check=0;

    public void onClickCheck(String result) {

        better_check=1;
        //введенный пользователем текст
        String checkText = etInputText.getText().toString();
        String[] mass_words = checkText.split(" ");

        if(result!="-") {
            String[] parts = result.split("-");
            badwords_str = parts[0]; // 004
            better = parts[1]; // 034556
        }
//        String b = "";
//        resultStr(result,b);

        int break_lable = 0;

        String htmlTaggedString = "";

        String[] badwords = badwords_str.split(" ");


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(checkBtn.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        //Это работает
//        String htmlTaggedString = "<u><font color=\"red\" >" + badwords_str + "</font></u>" + " ";
//        Spanned textSpan = android.text.Html.fromHtml(htmlTaggedString);
//        etInputText.setText(textSpan);


//        //!!!! вывести по очереди слова обычные и подчеркнутые

        for (int i = 0; i < mass_words.length; i++) {
            break_lable = 0;
            for (int j = 0; j < badwords.length;  j++) {
                if (mass_words[i].equals( badwords[j])) {
                    htmlTaggedString += "<u><font color=\"red\" >" + badwords[j] + "</font></u>" + " ";
                    break_lable = 1;
//                     Spanned textSpan = android.text.Html.fromHtml(htmlTaggedString);
//                     etInputText.setText(textSpan);
                }
            }
           if(break_lable == 1) continue;
            htmlTaggedString += mass_words[i] + " ";
        }
         Spanned textSpan = android.text.Html.fromHtml(htmlTaggedString);
         etInputText.setText(textSpan);



       // return (result);

    }

//    public String resultStr(String result, String bad){
//        String s = result;
//        bad = s;
//        return bad;
//    }

    public void onClickBetter(View view) {
        Intent intent3 = new Intent(this, BetterWordsActivity.class);
       // String result = "";
//        onClickCheck(result);

//        String[] parts = result.split("-");
//        String badwords_str = parts[0]; // 004
//        String better = parts[1]; // 034556
        if(better_check==0){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Сначала проверте текст!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
        intent3.putExtra("bad", badwords_str);
        intent3.putExtra("better", better);
        startActivity(intent3);}
    }
}