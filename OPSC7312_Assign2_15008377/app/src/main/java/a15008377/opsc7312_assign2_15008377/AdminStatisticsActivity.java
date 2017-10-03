package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminStatisticsActivity extends AdminBaseActivity {
    //Declarations
    ArrayList<User> lstUsers;
    ArrayList<Statistic> lstStatistics;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_statistics);
        context = this;

        //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Statistics
        super.onCreateDrawer();
        super.setSelectedNavItem(R.id.nav_statistics);

        //Fetches the necessary data for this Activity
        new User().requestUsers(getApplicationContext(), new DataReceiver(new Handler()));
    }

    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult ( int resultCode, Bundle resultData){
            //Performs the appropriate action once the data has been fetched from Firebase
            if(resultCode == FirebaseService.ACTION_FETCH_USER_RESULT_CODE){
                lstUsers = (ArrayList<User>) resultData.getSerializable(FirebaseService.ACTION_FETCH_USER);
                for(User user : lstUsers){
                    Toast.makeText(getApplicationContext(), user.getUserFullName(), Toast.LENGTH_LONG).show();
                }
                new Statistic().requestStatistics(null, getApplicationContext(), new DataReceiver(new Handler()));
            }
            else if (resultCode == FirebaseService.ACTION_FETCH_STATISTIC_RESULT_CODE) {
                lstStatistics = (ArrayList<Statistic>) resultData.getSerializable(FirebaseService.ACTION_FETCH_STATISTIC);
                AdminStatisticListViewAdapter adminStatisticListViewAdapter = new AdminStatisticListViewAdapter(context, lstUsers, lstStatistics);
                ListView listView = (ListView) findViewById(R.id.list_view_admin_statistics);
                listView.setAdapter(adminStatisticListViewAdapter);
            }
        }
    }
}
