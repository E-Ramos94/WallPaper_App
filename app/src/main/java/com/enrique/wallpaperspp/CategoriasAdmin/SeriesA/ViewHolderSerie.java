package com.enrique.wallpaperspp.CategoriasAdmin.SeriesA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enrique.wallpaperspp.R;
import com.squareup.picasso.Picasso;

public class ViewHolderSerie extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderSerie.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);     //ADMIN PRESIONA NORMAL EL ITEM
        void onItemLongClick(View view, int position); //ADMIN MANTIENE PRESIONADO EL ITEM
    }

    //METODO PARA PODER PRESIONAR UN ITEM
    public void setOnClickListener(ViewHolderSerie.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderSerie(@NonNull View itemView) {
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

    public void SeteoSeries(Context context, String nombre, int vista, String imagen){
        ImageView Imagen_Serie;
        TextView NombreImagen_Serie;
        TextView Vista_Serie;

        //CONEXION CON EL ITEM
        Imagen_Serie = mView.findViewById(R.id.Imagen_Serie);
        NombreImagen_Serie = mView.findViewById(R.id.NombreImagen_Serie);
        Vista_Serie = mView.findViewById(R.id.Vista_Serie);


        NombreImagen_Serie.setText(nombre);

        //CONVERTIR A STRING EL PARAMETRO VISTA
        String vistaString = String.valueOf(vista);

        Vista_Serie.setText(vistaString);

        //CONTROLAR POSIBLES ERRORES
        try {
            //SI LA IMAEN FUE TRAIDA EXISTOSAMENTE
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(Imagen_Serie);
        } catch (Exception e){
            //SI LA IMAGEN NO FUE TRAIDA EXISTOSAMENTE
            Picasso.get().load(R.drawable.categoria).into(Imagen_Serie);
        }
    }
}
