/*
 * Author: Matthew Syrén
 *
 * Date:   29 August 2017
 *
 * Description: Class provides the basis for the NavigationDrawer. The NavigationDrawer's processing is completed
 *              here, meaning any class that extends this class will have ful NavigationDrawer functionality
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class UserBaseActivity extends FragmentActivity
                          implements NavigationView.OnNavigationItemSelectedListener {

    //Declarations
    private NavigationView navigationView;

    protected void onCreateDrawer() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton btnMenu = (ImageButton) findViewById(R.id.button_menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer(v);
            }
        });
    }

    //Method opens the NavigationDrawer when the menu button is clicked
    public void toggleDrawer(View view){
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
    }

    //Method sets the selected item in the Navigation Drawer
    public void setSelectedNavItem(int id){
        navigationView.setCheckedItem(id);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Handles clicks on the NavigationDrawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //Opens the appropriate Activity based on the menu item clicked in the Navigation Drawer
        if(id == R.id.nav_home){
            startActivity(new Intent(getApplicationContext(), QuizFetcherActivity.class));
        }
        else if(id == R.id.nav_quizzes){
            startActivity(new Intent(getApplicationContext(), PastQuizActivity.class));
        }
        else if(id == R.id.nav_statistics){
            startActivity(new Intent(getApplicationContext(), UserStatisticsActivity.class));
        }
        else if(id == R.id.nav_videos){
            startActivity(new Intent(getApplicationContext(), VideoViewerActivity.class));
        }
        else if(id == R.id.nav_sign_out){
            //Signs the user out of Firebase
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();

            //Removes the user's email and key from SharedPreferences
            SharedPreferences preferences = getSharedPreferences("", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userEmail", null);
            editor.putString("userKey", null);
            editor.putBoolean("userAdminRights", false);
            editor.apply();

            //Takes the user back to the LoginActivity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        //Closes the NavigationDrawer once the action has been completed
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}