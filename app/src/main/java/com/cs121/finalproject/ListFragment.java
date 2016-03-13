package com.cs121.finalproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


    ListAdapter adapter1;
    ListView scrollview1;
    List<MenuItem> resultlist1;
    ListAdapter adapter2;
    ListView scrollview2;
    List<MenuItem> resultlist2;
    /*ListAdapter adapter2;
    ListView scrollview;
    List<MenuItem> resultlist;*/
    ArrayList<ArrayList<List<MenuItem>>> allmenus = new ArrayList<ArrayList<List<MenuItem>>>(5);
    ;


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
        String namedininghall = " ";
        String namemeal = " ";

        int howmanyadded = 1;
        if (mParam1 != null) {
            for (int i = 0; i < mParam1.length; i++) {
                int j = -1;
                int k = -1;
                if (mParam1[i] == 1) {
                    if (0 <= i && i <= 2) {
                        //Crown/Merrill
                        j = 0;
                        namedininghall = "Crown/Merrill";
                    }
                    if (3 <= i && i <= 5) {
                        //Cowell/Stevenson
                        j = 1;
                        namedininghall = "Cowell/Stevenson";
                    }
                    if (6 <= i && i <= 8) {
                        //Eight/Oakes
                        j = 2;
                        namedininghall = "Eight/Oakes";
                    }
                    if (9 <= i && i <= 11) {
                        //Nine/Ten
                        j = 3;
                        namedininghall = "Nine/Ten";
                    }
                    if (12 <= i && i <= 15) {
                        //PorterKresge
                        j = 4;
                        namedininghall = "Porter/Kresge";
                    }
                    if ((i + 1) % 3 == 1) {
                        //Breakfast
                        k = 0;
                        namemeal = "Breakfast";
                    }
                    if ((i + 1) % 3 == 2) {
                        //Lunch
                        k = 1;
                        namemeal = "Lunch";
                    }
                    if ((i + 1) % 3 == 0) {
                        //Dinner
                        k = 2;
                        namemeal = "Dinner";
                    }

                    //resultlist = allmenus.get(j).get(k);
                    List<MenuItem> resultlist = allmenus.get(j).get(k);
                    int scrollviewid = -1;

                    if (resultlist != null) {
                        if (!resultlist.isEmpty()) {
                            switch (howmanyadded) {
                                case 1:
                                    scrollviewid = R.id.scrollview1;
                                    break;
                                case 2:
                                    scrollviewid = R.id.scrollview2;
                                    break;
                                case 3:
                                    scrollviewid = R.id.scrollview3;
                                    break;
                                case 4:
                                    scrollviewid = R.id.scrollview4;
                                    break;
                                case 5:
                                    scrollviewid = R.id.scrollview5;
                                    break;
                                case 6:
                                    scrollviewid = R.id.scrollview6;
                                    break;
                                case 7:
                                    scrollviewid = R.id.scrollview7;
                                    break;
                                case 8:
                                    scrollviewid = R.id.scrollview8;
                                    break;
                                case 9:
                                    scrollviewid = R.id.scrollview9;
                                    break;
                                case 10:
                                    scrollviewid = R.id.scrollview10;
                                    break;
                                case 11:
                                    scrollviewid = R.id.scrollview11;
                                    break;
                                case 12:
                                    scrollviewid = R.id.scrollview12;
                                    break;
                                case 13:
                                    scrollviewid = R.id.scrollview13;
                                    break;
                                case 14:
                                    scrollviewid = R.id.scrollview14;
                                    break;
                                case 15:
                                    scrollviewid = R.id.scrollview15;
                                    break;
                                default:
                                    scrollviewid = R.id.scrollview1;
                                    break;
                            }

                            if (!resultlist.get(0).name.equals(namedininghall)) {
                                MenuItem tmp = new MenuItem();

                                tmp.name = namedininghall;
                                tmp.allergens = namemeal;
                                resultlist.add(0, tmp);
                            }

                            Log.d("Testing", "j is: " + j);
                            Log.d("Testing", "k is: " + k);
                            Log.d("Testing", "howmanyadded is: " + howmanyadded);
                            ListAdapter adapter2 = new ListAdapter(getContext(), R.layout.list_element, R.layout.list_meal_header, resultlist, R.layout.list_dining_header, "test");
                            ListView scrollview = (ListView) l.findViewById(scrollviewid);
                            scrollview.setAdapter(adapter2);
                            adapter2.notifyDataSetChanged();


                            int howmanyitems = resultlist.size();
                            int howbig = howmanyitems * (int) getResources().getDimension(R.dimen.item_height);
                            ;
                            Log.d("big", "" + howmanyitems);


                            TextView txt = (TextView) l.findViewById(R.id.listtextview);
                            txt.setVisibility(l.GONE);

                            //Set the heigh programmatically
                            //
                            //Used ideas from http://stackoverflow.com/questions/7441696/how-to-change-list-views-height-progmmatically
                            //
                            ViewGroup.LayoutParams params = scrollview.getLayoutParams();
                            params.height = howbig;
                            scrollview.setLayoutParams(params);
                            scrollview.requestLayout();
                        }
                    }






                /*if (resultlist != null) {
                    adapter2 = new ListAdapter(getContext(), R.layout.list_element, R.layout.list_meal_header, allmenus.get(3).get(2), R.layout.list_dining_header, "test");
                    scrollview = (ListView) l.findViewById(R.id.scrollview2);
                    scrollview.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();
                }*/

                    howmanyadded++;
                }
            }
        }


    }

}
