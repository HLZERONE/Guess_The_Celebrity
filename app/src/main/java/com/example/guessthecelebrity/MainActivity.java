package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    int correctpos;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    //Button listener
    public void CheckAnswer(View view){
        if(view.getTag().toString().equals(Integer.toString(correctpos))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong, the correct answer is " + name.get(correctpos), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    //Reading html
    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char)data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    //Downloading image
    public class ImageDownload extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(in);
                return image;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    //Create Question
    public void newQuestion(){
        try{
            //Assign the image
            Random random = new Random();
            int nextcele = random.nextInt(images.size()); // get the next image we want
            ImageDownload imageDownload = new ImageDownload();
            Log.i("Image", images.get(nextcele));
            Bitmap image = imageDownload.execute(images.get(nextcele)).get();
            imageView.setImageBitmap(image);
            //Assign buttons
            correctpos = random.nextInt(4);
            int incorrectpos;
            String[] options = new String[4];
            for(int i=0; i<4; i++){
                if(i == correctpos){
                    options[i] = name.get(nextcele);
                }
                else{
                    do{
                        incorrectpos = random.nextInt(images.size());
                    }while(incorrectpos == nextcele || options.toString().contains(name.get(incorrectpos)));
                    options[i] = name.get(incorrectpos);
                }
            }
            button0.setText(options[0]);
            button1.setText(options[1]);
            button2.setText(options[2]);
            button3.setText(options[3]);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign components
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        //downloading from the website
        DownloadTask task = new DownloadTask();
        String context = null;
        try{
            context = task.execute("https://www.imdb.com/list/ls052283250/").get();
            Pattern p = Pattern.compile("<img alt=\"(.*?)\"");
            Matcher m = p.matcher(context);
            while(m.find()){
                name.add(m.group(1));
            }
            p = Pattern.compile("src=\"(.*?).jpg\"");
            m = p.matcher(context);
            while(m.find()){
                images.add(m.group(1) + ".jpg");
            }
            newQuestion();


        }catch(Exception e){
            e.printStackTrace();
        }

    }
}