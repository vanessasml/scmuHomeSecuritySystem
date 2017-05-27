package com.p1.scmu.home_security_system;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by Vanessa on 5/27/2017.
 */

public abstract class ActivityUser extends AppCompatActivity {

    protected TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutNumber;
    protected EditText inputName, inputEmail, inputNumber, inputRFID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void submitForm(){
        if(!validateEmail()) return;
        if(!validateName()) return;
        if(!validatePhoneNumber()) return;
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
