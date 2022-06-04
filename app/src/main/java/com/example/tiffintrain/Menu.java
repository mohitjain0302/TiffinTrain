package com.example.tiffintrain;

import java.util.ArrayList;

public class Menu {
    private String menuName ;
    private int menuRate ;
    private ArrayList<String> menuItems ;

    public Menu(){

    }

    public Menu(String menuName , int menuRate , ArrayList<String> menuItems){
        this.menuName = menuName ;
        this.menuRate = menuRate ;
        this.menuItems = menuItems ;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getMenuRate() {
        return menuRate;
    }

    public ArrayList<String> getMenuItems() {
        return menuItems;
    }
}
