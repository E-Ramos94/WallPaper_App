package com.enrique.wallpaperspp.FragmentosCliente;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enrique.wallpaperspp.Categorias.Cat_Dispositivo.CategoriaD;
import com.enrique.wallpaperspp.Categorias.Cat_Dispositivo.ViewHolderCD;
import com.enrique.wallpaperspp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InicioCliente extends Fragment {

    RecyclerView recycleViewCategoriasD;
    FirebaseDatabase firebaseDatabaseD;
    DatabaseReference referenceD;
    LinearLayoutManager linearLayoutManagerD;

    FirebaseRecyclerAdapter<CategoriaD, ViewHolderCD> firebaseRecyclerAdapterD;
    FirebaseRecyclerOptions<CategoriaD> optionsD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio_cliente, container, false);

        firebaseDatabaseD = FirebaseDatabase.getInstance();
        referenceD = firebaseDatabaseD.getReference("CATEGORIAS_D");
        linearLayoutManagerD = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recycleViewCategoriasD = view.findViewById(R.id.recycleViewCategoriasD);
        recycleViewCategoriasD.setHasFixedSize(true);
        recycleViewCategoriasD.setLayoutManager(linearLayoutManagerD);

        VerCategoriasD();

        return view;
    }

    private void VerCategoriasD() {
        optionsD = new FirebaseRecyclerOptions.Builder<CategoriaD>().setQuery(referenceD, CategoriaD.class).build();
        firebaseRecyclerAdapterD = new FirebaseRecyclerAdapter<CategoriaD, ViewHolderCD>(optionsD) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCD holder, int position, @NonNull CategoriaD model) {
                holder.SeteoCategoriaD(
                        getActivity(),
                        model.getCategoria(),
                        model.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderCD onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //INFLAR EL LAYOUT
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorias_dispositivo, parent, false);
                ViewHolderCD viewHolderCD = new ViewHolderCD(itemView);

                viewHolderCD.setOnClickListener(new ViewHolderCD.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String categoria = getItem(position).getCategoria();
                        Toast.makeText(getActivity(), categoria, Toast.LENGTH_SHORT).show();
                    }
                });

                return viewHolderCD;
            }
        };
        recycleViewCategoriasD.setAdapter(firebaseRecyclerAdapterD);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapterD != null) {
            firebaseRecyclerAdapterD.startListening();
        }
    }
}