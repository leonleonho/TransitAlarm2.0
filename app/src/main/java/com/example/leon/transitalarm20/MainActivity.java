package com.example.leon.transitalarm20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends Activity {

    String temp2 = "http://api.translink.ca/rttiapi/v1/stops?apikey=uqHksMgJHyOOpCRjXNKM&lat=49.248523&long=-123.108800&radius=500";
    public static TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv);
    }

    public void getJSONObjectClick(View view) {
        EditText et = (EditText)findViewById(R.id.et);
        String busNo = et.getText().toString();
        tv.setText("Loading...");
        String url = "http://api.translink.ca/rttiapi/v1/stops/" + busNo + "?apikey=uqHksMgJHyOOpCRjXNKM";
        JSONObject jsonObject = new Translink().getJSONObject(url);
    }

    public void getJSONArrayClick(View view) {
        tv.setText("Loading...");
        EditText et = (EditText)findViewById(R.id.et);
        String busNo = et.getText().toString();
        String url = "http://api.translink.ca/rttiapi/v1/stops/"+busNo+"/estimates?apikey=uqHksMgJHyOOpCRjXNKM";
        JSONArray jsonArray = new Translink().getJSONArray(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }
    public void gotoMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
