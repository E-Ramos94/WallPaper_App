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

import com.enrique.wallpaperspp.CategoriasAdmin.MusicaA.AgregarMusica;
import com.enrique.wallpaperspp.CategoriasAdmin.MusicaA.Musica;
import com.enrique.wallpaperspp.CategoriasAdmin.MusicaA.MusicaA;
import com.enrique.wallpaperspp.CategoriasAdmin.MusicaA.ViewHolderMusica;
import com.enrique.wallpaperspp.DetalleCliente.DetalleCliente;
import com.enrique.wallpaperspp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MusicaCliente extends AppCompatActivity {

    RecyclerView recyclerViewMusicaC;
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Musica, ViewHolderMusica> firebaseRecycleAdapter;
    FirebaseRecyclerOptions<Musica> options;

    SharedPreferences sharedPreferences;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musica_cliente);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Musica");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewMusicaC = findViewById(R.id.recyclerViewMusicaC);
        recyclerViewMusicaC.setHasFixedSize(true);

        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("MUSICA");

        dialog = new Dialog(MusicaCliente.this);

        ListarImaganesMusica();
    }

    private void ListarImaganesMusica() {
        options = new FirebaseRecyclerOptions.Builder<Musica>().setQuery(mRef, Musica.class).build();

        firebaseRecycleAdapter = new FirebaseRecyclerAdapter<Musica, ViewHolderMusica>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderMusica holder, int position, @NonNull Musica model) {
                holder.SeteoMusica(
                        getApplicationContext(),
                        model.getNombre(),
                        model.getVistas(),
                        model.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderMusica onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //INFLAR EN ITEM
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musica, parent, false);

                ViewHolderMusica viewHolderMusica = new ViewHolderMusica(itemView);

                viewHolderMusica.setOnClickListener(new ViewHolderMusica.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //OBTENER LOS DATOS DE LA IMAGEN
                        String imagen = getItem(position).getImagen();
                        String Nombres = getItem(position).getNombre();
                        int Vistas = getItem(position).getVistas();
                        //CONVERTIR A STRING VISTAS
                        String VistaString = String.valueOf(Vistas);

                        //PASAMOS A LA ACTIVIDAD DETALLE CLIENTE
                        Intent intent = new Intent(MusicaCliente.this, DetalleCliente.class);

                        //DATOS A PASAR
                        intent.putExtra("Imagen", imagen);
                        intent.putExtra("Nombres", Nombres);
                        intent.putExtra("Vista", VistaString);

                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });


                return viewHolderMusica;
            }
        };

        /*AL INICIAR LA ACTIVIDAD SE VA A LISTAR EN DOS COLUMNAS*/
        sharedPreferences = MusicaCliente.this.getSharedPreferences("MUSICA", MODE_PRIVATE);
        String ordenar_en = sharedPreferences.getString("Ordenar", "Dos");
        int spanorder = 0;

        //ELEGIR TIPO DE VISTA
        if (ordenar_en.equals("Dos")) {
            spanorder = 2;
        } else if (ordenar_en.equals("Tres")){
            spanorder = 3;
        }

        recyclerViewMusicaC.setLayoutManager(new GridLayoutManager(MusicaCliente.this, spanorder));
        firebaseRecycleAdapter.startListening();
        recyclerViewMusicaC.setAdapter(firebaseRecycleAdapter);
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
        Typeface tf = Typeface.createFromAsset(MusicaCliente.this.getAssets(),ubicacion);

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