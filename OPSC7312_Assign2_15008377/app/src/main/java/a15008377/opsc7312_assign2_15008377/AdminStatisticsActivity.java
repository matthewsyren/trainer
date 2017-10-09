/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class displays user's average results to the admin. The admin can click on a specific user for more information
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Enumeration;

public class AdminStatisticsActivity extends AdminBaseActivity {
    //Declarations
    ArrayList<User> lstUsers;
    ArrayList<Statistic> lstStatistics;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_statistics);
            context = this;

            //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Statistics
            super.onCreateDrawer();
            super.setSelectedNavItem(R.id.nav_statistics);

            //Fetches the necessary data for this Activity
            new User().requestUsers(null, getApplicationContext(), new DataReceiver(new Handler()));
            toggleProgressBarVisibility(View.VISIBLE);

            //Sets the TextChangedListener for the text_search_users, which will perform a search when the user types
            final EditText txtSearchUsers = (EditText) findViewById(R.id.text_search_users);
            txtSearchUsers.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchUsers(txtSearchUsers);
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

    //Method fetches all Users that match the search
    public void searchUsers(EditText txtSearchUsers){
        try{
            //Fetches the search term and requests Users that match the search term
            String searchTerm = txtSearchUsers.getText().toString();

            //Displays ProgressBar
            toggleProgressBarVisibility(View.VISIBLE);

            //Fetches the Users from the Firebase Database that match the search term
            new User().requestUsers(searchTerm, this, new DataReceiver(new Handler()));
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

    //Processes the data returned from FirebaseService
    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult ( int resultCode, Bundle resultData){
            //Performs the appropriate action once the data has been fetched from Firebase
            if(resultCode == FirebaseService.ACTION_FETCH_USER_RESULT_CODE){
                //Saves Users and requests Statistics
                lstUsers = (ArrayList<User>) resultData.getSerializable(FirebaseService.ACTION_FETCH_USER);
                new Statistic().requestStatistics(null, getApplicationContext(), new DataReceiver(new Handler()));
            }
            else if (resultCode == FirebaseService.ACTION_FETCH_STATISTIC_RESULT_CODE) {
                lstStatistics = (ArrayList<Statistic>) resultData.getSerializable(FirebaseService.ACTION_FETCH_STATISTIC);

                if(lstStatistics.size() == 0){
                    Toast.makeText(getApplicationContext(), "There are no results for any quizzes in the database", Toast.LENGTH_LONG).show();
                }
                else{
                    //Displays the users and their average results in the ListView
                    AdminStatisticListViewAdapter adminStatisticListViewAdapter = new AdminStatisticListViewAdapter(context, lstUsers, lstStatistics);
                    ListView listView = (ListView) findViewById(R.id.list_view_admin_statistics);
                    listView.setAdapter(adminStatisticListViewAdapter);

                    //Sets an OnItemClickListener for the ListView, which will allow the Admin to see all the Quiz results for the User that they click on
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(AdminStatisticsActivity.this, AdminSpecificStatisticsActivity.class);
                            intent.putExtra("userKey", lstUsers.get(position).getUserKey());
                            startActivity(intent);
                        }
                    });
                }
                toggleProgressBarVisibility(View.INVISIBLE);
            }
        }
    }
}