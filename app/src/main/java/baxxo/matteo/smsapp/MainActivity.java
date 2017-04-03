package baxxo.matteo.smsapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    FragmentContatti myFragment = null;
    private int anno = 1;
    private int mese = 1;
    private int giorno = 1;
    private int ora = 1;
    private int minuto = 1;
    private String testo;
    private String numero;
    String nomeNumero;
    static EditText n;
    static EditText t;
    static DatePicker data;
    static TimePicker timepicker;
    static EditText editOra;
    static EditText editMin;
    static EditText editAnno;
    static EditText editMese;
    static EditText editGiorno;
    static TextView puntini;
    static TextView barrette1;
    static TextView barrette2;
    static TextView hhmm;
    static TextView dataS;
    static TextView conta;
    private String text;
    private Calendar calendar;
    private TabLayout tabLayout;
    ArrayList<Contact> contact;
    private TabsPagerAdapter tabsPagerAdapter;

    public void alarm() {

        nomeNumero = numero;

        //prendo il nome del destinatario
        if (!FragmentContatti.contatti.isEmpty()) {
            contact = FragmentContatti.contatti;
            for (int i = 0; i < contact.size(); i++) {
                if (numero.equals(contact.get(i).number)) {
                    nomeNumero = contact.get(i).name;
                }
            }
        }

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, giorno);
        calendar.set(Calendar.HOUR_OF_DAY, ora);
        calendar.set(Calendar.MINUTE, minuto);


        Intent intent = new Intent(this, Receiver.class);
        intent.putExtra("Numero", numero);
        intent.putExtra("Testo", testo);
        intent.putExtra("Nome", nomeNumero);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        System.out.println(ora + " : " + minuto);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        myFragment.svuota();
        // Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.vibrate(1000);
    }

    static final TextWatcher contaCaratteri = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String t = "Caratteri: " + String.valueOf(s.length()) + "/160";
            conta.setText(t);
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.READ_CALENDAR
            }, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }

        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(tabsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //prendo l'ora
                //se android è >= M allora uso il time e date picker
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ora = timepicker.getHour();
                    minuto = timepicker.getMinute();
                    anno = data.getYear();
                    mese = data.getMonth();
                    giorno = data.getDayOfMonth();
                } else {
                    //altrimenti due campi di testo
                    ora = Integer.parseInt(String.valueOf(editOra.getText()));
                    minuto = Integer.parseInt(String.valueOf(editMin.getText()));

                    if (ora < 0) {
                        ora = 0;
                    }
                    if (ora > 23) {
                        ora = 23;
                    }
                    if (minuto < 0) {
                        minuto = 0;
                    }
                    if (minuto > 59) {
                        minuto = 59;
                    }

                    anno = Integer.parseInt(String.valueOf(editAnno.getText()));
                    mese = Integer.parseInt(String.valueOf(editMese.getText()));
                    giorno = Integer.parseInt(String.valueOf(editGiorno.getText()));
                }

                //prendo il testo del messaggio e il numero
                testo = String.valueOf(t.getText());
                numero = String.valueOf(n.getText());

                if (testo.length() > 160) {

                    Toast.makeText(getApplicationContext(), "Troppi caratteri", Toast.LENGTH_LONG).show();

                } else {
                    alarm();
                    testo = "";
                    t.setText("");

                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }
                String m = minuto + "";
                if (m.length() < 1) {
                    m = "0" + m;
                }
                text = "Il messaggio verrà inviato alle " + ora + ":" + m + " del " + giorno + "/" + mese + "/" + anno;
                Snackbar.make(view, text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        });

    }

    //swipe----------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_message);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_account_circle);
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }*/

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return myFragment = new FragmentContatti();
            }

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getString(R.string.title2);
            }
            return getString(R.string.title1);
        }

    }


    //fragment
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Calendar calendar = Calendar.getInstance();


            n = (EditText) rootView.findViewById(R.id.Numero);
            t = (EditText) rootView.findViewById(R.id.Testo);
            conta = (TextView) rootView.findViewById(R.id.textView3);
            data = (DatePicker) rootView.findViewById(R.id.datePicker);
            timepicker = (TimePicker) rootView.findViewById(R.id.timePicker);
            editOra = (EditText) rootView.findViewById(R.id.ora);
            editMin = (EditText) rootView.findViewById(R.id.min);
            editAnno = (EditText) rootView.findViewById(R.id.anno);
            editMese = (EditText) rootView.findViewById(R.id.mese);
            editGiorno = (EditText) rootView.findViewById(R.id.giorno);
            hhmm = (TextView) rootView.findViewById(R.id.textView4);
            dataS = (TextView) rootView.findViewById(R.id.textView6);
            puntini = (TextView) rootView.findViewById(R.id.textView7);
            barrette1 = (TextView) rootView.findViewById(R.id.textView8);
            barrette2 = (TextView) rootView.findViewById(R.id.textView9);

            t.addTextChangedListener(contaCaratteri);
            editOra.setText(calendar.get(Calendar.HOUR_OF_DAY) + "");
            editMin.setText(calendar.get(Calendar.MINUTE) + "");
            editAnno.setText(calendar.get(Calendar.YEAR) + "");
            editMese.setText(calendar.get(Calendar.MONTH) + "");
            editGiorno.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timepicker.setVisibility(View.VISIBLE);
                data.setVisibility(View.VISIBLE);

                editOra.setVisibility(View.INVISIBLE);
                editMin.setVisibility(View.INVISIBLE);
                editAnno.setVisibility(View.INVISIBLE);
                editMese.setVisibility(View.INVISIBLE);
                editGiorno.setVisibility(View.INVISIBLE);
                hhmm.setVisibility(View.INVISIBLE);
                dataS.setVisibility(View.INVISIBLE);
                puntini.setVisibility(View.INVISIBLE);
                barrette1.setVisibility(View.INVISIBLE);
                barrette2.setVisibility(View.INVISIBLE);
            } else {
                timepicker.setVisibility(View.INVISIBLE);
                data.setVisibility(View.INVISIBLE);

                editOra.setVisibility(View.VISIBLE);
                editMin.setVisibility(View.VISIBLE);
                editAnno.setVisibility(View.VISIBLE);
                editMese.setVisibility(View.VISIBLE);
                editGiorno.setVisibility(View.VISIBLE);
                hhmm.setVisibility(View.VISIBLE);
                dataS.setVisibility(View.VISIBLE);
                puntini.setVisibility(View.VISIBLE);
                barrette1.setVisibility(View.VISIBLE);
                barrette2.setVisibility(View.VISIBLE);
            }
            return rootView;
        }
    }
}
