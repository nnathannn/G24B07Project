package com.example.smartair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddProviderFragment extends Fragment {

    private String parentId;
    FirebaseAuth myauth = FirebaseAuth.getInstance();

    public AddProviderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentId = myauth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_provider, container, false);
        EditText inputChildUname = view.findViewById(R.id.inputChildUname);
        Button addProviderButton = view.findViewById(R.id.add_provider_button);
        addProviderButton.setOnClickListener(v -> {
            String childUname = inputChildUname.getText().toString();
            if(childUname.isEmpty()){
                inputChildUname.setError("Please enter a child username");
                return;
            }
            else{
                checkUname(childUname);
            }
        });
        return view;
    }

    private void checkUname(String Uname){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("parent-users")
                .child(parentId).child("child-ids");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String childId = ds.getKey();
                    checkChild(childId, Uname);
                }
//                Toast.makeText(getContext(), "No child matches the username.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });

    }

    private void checkChild(String childId, String Uname){
        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("child-users")
                .child(childId);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String childName = dataSnapshot.child("name").getValue(String.class);
                if (childName!=null && childName.equals(Uname)) {
                    createToken(childId, Uname);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.parent_frame_layout, new ManageProviderAccessFragment());
                    fragmentTransaction.commit();
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }

    private void createToken(String childId, String Uname){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("invite-code");
        String token = ref.push().getKey();
        ref.child(parentId).child(token).child("child-id").setValue(childId);
        ref.child(parentId).child(token).child("child-name").setValue(Uname);
        ref.child(parentId).child(token).child("code").setValue(token);
        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = currentDate.plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expiryDate = futureDate.format(formatter);
        ref.child(parentId).child(token).child("end-date").setValue(expiryDate);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Invite Code Generated for" + Uname);
        builder.setMessage("The provider invite code for " + Uname + " is " + token + ". You can see it in the pending invite code page.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}