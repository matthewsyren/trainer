package a15008377.opsc7312_assign2_15008377;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuizFetcherActivity extends UserBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz_fetcher);

            //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Home
            super.onCreateDrawer();
            super.setSelectedNavItem(R.id.nav_home);

            //Fetches Quizzes
            new Quiz().requestQuizzes(null, this, new DataReceiver(new Handler()));
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method displays the available Quizzes to the user
    public void displayQuizzes(final ArrayList<Quiz> lstQuizzes){
        try{
            QuizListViewAdapter quizListViewAdapter = new QuizListViewAdapter(this, lstQuizzes);
            ListView listView = (ListView) findViewById(R.id.list_view_quizzes);
            listView.setAdapter(quizListViewAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(QuizFetcherActivity.this, QuizActivity.class);
                    intent.putExtra("quiz", lstQuizzes.get(position));
                    startActivity(intent);
                }
            });
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult ( int resultCode, Bundle resultData){
            //Processes the result when the Stock has been written to the Firebase Database
            if (resultCode == FirebaseService.ACTION_FETCH_QUIZ_RESULT_CODE) {
                ArrayList<Quiz> lstQuizzes = (ArrayList<Quiz>) resultData.getSerializable(FirebaseService.ACTION_FETCH_QUIZ);
                displayQuizzes(lstQuizzes);
            }
        }
    }
}