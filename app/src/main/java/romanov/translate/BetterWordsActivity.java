package romanov.translate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BetterWordsActivity extends AppCompatActivity {

    private TextView tvBad;
    private TextView tvBetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_better_words);

        tvBad = (TextView) findViewById(R.id.tvbad);
        tvBetter = (TextView) findViewById(R.id.tvbetter);

        int coutn = 0;

        Intent intent = getIntent();
        String badStr = intent.getStringExtra("bad");
        String betterStr = intent.getStringExtra("better");

        String[] badwords = badStr.split(" / ");
        String[] betterwords = betterStr.split(" / ");


        coutn++;

        String finalBadStr = "";
        for (int i = 0; i <badwords.length ; i++) {
            finalBadStr += (i+1) +") " + badwords[i] + "\n \n";
        }

        String finalBetterStr = "";
        for (int i = 0; i <betterwords.length-1 ; i++) {
            finalBetterStr += (i+1) +") " + betterwords[i] + "\n";
        }
        tvBad.setText(finalBadStr);

        tvBetter.setText(finalBetterStr);

    }
}
