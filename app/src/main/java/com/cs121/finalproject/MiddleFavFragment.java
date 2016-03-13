package com.cs121.finalproject;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiddleFavFragment extends Fragment {




    public MiddleFavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ByDiningHallFragment fragment = new ByDiningHallFragment();
        fragmentTransaction.replace(R.id.frag, fragment);
        fragmentTransaction.commit();*/

        //This is the default page
        //ListFragment fragment3 = new ListFragment();

        return inflater.inflate(R.layout.fragment_fav_middle, container, false);
    }

    public void MenusFavview(int choice, int[] intarray, String str) {
        switch (choice) {
            case 0:
                FragmentManager fragmentManager3 = getFragmentManager();
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();

                //ListFragment fragment3 = new ListFragment();
                ListFavFragment fragment3 = ListFavFragment.newInstance(intarray, str);
                fragmentTransaction3.replace(R.id.favfrag, fragment3);
                //fragmentTransaction3.addToBackStack("test");
                fragmentTransaction3.commit();
                break;
        }
    }


}
