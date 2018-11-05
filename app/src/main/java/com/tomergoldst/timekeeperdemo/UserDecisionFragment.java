package com.tomergoldst.timekeeperdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

public class UserDecisionFragment extends DialogFragment {

    private static final String TAG = UserDecisionFragment.class.getSimpleName();

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_MESSAGE = "arg_message";
    private static final String ARG_CANCELABLE = "arg_cancelable";
    private static final String ARG_POSITIVE_BUTTON_TEXT = "arg_positive_button_text";
    private static final String ARG_NEGATIVE_BUTTON_TEXT = "arg_negative_button_text";
    private static final String ARG_BUNDLE = "arg_bundle";

    private OnDialogInteractionListener mListener;
    private Bundle mBundle;

    // Container Activity must implement this interface
    public interface OnDialogInteractionListener {
        void onPositiveDecision(String tag, Bundle bundle);

        void onNegativeDecision(String tag, Bundle bundle);

        void onCancelDecision(String tag, Bundle bundle);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onCancelDecision(getTag(), mBundle);
    }

    public static UserDecisionFragment newInstance(Builder builder) {
        UserDecisionFragment f = new UserDecisionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, builder.mTitle);
        args.putString(ARG_MESSAGE, builder.mMessage);
        args.putBoolean(ARG_CANCELABLE, builder.mCancelable);
        args.putString(ARG_POSITIVE_BUTTON_TEXT, builder.mPositiveButtonText);
        args.putString(ARG_NEGATIVE_BUTTON_TEXT, builder.mNegativeButtonText);
        args.putParcelable(ARG_BUNDLE, builder.mBundle);
        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = null;
        String message = null;
        boolean cancelable = true;
        String positiveButtonText = null;
        String negativeButtonText = null;

        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            message = getArguments().getString(ARG_MESSAGE);
            cancelable = getArguments().getBoolean(ARG_CANCELABLE);
            positiveButtonText = getArguments().getString(ARG_POSITIVE_BUTTON_TEXT);
            negativeButtonText = getArguments().getString(ARG_NEGATIVE_BUTTON_TEXT);
            mBundle = getArguments().getParcelable(ARG_BUNDLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        if (!TextUtils.isEmpty(title)) builder.setTitle(title);
        if (!TextUtils.isEmpty(message)) builder.setMessage(message);
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onPositiveDecision(getTag(), mBundle);
                }
            });
        }
        if (!TextUtils.isEmpty(negativeButtonText)) {
            builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onNegativeDecision(getTag(), mBundle);
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(cancelable);
        alertDialog.setCanceledOnTouchOutside(cancelable);

        return alertDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnDialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnDialogInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(UserDecisionFragment.class.getSimpleName()
                    + " must implement OnDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static class Builder {
        private String mTitle;
        private String mMessage;
        private boolean mCancelable;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private Bundle mBundle;

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setPositiveButtonText(String text) {
            mPositiveButtonText = text;
            return this;
        }

        public Builder setNegativeButtonText(String text) {
            mNegativeButtonText = text;
            return this;
        }

        public Builder setBundle(Bundle bundle){
            mBundle = bundle;
            return this;
        }

        public UserDecisionFragment create() {
            return newInstance(this);
        }

    }
}
