package com.example.lab08_completable_httpurlcon;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText inputBook;
    private List<Book> libros = new ArrayList<>();
    private RVAdapter rvAdapter = null;
    private RecyclerView recyclerView;
    private AlertDialog dialog;
    private Spinner spnLanguage;

    String language = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputBook = (EditText)findViewById(R.id.inputbook);

        spnLanguage = (Spinner)findViewById(R.id.spnLanguage);
    }



    public void searchBook(View view) {

        spnLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: language = "0"; break;
                    case 1: language = "es"; break;
                    case 2: language = "en"; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        String searchString = inputBook.getText().toString();
        if(searchString.isEmpty()|| searchString == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.atencion);
            builder.setMessage(R.string.busqueda);
            builder.setPositiveButton(R.string.aceptar,null);
            dialog = builder.create();
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage(R.string.espere);
            dialog= builder.create();
            dialog.show();
            libros.clear();
            if(recyclerView!=null) {
                recyclerView.setAdapter(null);
            }
            new GetBook().execute(searchString);
        }

    }
    public class GetBook extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            Spinner spinner = (Spinner)findViewById(R.id.spType);
            String val = spinner.getSelectedItem().toString();
            String search="";
            if(val.equals("Libro") || val.equals("Book")){
                search="books";
            }else if(val.equals("Revista") || val.equals("Magazine")){
                search="magazines";
            }else{
                search="all";
            }
            return NetUtilities.getBookInfo(strings[0],search);
        }


        @Override
        protected void onPostExecute(String s){

            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int i = 0;
                String titulo=null;
                String info=null;
                String foto=null;
                String autor=null;
                String lenguaje=null;
                String publicacion=null;
                while( i < itemsArray.length()){
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    JSONObject images= volumeInfo.getJSONObject("imageLinks");
                    try {
                        titulo = volumeInfo.getString("title");
                        info = volumeInfo.getString("infoLink");
                        lenguaje=volumeInfo.getString("language");
                        publicacion=volumeInfo.getString("publishedDate");
                        foto = images.getString("thumbnail");
                        autor = volumeInfo.getString("authors");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    i++;
                    if(autor != null){
                        libros.add(new Book(titulo,foto,autor,lenguaje,publicacion,info,getApplicationContext()));
                    }else{
                        libros.add(new Book(titulo,foto,"No Hay Autores Disponibles",lenguaje,publicacion,info,getApplicationContext()));
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

            recyclerView= (RecyclerView) findViewById(R.id.rv);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            rvAdapter= new RVAdapter(libros,getApplicationContext());
            recyclerView.setAdapter(rvAdapter);
            dialog.dismiss();
        }
    }
}