package com.example.digitale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NonNls;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Sélection du bouton et du texte
        TextView msg_txt = findViewById(R.id.txt_msg);
        Button login_btn = findViewById(R.id.login_btn);

        //Création d'un Manager de biométrique et check si l'utilisateur peut utiliser le capteur d'empreinte digitale ou non
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){ //changement de constante pour vérifier différents cas
            case BiometricManager.BIOMETRIC_SUCCESS: //Dans le cas ou l'authentification est possible
                msg_txt.setText("Vous pouvez utiliser le capteur pour vous identifier");
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: //Test afin de vérifier si le téléphone n'as pas de capteurs digital
                msg_txt.setText("L'appareil de possède pas de capteurs biométriques");
                login_btn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                msg_txt.setText("Le capteur biométrique est actuellement indisponible");
                login_btn.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                msg_txt.setText("Votre appareil ne possède aucune empreinte d'enregistré, veuillez en rentrez une dans vos paramètres de sécurité");
                login_btn.setVisibility(View.GONE);
                break;
        }

        //Création de la boîte de dialogue biométrique

        //Création d'un executable
        Executor executor = ContextCompat.getMainExecutor( context: this);

        //Création d'une fonction de rappel qui va nous donner le résultat de l'authentification et de la possibilité d'authentification ou non
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,executor,new BiometricPrompt.AuthenticationCallback(){
            @Override //Methode appelée quand une erreur dans une erreur dans l'authentification survient
            public void onAuthentificationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override //Methode appelée quand l'authentification est réussie
            public void onAuthentificationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),"Authentification réussie", Toast.LENGTH_SHORT).show();
            }

            @Override //Methode appelée quand l'authentification ne réussit pas
            public void onAuthentificationFailed() {
                super.onAuthenticationFailed();
            }
        });

        //Création du dialogue biométrique
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Utiliser votre empreinte pour vous authentifier")
                .setNegativeButtonText("Cancel")
                .build();

        //Appel du dialogue quand le boutton Login est pressé
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

    }
}