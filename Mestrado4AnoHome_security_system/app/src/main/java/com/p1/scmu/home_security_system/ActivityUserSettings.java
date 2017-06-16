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
    public static final String MEMBER = "Member";

        private static final int request_code =1;

        private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutNumber;
        private LinearLayout inputLayoutRFID;
        private EditText inputName, inputEmail, inputNumber, inputRFID;
        private ImageView inputImage;
        private Button btn_cancel, btn_ok;
        private ImageButton btn_rfid;
        private boolean readyToSubmit=false;
        private boolean alreadyCreating = false;
        private Member selectedMember;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_settings);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            selectedMember = bundle.getParcelable(MEMBER);

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
            //inputNumber.setText(selectedMember.mobile);
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

            btn_ok = (Button) findViewById(R.id.button_ok_user_settings);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                    updateMember();

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

        protected boolean submitForm(){
            boolean canSubmit = super.submitForm();
            return canSubmit;
        }

        public void startRFIDActivity(View view) {
            Log.i("ActivityRFIDReader", "Ready");
            Intent intent = new Intent(ActivityUserSettings.this, ActivityRFIDReader.class);
            startActivityForResult(intent, request_code);
        }


    private void updateMember() {

        Log.i("atButtonokUpdate", "sendingData!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        selectedMember.fullName = inputName.getText().toString().trim();
        Log.i("updated fullname:", selectedMember.fullName);
        selectedMember.email = inputEmail.getText().toString().trim();
        Log.i("updated email:", selectedMember.email);
        //selectedMember.mobile = Integer.parseInt(inputNumber.getText().toString());
        selectedMember.rfid = inputRFID.getText().toString().trim();
        Log.i("updated rfid:", selectedMember.rfid);


        Intent i = new Intent();
        i.putExtra(UPDATE_MEMBER, selectedMember);

        setResult(RESULT_OK, i);
        finish();

    }
}
