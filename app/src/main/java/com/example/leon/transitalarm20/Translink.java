package com.example.leon.transitalarm20;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jens on 2/7/2015.
 */
public class Translink extends AsyncTask<String, String, String> {

    public static enum TaskType {
        STOP_ARRAY, STOP_DETAILS, BUS_TIMES
    }

    private AsyncTaskCompletedListener caller;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    public TaskType taskType;

    public Translink(AsyncTaskCompletedListener caller, TaskType taskType) {
        this.caller = caller;
        this.taskType = taskType;
    }

    public void printArray(String s) {
        taskType = TaskType.STOP_ARRAY;
        execute(s);
    }

    public void printBusNo(String s) {
        taskType = TaskType.BUS_TIMES;
        execute(s);
    }

    @Override
    protected String doInBackground(String... params) {
        HttpGet httpGet = new HttpGet(params[0]);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("ContentType", "application/json");
        DefaultHttpClient client = new DefaultHttpClient();
        String result = null;
        try {
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            //result = EntityUtils.toString(entity);
            InputStream inputStream = entity.getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
                caller.onTaskComplete(new JSONArray(s), taskType);

        } catch (Exception e) {
            e.printStackTrace();
        }



        /*
        MainActivity.tv.setText("");
        if (s.charAt(0) == '[') {
            parseArray(s);
        } else {
            parseObject(s);
        }
        */
    }

    private void parseObject(String s) {
        try {
            jsonObject = new JSONObject(s);
            MainActivity.tv.setText(jsonObject.getString("Name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseArray(String s) {
        try {
            jsonArray = new JSONArray(s);
            jsonObject = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                MainActivity.tv.append(jsonObject.getString("Name") + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
