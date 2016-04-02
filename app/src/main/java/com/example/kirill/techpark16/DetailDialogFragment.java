package com.example.kirill.techpark16;

;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kirill on 02.04.16
 */
public class DetailDialogFragment extends Fragment {
    public static String DIALOG_NO = "dialog_no";

    public static DetailDialogFragment getInstance(int dialog_no){
        DetailDialogFragment detailDialogFragment = new DetailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_NO, dialog_no);
        detailDialogFragment.setArguments(bundle);
        return detailDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detail_fragment,null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView textView = (TextView)view.findViewById(R.id.dialog_detail_texview);
        textView.setText("Test Text");
    }
}
