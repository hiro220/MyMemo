package com.example.mymemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class DeleteCheckDialogFlagment extends DialogFragment {
    private static String TAG = "DeleteCheckDialogFlagment";

    public interface DeleteCheckDialogListener {
        // ボタンクリック時の処理のプロトタイプ宣言(処理は使用するアクティビティで定義する)
        public void onDialogPositiveClick(DialogFragment dialog, int i);
        public void onDialogNegativeClick(DialogFragment dialog, int i);
    }

    DeleteCheckDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteCheckDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString() + " must implement DeleteCheckDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // ダイアログを使用するアクティビティからパラメータを受け取る
        Bundle args = Objects.requireNonNull(getArguments());
        final int position = args.getInt("position");
        // ダイアログを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // ダイアログの設定
        builder.setTitle("確認").setMessage("削除しますか？");
        // PositiveButtonクリック時処理
        builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogPositiveClick(DeleteCheckDialogFlagment.this, position);
            }
        });
        // NegativeButtonクリック時処理
        builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogNegativeClick(DeleteCheckDialogFlagment.this, position);
            }
        });

        Log.i(TAG, "削除確認ダイアログの表示");
        return builder.create();
    }

}
