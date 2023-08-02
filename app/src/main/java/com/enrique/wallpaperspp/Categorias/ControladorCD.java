package com.enrique.wallpaperspp.Categorias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.enrique.wallpaperspp.CategoriasCliente.MusicaCliente;
import com.enrique.wallpaperspp.CategoriasCliente.PeliculasCliente;
import com.enrique.wallpaperspp.CategoriasCliente.SeriesCliente;
import com.enrique.wallpaperspp.CategoriasCliente.VideojuegosCliente;
import com.enrique.wallpaperspp.R;

public class ControladorCD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlador_cd);

        String CategoriaRecuperada =  getIntent().getStringExtra("Categoria");

        if (CategoriaRecuperada.equals("Peliculas")) {
            startActivity(new Intent(ControladorCD.this, PeliculasCliente.class));
            finish();
        } else if (CategoriaRecuperada.equals("Series")) {
            startActivity(new Intent(ControladorCD.this, SeriesCliente.class));
            finish();
        } else if (CategoriaRecuperada.equals("Musica")) {
            startActivity(new Intent(ControladorCD.this, MusicaCliente.class));
            finish();
        } else if (CategoriaRecuperada.equals("Videojuegos")) {
            startActivity(new Intent(ControladorCD.this, VideojuegosCliente.class));
            finish();
        }
    }
}