package a15008377.opsc7312_assign2_15008377;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserStatisticsActivity extends UserBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

        //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Statistics
        super.onCreateDrawer();
        super.setSelectedNavItem(R.id.nav_statistics);

        new Statistic().requestStatistics(new User(this).getUserKey(), this, new DataReceiver(new Handler()));
    }

    //Method calculates the user's average Quiz score
    public void calculateAverageScore(ArrayList<Statistic> lstStatistics){
        try{
            double total = 0;
            for(Statistic statistic: lstStatistics){
                total += statistic.getResult();
            }
            double average = total / lstStatistics.size();
            TextView txtAverage = (TextView) findViewById(R.id.text_statistics);
            txtAverage.setText("Average: " + average + "%");
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
                ArrayList<Statistic> lstStatistics = (ArrayList<Statistic>) resultData.getSerializable(FirebaseService.ACTION_FETCH_STATISTIC);
                calculateAverageScore(lstStatistics);
            }
        }
    }
}
