package com.example.week9;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.R.layout.simple_spinner_dropdown_item;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    ListView listView;
    TheaterList list = new TheaterList();
    Context context = null;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        spinner = (Spinner) findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listview);

        getTheaters();
        startApp();


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovies();
            }
        });

    }

    // get theaters listed
    public void getTheaters() {
        try{
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document doc = dBuilder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " +doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String name = element.getElementsByTagName("Name").item(0).getTextContent();
                    String id = element.getElementsByTagName("ID").item(0).getTextContent();

                    list.addTheater(name, id);
                }
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("###########DONE###########");
        }

    }

    // list theaters in spinner
    public void startApp() {

        ArrayList names = list.getNames();

        spinner = (Spinner) findViewById(R.id.spinner);
        //String[] theaters = new String[]{"Valitse teatteri", "a", "b", "c", "d"};
        ArrayAdapter<Theaters> adapter = new ArrayAdapter<Theaters>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, names);
        adapter.setDropDownViewResource(simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
    // show movies when theater is selected
    public void getMovies() {

        ArrayList<String> data_list = new ArrayList<String>();

        // Change the format of dates, in data format is 2021-03-22'T'21:00:00
        SimpleDateFormat format_in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat format_out = new SimpleDateFormat("HH:mm dd.MM.yyyy");

        Date dt = null;

        //get right id for chosen theater
        String theater = spinner.getSelectedItem().toString();
        String id = list.getId(theater);
        System.out.println(id);


        //set the right date for view
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String date = df.format(d);
        System.out.println(date);

        try {
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "https://www.finnkino.fi/xml/Schedule/?area="+id+"&dt="+date;
            Document doc = dBuilder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
            System.out.println(nList.getLength());

            for (int i = 0; i < nList.getLength() ; i++) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String Title = element.getElementsByTagName("Title").item(0).getTextContent();
                    String start = element.getElementsByTagName("dttmShowStart").item(0).getTextContent();
                    try {
                        dt = format_in.parse(start);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String start_time = format_out.format(dt);
                    String line = Title + "\nStarts at: " + start_time;

                    data_list.add(line);
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(data_list);
            System.out.println("###########DONE###########");
        }


        ArrayAdapter<String> linesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data_list);
        listView.setAdapter(linesAdapter);

    }
}

