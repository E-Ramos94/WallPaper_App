package com.enrique.wallpaperspp.FragmentosAdministrador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enrique.wallpaperspp.Adaptador.Adaptador;
import com.enrique.wallpaperspp.Modelo.Administrador;
import com.enrique.wallpaperspp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaAdmin extends Fragment {

    RecyclerView administradores_recyclerview;
    Adaptador adaptador;
    List<Administrador> administradoresList;
    FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_admin, container, false);

        administradores_recyclerview = view.findViewById(R.id.administradores_recyclerview);
        administradores_recyclerview.setHasFixedSize(true);
        administradores_recyclerview.setLayoutManager(new GridLayoutManager(getActivity(),1));
        administradoresList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        ObtenerLista();

        return view;
    }

    private void ObtenerLista() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");
        reference.orderByChild("APELLIDOS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                administradoresList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Administrador administrador = ds.getValue(Administrador.class);
                    //CONDICION PARA QUE LA LISTA SE VISUALICEN TOOS LOS USUARIOS, EXCEPTO EL QUE HA INICIADO SESION
                    assert user != null;
                    assert administrador != null;

                    if (!administrador.getUID().equals(user.getUid())) {
                        administradoresList.add(administrador);
                    }
                    adaptador = new Adaptador(getActivity(), administradoresList);
                    administradores_recyclerview.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}