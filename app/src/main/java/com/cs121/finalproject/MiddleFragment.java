package com.cs121.finalproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MiddleFragment extends Fragment {




    public MiddleFragment() {
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
        Menusview(0);

        return inflater.inflate(R.layout.fragment_middle, container, false);
    }

    public void Menusview(int choice) {
        switch (choice) {
            case 0:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ByDiningHallFragment fragment = new ByDiningHallFragment();
                fragmentTransaction.replace(R.id.frag, fragment);
                fragmentTransaction.commit();
                break;
            case 1:
                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();

                ByMealFragment fragment2 = new ByMealFragment();
                fragmentTransaction2.replace(R.id.frag, fragment2);
                fragmentTransaction2.commit();
                break;
            case 3:
                FragmentManager fragmentManager3 = getFragmentManager();
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();

                ListFragment fragment3 = new ListFragment();
                fragmentTransaction3.replace(R.id.frag, fragment3);
                fragmentTransaction3.addToBackStack("test");
                fragmentTransaction3.commit();
                break;
        }
    }


}