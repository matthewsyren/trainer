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

import java.util.ArrayList;

public class QuizManagerActivity extends AdminBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz_manager);

            //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Home
            super.onCreateDrawer();
            super.setSelectedNavItem(R.id.nav_home);

            new Quiz().requestQuizzes(null, this, new DataReceiver(new Handler()));
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

    //Method takes the user to the QuizSetterActivity
    public void addQuizOnClick(View view){
        try{
            Intent intent = new Intent(QuizManagerActivity.this, QuizSetterActivity.class);
            startActivity(intent);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method displays the available Quizzes to the user
    public void displayQuizzes(final ArrayList<Quiz> lstQuizzes){
        try{
            QuizManagementListViewAdapter quizListViewAdapter = new QuizManagementListViewAdapter(this, lstQuizzes);
            ListView listView = (ListView) findViewById(R.id.list_view_quizzes);
            listView.setAdapter(quizListViewAdapter);

            //Sets OnItemClickListener to open the Quiz that the user clicked on
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(QuizManagerActivity.this, QuizActivity.class);
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
                toggleProgressBarVisibility(View.INVISIBLE);
            }
        }
    }
}
