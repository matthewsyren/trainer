/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class displays information on user statistics in a ListView
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminSpecificStatisticsActivity extends AppCompatActivity {
    //Declarations
    private ArrayList<Statistic> lstStatistics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_specific_statistics);

            //Fetches the user's Statistics
            fetchUserStatistics();

            //Displays Back button in ActionBar
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.setTitle("User Statistics");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            //Displays the ProgressBar
            toggleProgressBarVisibility(View.VISIBLE);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method toggles the ProgressBar's visibility
    public void toggleProgressBarVisibility(int visible){
        try{
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(visible);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Takes the user back to the AdminStatisticsActivity when the back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            int id = item.getItemId();

            //Takes the user back to the AdminStatisticsActivity if the button that was pressed was the back button
            if (id == android.R.id.home) {
                Intent intent = new Intent(AdminSpecificStatisticsActivity.this, AdminStatisticsActivity.class);
                startActivity(intent);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
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

            //Sets the Adapter for the ListView
            final ListView listView = (ListView) findViewById(R.id.list_view_user_results);
            listView.setAdapter(pastQuizListViewAdapter);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Processes the data returned from FirebaseSercice
    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult ( int resultCode, Bundle resultData){
            //Processes the result when the Statistic has been fetched from the Firebase Database
            if (resultCode == FirebaseService.ACTION_FETCH_STATISTIC_RESULT_CODE) {
                lstStatistics = (ArrayList<Statistic>) resultData.getSerializable(FirebaseService.ACTION_FETCH_STATISTIC);

                if(lstStatistics.size() == 0){
                    Toast.makeText(getApplicationContext(), "This user hasn't completed any quizzes yet", Toast.LENGTH_LONG).show();

                    //Hides the ProgressBar
                    toggleProgressBarVisibility(View.INVISIBLE);
                }
                else{
                    //Requests Quizzes
                    new Quiz().requestQuizzes(null, getApplicationContext(), new DataReceiver(new Handler()));
                }
            }
            else if(resultCode == FirebaseService.ACTION_FETCH_QUIZ_RESULT_CODE){
                //Displays the user's Quizzes and their results to the user
                ArrayList<Quiz> lstQuizzes = (ArrayList<Quiz>) resultData.getSerializable(FirebaseService.ACTION_FETCH_QUIZ);
                displayPastQuizzes(lstQuizzes, lstStatistics);

                //Hides the ProgressBar
                toggleProgressBarVisibility(View.INVISIBLE);
            }
        }
    }
}
