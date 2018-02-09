package romanov.translate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LanguageListActivityTo extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_list);

        final String[] languageList = getResources().getStringArray(R.array.languageList);

        final ListView listView = (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item, R.id.label, languageList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Log.i(LOG_TAG, "itemClick: position = " + position + ", id = " + languageList[position]);
                onClickBack2(position);

            }
        });

    }

    public void onClickBack2(int s) {
        Intent intent = new Intent(this, TranslateActivity.class);
        intent.putExtra("OutPutLanguage", s);
        intent.putExtra("OutPutLanguageKey", s);
        startActivity(intent);
    }



}
