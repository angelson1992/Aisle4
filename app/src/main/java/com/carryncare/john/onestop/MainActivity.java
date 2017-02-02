package com.carryncare.john.onestop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.carryncare.onestop.MESSAGE";
    public final static int STARTING_ROW_NUM = 10;
    public static int currentRowNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**EditText list1 = (EditText) findViewById(R.id.list1);
        list1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                makeRow(v);
            }
        });*/

        for(int i = 0; i < STARTING_ROW_NUM; i++) { //Populate the main screen with the dynamic rows.
            makeRow(findViewById(R.id.baseView));
        }

        getTotalPrice();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.list1);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    //Used to dynamically create new rows of edit text
    public void makeRow(View view){

        LinearLayout baseView = (LinearLayout) findViewById(R.id.baseView); //Get the root view.

        LinearLayout firstRow = (LinearLayout) findViewById(R.id.row1); //Gather the views that we will base the new views on
        EditText firstCounter = (EditText) findViewById(R.id.count1);
        EditText firstList = (EditText) findViewById(R.id.list1);
        EditText firstPrice = (EditText) findViewById(R.id.price1);

        ViewGroup.LayoutParams paramsRow = firstRow.getLayoutParams(); //These four lines are to
        ViewGroup.LayoutParams paramsCount = firstCounter.getLayoutParams(); //get layout parameters from the hardcoded
        ViewGroup.LayoutParams paramsList = firstList.getLayoutParams(); //view in the program to be used for the
        ViewGroup.LayoutParams paramsPrice = firstPrice.getLayoutParams(); //dynamically created views.

        LinearLayout row = new LinearLayout(this); //Format the rows that the various editViews will be inserted into
        row.setOrientation(firstRow.getOrientation());
        row.setLayoutMode(firstRow.getLayoutMode());
        row.setId(View.generateViewId());

        EditText countSlot = new EditText(this); //Setting up the edit view responsible for how many of an item to list
        countSlot.setText(firstCounter.getText());
        countSlot.setInputType(firstCounter.getInputType());
        countSlot.setGravity(firstCounter.getGravity());
        countSlot.setSelectAllOnFocus(true);
        countSlot.setContentDescription("Count Slot");
        countSlot.setId(View.generateViewId());


        /** This sets up a listener for then the text of the countSlot is changed and calls the getTotalPrice function after the text as been changed*/
        countSlot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {getTotalPrice();}
        });

        EditText itemSlot = new EditText(this); //Setting up the edit view responsible for what item is listed
        itemSlot.setHint(firstList.getHint());
        itemSlot.setInputType(firstList.getInputType());
        itemSlot.setContentDescription("Item Slot");
        itemSlot.setId(View.generateViewId());

        EditText priceSlot = new EditText(this); //Setting up the edit view responsible to showing how much an item cost
        priceSlot.setInputType(firstPrice.getInputType());
        priceSlot.setHint(firstPrice.getHint());
        priceSlot.setGravity(firstPrice.getGravity());
        priceSlot.setContentDescription("Price Slot");
        priceSlot.setId(View.generateViewId());

        /** This sets up a listener for then the text of the priceSlot is changed and calls the getTotalPrice function after the text as been changed*/
        priceSlot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {getTotalPrice();}
        });

        countSlot.setNextFocusForwardId(itemSlot.getId());
        itemSlot.setNextFocusForwardId(priceSlot.getId());

        row.addView(countSlot, paramsCount); //Adding the views in order to rows to be inserted into the rowContainer view
        row.addView(itemSlot, paramsList);
        row.addView(priceSlot, paramsPrice);

        LinearLayout rowContainer = (LinearLayout)  findViewById(R.id.rowContainer);

        rowContainer.addView(row, paramsRow);
        currentRowNum++;

    }

    public void getTotalPrice(){ //This method called to calculate the total cost of all listed items

        double total = 0.0;

        ArrayList countViews = new ArrayList<>();
        ArrayList priceViews = new ArrayList<>();

        LinearLayout rowContainer = (LinearLayout) findViewById(R.id.rowContainer);
        rowContainer.findViewsWithText(countViews, "Count Slot", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION); //This gathers all of the countSlot views
        rowContainer.findViewsWithText(priceViews, "Price Slot", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION); //This gathers all of the priceSlot views

        for(int i = 0; i < priceViews.size();i++){ //This for loop is used to go through all of the views and add of their total prices
            int count = 0;
            if(!(((EditText) countViews.get(i)).getText().toString().isEmpty())){ //This checks that the countSlot is not empty as it would crash the app
                count = Integer.valueOf(((EditText) countViews.get(i)).getText().toString()); //This gets the value from the countSlot
            }
            float price = 0.00f;
            if((((EditText) priceViews.get(i)).getText().toString().compareTo(".") == 0)){//This checks and handles that the user presses "." first as it would crash the app
                ((EditText) priceViews.get(i)).setText("0.");
                ((EditText) priceViews.get(i)).setSelection(2);
            }
            if(!(((EditText) priceViews.get(i)).getText().toString().isEmpty())){ //This checks that the priceSlot is not empty as it would crash the app
                price = Float.valueOf(((EditText) priceViews.get(i)).getText().toString()); //This gets the value from the priceSlot
            }
            total = total + (count * price);
        }

        TextView totalView = (TextView) findViewById(R.id.totalPrice);
        totalView.setText(String.format("$%.2f", total)); //Outputs the total price to the totalPrice editView and formats the result.

    }

}
