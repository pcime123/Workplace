package com.sscctv.seeeyesmonitor;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
//import android.util.Log;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sscctv.seeeyes.ptz.PtzMode;
import com.sscctv.seeeyes.ptz.UtcProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PtzOverlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PtzOverlayFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    //private static final String TAG = "PtzOverlayFragment";

    private static final String ARG_UTC_TYPE = "utcType";

    public static final String CONTENTS_FRAGMENT_TAG = "ptz_contents_fragment";

    public static final int CONTROL_NONE = 0;
    public static final int CONTROL_PAN_TILT = 1;
    public static final int CONTROL_ZOOM_FOCUS = 2;
    public static final int CONTROL_OSD = 3;
    public static final int CONTROL_RX = 4;
    public static final int CONTROL_ANALYZER = 5;

    private int mUtcType;
    private int mControlMode;
    private int mPtzMode;
    private int iProtocolSelection;
    private int iAddressSelection;
    private int iBaudrateSelection;

    private TextView mMode;
    private String getMode = "";


    private Spinner spinner_protocol, spinner_address, spinner_baudrate;

    //    private ListView mListView;
    private final static String TAG = "PtzOverlayFragment";

    private int protocolKey;
    private int labelKey;
    private String key = "ptzOverlay";


    public PtzOverlayFragment() {
        // Required empty public constructor
    }

    private boolean isUtcSupported() {
        return mUtcType != UtcProtocol.TYPE_NONE;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param utcType Type of UTC mode to support.
     * @return A new instance of fragment PtzOverlayFragment.
     */
    public static PtzOverlayFragment newInstance(int utcType) {
        PtzOverlayFragment fragment = new PtzOverlayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_UTC_TYPE, utcType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_ptz_overlay, container, false);

        mMode = view.findViewById(R.id.value_mode);


        spinner_protocol = view.findViewById(R.id.value_protocol);
        spinner_address = view.findViewById(R.id.value_address);
        spinner_baudrate = view.findViewById(R.id.value_baudrate);

        iProtocolSelection = spinner_protocol.getSelectedItemPosition();
        iAddressSelection = spinner_address.getSelectedItemPosition();
        iBaudrateSelection = spinner_baudrate.getSelectedItemPosition();

        spinner_protocol.setFocusable(false);
        spinner_protocol.setFocusableInTouchMode(false);

        spinner_address.setFocusable(false);
        spinner_address.setFocusableInTouchMode(false);

        spinner_baudrate.setFocusable(false);
        spinner_baudrate.setFocusableInTouchMode(false);
        return view;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUtcType = getArguments().getInt(ARG_UTC_TYPE, UtcProtocol.TYPE_NONE);
            mControlMode = CONTROL_OSD;

//            Log.d(TAG, "Utc Type: " + mUtcType);
//
//            PreferenceManager mPreferenceManager = new PreferenceManager(Objects.requireNonNull(getContext()));
//
//            PreferenceScreen preferenceScreen = mPreferenceManager.inflateFromResource(getContext(), R.xml.prefs_ptz, null);
//
//            mPreferenceManager.setPreferences(preferenceScreen);
//
//            // mMode = preferenceScreen.findPreference(mPtzModeKey);//
//            Preference mPtzProtocol = preferenceScreen.findPreference(getString(R.string.pref_ptz_protocol));
//
//            switch (mUtcType) {
//                case UtcProtocol.TYPE_CVBS:
//                    Preference mUtcProtocol = preferenceScreen.findPreference(getString(R.string.pref_utc_protocol_cvbs));
//                    break;
//
//                case UtcProtocol.TYPE_TVI:
//                    mUtcProtocol = preferenceScreen.findPreference(getString(R.string.pref_utc_protocol_tvi));
//                    break;
//
//                case UtcProtocol.TYPE_AHD:
//                    mUtcProtocol = preferenceScreen.findPreference(getString(R.string.pref_utc_protocol_ahd));
//                    break;
//
//                case UtcProtocol.TYPE_CVI:
//                    mUtcProtocol = preferenceScreen.findPreference(getString(R.string.pref_utc_protocol_cvi));
//                    break;
//            }
//
//            NumberPickerPreference mAddress = (NumberPickerPreference) preferenceScreen.findPreference(getString(R.string.pref_ptz_address));
//            Preference mBaudRate = preferenceScreen.findPreference(getString(R.string.pref_ptz_baudrate));
//            SwitchPreference mTermination = (SwitchPreference) preferenceScreen.findPreference(getString(R.string.pref_ptz_termination));
//
//            SharedPreferences mSharedPrefs = mPreferenceManager.getSharedPreferences();
//

//            Log.d("TEST", "onCreate");
        }

    }

    public void reCheck(SharedPreferences sharedPreferences) {
        if (mUtcType != 0) {
            mUtcType = getUtcMode(sharedPreferences);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume");
        setControlFragment(mControlMode);

        // 모든 설정의 초기값을 표시
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        for (String key : sharedPreferences.getAll().keySet()) {
            onSharedPreferenceChanged(sharedPreferences, key);
            protocolSet();
            addressSet();
            baudrateSet();
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        putIntegerPreference(sharedPreferences, key, 1);

    }

    @Override
    public void onPause() {
//        Log.d(TAG, "onPause");
        super.onPause();

        // 제어 fragment를 제거하는 효과가 있음
        setControlMode(PtzOverlayFragment.CONTROL_NONE);

        PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).unregisterOnSharedPreferenceChangeListener(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        putIntegerPreference(sharedPreferences, key, 0);

    }

    public void setControlMode(int controlMode) {
        mControlMode = controlMode;

        if (getActivity() != null) {
            setContentsFragment(controlMode);
            setControlFragment(controlMode);
        }
    }

    private void setContentsFragment(int mode) {
        Fragment contentsFragment = null;
//        Log.d(TAG, "setContentsFragment: " + mode);
        switch (mode) {
            case CONTROL_RX:
                contentsFragment = PtzRxContentsFragment.newInstance();
                break;

            case CONTROL_ANALYZER:
                contentsFragment = PtzAnalyzerContentsFragment.newInstance();
                break;

            default:
                break;
        }

        FragmentManager fragmentManager = getFragmentManager();
        if (contentsFragment != null) {
            assert fragmentManager != null;
            fragmentManager.beginTransaction()
                    .replace(R.id.ptz_contents, contentsFragment, CONTENTS_FRAGMENT_TAG)
                    .commit();
        } else {
            assert fragmentManager != null;
            contentsFragment = fragmentManager.findFragmentByTag(CONTENTS_FRAGMENT_TAG);
            if (contentsFragment != null) {
                fragmentManager.beginTransaction().remove(contentsFragment).commit();
            }
        }
    }

    private void setControlFragment(int mode) {
        Fragment controlFragment = null;

        switch (mode) {
            case CONTROL_PAN_TILT:
                controlFragment = PanTiltControlFragment.newInstance();
                break;

            case CONTROL_ZOOM_FOCUS:
                controlFragment = ZoomFocusControlFragment.newInstance();
                break;

            case CONTROL_OSD:
                controlFragment = OsdControlFragment.newInstance();
                break;

            case CONTROL_RX:
                controlFragment = PtzRxControlFragment.newInstance();
                break;

            case CONTROL_ANALYZER:
                controlFragment = PtzAnalyzerControlFragment.newInstance();
                break;

            default:
                break;
        }

        FragmentManager fragmentManager = getFragmentManager();

        if (controlFragment != null) {
            assert fragmentManager != null;
            fragmentManager.beginTransaction()
                    .replace(R.id.ptz_controls, controlFragment, PtzControlFragment.TAG)
                    .commit();
        } else {
            assert fragmentManager != null;
            controlFragment = fragmentManager.findFragmentByTag(PtzControlFragment.TAG);
            if (controlFragment != null) {
                fragmentManager.beginTransaction().remove(controlFragment).commit();
            }
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        View view = getView();

        if (view == null) {
            return;
        }

        TextView textView;


        if ((isUtcSupported() && key.equals(getString(R.string.pref_ptz_mode_utc))) ||
                (!isUtcSupported() && key.equals(getString(R.string.pref_ptz_mode_sdi)))) {
            final String[] ptzModes = getResources().getStringArray(isUtcSupported() ? R.array.ptz_modes_utc : R.array.ptz_modes_sdi);
            final String value = sharedPreferences.getString(key, null);

            textView = view.findViewById(R.id.value_mode);
            if (value != null) {
                mPtzMode = Integer.parseInt(value);
                textView.setText(ptzModes[mPtzMode]);
                getMode = ptzModes[mPtzMode];
//                Log.d(TAG, "onSharedPreferenceChanged mode: " + mPtzMode);
                switch (mPtzMode) {
                    case PtzMode.TX:
                    case PtzMode.UTC:
                        setControlMode(PtzOverlayFragment.CONTROL_OSD);
                        break;

                    case PtzMode.RX:
                        setControlMode(PtzOverlayFragment.CONTROL_RX);
                        break;

                    case PtzMode.ANALYZER:
                        setControlMode(PtzOverlayFragment.CONTROL_ANALYZER);
                        break;
                }
//                Log.i(TAG, "Update 1 " + isUtcSupported() + "   " + getMode);
                updateProtocol(sharedPreferences, mPtzMode == PtzMode.UTC);
                updateAddress(sharedPreferences, isUtcSupported(), getMode);
                updateBaudRate(sharedPreferences, isUtcSupported(), getMode);
            }
        } else if (key.equals(getString(R.string.pref_ptz_protocol))) {
            updateProtocol(sharedPreferences, false);
        } else if (key.equals(getString(R.string.pref_utc_protocol_cvbs))
                || key.equals(getString(R.string.pref_utc_protocol_tvi))
                || key.equals(getString(R.string.pref_utc_protocol_ahd))
                || key.equals(getString(R.string.pref_utc_protocol_cvi))) {
            int ptzMode = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString(isUtcSupported() ? "ptz_mode_utc" : "ptz_mode_sdi", null)));
            updateProtocol(sharedPreferences, ptzMode == PtzMode.UTC);
        } else if (key.equals(getString(R.string.pref_ptz_address))) {
            int ptzMode = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString(isUtcSupported() ? "ptz_mode_utc" : "ptz_mode_sdi", null)));
            updateAddress(sharedPreferences, isUtcSupported(), getMode);
//            Log.i(TAG, "Update 2  " + isUtcSupported() + "   " + getMode);


        } else if (key.equals(getString(R.string.pref_ptz_baudrate))) {
            int ptzMode = Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString(isUtcSupported() ? "ptz_mode_utc" : "ptz_mode_sdi", null)));
            updateBaudRate(sharedPreferences, isUtcSupported(), getMode);
//        }
//        else if (key.equals(getString(R.string.pref_ptz_termination))) {
//            Log.d(TAG, key + " = " + sharedPreferences.getBoolean(key, false));
        } else if (key.equals(getString(R.string.pref_utc_mode))) {
            reCheck(sharedPreferences);
//            Log.i(TAG, "Update 3  " + isUtcSupported() + "   " + getMode);

            updateProtocol(sharedPreferences, mPtzMode == PtzMode.UTC);
            updateAddress(sharedPreferences, isUtcSupported(), getMode);
            updateBaudRate(sharedPreferences, isUtcSupported(), getMode);
//            Log.i(TAG, "Update 3 " + isUtcSupported());

        }


    }

    private void updateProtocol(SharedPreferences sharedPreferences, boolean isUtc) {
//        TextView textView = (TextView) view.findViewById(R.id.value_protocol);

        if (mControlMode == CONTROL_RX) {
            spinner_protocol.setVisibility(View.INVISIBLE);
//            textView.setText("-");
        } else {
            spinner_protocol.setVisibility(View.VISIBLE);

            switch (isUtc ? mUtcType : UtcProtocol.TYPE_NONE) {
                case UtcProtocol.TYPE_CVBS:
                    protocolKey = R.string.pref_utc_protocol_cvbs;
                    labelKey = R.array.utc_protocols_cvbs;
                    break;

                case UtcProtocol.TYPE_TVI:
                    protocolKey = R.string.pref_utc_protocol_tvi;
                    labelKey = R.array.utc_protocols_tvi;
                    break;

                case UtcProtocol.TYPE_AHD:
                    protocolKey = R.string.pref_utc_protocol_ahd;
                    labelKey = R.array.utc_protocols_ahd;
                    break;

                case UtcProtocol.TYPE_CVI:
                    protocolKey = R.string.pref_utc_protocol_cvi;
                    labelKey = R.array.utc_protocols_cvi;
                    break;

                default:
                    protocolKey = R.string.pref_ptz_protocol;
                    labelKey = R.array.ptz_protocols;
                    break;
            }


            final String value = sharedPreferences.getString(getString(protocolKey), null);
            if (value != null) {
                final String[] ptzProtocols = getResources().getStringArray(labelKey);
//                Log.d(TAG, "protocolkey: " + Arrays.toString(ptzProtocols));

                int index = Integer.parseInt(value);
                if (index < ptzProtocols.length) {
                    ArrayAdapter<String> adapter_protocol = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_normal, ptzProtocols);
                    adapter_protocol.setDropDownViewResource(R.layout.spinner_dropdown);
                    spinner_protocol.setAdapter(adapter_protocol);
//                    textView.setText(ptzProtocols[index]);
                    spinner_protocol.setSelection(index);
//                    Log.d(TAG, "Test: " + spinner_protocol.getSelectedItem());

                }
            } else {
//                textView.setText("-");
                spinner_protocol.setVisibility(View.INVISIBLE);

            }
        }
    }

    public void protocolSet() {
        spinner_protocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int item = spinner_protocol.getSelectedItemPosition();
                final String key = getString(protocolKey);
//                changePtzProtocol(item);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
                putIntegerPreference(sharedPreferences, key, item);

                if (iProtocolSelection != -1) {
                    if (iProtocolSelection != position) {
//                        Log.d(TAG, "PtzOverlayFragment protocolSet");
                        changePtzMode();
                    }
                }
                iProtocolSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void updateAddress(SharedPreferences sharedPreferences, boolean forceDisable, String mode) {
//        TextView textView = (TextView) view.findViewById(R.id.value_address);

//        Log.d(TAG, "mControlMode: " + mControlMode);
        if (forceDisable || mControlMode == CONTROL_RX || mControlMode == CONTROL_ANALYZER) {
            spinner_address.setVisibility(View.INVISIBLE);
//            textView.setText("-");
            if (isUtcSupported() && !mode.equals("UTC") && !mode.equals("RS-485 RX") && !mode.equals("Analyze")) {
                spinner_address.setVisibility(View.VISIBLE);

                final String[] ptzAddress = getResources().getStringArray(R.array.ptz_address);
                final String value = sharedPreferences.getString(getString(R.string.pref_ptz_address), null);
//            Log.d(TAG, "Address: " + value);

                if (value != null) {
                    ArrayAdapter<String> adapter_address = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), R.layout.spinner_normal, ptzAddress) {

                        @Override
                        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if (position == 0) {
                                tv.setVisibility(View.GONE);
                            } else {
                                tv.setVisibility(View.VISIBLE);
                            }
                            return view;
                        }
                    };

                    adapter_address.setDropDownViewResource(R.layout.spinner_dropdown);
                    spinner_address.setAdapter(adapter_address);
                    spinner_address.setSelection(Integer.parseInt(value));
//  String format = getString(R.string.ptz_address_format);
//                textView.setText(String.format(format, Integer.parseInt(value)));
                } else {
                    spinner_address.setVisibility(View.INVISIBLE);

//                textView.setText("-");
                }
            }
        } else {


            spinner_address.setVisibility(View.VISIBLE);

            final String[] ptzAddress = getResources().getStringArray(R.array.ptz_address);
            final String value = sharedPreferences.getString(getString(R.string.pref_ptz_address), null);
//            Log.d(TAG, "Address: " + value);

            if (value != null) {
                ArrayAdapter<String> adapter_address = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), R.layout.spinner_normal, ptzAddress) {

                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setVisibility(View.GONE);
                        } else {
                            tv.setVisibility(View.VISIBLE);
                        }
                        return view;
                    }
                };

                adapter_address.setDropDownViewResource(R.layout.spinner_dropdown);
                spinner_address.setAdapter(adapter_address);
                spinner_address.setSelection(Integer.parseInt(value));
//  String format = getString(R.string.ptz_address_format);
//                textView.setText(String.format(format, Integer.parseInt(value)));
            } else {
                spinner_address.setVisibility(View.INVISIBLE);

//                textView.setText("-");
            }
        }
    }

    public void addressSet() {
        spinner_address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int item = spinner_address.getSelectedItemPosition();

                changePtzAddress(item);

                if (iAddressSelection != -1) {
                    if (iAddressSelection != position) {
                        changePtzMode();
                        Log.d(TAG, "PtzOverlayFragment addressSet");

                    }
                }
                iAddressSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateBaudRate(SharedPreferences sharedPreferences, boolean forceDisable, String mode) {
//        TextView textView = (TextView) view.findViewById(R.id.value_baudrate);

        if (forceDisable) {
            spinner_baudrate.setVisibility(View.INVISIBLE);
//            textView.setText("-");
            if (isUtcSupported() && !mode.equals("UTC")) {
                spinner_baudrate.setVisibility(View.VISIBLE);

                final String[] ptzBaudrates = getResources().getStringArray(R.array.ptz_baudrates);
                final String value = sharedPreferences.getString(getString(R.string.pref_ptz_baudrate), null);
                if (value != null) {
                    ArrayAdapter<String> adapter_baudrate = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_normal, ptzBaudrates);
                    adapter_baudrate.setDropDownViewResource(R.layout.spinner_dropdown);
                    spinner_baudrate.setAdapter(adapter_baudrate);
                    spinner_baudrate.setSelection(Integer.parseInt(value));
//                textView.setText(ptzBaudrates[Integer.parseInt(value)]);
                }
            }
        } else {
            spinner_baudrate.setVisibility(View.VISIBLE);

            final String[] ptzBaudrates = getResources().getStringArray(R.array.ptz_baudrates);
            final String value = sharedPreferences.getString(getString(R.string.pref_ptz_baudrate), null);
            if (value != null) {
                ArrayAdapter<String> adapter_baudrate = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_normal, ptzBaudrates);
                adapter_baudrate.setDropDownViewResource(R.layout.spinner_dropdown);
                spinner_baudrate.setAdapter(adapter_baudrate);
                spinner_baudrate.setSelection(Integer.parseInt(value));
//                textView.setText(ptzBaudrates[Integer.parseInt(value)]);
            }
        }
    }

    public void baudrateSet() {
        spinner_baudrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int item = spinner_baudrate.getSelectedItemPosition();
                changePtzBaudrate(item);

                if (iBaudrateSelection != -1) {
                    if (iBaudrateSelection != position) {
                        changePtzMode();
                        Log.d(TAG, "PtzOverlayFragment baudrateSet");

                    }
                }
                iBaudrateSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private int getPtzProtocol(SharedPreferences sharedPreferences) {
        return Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_ptz_protocol), "0"));
    }


    private void putIntegerPreference(SharedPreferences sharedPreferences, String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, Integer.toString(value));
        editor.apply();
    }

    private void changePtzProtocol(int item) {
        final String key = getString(R.string.pref_ptz_protocol);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        putIntegerPreference(sharedPreferences, key, item);
    }


    private void changePtzAddress(int item) {
        final String key = getString(R.string.pref_ptz_address);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        putIntegerPreference(sharedPreferences, key, item);
    }

    private void changePtzBaudrate(int item) {
        final String key = getString(R.string.pref_ptz_baudrate);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        putIntegerPreference(sharedPreferences, key, item);
    }

    private int getUtcMode(SharedPreferences sharedPreferences) {
        return Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_utc_mode), "0"));
    }

    private String getMode(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("getMode", "0");
    }

    private void changePtzMode() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
        MainActivity.PtzSettings ptzSettings = ((MainActivity) MainActivity.mContext).getPtzSettings(pref);
        ((MainActivity) MainActivity.mContext).changePtzMode(ptzSettings, false);
    }


}
