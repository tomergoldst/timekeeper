package com.tomergoldst.timekeeperdemo;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    private static final String ARG_HOUR = "arg_hour";
    private static final String ARG_MINUTES = "arg_minutes";

    private OnDialogInteractionListener mListener;

    // Container Activity must implement this interface
    public interface OnDialogInteractionListener {
        void onTimeSelected(int hourOfDay, int minute);
    }

    public static TimePickerFragment newInstance() {
        return new TimePickerFragment();
    }

    public static TimePickerFragment newInstance(int hour, int minutes) {
        TimePickerFragment f = newInstance();
        Bundle args = new Bundle();
        args.putInt(ARG_HOUR, hour);
        args.putInt(ARG_MINUTES, minutes);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour;
        int minutes;

        Bundle args = getArguments();
        if (args == null) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minutes = c.get(Calendar.MINUTE);
        } else {
            hour = args.getInt(ARG_HOUR);
            minutes = args.getInt(ARG_MINUTES);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minutes,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onTimeSelected(hourOfDay, minute);
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
            throw new ClassCastException(TimePickerFragment.class.getSimpleName()
                    + " must implement OnDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
