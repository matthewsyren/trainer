package a15008377.opsc7312_assign2_15008377;

import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminSpecificStatisticsActivity extends AppCompatActivity {
    //Declarations
    ArrayList<Statistic> lstStatistics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_specific_statistics);

        //Fetches the user's Statistics
        fetchUserStatistics();
    }

    //Method fetches the user's Statistics
    public void fetchUserStatistics(){
        try{
            Bundle bundle = getIntent().getExtras();
            String userKey = bundle.getString("userKey");
            new Statistic().requestStatistics(userKey, this, new DataReceiver(new Handler()));
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method displays the Quizzes the user has already taken
    public void displayPastQuizzes(ArrayList<Quiz> lstQuizzes, ArrayList<Statistic> lstStatistics){
        try{
            PastQuizListViewAdapter pastQuizListViewAdapter = new PastQuizListViewAdapter(this, new ArrayList<Quiz>(), lstStatistics);

            //Adds Quizzes that have been taken already to the Adapter
            for(int i = 0; i < lstQuizzes.size(); i++){
                for(Statistic statistic: lstStatistics){
                    if(lstQuizzes.get(i).getKey().equals(statistic.getQuizKey())){
                        pastQuizListViewAdapter.add(lstQuizzes.get(i));
                        break;
                    }
                }
            }

            //Sets the Adapter for the ListVeiw
            final ListView listView = (ListView) findViewById(R.id.list_view_user_results);
            listView.setAdapter(pastQuizListViewAdapter);
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
            //Processes the result when the Statistic has been fetched from the Firebase Database
            if (resultCode == FirebaseService.ACTION_FETCH_STATISTIC_RESULT_CODE) {
                lstStatistics = (ArrayList<Statistic>) resultData.getSerializable(FirebaseService.ACTION_FETCH_STATISTIC);
                new Quiz().requestQuizzes(null, getApplicationContext(), new DataReceiver(new Handler()));
            }
            else if(resultCode == FirebaseService.ACTION_FETCH_QUIZ_RESULT_CODE){
                ArrayList<Quiz> lstQuizzes = (ArrayList<Quiz>) resultData.getSerializable(FirebaseService.ACTION_FETCH_QUIZ);

                displayPastQuizzes(lstQuizzes, lstStatistics);
            }
        }
    }
}
