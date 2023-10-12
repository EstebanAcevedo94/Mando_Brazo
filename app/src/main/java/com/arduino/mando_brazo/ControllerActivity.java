package com.arduino.mando_brazo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ControllerActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private SeekBar sliderPinza, sliderMuneca, sliderAntebrazo, sliderCodo, sliderHombro, sliderBase;
    private TextView textPinza, textMuneca, textAntebrazo, textCodo, textHombro, textBase;

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
        inicializarSeekBar(sliderPinza, textPinza, 1500, "Pinza");
        inicializarSeekBar(sliderMuneca, textMuneca, 1500, "Muneca");
        inicializarSeekBar(sliderAntebrazo, textAntebrazo, 1500, "Antebrazo");
        inicializarSeekBar(sliderCodo, textCodo, 1500, "Codo");
        inicializarSeekBar(sliderHombro, textHombro, 1500, "Hombro");
        inicializarSeekBar(sliderBase, textBase, 1500, "Base");

        // Asignar OnClickListener a cada botón
        btnPinza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderPinza.setProgress(1500);
                databaseReference.child("Pinza").setValue(1500);
            }
        });

        btnMuneca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderMuneca.setProgress(1500);
                databaseReference.child("Muneca").setValue(1500);
            }
        });

        btnAntebrazo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderAntebrazo.setProgress(1500);
                databaseReference.child("Antebrazo").setValue(1500);
            }
        });

        btnCodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderCodo.setProgress(1500);
                databaseReference.child("Codo").setValue(1500);
            }
        });

        btnHombro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderHombro.setProgress(1500);
                databaseReference.child("Hombro").setValue(1500);
            }
        });

        btnBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderBase.setProgress(1500);
                databaseReference.child("Base").setValue(1500);
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
                    seekBar.setProgress(1500);
                    databaseReference.child(servoKey).setValue(1500);
                }
            }
        });
    }
}