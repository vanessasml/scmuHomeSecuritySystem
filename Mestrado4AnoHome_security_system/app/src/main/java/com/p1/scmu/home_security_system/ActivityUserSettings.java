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
import android.widget.Toast;

/**
 * Created by Vanessa on 5/27/2017.
 */

public class ActivityUserSettings extends ActivityUser {

        public static final String UPDATE_MEMBER = "UpdateMember";

        private static final int request_code =1;

        private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutNumber;
        private LinearLayout inputLayoutRFID;
        private EditText inputName, inputEmail, inputNumber, inputRFID;
        private ImageView inputImage;
        private Button btn_cancel, btn_ok;
        private ImageButton btn_rfid;
        private boolean readyToSubmit=false;
        private boolean alreadyCreating = false;
        private Member selectedMember, updatedMember;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_user);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            selectedMember = (Member) bundle.get("Member");

            inputLayoutName = (TextInputLayout) findViewById(R.id.settings_text_input_name);
            inputLayoutEmail = (TextInputLayout) findViewById(R.id.settings_text_input_email);
            inputLayoutNumber = (TextInputLayout) findViewById(R.id.settings_text_input_number);

            inputLayoutRFID = (LinearLayout) findViewById(R.id.settings_text_input_rfid);

            inputName = (EditText) findViewById(R.id.settings_name_field);
            inputEmail = (EditText) findViewById(R.id.settings_email_field);
            inputNumber = (EditText) findViewById(R.id.settings_phone_field);
            inputRFID = (EditText) findViewById(R.id.settings_rfid_field);
            inputImage = (ImageView) findViewById(R.id.settings_img_user);

            inputName.setText(selectedMember.fullName);
            inputEmail.setText(selectedMember.email);
            inputNumber.setText(selectedMember.mobile);
            inputRFID.setText(selectedMember.rfid);

            btn_rfid = (ImageButton) findViewById(R.id.rfid_change_button);
            btn_rfid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startRFIDActivity(view);
                }
            });

            btn_cancel = (Button) findViewById(R.id.button_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Changes canceled", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            btn_ok = (Button) findViewById(R.id.button_ok);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitForm();
                }
            });
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
                    readyToSubmit = true;
                }
            }
        }

        protected void submitForm(){
            super.submitForm();
            updateMember();
        }

        public void startRFIDActivity(View view) {
            Log.i("ActivityRFIDReader", "Ready");
            Intent intent = new Intent(ActivityUserSettings.this, ActivityRFIDReader.class);
            startActivityForResult(intent, request_code);
        }


    private void updateMember() {

            Log.i("atButton", "sendingData");

            updatedMember = selectedMember; // para ficar com a mesma lista de arriveDepartures
            updatedMember.fullName = inputName.getText().toString().trim();
            updatedMember.email = inputEmail.getText().toString().trim();
            updatedMember.mobile = Integer.parseInt(inputNumber.getText().toString());
            updatedMember.rfid = inputRFID.getText().toString().trim();

            if(updatedMember.fullName.equals(selectedMember.fullName)
                    &&  updatedMember.email.equals(selectedMember.email)
                    && updatedMember.mobile==selectedMember.mobile
                    && updatedMember.rfid.equals(selectedMember.rfid))
                return;

            Intent i = new Intent();
            i.putExtra(UPDATE_MEMBER, updatedMember);
            setResult(RESULT_OK, i);

            finish();
        }

}
