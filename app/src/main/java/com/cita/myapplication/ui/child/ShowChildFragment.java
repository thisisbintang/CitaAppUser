package com.cita.myapplication.ui.child;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cita.myapplication.LoginActivity;
import com.cita.myapplication.R;
import com.cita.myapplication.app.AppController;
import com.cita.myapplication.utils.Server;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowChildFragment extends Fragment {

    private ProgressDialog progressDialog;
    private TextView tvChildName, tvDateOfBirth, tvGender;

    private static final String TAG_JSON_OBJ = "json_obj_req";
    private static final String TAG_CHILD_NAME = "child_name";
    private static final String TAG_DATE_OF_BIRTH = "date_of_birth";
    private static final String TAG_GENDER = "gender";
    private static final String TAG = ShowChildFragment.class.getSimpleName();
    private static final String URL = Server.URL + "user/show_child.php";
    private static final String TAG_CHILD_ID = "child_id";
    private static final String TAG_MESSAGE = "message";

    //    private AutoCompleteTextView dropdownGender;
    private int chilId;

    public ShowChildFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_show_child, container, false);
        assert getArguments() != null;
        chilId = ShowChildFragmentArgs.fromBundle(getArguments()).getChildId();

//        dropdownGender = root.
        MaterialCardView mcvChildName = root.findViewById(R.id.mcv_child_name);
        MaterialCardView mcvDateOfBirth = root.findViewById(R.id.mcv_date_of_birth);
        MaterialCardView mcvGender = root.findViewById(R.id.mcv_gender);

        tvChildName = root.findViewById(R.id.tv_child_name);
        tvDateOfBirth = root.findViewById(R.id.tv_date_of_birth);
        tvGender = root.findViewById(R.id.tv_gender);


        show();

        MaterialButton btnDelete = root.findViewById(R.id.btn_delete);
        MaterialButton btnBack = root.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroy();
            }
        });

        mcvChildName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                final EditText input = new EditText(getActivity());
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                input.setSingleLine();
                input.setLayoutParams(params);
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                input.setText(tvChildName.getText().toString());
                container.addView(input);
                alert.setTitle("Nama Anak");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String childName = input.getText().toString();
                        if (!childName.equals(tvChildName.getText().toString())) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    Server.URL + "user/update_child_child_name.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e(TAG, "Update response: " + response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(getActivity(),
                                                        jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                        Toast.LENGTH_SHORT).show();
                                                show();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(TAG_CHILD_NAME, childName);
                                    params.put(TAG_CHILD_ID, String.valueOf(chilId));
                                    return params;
                                }
                            };
                            AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
                        }

                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        View view = getActivity().getCurrentFocus();
                        if (view == null) {
                            view = new View(getActivity());
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
                alert.show();
            }
        });
        mcvDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalendar = Calendar.getInstance();
                String myFormat = "dd/MM/yyyy";
                final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                try {
                    myCalendar.setTime(Objects.requireNonNull(sdf.parse(tvDateOfBirth.getText().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        final String dateOfBirth = sdf.format(myCalendar.getTime());
                        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                                Server.URL + "user/update_child_date_of_birth.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e(TAG, "Update response: " + response);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Toast.makeText(getActivity(),
                                                    jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                    Toast.LENGTH_SHORT).show();
                                            show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put(TAG_DATE_OF_BIRTH, dateOfBirth);
                                params.put(TAG_CHILD_ID, String.valueOf(chilId));
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(stringRequest1, TAG_JSON_OBJ, getActivity());
                    }

                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(System.currentTimeMillis());
                datePicker.setMinDate(System.currentTimeMillis() - (DateUtils.YEAR_IN_MILLIS * 5));
                datePickerDialog.show();


            }
        });
        mcvGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FrameLayout container = new FrameLayout(Objects.requireNonNull(getActivity()));
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                params.rightMargin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);

                final Spinner dropdownGender = new Spinner(getActivity());
                final String[] COUNTRIES = new String[]{"Laki - Laki", "Perempuan"};
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                                Objects.requireNonNull(getContext()),
                                R.layout.dropdown_menu_popup_gender,
                                COUNTRIES);
                dropdownGender.setAdapter(adapter);

                dropdownGender.setLayoutParams(params);
                container.addView(dropdownGender);

                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                alert.setTitle("Jenis Kelamin");
                alert.setView(container);

                alert.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String gender = dropdownGender.getSelectedItem().toString();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                Server.URL + "user/update_child_gender.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e(TAG, "Update response: " + response);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            Toast.makeText(getActivity(),
                                                    jsonObject.getString(LoginActivity.TAG_MESSAGE),
                                                    Toast.LENGTH_SHORT).show();
                                            show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put(TAG_GENDER, gender);
                                params.put(TAG_CHILD_ID, String.valueOf(chilId));
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
                    }
                });
                alert.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });
        return root;
    }

    private void show() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu sebentar ...");
        showProgressDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();
                        Log.e(TAG, "Show response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            tvChildName.setText(jsonObject.getString(TAG_CHILD_NAME));
                            tvDateOfBirth.setText(jsonObject.getString(TAG_DATE_OF_BIRTH));
                            tvGender.setText(jsonObject.getString(TAG_GENDER));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(TAG_CHILD_ID, String.valueOf(chilId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());

    }

    private void destroy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage("Apakah anda yakin?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Memproses ...");
                showProgressDialog();

                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Server.URL + "user/destroy_child.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                hideProgressDialog();
                                Log.e(TAG, "Response: " + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    Toast.makeText(getActivity(), jsonObject.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                    Objects.requireNonNull(getActivity()).onBackPressed();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                hideProgressDialog();
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put(TAG_CHILD_ID, String.valueOf(chilId));
                        return params;
                    }
                };
                AppController.getInstance().addToRequestQueue(stringRequest, TAG_JSON_OBJ, getActivity());
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
