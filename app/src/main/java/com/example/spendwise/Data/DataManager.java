package com.example.spendwise.Data;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.spendwise.R;

public class DataManager {

    public static List<Integer> iconList = new ArrayList<>(Arrays.asList(

            R.drawable.cash_payment,
            R.drawable.card_payment,
            R.drawable.airplane,
            R.drawable.black_clothes,
            R.drawable.green_present,
            R.drawable.credit_card,
            R.drawable.medical_heart,
            R.drawable.medicine,
            R.drawable.pet_shop,
            R.drawable.present,
            R.drawable.scissors,
            R.drawable.sneakers,
            R.drawable.book,
            R.drawable.clothes,
            R.drawable.food,
            R.drawable.glasses,
            R.drawable.fast_food,
            R.drawable.drink,
            R.drawable.shopping_cart,
            R.drawable.restaurant
    ));

    public static List<Integer> getIconList() {
        return iconList;
    }
}


