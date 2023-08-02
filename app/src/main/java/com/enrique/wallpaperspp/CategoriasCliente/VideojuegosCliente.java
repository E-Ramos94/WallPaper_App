package com.enrique.wallpaperspp.CategoriasCliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.enrique.wallpaperspp.CategoriasAdmin.VideojuegosA.AgregarVideojuegos;
import com.enrique.wallpaperspp.CategoriasAdmin.VideojuegosA.Videojuego;
import com.enrique.wallpaperspp.CategoriasAdmin.VideojuegosA.VideojuegosA;
import com.enrique.wallpaperspp.CategoriasAdmin.VideojuegosA.ViewHolderVideojuego;
import com.enrique.wallpaperspp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VideojuegosCliente extends AppCompatActivity {

    RecyclerView recyclerViewVideojuegoC;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Videojuego, ViewHolderVideojuego> firebaseRecycleAdapter;
    FirebaseRecyclerOptions<Videojuego> options;

    SharedPreferences sharedPreferences;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videojuegos_cliente);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Videojuegos");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewVideojuegoC = findViewById(R.id.recyclerViewVideojuegoC);
        recyclerViewVideojuegoC.setHasFixedSize(true);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("VIDEOJUEGO");

        dialog = new Dialog(VideojuegosCliente.this);

        ListarImaganesVideojuego();
    }

    private void ListarImaganesVideojuego() {
        options = new FirebaseRecyclerOptions.Builder<Videojuego>().setQuery(mRef, Videojuego.class).build();

        firebaseRecycleAdapter = new FirebaseRecyclerAdapter<Videojuego, ViewHolderVideojuego>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderVideojuego holder, int position, @NonNull Videojuego model) {
                holder.SeteoVideojuegos(
                        getApplicationContext(),
                        model.getNombre(),
                        model.getVistas(),
                        model.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderVideojuego onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //INFLAR EN ITEM
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videojuego, parent, false);

                ViewHolderVideojuego viewHolderVideojuego = new ViewHolderVideojuego(itemView);

                viewHolderVideojuego.setOnClickListener(new ViewHolderVideojuego.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(VideojuegosCliente.this, "ITEM CLICK", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });


                return viewHolderVideojuego;
            }
        };

        /*AL INICIAR LA ACTIVIDAD SE VA A LISTAR EN DOS COLUMNAS*/
        sharedPreferences = VideojuegosCliente.this.getSharedPreferences("VIDEOJUEGO", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");
        int spanorder = 0;

        //ELEGIR TIPO DE VISTA
        if (ordenar_en.equals("Dos")) {
            spanorder = 2;
        } else if (ordenar_en.equals("Tres")){
            spanorder = 3;
        }

        recyclerViewVideojuegoC.setLayoutManager(new GridLayoutManager(VideojuegosCliente.this, spanorder));
        firebaseRecycleAdapter.startListening();
        recyclerViewVideojuegoC.setAdapter(firebaseRecycleAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecycleAdapter!= null){
            firebaseRecycleAdapter.startListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_vista, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Vista) {
            Ordenar_Imagenes();
        }
        return super.onOptionsItemSelected(item);
    }

    private void Ordenar_Imagenes(){
        //CAMBIO DE LETRA
        String ubicacion = "fuentes/sans_negrita.ttf";
        Typeface tf = Typeface.createFromAsset(VideojuegosCliente.this.getAssets(),ubicacion);

        //DECLARAMOS LAS VISTAS
        TextView OrdenarTXT;
        Button Dos_Columnas, Tres_Columnas;

        //CONEXION CON EL CUADRO DE DIALOGO
        dialog.setContentView(R.layout.dialog_ordenar);

        //INICIALIZAR LAS VISTAS
        OrdenarTXT = dialog.findViewById(R.id.OrdenarTXT);
        Dos_Columnas = dialog.findViewById(R.id.Dos_Columnas);
        Tres_Columnas = dialog.findViewById(R.id.Tres_Columnas);

        //CAMBIO DE LA FUENTE DE LETRA
        OrdenarTXT.setTypeface(tf);
        Dos_Columnas.setTypeface(tf);
        Tres_Columnas.setTypeface(tf);

        //EVENTO DOS COLUMNAS
        Dos_Columnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Ordenar", "Dos");
                editor.apply();
                recreate();
                dialog.dismiss();
            }
        });

        //EVENTO TRES COLUMNAS
        Tres_Columnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Ordenar", "Tres");
                editor.apply();
                recreate();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}