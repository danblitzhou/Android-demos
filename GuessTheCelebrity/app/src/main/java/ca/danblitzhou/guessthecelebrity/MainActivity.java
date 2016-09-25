package ca.danblitzhou.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    protected String result = null;
    protected static final String SRC_URL = "http://www.posh24.com/celebrities";
    protected static final String SPLIT_FLAG = "<div class=\"sidebarContainer\">";
    private ArrayList<String> imageURLs = new ArrayList<String>();
    private ArrayList<String> celebNames = new ArrayList<String>();
    private int chosenCeleb;
    private String[] answers = new String[4];
    private Random random;
    private int locationOfCorrectAns;
    Button buttonA, buttonB, buttonC, buttonD;
    ImageView celebImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonA = (Button)findViewById(R.id.buttonA);
        buttonB = (Button)findViewById(R.id.buttonB);
        buttonC = (Button)findViewById(R.id.buttonC);
        buttonD = (Button)findViewById(R.id.buttonD);

        LoadTask dlTask = new LoadTask();
        try{
            result = dlTask.execute(SRC_URL).get();

            String[] page_split = result.split(SPLIT_FLAG);
            Pattern imagPattern = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = imagPattern.matcher(page_split[0]);

            while (m.find()){
                imageURLs.add(m.group(1));
                // Log.i("image URL", m.group(1))
            }

            Pattern namePatter = Pattern.compile("alt=\"(.*?)\"");
            Matcher n = namePatter.matcher(page_split[0]);

            while (n.find()){
                celebNames.add(n.group(1));
//                Log.i("celeb name", n.group(1));
            }

        } catch (InterruptedException |
                ExecutionException e){
            e.printStackTrace();
        }

        random = new Random();
        chosenCeleb = random.nextInt(celebNames.size());

        fillAnswers(chosenCeleb);

        celebImageView = (ImageView)findViewById(R.id.celebrityImage);
        fillImage(celebImageView, chosenCeleb);


    }

    /**
     * onClick action for choosing an answer
     * detects whether the answer correct
     * if correct: show "correct" toast
     * if wrong: show correct answer
     * refreshes to a new celebrity
     * in both scenarios
     * @param view view(button) clicked
     */
    public void chooseCeleb(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAns))){
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_LONG).show();

        }else {
            Toast.makeText(getApplicationContext(),
                    "Wrong, correct Answer is " + answers[locationOfCorrectAns],
                    Toast.LENGTH_LONG)
                    .show();
        }
        chosenCeleb = random.nextInt(celebNames.size());
        fillAnswers(chosenCeleb);
        fillImage(celebImageView, chosenCeleb);
    }


    /**
     * fillAnswers sets up an array of 4 Strings(celeb names)
     * as the current answers on display
     * @param chosenCeleb: current randomly selected celebrity index
     */
    public void fillAnswers(int chosenCeleb){
        String CurrentName = celebNames.get(chosenCeleb);
        locationOfCorrectAns = random.nextInt(4);
        int randomNameIndex;

        for (int i = 0; i < 4; i++){
            if (i == locationOfCorrectAns){
                answers[i] = CurrentName;
            }else {
                do {
                    randomNameIndex = random.nextInt(celebNames.size());
                }while (randomNameIndex == locationOfCorrectAns);
                answers[i] = celebNames.get(randomNameIndex);
            }
        }

        buttonA.setText(answers[0]);
        buttonB.setText(answers[1]);
        buttonC.setText(answers[2]);
        buttonD.setText(answers[3]);

    }

    /**
     * fillImages fills the image view with the image of
     * chosen celebrity downloaded by ImageDLoader
     * @param celebImageView Main celebrity image view
     * @param chosenCeleb index of chosen celebrity
     */
    public void fillImage(ImageView celebImageView, int chosenCeleb){
        try{
            ImageDownloader imageDownloader = new ImageDownloader();

            Bitmap celebImage = imageDownloader.execute(imageURLs.get(chosenCeleb)).get();

            celebImageView.setImageBitmap(celebImage);

        } catch (InterruptedException |
                ExecutionException e){
            e.printStackTrace();
        }

    }

    /**
     * The class LoadTask is an AsyncTask that runs
     * in the background getting image url
     * and celebrity name for the app
     */
    public class LoadTask extends AsyncTask<String, Void, String>{
        protected String result = "";
        protected URL url;
        protected HttpURLConnection urlConnection = null;
        protected InputStream in;
        protected InputStreamReader reader;
        protected int data;
        protected char current;
        @Override
        protected String doInBackground(String... urls) {

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = urlConnection.getInputStream();
                reader = new InputStreamReader(in);
                data = reader.read();

                while (data != -1){
                    current = (char)data;
                    result+=current;
                    data = reader.read();
                }

                return result;

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return null;
        }
    }

    /**
     * The class ImageDownloader is an AsyncTask that
     * downloads all the images from URLs from
     * posh24 site
     */

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{
        protected URL url;
        protected HttpURLConnection urlConnection = null;
        protected InputStream in;
        protected Bitmap image;
        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();
                in = urlConnection.getInputStream();
                image = BitmapFactory.decodeStream(in);

                return image;
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return null;
        }
    }
}
