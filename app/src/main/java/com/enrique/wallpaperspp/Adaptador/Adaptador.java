package com.enrique.wallpaperspp.Adaptador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enrique.wallpaperspp.Detalle.Detalle_Administrador;
import com.enrique.wallpaperspp.FragmentosAdministrador.PerfilAdmin;
import com.enrique.wallpaperspp.Modelo.Administrador;
import com.enrique.wallpaperspp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyHolder> {

    private Context context;
    private List<Administrador> administradores;

    public Adaptador(Context context, List<Administrador> administradores) {
        this.context = context;
        this.administradores = administradores;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //INFLAR EL ADMIN_LAYOUT
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //OBTENEMOS LOS DATO DEL MODELO
        String UID = administradores.get(position).getUID();
        String IMAGEN = administradores.get(position).getIMAGEN();
        String NOMBRES = administradores.get(position).getNOMBRES();
        String APELLIDOS = administradores.get(position).getAPELLIDOS();
        String CORREO = administradores.get(position).getCORREO();
        int EDAD = administradores.get(position).getEDAD();
        String EdadString = String.valueOf(EDAD);

        //SETEO DE DATOS
        holder.NombresADMIN.setText(NOMBRES);
        holder.CorreoADMIN.setText(CORREO);

        try {
            //SI EXISTE LA IMAGEN EN LA BD
            Picasso.get().load(IMAGEN).placeholder(R.drawable.admin_item).into(holder.PerfilADMIN);
        } catch (Exception e){
            //SI NO EXISTE LA IMAGEN EN LA BD
            Picasso.get().load(R.drawable.admin_item).into(holder.PerfilADMIN);
        }

        //AL HACER CLICK EN UN ADMINISTRADOR
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Detalle_Administrador.class);
                //PASAR DATOS A LA SIGUIENTE ACTIVIDAD
                intent.putExtra("UID", UID);
                intent.putExtra("NOMBRES", NOMBRES);
                intent.putExtra("APELLIDOS", APELLIDOS);
                intent.putExtra("CORREO", CORREO);
                intent.putExtra("EDAD", EdadString);
                intent.putExtra("IMAGEN", IMAGEN);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return administradores.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        //DECLARAMOS LAS VISTAS
        CircleImageView PerfilADMIN;
        TextView NombresADMIN, CorreoADMIN;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            PerfilADMIN = itemView.findViewById(R.id.PerfilADMIN);
            NombresADMIN = itemView.findViewById(R.id.NombresADMIN);
            CorreoADMIN = itemView.findViewById(R.id.CorreoADMIN);

        }
    }
}
