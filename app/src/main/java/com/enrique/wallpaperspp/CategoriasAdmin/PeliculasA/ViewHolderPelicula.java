package com.enrique.wallpaperspp.CategoriasAdmin.PeliculasA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enrique.wallpaperspp.R;
import com.squareup.picasso.Picasso;

public class ViewHolderPelicula extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderPelicula.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);     //ADMIN PRESIONA NORMAL EL ITEM
        void onItemLongClick(View view, int position); //ADMIN MANTIENE PRESIONADO EL ITEM
    }

    //METODO PARA PODER PRESIONAR UN ITEM
    public void setOnClickListener(ViewHolderPelicula.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderPelicula(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });
    }

    public void SeteoPeliculas(Context context, String nombre, int vista, String imagen){
        ImageView ImagenPelicula;
        TextView NombreImagenPelicula;
        TextView VistaPelicula;

        //CONEXION CON EL ITEM
        ImagenPelicula = mView.findViewById(R.id.ImagenPelicula);
        NombreImagenPelicula = mView.findViewById(R.id.NombreImagenPelicula);
        VistaPelicula = mView.findViewById(R.id.VistaPelicula);


        NombreImagenPelicula.setText(nombre);

        //CONVERTIR A STRING EL PARAMETRO VISTA
        String vistaString = String.valueOf(vista);

        VistaPelicula.setText(vistaString);

        //CONTROLAR POSIBLES ERRORES
        try {
            //SI LA IMAEN FUE TRAIDA EXISTOSAMENTE
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(ImagenPelicula);
        } catch (Exception e){
            //SI LA IMAGEN NO FUE TRAIDA EXISTOSAMENTE
            Picasso.get().load(R.drawable.categoria).into(ImagenPelicula);
        }
    }
}
