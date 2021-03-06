package com.riskwizard.incidentlogger.activities;

import com.riskwizard.incidentlogger.dao.ContactDAO;
import com.riskwizard.incidentlogger.dao.IncidentDAO;
import com.riskwizard.incidentlogger.dao.IncidentTypeDAO;
import com.riskwizard.incidentlogger.helper.DatabaseHelper;
import com.riskwizard.incidentlogger.helper.LogHelper;
import com.riskwizard.incidentlogger.model.IncidentType;
import com.riskwizard.incidentlogger.model.Incident;
import com.riskwizard.incidentlogger.model.Contact;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.riskwizard.incidentlogger.R;

//TODO google naming conventions in Android for buttons, textEditViews etc...
//https://source.android.com/source/code-style.html (Follow Field Naming Conventions)
//TODO apply some of these for organising resource files https://github.com/futurice/android-best-practices

public class MainActivity extends ActionBarActivity {

    private String[] mDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogHelper.LogCallback(this, "onCreate");

        mTitle = "test";

        mDrawerItemTitles = getResources().getStringArray(R.array.drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerItemTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                //getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mTitle);
                //getSupportActionBar().setTitle(mTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_rw);

        setupViews();

        // Load Shared Preference
        SharedPreferences _SharedPref = getApplicationContext().getSharedPreferences("MyAppData", 0);
        final SharedPreferences.Editor _SharedPrefEditor = _SharedPref.edit();
        int UserKey = _SharedPref.getInt("UserKey", -1);
        String UserName = _SharedPref.getString("UserName", "");
        String Password = _SharedPref.getString("Password", "");

        //If User is logged In and Display User Data
        TextView txt_Name = (TextView) findViewById(R.id.textView);
        if (UserKey != -1) {
            txt_Name.setText(String.format("UserKey: %d, Name: %s, Password: %s",UserKey, UserName, Password));
        } else {
            txt_Name.setText("");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        /*
        switch (id) {
            case R.id.action_settings:
                //start settings activity
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
                return true;
            case R.id.action_help:
                //start help activity
                Intent i2 = new Intent(this, Help.class);
                startActivity(i2);
                return true;
            case R.id.action_about:
                //start about activity
                Intent i3 = new Intent(this, About.class);
                startActivity(i3);
                return true;
            case R.id.action_contact:
                //start contact activity
                Intent i4 = new Intent(this, com.riskwizard.incidentlogger.activities.Contact.class);
                startActivity(i4);
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return true;
        }
        */
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void logout() {

        SharedPreferences _SharedPref = getApplicationContext().getSharedPreferences("MyAppData", 0);
        SharedPreferences.Editor _SharedPrefEditor = _SharedPref.edit();

        _SharedPrefEditor.remove("UserKey");
        _SharedPrefEditor.remove("UserName");
        _SharedPrefEditor.remove("Password");
        _SharedPrefEditor.remove("RememberMe");
        //_SharedPrefEditor.clear(); //This will clear all data inside MyAppData Shared Preference File
        _SharedPrefEditor.commit();

        startActivity(new Intent(this, Login.class));
    }

    private void setupViews() {

        Button button2 = (Button) findViewById(R.id.logIncidentButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, IncidentForm.class);
                //i.putExtra("incident_id", new Long("63"));
                startActivity(i);
            }
        });

        Button button3 = (Button) findViewById(R.id.incidentRegistryButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, IncidentRegistry.class);
                startActivity(i);
            }
        });

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectDrawerItem(position);
        }
    }

    private void selectDrawerItem(int position) {
        //Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mDrawerItemTitles[position]);

        switch (mDrawerItemTitles[position]) {
            case "Create Incident":
                Intent i = new Intent(MainActivity.this, IncidentForm.class);
                //i.putExtra("incident_id", new Long("63"));
                startActivity(i);
                break;
            case "Incident Registry":
                startActivity(new Intent(MainActivity.this, IncidentRegistry.class));
                break;
            case "Settings":
                startActivity(new Intent(this, Settings.class));
                break;
            case "Help":
                startActivity(new Intent(this, Help.class));
                break;
            case "About":
                startActivity(new Intent(this, About.class));
                break;
            case "Contact Us":
                startActivity(new Intent(this, com.riskwizard.incidentlogger.activities.Contact.class));
                break;
            case "Logout":
                logout();
                break;
            default:
                break;
        }

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    // <editor-fold desc="lifecycle callbacks">

    @Override
    protected void onStart()
    {
        super.onStart();
        LogHelper.LogCallback(this, "onStart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LogHelper.LogCallback(this, "onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        LogHelper.LogCallback(this, "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        LogHelper.LogCallback(this, "onSaveInstanceState");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        LogHelper.LogCallback(this, "onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LogHelper.LogCallback(this, "onDestroy");

        //todo if not null close in every activity on destroy
        //incidentType_dao.close();
        //incident_dao.close();
        //contact_dao.close();
    }

// </editor-fold>

}
