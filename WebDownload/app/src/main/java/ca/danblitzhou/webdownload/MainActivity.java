package ca.danblitzhou.webdownload;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask dlTask = new DownloadTask();

        String result = null;
        try{
            result = dlTask.execute("https://api.blue.wemesh.ca/").get();
        } catch (InterruptedException |
                ExecutionException e){
            e.printStackTrace();
        }

        Log.i("Result", result);
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        protected String result = "";
        protected URL url;
        protected HttpURLConnection urlConnection = null;
        protected int data;
        @Override
        protected String doInBackground(String... urls) {

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream inStream = urlConnection.getInputStream();

                InputStreamReader inStreamReader = new InputStreamReader(inStream);


                data = inStreamReader.read();

                while (data != -1){
                    char currentChar = (char)data;
                    result += currentChar;
                    data = inStreamReader.read();
                }

            } catch (MalformedURLException e){
                e.printStackTrace();
                return "failed";
            } catch (IOException e){
                e.printStackTrace();
                return "failed";
            } finally {
                urlConnection.disconnect();
            }

            return result;

        }
    }
}
