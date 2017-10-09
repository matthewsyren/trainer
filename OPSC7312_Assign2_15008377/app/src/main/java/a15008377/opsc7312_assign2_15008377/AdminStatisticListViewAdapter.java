/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class displays user statistic information in a ListView
 */

package a15008377.opsc7312_assign2_15008377;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdminStatisticListViewAdapter extends ArrayAdapter {
    //Declarations
    private Context context;
    private ArrayList<User> lstUsers;
    private ArrayList<Statistic> lstStatistics;

    //Constructor
    public AdminStatisticListViewAdapter(Context context, ArrayList<User> lstUsers, ArrayList<Statistic> lstStatistics) {
        super(context, R.layout.list_view_row_admin_statistics, lstUsers);
        this.context = context;
        this.lstUsers = lstUsers;
        this.lstStatistics = lstStatistics;
    }

    //Method populates the appropriate Views with the appropriate data
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtUserName;
        TextView txtAverageResult;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_admin_statistics, parent, false);

        //View assignments
        txtUserName = (TextView) convertView.findViewById(R.id.text_user_name);
        txtAverageResult = (TextView) convertView.findViewById(R.id.text_average_result);

        //Calculates the user's average score
        double total = 0;
        int count = 0;
        double average = 0;

        for(Statistic statistic : lstStatistics){
            if(statistic.getUserKey().equals(lstUsers.get(position).getUserKey())){
                total += statistic.getResult();
                count++;
            }
        }

        if(count > 0){
            average = total / count;
        }

        //Displays the data in the appropriate Views
        Resources resources = context.getResources();
        txtUserName.setText(resources.getString(R.string.list_view_text_user_name, lstUsers.get(position).getUserFullName()));
        txtAverageResult.setText(resources.getString(R.string.list_view_text_user_average_result, average));
        return convertView;
    }
}