package msmartds.in.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import  msmartds.in.R;


public class ProgressDialogFragment extends DialogFragment {
    public static final String ARG_TITLE = "Titel";
    public static final String ARG_MESSAGE = "Message";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_view, container);
        TextView tvTitle = view.findViewById(R.id.tvProgress_title);

        TextView tvMessage = view.findViewById(R.id.tvProgress_message);
        String title = requireArguments().getString(ARG_TITLE);
        String message = requireArguments().getString(ARG_MESSAGE);

        assert title != null;
        if (!title.isEmpty())
        tvTitle.setText(title);
        tvMessage.setText(message);
        return view;

    }

    public static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public static void showDialog(ProgressDialogFragment progressDialogFragment, FragmentManager manager){
        progressDialogFragment.show(manager,"Show Progress");
    }

    public static void hideDialog(ProgressDialogFragment progressDialogFragment){
        progressDialogFragment.dismiss();
    }
}