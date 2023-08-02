package com.enrique.wallpaperspp.CategoriasAdmin.VideojuegosA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enrique.wallpaperspp.CategoriasAdmin.SeriesA.ViewHolderSerie;
import com.enrique.wallpaperspp.R;
import com.squareup.picasso.Picasso;

public class ViewHolderVideojuego extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderVideojuego.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);     //ADMIN PRESIONA NORMAL EL ITEM
        void onItemLongClick(View view, int position); //ADMIN MANTIENE PRESIONADO EL ITEM
    }

    //METODO PARA PODER PRESIONAR UN ITEM
    public void setOnClickListener(ViewHolderVideojuego.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderVideojuego(@NonNull View itemView) {
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

    public void SeteoVideojuegos(Context context, String nombre, int vista, String imagen){
        ImageView Imagen_Videojuego;
        TextView NombreImagen_Videojuego;
        TextView Vista_Videojuego;

        //CONEXION CON EL ITEM
        Imagen_Videojuego = mView.findViewById(R.id.Imagen_Videojuego);
        NombreImagen_Videojuego = mView.findViewById(R.id.NombreImagen_Videojuego);
        Vista_Videojuego = mView.findViewById(R.id.Vista_Videojuego);


        NombreImagen_Videojuego.setText(nombre);

        //CONVERTIR A STRING EL PARAMETRO VISTA
        String vistaString = String.valueOf(vista);

        Vista_Videojuego.setText(vistaString);

        //CONTROLAR POSIBLES ERRORES
        try {
            //SI LA IMAEN FUE TRAIDA EXISTOSAMENTE
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(Imagen_Videojuego);
        } catch (Exception e){
            //SI LA IMAGEN NO FUE TRAIDA EXISTOSAMENTE
            Picasso.get().load(R.drawable.categoria).into(Imagen_Videojuego);
        }
    }
}
