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
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Vanessa on 5/24/2017.
 */

public class ActivityAddUser extends AppCompatActivity {

    private static final int request_code =1;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutNumber;
    private LinearLayout inputLayoutRFID;
    private EditText inputName, inputEmail, inputNumber, inputRFID;
    private ImageView inputImage;
    private Button btn_submit;
    private boolean readyToSubmit=false;
        private boolean alreadyCreating = false;

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

        btn_submit = (Button) findViewById(R.id.button_next_rfid);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               submitForm(view);
            }
        });
    }

    public void startRFIDActivity(View view) {
        Log.i("ActivityRFIDReader", "Ready");
        Intent intent = new Intent(ActivityAddUser.this, ActivityRFIDReader.class);
        startActivityForResult(intent, request_code);

    }


    private void submitForm(View view) {
        if (!validateName()) return;
        if (!validatePhoneNumber()) return;
        if (!validateEmail()) return;

        if(!readyToSubmit) startRFIDActivity(view);
        else createMember();
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhoneNumber() {
        String text = inputNumber.getText().toString();
        if (text.trim().isEmpty()) {
            inputLayoutNumber.setError(getString(R.string.err_msg_number));
            requestFocus(inputNumber);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ActivityRFIDReader", "onActivity");
        if (requestCode == request_code){
            if(resultCode == RESULT_OK) {
                Log.i("ActivityRFIDReader", "guardando");
                String dataString = data.getExtras().getString(ActivityRFIDReader.CONTENT);
                Log.i("ActivityAddUser", dataString);
                inputRFID.setText(dataString);
                inputLayoutRFID.setVisibility(View.VISIBLE);
                btn_submit.setText("FINISH");
                readyToSubmit = true;
            }
        }
    }

    public void createMember() {
        if (alreadyCreating)
            return;

        alreadyCreating = true;

        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        int phone = Integer.parseInt(inputNumber.getText().toString());
        String rfidString = inputRFID.getText().toString().trim();

        int icon = (Integer)inputEmail.getTag();

        if (icon == 0)
            icon = 0;

        //Map<String, Object> members = new HashMap<>();

        Member member = new Member(name, email, icon, phone, rfidString);

        //send to server new member
        //member.asJSON();

    }

}
