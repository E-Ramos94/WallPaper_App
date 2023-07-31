package com.enrique.wallpaperspp.FragmentosAdministrador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    //OBTENER TODA LA LISTA
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

    private void BuscarAdministrador(String consulta) {
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


                        //BUSCAR POR NOMBRE Y CORREO
                        if (administrador.getNOMBRES().toLowerCase().contains(consulta.toLowerCase()) ||
                            administrador.getCORREO().toLowerCase().contains(consulta.toLowerCase())) {
                            administradoresList.add(administrador);
                        }
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

    //CREANDO EL MENU
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_buscar, menu);
        MenuItem item = menu.findItem(R.id.buscar_administrador);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String consulta) {
                //SE LLAMA CUANDO EL USUARUO PRESIONA EL BOTON BUSQUEDA DESDE EL TECLADO
                if (!TextUtils.isEmpty(consulta.trim())){
                    //SI LA CONSULTA DE BUSQUEDA NO ESTA VACIA, ENTONCES QUE BUSQUE
                    BuscarAdministrador(consulta);
                } else {
                    //SI LA BUSQUEDA ES VACIA QUE MUESTRE TODA LA LISTA
                    ObtenerLista();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String consulta) {
                //LA BUSQUEDA SE VA ACTUALIZANDO CONFORME VAMOS ESCRIBIENDO
                if (!TextUtils.isEmpty(consulta.trim())) {
                    BuscarAdministrador(consulta);
                } else {
                    //SI LA BUSQUEDA ES VACIA QUE MUESTRE TODA LA LISTA
                    ObtenerLista();
                }

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    //VISUALIZAR EL MENU
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}