package com.example.week9;


import java.util.ArrayList;



public class TheaterList {
    ArrayList<Theaters> theater_list;
    int length;

    public TheaterList() {
        theater_list = new ArrayList<Theaters>();

    }

    public void addTheater(String name, String id) {
        theater_list.add(new Theaters(name, id));
        length++;
    }

    public ArrayList getNames() {
        ArrayList names = new ArrayList();
        for (int i = 0; i < length; i++) {
            String a = theater_list.get(i).getName();
            names.add(a);
        }
        return names;
    }

    public String getId(String theater) {
        String id = null;
        for (int i = 0; i < length; i++) {
            if (theater.contains(theater_list.get(i).getName())) {
                id = theater_list.get(i).id;
            }
        }
        return id;
    }
}
