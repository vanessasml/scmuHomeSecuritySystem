package com.p1.scmu.home_security_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Vanessa on 5/24/2017.
 */

public class ActivityAddUser extends ActivityUser {

    private static final int request_code_rfid =1;
    //private static final int request_code_add_user =2;
    public static final String MEMBER = "Member";

    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutNumber;
    private LinearLayout inputLayoutRFID;
    private EditText inputName, inputEmail, inputNumber, inputRFID;
    private ImageView inputImage;
    private Button btn_submit;
    private ImageButton btn_rfid;
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
               submitForm(view);
            }
        });
    }

    public void startRFIDActivity(View view) {
        Log.i("ActivityRFIDReader", "Ready");
        Intent intent = new Intent(ActivityAddUser.this, ActivityRFIDReader.class);
        startActivityForResult(intent, request_code_rfid);
    }


    protected void submitForm(View view) {
        super.submitForm();
        if(!readyToSubmit) startRFIDActivity(view);
        else createMember();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ActivityRFIDReader", "onActivity");
        if (requestCode == request_code_rfid){
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

        Member member = new Member(name, email, phone, rfidString);

        Log.i("atButton", "sendingData");

        Intent i = new Intent();
        i.putExtra(MEMBER, member);
        setResult(RESULT_OK, i);

        finish();

    }

}
