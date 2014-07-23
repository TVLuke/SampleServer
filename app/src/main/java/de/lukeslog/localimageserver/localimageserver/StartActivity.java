package de.lukeslog.localimageserver.localimageserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static de.lukeslog.localimageserver.localimageserver.Utils.getListOfLocalFolderPathsToDepth;


public class StartActivity extends Activity
{

    Context ctx;
    SharedPreferences settings;
    ArrayList<String> localFolderList;
    boolean running=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ctx = this;
        settings = getSharedPreferences(Constants.SETTINGS_NAME, 0);

        buttonBehavior();

        pupulateSpiner();

    }

    private void buttonBehavior()
    {
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d("SERVER", "click");
                if(running)
                {
                    Intent i = new Intent(ctx, Server.class);
                    stopService(i);
                    button.setText("Start Server");
                    running=false;
                    TextView textview = (TextView) findViewById(R.id.textView);
                    textview.setText("");
                }
                else
                {
                    Intent i = new Intent(ctx, Server.class);
                    startService(i);
                    button.setText("Stop Server");
                    running=true;
                    TextView textview = (TextView) findViewById(R.id.textView);
                    textview.setText(Utils.getIPv4Address()+":"+Constants.WEBSERVER_PORT);
                }

            }
        });
    }

    private void pupulateSpiner()
    {
        localFolderList = getListOfLocalFolderPathsToDepth(Constants.FOLDER_DEPTH);

        final Spinner localfolderlist = (Spinner) findViewById(R.id.spinner);
        final List<String> localfolderspinerArray = new ArrayList<String>();
        int lsf = settings.getInt("selectedLocalFolder", 0);
        for(int i=0; i<localFolderList.size(); i++)
        {
            localfolderspinerArray.add(localFolderList.get(i));
        }
        localfolderlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                Log.d("SERVER", "selected");
                SharedPreferences.Editor edit = settings.edit();
                edit.putInt("selectedLocalFolder", arg2);
                edit.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

                Log.d("SERVER", "not selected");
            }
        });
        ArrayAdapter<String> localadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, localfolderspinerArray);
        localadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        localadapter.notifyDataSetChanged();
        localfolderlist.setAdapter(localadapter);
        localfolderlist.setClickable(true);
        localfolderlist.setSelected(true);
        if(localFolderList.size()>=lsf)
        {
            localfolderlist.setSelection(lsf);
        }
        localadapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Intent i = new Intent(ctx, Server.class);
        stopService(i);
    }

    public void onPause()
    {
        super.onPause();
        StartActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
