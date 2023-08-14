package com.enrique.wallpaperspp.DetalleCliente;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enrique.wallpaperspp.R;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DetalleCliente extends AppCompatActivity {

    ImageView ImagenDetalle;
    TextView NombreImgenDetalle;
    TextView VistaDetalle;

    FloatingActionButton fabDescargar, fabCompatir, fabEstablecer;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cliente);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImagenDetalle = findViewById(R.id.ImagenDetalle);
        NombreImgenDetalle = findViewById(R.id.NombreImgenDetalle);
        VistaDetalle = findViewById(R.id.VistaDetalle);

        fabDescargar = findViewById(R.id.fabDescargar);
        fabCompatir = findViewById(R.id.fabCompatir);
        fabEstablecer = findViewById(R.id.fabEstablecer);

        String imagen = getIntent().getStringExtra("Imagen");
        String nombres = getIntent().getStringExtra("Nombres");
        String vista = getIntent().getStringExtra("Vista");

        try {
            //SI LA IMAGEN FUE TRAIDA
            Picasso.get().load(imagen).placeholder(R.drawable.detalle_imagen).into(ImagenDetalle);
        } catch (Exception e) {
            //SI LA IMAGEN NO FUE TRAIDA
            Picasso.get().load(R.drawable.detalle_imagen).into(ImagenDetalle);
        }

        NombreImgenDetalle.setText(nombres);
        VistaDetalle.setText(vista);

        fabDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarImagen_11();
            }
        });

        fabCompatir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetalleCliente.this, "Compartir", Toast.LENGTH_SHORT).show();
            }
        });

        fabEstablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetalleCliente.this, "Establcecer", Toast.LENGTH_SHORT).show();
            }
        });

        //EN CASO NO DESCARGUE LA IMAGEN
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());

    }

    private void DescargarImagen() {
        //OBTENER MAPA DE BITS DE LA IMAGEN
        bitmap = ((BitmapDrawable)ImagenDetalle.getDrawable()).getBitmap();
        //OBTENER LA FECHA DE DESCARGA
        String FechaDescarga = new SimpleDateFormat("'Fecha Descarga: ' yyyy_MM_dd 'Hora: ' HH:mm:ss",
                Locale.getDefault()).format(System.currentTimeMillis());

        //DEFINIR LA RUTA DE ALMACENAMIENTO
        File ruta = Environment.getExternalStorageDirectory();
        //DEFINIR EL NMBRE DE LA CARPETA
        File NombreCarpeta = new File(ruta+"/WALLPAPERSAPP/");
        NombreCarpeta.mkdir();
        //DEFINIR EL NOMBRE DE LA IMAGEN DESCARGADA
        String ObtenerNombreImagen = NombreImgenDetalle.getText().toString();
        String NombreImagen = ObtenerNombreImagen +" "+ FechaDescarga + ".JPEG";
        File file = new File(NombreCarpeta, NombreImagen);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "La imagen se ha descargado con exito", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            //Log.e("FILE", e.getMessage());
        }
    }

    private void DescargarImagen_11() {
        bitmap = ((BitmapDrawable)ImagenDetalle.getDrawable()).getBitmap();
        OutputStream fos;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        //Obtener el nombre de la imagen
        String nombre_imagen = NombreImgenDetalle.getText().toString() + "_" + currentDate;

        try {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, nombre_imagen);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "Image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"/WALLPAPERSAPP/");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (imageUri != null) { // Check for null before proceeding
                fos = resolver.openOutputStream(imageUri);
                if (fos != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    Toast.makeText(this, "Imagen descargada con exito", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Image URI creation failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "No se pudo descarar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}