package com.example.pdfview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class dialogFragment extends DialogFragment {
    public static int createReportVer = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createReportVer = 0;
        final String[] items = {"案件全体で作成", "個人で作成"};
        /*final ArrayList<String> array = new ArrayList<>();
        array.add("sample1");
        array.add("sample1");
        array.add("sample1");
        String[] arrays = array.toArray(new String[array.size()]);*/
        //final String[] items2 = {array.get(0), array.get(1), array.get(2)};*/
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        return new AlertDialog.Builder(getActivity())
                .setTitle("報告書作成範囲を指定してください")
                .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            Log.d("checkedItem:", "" + checkedItems.get(0));
                            createReportVer = checkedItems.get(0);
                            //アラートダイアログの出力
                            dialogFragmentDate dfd = new dialogFragmentDate();
                            dfd.show(getFragmentManager(),"selectingDate");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }
}
