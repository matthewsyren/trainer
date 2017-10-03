package a15008377.opsc7312_assign2_15008377;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PastQuizActivity extends UserHomeActivity {
    ArrayList<Statistic> lstStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_past_quiz);

            //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Quizzes
            super.onCreateDrawer();
            super.setSelectedNavItem(R.id.nav_quizzes);
            fetchPastQuizzes();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method fetches the Quizzes that the user has already taken
    public void fetchPastQuizzes(){
        try{
            new Statistic().requestStatistics(new User(this).getUserKey(), this, new DataReceiver(new Handler()));
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
            final ListView listView = (ListView) findViewById(R.id.list_view_past_quizzes);
            listView.setAdapter(pastQuizListViewAdapter);

            //Makes the ListView clickable to allow the user to retake a Quiz
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(PastQuizActivity.this, QuizActivity.class);
                    intent.putExtra("quiz", (Quiz) listView.getAdapter().getItem(position));
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