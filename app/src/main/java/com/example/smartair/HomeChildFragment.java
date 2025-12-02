package com.example.smartair;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeChildFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter badgeAdapter;
    private List<Item> itemList;
    private String childId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        checkUser();

        //Copy the code up until fetchData() and change with respective view holders for recycler view
        recyclerView = view.findViewById(R.id.badgeRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));

        itemList = new ArrayList<>();
        badgeAdapter = new BadgeAdapter(itemList);
        recyclerView.setAdapter(badgeAdapter);

        ItemAdapter.fetchData(badgeAdapter, "badge");

        Button buttonTriage = view.findViewById(R.id.buttonTriage);
        Button buttonMedicine = view.findViewById(R.id.buttonMedicine);
        Button buttonDaily = view.findViewById(R.id.buttonDaily);
        Button buttonTechnique = view.findViewById(R.id.buttonTechnique);
        Button buttonPEF = view.findViewById(R.id.buttonPEF);
        Button buttonProfile = view.findViewById(R.id.buttonProfile);

        buttonTriage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new TriageFragment());
            }
        });

        buttonMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new MedicineFragment());
            }
        });

        buttonDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new SymptomFragment()); }
        });

        buttonTechnique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new TechniqueFragment());
            }
        });

//        buttonPEF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadFragment(new PEFFragment());
//            }
//        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment());
            }
        });

        return view;
    }

    private void checkUser() {
        if (getArguments() == null) {
            childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            childId = getArguments().getString("childId");
        }
    }

    private void loadFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("childId", childId);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}