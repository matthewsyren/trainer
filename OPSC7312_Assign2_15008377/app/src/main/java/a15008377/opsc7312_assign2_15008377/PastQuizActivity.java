package a15008377.opsc7312_assign2_15008377;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PastQuizActivity extends UserBaseActivity {
    ArrayList<Statistic> lstStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_past_quiz);

            //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Quizzes
            super.onCreateDrawer();
            super.setSelectedNavItem(R.id.nav_quizzes);

            //Fetches the user's Statistics
            new Statistic().requestStatistics(new User(this).getUserKey(), this, new DataReceiver(new Handler()));

            //Displays the ProgressBar
            toggleProgressBarVisibility(View.VISIBLE);

            //Sets the TextChangedListener for the text_search_quizzes, which will perform a search when the user types
            final EditText txtSearchQuizzes = (EditText) findViewById(R.id.text_search_quizzes);
            txtSearchQuizzes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQuizzes(txtSearchQuizzes);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //Prevents the keyboard from appearing automatically. Learnt from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method fetches all Quizzes that match the search
    public void searchQuizzes(EditText txtSearchQuizzes){
        try{
            //Fetches the search term and requests Quizzes that match the search term
            String searchTerm = txtSearchQuizzes.getText().toString();

            //Displays ProgressBar
            toggleProgressBarVisibility(View.VISIBLE);

            //Fetches the Quizzes from the Firebase Database that match the search term
            new Quiz().requestQuizzes(searchTerm, this, new DataReceiver(new Handler()));
        }
        catch(Exception exc){
            Toast.makeText(getBaseContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
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

                if(lstStatistics.size() == 0){
                    Toast.makeText(getApplicationContext(), "You have not completed any quizzes yet", Toast.LENGTH_LONG).show();

                    //Hides the ProgressBar
                    toggleProgressBarVisibility(View.INVISIBLE);
                }
                else{
                    new Quiz().requestQuizzes(null, getApplicationContext(), new DataReceiver(new Handler()));
                }
            }
            else if(resultCode == FirebaseService.ACTION_FETCH_QUIZ_RESULT_CODE){
                ArrayList<Quiz> lstQuizzes = (ArrayList<Quiz>) resultData.getSerializable(FirebaseService.ACTION_FETCH_QUIZ);

                displayPastQuizzes(lstQuizzes, lstStatistics);
                
                //Hides the ProgressBar
                toggleProgressBarVisibility(View.INVISIBLE);
            }
        }
    }
}