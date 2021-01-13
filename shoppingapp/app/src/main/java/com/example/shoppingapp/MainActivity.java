package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ImageView ivCart;

    EditText etUsername, etPassword;
    Button btnLogin, btnRegisterLink;

    EditText etRegisterUsername, etRegisterEmail, etRegisterPassword, etRegisterConfirmPassword;
    Button btnRegisterUser;

    EditText etUploadName, etUploadPrice, etUploadDescription;
    Button btnUploadItem;

    RecyclerView rvList, rvCart;
    RecyclerView.Adapter<ItemAdapter.ViewHolder> myAdapter;
    //TODO declare cartAdapter w corresponding viewholder here, then initialize like with itemAdapter

    //TODO probably need to setup a basic cartItem class and adapter for the cartFragment exactly
    //how I did the shopFragment(or any way you think is better @Andrew)

    ArrayList<Item> items;

    FragmentManager fragmentManager;
    Fragment fragLoginLayout, fragRegisterLayout, fragUploadLayout, fragShopLayout,
            fragCartLayout;

    Boolean isLoggedIn; //bool for use for buttonClicks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id. etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterLink = findViewById(R.id.btnRegisterLink);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);

        btnRegisterUser = findViewById(R.id.btnRegisterUser);
        etUploadName = findViewById(R.id.etUploadName);
        etUploadPrice = findViewById(R.id.etUploadPrice);
        etUploadDescription = findViewById(R.id.etUploadDescription);
        btnUploadItem = findViewById(R.id.btnUploadItem);

        rvList = findViewById(R.id.rvList);
        rvCart = findViewById(R.id.rvCart);

        //TODO setup ArrayList<Item> items here by downloading stuff from DB

        //TODO setup adapter constructor in ItemAdapter and create it accordingly(w arraylist again)
        // once we gain access to the backend API from Tony
        // example: myAdapter = new ItemAdapter(getApplicationContext(),insert list name here);

        fragmentManager = getSupportFragmentManager();

        fragLoginLayout = fragmentManager.findFragmentById(R.id.fragLoginLayout);
        fragRegisterLayout = fragmentManager.findFragmentById(R.id.fragRegisterLayout);
        fragUploadLayout = fragmentManager.findFragmentById(R.id.fragUploadLayout);
        fragShopLayout = fragmentManager.findFragmentById(R.id.fragShopLayout);
        fragCartLayout = fragmentManager.findFragmentById(R.id.fragCartLayout);

        //temporary, if we end up sticking to actionBar then we'll remove navFrag entirely
        fragmentManager.beginTransaction() .show(fragLoginLayout)
                                            .hide(fragRegisterLayout)
                                            .hide(fragUploadLayout)
                                            .hide(fragShopLayout)
                                            .hide(fragCartLayout)
                                            .commit();
        isLoggedIn=false;
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // show shop fragment after web server login request
                if ( isValidUsername(etUsername.getText().toString()) && isValidPassword(etPassword.getText().toString()) )
                {
                    new LoginInBackground().execute(etUsername.getText().toString(), etPassword.getText().toString());
                }
                else
                {
                    Log.i("shopLogin", "Username status: " + isValidUsername(etUsername.getText().toString()) + "\n" +
                            "Password status: " + isValidPassword(etPassword.getText().toString()));
                }
            }
        });

        btnRegisterLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fragmentManager.beginTransaction()
                        .hide(fragLoginLayout)
                        .show(fragRegisterLayout).addToBackStack(null)
                        .commit();
            }
        });

        btnRegisterUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // save to database then return to login screen
                if ( isValidUsername(etRegisterUsername.getText().toString())
                        && isValidPassword(etRegisterPassword.getText().toString())
                        && etRegisterPassword.getText().toString().equals(etRegisterConfirmPassword.getText().toString())
                        && isValidEmail(etRegisterEmail.getText().toString()) ) {
                    new RegisterInBackground().execute(etRegisterUsername.getText().toString(), etRegisterPassword.getText().toString(), etRegisterEmail.getText().toString());
                }
                else
                {
                    Log.i( "shopLogin", "Username status: " + isValidUsername(etRegisterUsername.getText().toString()) + "\n" +
                            "Password status: " + isValidPassword(etRegisterPassword.getText().toString()) + "\n" +
                            "Confirm Password status: " + etRegisterPassword.getText().toString().equals(etRegisterConfirmPassword.getText().toString()) + "\n" +
                            "Email status: " + isValidEmail(etRegisterEmail.getText().toString()) );
                }
            }
        });

        btnUploadItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // save to database possibly, but definitely update the current shop then return to shop
                if ( (etUploadName.getText().toString().isEmpty() == false) && (etUploadPrice.getText().toString().isEmpty() == false) )
                {
                    new UploadInBackground().execute(etUploadName.getText().toString(), etUploadPrice.getText().toString(), etUploadDescription.getText().toString());
                }
            }
        });
    }

    protected Boolean isValidUsername(String inputUsername)
    {
        if ( inputUsername.isEmpty() || (inputUsername == null) || (inputUsername.length() < 1) )
        {
            return false;
        }

        return true;
    }

    protected Boolean isValidPassword(String inputPassword)
    {
        if ( inputPassword.isEmpty() || (inputPassword == null) || (inputPassword.length() < 1) )
        {
            return false;
        }

        return true;
    }

    protected Boolean isValidEmail(String inputEmail)
    {
        return (!TextUtils.isEmpty(inputEmail) && Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches());
    }


    //ActionBar functionality here
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.cart:
                if(isLoggedIn)fragmentManager.beginTransaction().show(fragCartLayout)
                                                .hide(fragLoginLayout)
                                                .hide(fragRegisterLayout)
                                                .hide(fragUploadLayout)
                                                .hide(fragShopLayout).addToBackStack(null)
                                                .commit();
                else Toast.makeText(getApplicationContext(),"Not logged in, cannot access cart.",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.upload:
                if(isLoggedIn)fragmentManager.beginTransaction().show(fragUploadLayout)
                                                    .hide(fragLoginLayout)
                                                    .hide(fragRegisterLayout)
                                                    .hide(fragCartLayout)
                                                    .hide(fragShopLayout).addToBackStack(null)
                                                    .commit();
                else Toast.makeText(getApplicationContext(),"Not logged in, cannot upload item.",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class LoginInBackground extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... userData) {
            Log.i("shopData", "user: " + userData[0] + " password: " + userData[1]);

            String urlString = "https://install-gentoo.herokuapp.com/users/login";

            try
            {
                String urlParameters  = "username=" + userData[0] +  "&password=" + userData[1];
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                writer.write(postData);
                urlConnection.connect();

                writer.flush();
                writer.close();
//                out.close();

                String response = "";
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while ((line = responseReader.readLine()) != null)
                    {
                        response += line;
                    }

                    Log.i("shopLog", response);
                    return true;
                }
                else
                {
                    response = "ERR";

                    Log.i("shopLog", response);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.i("shopLog", "Error:" + e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean loginStatus) {
            super.onPostExecute(loginStatus);

            // login success
            if (loginStatus)
            {
                // switch to shop fragment
                
                new DownloadInBackground().execute();
                
            }
            else // login failure
            {
                Toast.makeText(getApplicationContext(),"Login failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class RegisterInBackground extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... userData) {
            Log.i("shopData", "user: " + userData[0] + " password: " + userData[1] + "email: " + userData[2]);

            String urlString = "https://install-gentoo.herokuapp.com/users/signup";

            try
            {
                String urlParameters  = "username=" + userData[0] +  "&password=" + userData[1] + "&email=" + userData[2];
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                writer.write(postData);
                urlConnection.connect();

                writer.flush();
                writer.close();
//                out.close();

                String response = "";
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while ((line = responseReader.readLine()) != null)
                    {
                        response += line;
                    }

                    Log.i("shopLog", response);
                    return true;
                }
                else
                {
                    response = "ERR";

                    Log.i("shopLog", response);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.i("shopLog", "Error:" + e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean registerStatus) {
            super.onPostExecute(registerStatus);

            // login success
            if (registerStatus)
            {
                // switch to shop fragment
                fragmentManager.beginTransaction()
                        .hide(fragRegisterLayout)
                        .show(fragLoginLayout)
                        .addToBackStack(null)
                        .commit();
            }
            else // login failure
            {
                Toast.makeText(getApplicationContext(),"Registration failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class UploadInBackground extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... itemData) {
            Log.i("shopData", "item name: " + itemData[0] + " price: " + itemData[1] + "description: " + itemData[2]);

            String urlString = "https://install-gentoo.herokuapp.com/users/upload";

            try
            {
                String urlParameters  = "itemName=" + itemData[0] +  "&price=" + itemData[1] + "&description=" + itemData[2];
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                writer.write(postData);
                urlConnection.connect();

                writer.flush();
                writer.close();
//                out.close();

                String response = "";
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while ((line = responseReader.readLine()) != null)
                    {
                        response += line;
                    }

                    Log.i("shopLog", response);
                    return true;
                }
                else
                {
                    response = "ERR";

                    Log.i("shopLog", response);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.i("shopLog", "Error:" + e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean uploadStatus) {
            super.onPostExecute(uploadStatus);

            // login success
            if (uploadStatus)
            {
                // switch to shop fragment
                items.add( new Item( etUploadName.getText().toString(), etUploadPrice.getText().toString(), etUploadDescription.getText().toString() ) );
                myAdapter.notifyDataSetChanged();
                
                fragmentManager.beginTransaction()
                        .hide(fragUploadLayout)
                        .show(fragShopLayout)
                        .addToBackStack(null)
                        .commit();
            }
            else // login failure
            {
                Toast.makeText(getApplicationContext(),"Upload failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DownloadInBackground extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String...userNames) {

            String urlString = "https://install-gentoo.herokuapp.com/users/shop";

            try
            {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                
                JsonObject jsonObj = new JsonParser().parse(reader).getAsJsonObject();
                Set<String> keys = jsonObj.keySet();

                for(String s:keys){
                    //jsonObj.get(s).
                }


            }
            catch (Exception e)
            {
                Log.i("shopLog", "Exception:" + e);
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean shopStatus) {
            super.onPostExecute(shopStatus);
            
            if (shopStatus)
            {
                myAdapter.notifyDataSetChanged();

                isLoggedIn=true;
                fragmentManager.beginTransaction()
                        .hide(fragLoginLayout)
                        .show(fragShopLayout)
                        .addToBackStack(null)
                        .commit();
            }
            else
            {
                isLoggedIn=false;
                Toast.makeText(getApplicationContext(),"Shop download failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}