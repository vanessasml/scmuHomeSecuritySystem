package com.p1.scmu.home_security_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Vanessa on 5/27/2017.
 */

public abstract class ActivityUser extends AppCompatActivity {

    protected TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutNumber;
    protected EditText inputName, inputEmail, inputNumber, inputRFID;
    private Button btn_submit;
    private LinearLayout inputLayoutRFID;
    private ImageView inputImage;
    private ImageButton btn_rfid;
    private static final int request_code_rfid =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        inputLayoutName = (TextInputLayout) findViewById(R.id.text_input_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.text_input_email);
        inputLayoutNumber = (TextInputLayout) findViewById(R.id.text_input_number);

        inputLayoutRFID = (LinearLayout) findViewById(R.id.rfid_field_layout);

        inputName = (EditText) findViewById(R.id.name_field);
        inputEmail = (EditText) findViewById(R.id.email_field);
        inputNumber = (EditText) findViewById(R.id.phone_field);
        inputRFID = (EditText) findViewById(R.id.rfid_field);
        inputImage = (ImageView) findViewById(R.id.img_user);


        btn_rfid = (ImageButton) findViewById(R.id.rfid_add_user_button);
        btn_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRFIDActivity(view);
            }
        });

        btn_submit = (Button) findViewById(R.id.button_next_rfid);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

    }

    protected void submitForm(){
        if(!validateEmail()) return;
        if(!validateName()) return;
        if(!validatePhoneNumber()) return;
    }
    public void startRFIDActivity(View view) {
        Log.i("ActivityRFIDReader", "Ready");
        Intent intent = new Intent(ActivityUser.this, ActivityRFIDReader.class);
        startActivityForResult(intent, request_code_rfid);
    }
    protected boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    protected boolean validatePhoneNumber() {
        String text = inputNumber.getText().toString();
        if (text.trim().isEmpty()) {
            inputLayoutNumber.setError(getString(R.string.err_msg_number));
            requestFocus(inputNumber);
            return false;
        }
        return true;
    }

    protected boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if ((!email.isEmpty())&& (!isValidEmail(email))) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    protected static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
