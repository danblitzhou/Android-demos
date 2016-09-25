package ca.danblitzhou.apidemoapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_ = "04b3e13f6a87bbb610ca8a85a9cdb0b4";
    Button searchBtn;
    EditText catalogNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBtn = (Button)findViewById(R.id.search_button);

        catalogNumber = (EditText)findViewById(R.id.catalog_number);

    }

    /**
     * searchCourse checks if the entered course number is valid
     * if yes: call UW courses api with the course number
     * to get the course info; if not, display a
     * toast to warn user
     * @param view
     */
    public void searchCourse(View view){
        if (catalogNumber.getText().toString().length() == 3) {

            CourseLookup courseLookup = new CourseLookup();
            courseLookup.execute("https://api.uwaterloo.ca/v2/courses/ECE/"
                    + catalogNumber.getText().toString()
                    + ".json?key="
                    + KEY_);
        }else {
            Toast.makeText(this, "Enter a three digit course number", Toast.LENGTH_LONG).show();
        }
    }

    public class CourseLookup extends AsyncTask<String, Void, String>{
        private URL api;
        private HttpURLConnection connection;
        private InputStream in;
        private InputStreamReader reader;
        private String result = "";
        private int data;
        @Override
        protected String doInBackground(String... apis) {
            try {
                api = new URL(apis[0]);
                connection = (HttpURLConnection)api.openConnection();
                connection.connect();
                in = connection.getInputStream();
                reader = new InputStreamReader(in);

                do {
                    data = reader.read();
                    char current = (char)data;
                    result+=current;
                }while (data != -1);

                return result;
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }finally {
                connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView titleView = (TextView)findViewById(R.id.courseTitle);
            TextView descriptionView = (TextView)findViewById(R.id.description);
            try {
                JSONObject message = new JSONObject(result);
                JSONObject meta = message.getJSONObject("meta");
                String status = meta.getString("status");
                if (status.equals("200")) {
                    JSONObject data = message.getJSONObject("data");
                    String catalogNum = data.getString("catalog_number");
                    String title = data.getString("title");
                    titleView.setText("ECE " + catalogNum + " " + title);
                    String description = data.getString("description");
                    descriptionView.setText(description);
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Invalid course number",
                            Toast.LENGTH_LONG)
                            .show();
                    titleView.setText("");
                    descriptionView.setText("");
                }

            } catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
}
