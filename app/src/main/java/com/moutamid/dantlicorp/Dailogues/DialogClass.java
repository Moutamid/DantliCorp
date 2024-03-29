package com.moutamid.dantlicorp.Dailogues;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moutamid.dantlicorp.R;

public class DialogClass extends Dialog {

    public Activity c;
    Button button;
    ImageView imgBack;
    String name, url;

    public DialogClass(Activity a, String name, String url) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.name = name;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.deposit_dailogue);
        imgBack = findViewById(R.id.imgBack);
        button = findViewById(R.id.click_here);
        TextView name_tx = findViewById(R.id.name);
        name_tx.setText(name);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                c.startActivity(intent);
                dismiss();

            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


}