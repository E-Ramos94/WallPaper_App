package com.enrique.wallpaperspp.CategoriasAdmin.VideojuegosA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enrique.wallpaperspp.CategoriasAdmin.SeriesA.AgregarSerie;
import com.enrique.wallpaperspp.CategoriasAdmin.SeriesA.Serie;
import com.enrique.wallpaperspp.CategoriasAdmin.SeriesA.SeriesA;
import com.enrique.wallpaperspp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class AgregarVideojuegos extends AppCompatActivity {

    TextView VistaVideojuego;
    EditText NombreVideojuego;
    ImageView ImagenAgregarVideojuego;
    Button PublicarVideojuego;

    String RutaDeAlmacenamiento = "Videojuego_Subida/";
    String RutaDeBaseDeDatos = "VIDEOJUEGO";
    Uri RutaArchivoUri;

    StorageReference mStrorageReference;
    DatabaseReference DatabaseReference;

    ProgressDialog progressDialog;

    String rNombre, rImagen, rVista;

    int CODIGO_DE_SOLICITUD_IMAGEN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_videojuegos);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        VistaVideojuego = findViewById(R.id.VistaVideojuego);
        NombreVideojuego = findViewById(R.id.NombreVideojuego);
        ImagenAgregarVideojuego = findViewById(R.id.ImagenAgregarVideojuego);
        PublicarVideojuego = findViewById(R.id.PublicarVideojuego);

        mStrorageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference = FirebaseDatabase.getInstance().getReference(RutaDeBaseDeDatos);
        progressDialog = new ProgressDialog(AgregarVideojuegos.this);

        Bundle intent = getIntent().getExtras();
        if (intent != null){

            //Recuperar los datos de la actividad anterior
            rNombre = intent.getString("NombreEnviado");
            rImagen = intent.getString("ImagenEnviada");
            rVista = intent.getString("VistaEnviada");

            //setear
            NombreVideojuego.setText(rNombre);
            VistaVideojuego.setText(rVista);
            Picasso.get().load(rImagen).into(ImagenAgregarVideojuego);

            //cambiar el nombre actionbar
            actionBar.setTitle("Actualizar");
            String actuaizar = "Actualizar";
            //cambiar el nombre del boton
            PublicarVideojuego.setText(actuaizar);
        }

        ImagenAgregarVideojuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SDK <= 30
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"),CODIGO_DE_SOLICITUD_IMAGEN);*/

                //SDK >= 31
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                ObtenerImagenGaleria.launch(intent);
            }
        });

        PublicarVideojuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PublicarVideojuego.getText().equals("Publicar")) {
                    /*metodo para subir una imagen*/
                    SubirImagen();
                } else {
                    /*metodo para actualizar una imagen*/
                    EmpezarActualizacion();
                }
            }
        });
    }

    private void EmpezarActualizacion() {
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        EliminarImgenAnterior();
    }

    private void EliminarImgenAnterior() {
        StorageReference Imagen = getInstance().getReferenceFromUrl(rImagen);
        Imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Si la imagen se elimino
                Toast.makeText(AgregarVideojuegos.this, "La imagen anterior ha sido eliminada", Toast.LENGTH_SHORT).show();
                SubirNuevaImagen();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //GESTIONAR POSIBLE ERROR
                Toast.makeText(AgregarVideojuegos.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void SubirNuevaImagen() {
        //DECLARAMOS LA NUEVA IMAGEN A ACTUALIZAR
        String nuevaImagen = System.currentTimeMillis() + ".png";
        /*referecia de almacenamiento, para que la nueva imagen se pueda guardar en esa carpeta*/
        StorageReference mstorageReference2 = mStrorageReference.child(RutaDeAlmacenamiento + nuevaImagen);
        /*obtener mapa de bits de la nueva imagen seleccionada*/
        Bitmap bitmap = ((BitmapDrawable)ImagenAgregarVideojuego.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        /*comprimir imagen*/
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte [] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = mstorageReference2.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarVideojuegos.this, "Nueva imagen cargada", Toast.LENGTH_SHORT).show();
                /*obtener la URL de la imagen recien cargada*/
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                ActualizarImagenBD(downloadUri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarVideojuegos.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarImagenBD(final String NuevaImagen) {
        final String nombreActualizar = NombreVideojuego.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("VIDEOJUEGO");

        //CONSULTA
        Query query = databaseReference.orderByChild("nombre").equalTo(rNombre);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //DATOS A ACTUALIZAR
                for (DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().child("nombre").setValue(nombreActualizar);
                    ds.getRef().child("imagen").setValue(NuevaImagen);
                }
                progressDialog.dismiss();
                Toast.makeText(AgregarVideojuegos.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AgregarVideojuegos.this, VideojuegosA.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SubirImagen() {

        String mNombre = NombreVideojuego.getText().toString();

        //Validar que el nombre y la imagen no sean nulos
        if (mNombre.equals("") || RutaArchivoUri==null){
            Toast.makeText(this, "Asigne un nombre o una imagen", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Espere porfavor");
            progressDialog.setMessage("Subiendo Imagen VIDEOJUEGO...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StorageReference storageReference2 = mStrorageReference.child(RutaDeAlmacenamiento + System.currentTimeMillis()+ "."
                    + ObtenerExtensionDelArchivo(RutaArchivoUri));
            //25455_1234.PNG
            storageReference2.putFile(RutaArchivoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    Uri downloadURI = uriTask.getResult();


                    String mVista = VistaVideojuego.getText().toString();
                    int VISTA = Integer.parseInt(mVista);

                    Videojuego videojuego = new Videojuego(downloadURI.toString(), mNombre, VISTA);
                    String ID_IMAGEN = DatabaseReference.push().getKey();

                    DatabaseReference.child(ID_IMAGEN).setValue(videojuego);

                    progressDialog.dismiss();
                    Toast.makeText(AgregarVideojuegos.this, "Subido Exitosamente", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AgregarVideojuegos.this, VideojuegosA.class));
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AgregarVideojuegos.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setTitle("Publicando");
                    progressDialog.setCancelable(false);
                }
            });
        }
    }

    //obtenemos la extension .jpg / .png
    private String ObtenerExtensionDelArchivo(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    //SDK <= 30
    //COMPROBAR SI LA IMAGEN SELECCIONADA POR EL ADMINISTRADOR FUE CORRECTA
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CODIGO_DE_SOLICITUD_IMAGEN
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            RutaArchivoUri = data.getData();

            try {
                //CONVERTIMOS A BITMAP
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), RutaArchivoUri);
                //SETEAMOS LA IMAGEN
                ImagenAgregarVideojuego.setImageBitmap(bitmap);

            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    //SDK >= 31
    //Obtener imagen de galeria
    private ActivityResultLauncher<Intent> ObtenerImagenGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //manejar el resultado de nuestro intent
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Seleccion de imagen
                        Intent data = result.getData();
                        //Obtener uri de imagen
                        RutaArchivoUri = data.getData();
                        ImagenAgregarVideojuego.setImageURI(RutaArchivoUri);
                    } else {
                        Toast.makeText(AgregarVideojuegos.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}