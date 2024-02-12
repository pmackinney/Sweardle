package com.captivepet.sweardle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.captivepet.sweardle.databinding.FragmentExpertBinding;


public class ExpertFragment extends Fragment {

    private FragmentExpertBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentExpertBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        if (binding.pickerUrl.getDisplayedValues() == null) {
//            initLauncher();
//        }
//
//        binding.buttonLaunch.setOnClickListener(launch -> launchItemUrl());
//
//        binding.buttonDelete.setOnClickListener(delete -> deleteSelected());
//
//        binding.buttonEditor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                String name = binding.pickerUrl.getDisplayedValues()[binding.pickerUrl.getValue()];
//                bundle.putString(MainActivity.PASSED_NAME_KEY, name);
//                bundle.putString(MainActivity.PASSED_URL_KEY, MainActivity.lookupPersistUrl(name, getString(R.string.url_default)));
//                NavHostFragment.findNavController(com.captivepet.zombie.FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
//            }
//        });
//
//        binding.checkboxAutolaunch.setOnClickListener(autolaunch -> setAutolaunch((CheckBox) autolaunch));
//
//        if (getArguments() != null &&
//                MainActivity.ADD_NEW_ITEM.equals(getArguments().getString(MainActivity.PASSED_OPERATION_KEY))) {
//            addNewItem(getArguments());
//        }
//    }
//

    ListView dictionary;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // https://abhiandroid.com/ui/listview#gsc.tab=0
        dictionary = requireActivity().findViewById(R.id.listview_wordlist);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.fragment_expert, R.id.dictionaryArray, getResources().getStringArray(R.array.dict));
        dictionary.setAdapter(arrayAdapter);

        binding.buttonClosebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new GameFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_main, newFragment);
                fragmentTransaction.addToBackStack(MainActivity.GAME_SERVICE);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
