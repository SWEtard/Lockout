package cmpsc488.lockout.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import cmpsc488.lockout.R;
import cmpsc488.lockout.ServerRequest;
import cmpsc488.lockout.security.SecureServerRequest;


/**
 * Created by Gal on 4/7/2015.
 */

public class LoginFragment extends BaseFragment {
    String server = "http://146.186.64.169:6917/bin";
    Boolean login = false;
    private EditText username = null;
    private EditText password = null;
    public final static String LOG_TAG = "nebulock";
    private Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_login_screen, container, false);

        username = (EditText)rootView.findViewById(R.id.editTextEmail);
        password = (EditText)rootView.findViewById(R.id.editTextPassword);
        loginButton = (Button)rootView.findViewById(R.id.loginButton);

        // Set onclick listener for loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                login(v);
            }
        });

        return rootView;
    }

    public void login(View view){

        final String userCred = username.getText().toString();
        final String passwordCred = password.getText().toString();

        ServerRequest loginRequest = new SecureServerRequest() {

            @Override
            protected void onSuccess(JSONObject data) {

                Toast.makeText(getActivity(), "Redirecting...", Toast.LENGTH_SHORT).show();
                Context context = getActivity();
                SharedPreferences sharedPreferences = context.getSharedPreferences("email", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", userCred);
                editor.commit();
                sharedPreferences = context.getSharedPreferences("password", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("password", passwordCred);
                editor.commit();

                super.onSuccess(data);
            }

            @Override
            protected void onFailure(String message, JSONObject data) {
                super.onFailure(message, data);
                //Toast.makeText(LoginScreen.this, "Invalid credentials. Please try again", Toast.LENGTH_SHORT).show();
                //TODO: Add 1 to the lockdown counter
            }

            @Override
            protected void onError(String message, Integer code, JSONObject data) {
                super.onError(message, code, data);
            }
        };

        loginRequest
                .setPath("bin/login")
                .setParameter("email", userCred)
                .setParameter("password", passwordCred)
                .execute();

    }

    //Menu probably unneeded for login page
	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.main, menu);
    	return true;
	}*/

    public void createAccount(View view){
       // DO SUMTIN
    }


    public void forgotPassword(View view){
        /*
        //TODO: open dialog box, send email to user, etc...
// get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(LoginScreen.this);
        View promptView = layoutInflater.inflate(R.layout.forgotpasswordprompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginScreen.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //check if email is in database
                        //if so
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        */
    }

}
