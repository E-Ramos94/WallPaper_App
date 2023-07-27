package com.enrique.wallpaperspp.FragmentosAdministrador;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enrique.wallpaperspp.MainActivityAdministrador;
import com.enrique.wallpaperspp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

import java.util.HashMap;
import java.util.Objects;

public class PerfilAdmin extends Fragment {

    //FIREBASE
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference BASE_DE_DATOS_ADMINISTRADORES;

    StorageReference storageReference;
    String RutaDeAlmacenamiento = "Foto_Perfil_Adminitradores/*";


    //PERMISOS A SOLICITAR

    private Uri imagen_uri;
    private String imagen_perfil;
    private ProgressDialog progressDialog;

    //VISTAS
    ImageView FOTOPERFILIMG;
    TextView UIDPERFIL, NOMBREPERFIL, APELLIDOPERFIL, CORREOPERFIL, PASSWORDPERFIL, EDADPERFIL;
    Button ACTUALIZARPASS, ACTUALIZARDATOS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        FOTOPERFILIMG = view.findViewById(R.id.FOTOPERFILIMG);

        UIDPERFIL = view.findViewById(R.id.UIDPERFIL);
        NOMBREPERFIL = view.findViewById(R.id.NOMBREPERFIL);
        APELLIDOPERFIL = view.findViewById(R.id.APELLIDOPERFIL);
        CORREOPERFIL = view.findViewById(R.id.CORREOPERFIL);
        PASSWORDPERFIL = view.findViewById(R.id.PASSWORDPERFIL);
        EDADPERFIL = view.findViewById(R.id.EDADPERFIL);

        ACTUALIZARPASS = view.findViewById(R.id.ACTUALIZARPASS);
        ACTUALIZARDATOS = view.findViewById(R.id.ACTUALIZARDATOS);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storageReference = getInstance().getReference();


        progressDialog = new ProgressDialog(getActivity());

        BASE_DE_DATOS_ADMINISTRADORES = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");

        BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //OBTENER LOS DATOS

                    String uid = ""+snapshot.child("UID").getValue();
                    String nombre = ""+snapshot.child("NOMBRES").getValue();
                    String apellidos = ""+snapshot.child("APELLIDOS").getValue();
                    String correo = ""+snapshot.child("CORREO").getValue();
                    String pass = ""+snapshot.child("PASSWORD").getValue();
                    String edad = ""+snapshot.child("EDAD").getValue();
                    String imagen = ""+snapshot.child("IMAGEN").getValue();

                    UIDPERFIL.setText(uid);
                    NOMBREPERFIL.setText(nombre);
                    APELLIDOPERFIL.setText(apellidos);
                    CORREOPERFIL.setText(correo);
                    PASSWORDPERFIL.setText(pass);
                    EDADPERFIL.setText(edad);

                    try {
                        //SI EXISTE LA IMAGEN EN BD DEL ADMINISTRADOR
                        Picasso.get().load(imagen).placeholder(R.drawable.perfil).into(FOTOPERFILIMG);
                    } catch (Exception e) {
                        //NO EXISTE IMAGEN EN LA BD DEL ADMINISTRADOR
                        Picasso.get().load(R.drawable.perfil).into(FOTOPERFILIMG);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //NOS DIRIGE A LA ACTIVIDAD CAMBIAR PASSWORD
        ACTUALIZARPASS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Cambio_Pass.class));
                getActivity().finish();
            }
        });

        ACTUALIZARDATOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditarDatos();
            }
        });

        FOTOPERFILIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CambiarImagenPerfilAdministrador();
            }
        });

        return view;
    }

    private void EditarDatos() {
        //MOSTAR UN DIALOG QUE CONTIENE OPCIONES
        //1.- EDITAR NOMBRES
        //2.- EDITAR APELLIDOS
        //3.- EDITAR EDAD
        String [] opciones = {"Editar nombres", "Editar apellidos", "Editar edad"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elegir opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //EDITAR NOMBRES
                    EditarNombres();

                } else if (which == 1) {
                    //EDITAR APELLIDOS
                    EditarApellidos();

                } else if (which == 2) {
                    //EDITAR EDAD
                    EditarEdad();
                }
            }
        });
        builder.create().show();
    }


    private void EditarNombres() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizar informacion");
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getActivity());
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Ingrese nuevo dato ...");
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        linearLayoutCompat.addView(editText);
        builder.setView(linearLayoutCompat);
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoDato = editText.getText().toString().trim();
                if (!nuevoDato.equals("")){
                    HashMap<String, Object> resultado = new HashMap<>();
                    resultado.put("NOMBRES", nuevoDato);
                    BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).updateChildren(resultado)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Dato actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Campo vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancelado por usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void EditarApellidos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizar informacion");
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getActivity());
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Ingrese nuevo dato ...");
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        linearLayoutCompat.addView(editText);
        builder.setView(linearLayoutCompat);
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoDato = editText.getText().toString().trim();
                if (!nuevoDato.equals("")){
                    HashMap<String, Object> resultado = new HashMap<>();
                    resultado.put("APELLIDOS", nuevoDato);
                    BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).updateChildren(resultado)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Dato actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Campo vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancelado por usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void EditarEdad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Actualizar informacion");
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getActivity());
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Ingrese nuevo dato ...");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayoutCompat.addView(editText);
        builder.setView(linearLayoutCompat);
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoDato = editText.getText().toString().trim();
                if (!nuevoDato.equals("")){
                    int nuevoDatoEntero = Integer.parseInt(nuevoDato);
                    HashMap<String, Object> resultado = new HashMap<>();
                    resultado.put("EDAD", nuevoDatoEntero);
                    BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).updateChildren(resultado)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Dato actualizado", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Campo vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancelado por usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void CambiarImagenPerfilAdministrador() {
        String [] opcion = {"Cambiar foto de perfil"};
        //crear el alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elegir una opcion");
        builder.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    imagen_perfil = "IMAGEN";
                    ElegirFoto();
                }
            }
        });

        builder.create().show();

    }

    /*ELEGIR DE DONDE PROCEDE LA IMAGEN*/
    private void ElegirFoto() {
        String [] opciones = {"Cámara", "Galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccionar imagen de: ");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Elegir_De_Camara();
                    } else {
                        SolicitudPermisoCamara.launch(Manifest.permission.CAMERA);
                    }
                } else if (which == 1) {
                    Elegir_De_Galeria();
                }
            }
        });

        builder.create().show();
    }

    private void Elegir_De_Galeria() {
        Intent GaleriaIntent = new Intent(Intent.ACTION_PICK);
        GaleriaIntent.setType("image/*");
        ObtenerImagenGaleria.launch(GaleriaIntent);
    }

    //METODO PARA TOMAR IAGEN DESDE LA CAMARA
    private void Elegir_De_Camara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Foto Temporal");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion Temporal");
        imagen_uri = (requireActivity()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //ACTIVIDAD PARA ABRIR LA CAMARA
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagen_uri);
        ObtenerImagenCamara.launch(camaraIntent);
    }

    //METODO PARA ABRIR LA CAMARA Y REALIZAR LA ACTUALIZACIONES A LA BD
    private ActivityResultLauncher<Intent> ObtenerImagenCamara = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        ActualizarImagenEnBD(imagen_uri);
                        progressDialog.setTitle("Procesando");
                        progressDialog.setMessage("La imagen se esta actualizando, espere por favor...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    } else {
                        Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    //METODO PARA ABRIR LA GALERIA Y REALIZAR LA ACTUALIZACIONES A LA BD
    private ActivityResultLauncher<Intent> ObtenerImagenGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        imagen_uri = data.getData();
                        ActualizarImagenEnBD(imagen_uri);
                        progressDialog.setTitle("Procesando");
                        progressDialog.setMessage("La imagen se esta actualizando, espere por favor...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    } else {
                        Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    //METODO PARA VALIDAR LOS PERMISOS DE ACCESO A LA CAMARA
    private ActivityResultLauncher<String> SolicitudPermisoCamara =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Elegir_De_Camara();
                } else {
                    Toast.makeText(getActivity(), "Permisos denegado", Toast.LENGTH_SHORT).show();
                }
            });

    private void ActualizarImagenEnBD(Uri uri) {
        String Ruta_De_Archivo_y_nombre = RutaDeAlmacenamiento + "" + imagen_perfil + "_" + user.getUid();
        StorageReference storageReference2 = storageReference.child(Ruta_De_Archivo_y_nombre);
        storageReference2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(imagen_perfil, downloadUri.toString());
                            BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            startActivity(new Intent(getActivity(), MainActivityAdministrador.class));
                                            getActivity().finish();
                                            Toast.makeText(getActivity(), "Imagen cambiada con exito", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}