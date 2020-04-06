package com.example.edenappbeta.NotifiSlide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.edenappbeta.R;

import java.util.ArrayList;

public class NotifiSlideFragment extends Fragment {

    public ArrayList<String> arrayList=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.notifi_slide, container,false);
/*
        ListView idAlerty = rootView.findViewById(R.id.alerty);
        arrayList.add("cos");

        ArrayAdapter arrayAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,arrayList);
        idAlerty.setAdapter(arrayAdapter);


 */

        return rootView;
    }
}
