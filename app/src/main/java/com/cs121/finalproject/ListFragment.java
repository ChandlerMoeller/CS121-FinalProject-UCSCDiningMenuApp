package com.cs121.finalproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int[] mParam1;
    private String mParam2;


    ListAdapter adapter2;
    ListView scrollview;
    List<MenuItem> resultlist;
    ArrayList<ArrayList<List<MenuItem>>> allmenus = new ArrayList<ArrayList<List<MenuItem>>>(5);;


    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(int[] param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getIntArray(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View l = inflater.inflate(R.layout.fragment_list, container, false);

        allmenus = ((MainActivity) getActivity()).getmenus();
        adapt(l);

        return l;
    }

    public void adapt(View l) {
        //allmenus = ((MainActivity) getActivity()).getmenus();
        resultlist = allmenus.get(3).get(2);

        if (resultlist != null) {
            adapter2 = new ListAdapter(getContext(), R.layout.list_element, R.layout.list_meal_header, allmenus.get(3).get(2), R.layout.list_dining_header, "test");
            scrollview = (ListView) l.findViewById(R.id.scrollview1);
            scrollview.setAdapter(adapter2);
            adapter2.notifyDataSetChanged();
        }
    }

}
