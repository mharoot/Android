package com.example.michael.shoppingcartmvc;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by michael on 1/28/17.
 *
 * Purpose
 * ------------------------------------------------------
 * Controller class extends with android.app.Application and defined in the application tag in your
 * AndroidManifest.xml file. Android will create an instance of Controller class and make it
 * available for your entire application context. You can get object of your class on any activity /
 * broadcast receiver / service in application context(environment) by
 * Context.getApplicationContext() method.
 *
 * Controller class to interact with models and provide values to views.
 *
 * Create a ModelProducts type Arraylist to store ModelProducts instances.
 * Create a ModelCart type reference to store ModelCart instance to interact with ModelCart.
 */


public class Controller extends Application {
    private ArrayList<ModelProducts> myProducts = new ArrayList<ModelProducts>();
    private  ModelCart myCart = new ModelCart();


    public ModelProducts getProducts(int pPosition) {

        return myProducts.get(pPosition);
    }

    public void setProducts(ModelProducts Products) {

        myProducts.add(Products);

    }

    public ModelCart getCart() {

        return myCart;

    }

    public int getProductsArraylistSize() {

        return myProducts.size();
    }

}
