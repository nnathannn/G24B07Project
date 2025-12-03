//package com.example.smartair;
//
//
//// TODO: REMOVE LATER
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//class TempFragment extends Fragment {
//    private String childId;
//
//    private void checkUser() {
//        if (getArguments() == null) {
//            childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        } else {
//            childId = getArguments().getString("childId");
//        }
//    }
//}
//
//class TempActivity extends AppCompatActivity {
//    private String childId;
////    public String getUid() { return childId; }
//
//    // receive intent
//    private void checkUser() {
//        if (getIntent().getStringExtra("childId") == null) {
//            childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        } else {
//            childId = getIntent().getStringExtra("childId");
//        }
//    }
//}
//
//class SendBundle {
//    private String childId;
//
//    private void loadFragment(Fragment fragment) {
//        Bundle bundle = new Bundle();
//        bundle.putString("childId", childId);
//        fragment.setArguments(bundle);
//
//        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragmentContainerView, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
//
////    buttonDetail.setOnClickListener(v -> {
////        Intent intent = new Intent(this, Activity.class);
////        intent.putExtra("childId", childId);
////        startActivity(intent);
////    });
//}