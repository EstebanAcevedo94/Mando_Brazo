package com.arduino.mando_brazo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControllerActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private int VA = 0;
    private SeekBar sliderPinza, sliderMuneca, sliderAntebrazo, sliderCodo, sliderHombro, sliderBase;
    private TextView textPinza, textMuneca, textAntebrazo, textCodo, textHombro, textBase;
    private AppCompatImageView btn_remote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        databaseReference = FirebaseDatabase.getInstance().getReference("Servo");

        Button btnPinza = findViewById(R.id.btnPinza);
        Button btnMuneca = findViewById(R.id.btnMuneca);
        Button btnAntebrazo = findViewById(R.id.btnAntebrazo);
        Button btnCodo = findViewById(R.id.btnCodo);
        Button btnHombro = findViewById(R.id.btnHombro);
        Button btnBase = findViewById(R.id.btnBase);
        Button btnManual = findViewById(R.id.btn_manual_auto);
        btn_remote = findViewById(R.id.imageRemote);

        // Encuentra las referencias de los SeekBar y TextViews
        sliderPinza = findViewById(R.id.sliderPinza);
        sliderMuneca = findViewById(R.id.sliderMuneca);
        sliderAntebrazo = findViewById(R.id.sliderAntebrazo);
        sliderCodo = findViewById(R.id.sliderCodo);
        sliderHombro = findViewById(R.id.sliderHombro);
        sliderBase = findViewById(R.id.sliderBase);

        textPinza = findViewById(R.id.textPinza);
        textMuneca = findViewById(R.id.textMuneca);
        textAntebrazo = findViewById(R.id.textAntebrazo);
        textCodo = findViewById(R.id.textCodo);
        textHombro = findViewById(R.id.textHombro);
        textBase = findViewById(R.id.textBase);

        // Inicializa los SeekBars con 1500
        inicializarSeekBar(sliderPinza, textPinza, 90, "Pinza");
        inicializarSeekBar(sliderMuneca, textMuneca, 90, "Muneca");
        inicializarSeekBar(sliderAntebrazo, textAntebrazo, 90, "Antebrazo");
        inicializarSeekBar(sliderCodo, textCodo, 90, "Codo");
        inicializarSeekBar(sliderHombro, textHombro, 90, "Hombro");
        inicializarSeekBar(sliderBase, textBase, 90, "Base");



        databaseReference.child("VA").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    VA = dataSnapshot.getValue(Integer.class); // Obtiene el valor de VA desde Firebase
                    if (VA == 7) {
                        btnManual.setText("Cambiar a Manual");
                    } else {
                        btnManual.setText("Cambiar a Auto");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });

        // Asignar OnClickListener a cada botón
        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnManual.getText().toString().trim()=="Auto"){
                    databaseReference.child("VA").setValue(0);
                    btnManual.setText("Cambiar a Manual");
                }else {
                    databaseReference.child("VA").setValue(7);
                    btnManual.setText("Cambiar a Auto");
                }
            }
        });
        btnPinza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderPinza.setProgress(90);
                databaseReference.child("Pinza").setValue(90);
            }
        });

        btnMuneca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderMuneca.setProgress(90);
                databaseReference.child("Muneca").setValue(90);
            }
        });

        btnAntebrazo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderAntebrazo.setProgress(90);
                databaseReference.child("Antebrazo").setValue(90);
            }
        });

        btnCodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderCodo.setProgress(90);
                databaseReference.child("Codo").setValue(90);
            }
        });

        btnHombro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderHombro.setProgress(90);
                databaseReference.child("Hombro").setValue(90);
            }
        });

        btnBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderBase.setProgress(90);
                databaseReference.child("Base").setValue(90);
            }
        });
    }

    private void inicializarSeekBar(final SeekBar seekBar, final TextView textView, int valorInicial, final String servoKey) {
        seekBar.setProgress(valorInicial);
        textView.setText(String.valueOf(valorInicial));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress));

                if (fromUser) { // Solo si el cambio fue hecho por el usuario
                    // Guardar el valor en la base de datos
                    databaseReference.child(servoKey).setValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Solo si el SeekBar es Hombro, Base o Codo
                if (
                        seekBar == sliderBase || seekBar == sliderCodo) {
                    // Cuando se deja de tocar el SeekBar, establece el progreso a 1500 y actualiza en Firebase
                    seekBar.setProgress(90);
                    databaseReference.child(servoKey).setValue(90);
                }
            }
        });
    }
}