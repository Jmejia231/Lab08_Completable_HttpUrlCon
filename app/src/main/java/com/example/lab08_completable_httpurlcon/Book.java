package com.example.lab08_completable_httpurlcon;

import android.content.Context;

public class Book {
    String titulo;
    String info;
    String imagen;
    String autor;
    String lenguaje;
    String publicacion;
    private Context ctx;

    public Book(String titulo, String imagen, String autor, String lenguaje, String publicacion,String info,Context ctx) {
        this.ctx=ctx;
        if(imagen!=null){
            imagen = imagen.replace("http","https");
        }
        if(autor.contains("[")){
            autor=autor.replace("[","");
            autor=autor.replace("]","");
            autor=autor.replace("\"","");
        }
        this.titulo = titulo;
        this.imagen = imagen;
        this.autor = ctx.getString(R.string.autor) +" "+autor;
        this.lenguaje = ctx.getString(R.string.idioma)+" "+ lenguaje;
        this.publicacion = ctx.getString(R.string.publicacion)+" "+publicacion;
        this.info=info;
    }
}