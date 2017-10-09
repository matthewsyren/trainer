/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class provides information to the user about how to use the app
 */

package a15008377.opsc7312_assign2_15008377;

import android.os.Bundle;
import android.widget.Toast;

public class AdminHelpActivity extends AdminBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_help);

            //Sets the NavigationDrawer for the Activity and sets the selected item in the NavigationDrawer to Help
            super.onCreateDrawer();
            super.setSelectedNavItem(R.id.nav_help);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
