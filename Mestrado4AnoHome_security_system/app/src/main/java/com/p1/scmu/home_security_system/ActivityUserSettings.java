package com.p1.scmu.home_security_system;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Vanessa on 5/27/2017.
 */

public class ActivityUserSettings extends ActivityUser {

        private static final int request_code =1;
        private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutNumber;
        private LinearLayout inputLayoutRFID;
        private EditText inputName, inputEmail, inputNumber, inputRFID;
        private ImageView inputImage;
        private Button btn_cancel, btn_ok;
        private boolean readyToSubmit=false;
        private boolean alreadyCreating = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_user);

            inputLayoutName = (TextInputLayout) findViewById(R.id.settings_text_input_name);
            inputLayoutEmail = (TextInputLayout) findViewById(R.id.settings_text_input_email);
            inputLayoutNumber = (TextInputLayout) findViewById(R.id.settings_text_input_number);

            inputLayoutRFID = (LinearLayout) findViewById(R.id.settings_text_input_rfid);

            inputName = (EditText) findViewById(R.id.settings_name_field);
            inputEmail = (EditText) findViewById(R.id.settings_email_field);
            inputNumber = (EditText) findViewById(R.id.settings_phone_field);
            inputRFID = (EditText) findViewById(R.id.settings_rfid_field);
            inputImage = (ImageView) findViewById(R.id.settings_img_user);

            btn_cancel = (Button) findViewById(R.id.button_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Changes canceled", Toast.LENGTH_SHORT).show();
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

        protected void submitForm(){
            super.submitForm();
            updateMember();
        }

        private void updateMember() {

            Toast.makeText(this, "Changes canceled", Toast.LENGTH_SHORT).show();
        }

}
