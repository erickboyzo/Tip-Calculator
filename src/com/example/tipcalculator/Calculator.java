package com.example.tipcalculator;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.os.Build;

public class Calculator extends ActionBarActivity {
	private EditText billAmount;
	private EditText NumofPeople;
	private EditText OtherAmountTip;
	private RadioGroup rdoGroupTips;
	private Button btnCalculate;
	private Button btnReset;
	private Button saveTip;
	private Button viewTipSaved;

	private TextView txtTipAmount;
	private TextView txtTotalToPay;
	private TextView txtTipPerPerson;

	// For the id of radio button selected
	private int radioCheckedId = -1;

	// private NumberPickerLogic mLogic;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);

		// Access the various widgets by their id in R.java
		billAmount = (EditText) findViewById(R.id.txtAmount);
		// On app load, the cursor should be in the Amount field
		billAmount.requestFocus();

		OtherAmountTip = (EditText) findViewById(R.id.txtTipOther);

		rdoGroupTips = (RadioGroup) findViewById(R.id.RadioGroupTips);

		btnCalculate = (Button) findViewById(R.id.btnCalculate);
		// On app load, the Calculate button is disabled
		btnCalculate.setEnabled(false);

		btnReset = (Button) findViewById(R.id.btnReset);

		txtTipAmount = (TextView) findViewById(R.id.txtTipAmount);
		txtTotalToPay = (TextView) findViewById(R.id.txtTotalToPay);
		txtTipPerPerson = (TextView) findViewById(R.id.txtTipPerPerson);

		// On app load, disable the 'Other tip' percentage text field
		OtherAmountTip.setEnabled(false);

		/*
		 * Attach a OnCheckedChangeListener to the radio group to monitor radio
		 * buttons selected by user
		 */
		rdoGroupTips.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// Enable/disable Other Percentage tip field
				if (checkedId == R.id.radioFifteen
						|| checkedId == R.id.radioEighteen
						|| checkedId == R.id.radioTwenty) {
					OtherAmountTip.setEnabled(false);
					btnCalculate.setEnabled(billAmount.getText().length() > 0);
				}
				/*
				 * Enable the calculate button if Total Amount and No. of People
				 * fields have valid values. Also ensure that user has entered a
				 * Other Tip Percentage value before enabling the Calculate
				 * button.
				 */
				if (checkedId == R.id.radioOther) {
					OtherAmountTip.setEnabled(true);
					OtherAmountTip.requestFocus();
					btnCalculate.setEnabled(billAmount.getText().length() > 0
							&& OtherAmountTip.getText().length() > 0);
				}

				radioCheckedId = checkedId;
			}
		});

		/*
		 * Attach a KeyListener to the all buttons in app
		 */
		billAmount.setOnKeyListener(mKeyListener);
		OtherAmountTip.setOnKeyListener(mKeyListener);
		saveTip.setOnKeyListener(mKeyListener);
		viewTipSaved.setOnKeyListener(mKeyListener);

		btnCalculate.setOnClickListener(mClickListener);
		btnReset.setOnClickListener(mClickListener);

	}

	public void calculate() {
		Double txtbillAmount = Double.parseDouble(billAmount.getText()
				.toString());
		Double percentage = null;
		boolean isError = false;
		if (txtbillAmount < 1.0) {
			showErrorAlert("Enter a valid Total Amount.", billAmount.getId());
			isError = true;
		}

		/*
		 * If user never changes radio selection, then it means the default
		 * selection of 15% is in effect. But it's safer to verify...
		 */
		if (radioCheckedId == -1) {
			radioCheckedId = rdoGroupTips.getCheckedRadioButtonId();
		}
		if (radioCheckedId == R.id.radioFifteen) {
			percentage = 15.00;
		} else if (radioCheckedId == R.id.radioEighteen) {
			percentage = 18.00;
		} else if (radioCheckedId == R.id.radioTwenty) {
			percentage = 20.00;
		} else if (radioCheckedId == R.id.radioOther) {
			percentage = Double
					.parseDouble(OtherAmountTip.getText().toString());
			if (percentage < 1.0) {
				showErrorAlert("Enter a valid Tip percentage",
						OtherAmountTip.getId());
				isError = true;
			}

		}
	}

	public void showErrorAlert(String errorMessage, final int fieldId) {
		new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage(errorMessage)
				.setNeutralButton("Close",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								findViewById(fieldId).requestFocus();
							}
						}).show();
	}

	/*
	 * KeyListener for the Total Amount, No of People and Other Tip Percentage
	 * fields. We need to apply this key listener to check for following
	 * conditions:
	 * 
	 * 1) If user selects Other tip percentage, then the other tip text field
	 * should have a valid tip percentage entered by the user. Enable the
	 * Calculate button only when user enters a valid value.
	 * 
	 * 2) If user does not enter values in the Total Amount and No of People, we
	 * cannot perform the calculations. Hence enable the Calculate button only
	 * when user enters a valid values.
	 */
	public OnKeyListener mKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			switch (v.getId()) {
			case R.id.txtAmount:
				btnCalculate.setEnabled(billAmount.getText().length() > 0);
				break;
			case R.id.txtTipOther:
				btnCalculate.setEnabled(billAmount.getText().length() > 0

				&& OtherAmountTip.getText().length() > 0);
				break;
			}
			return false;
		}

	};

	/**
	 * ClickListener for the Calculate and Reset buttons. Depending on the
	 * button clicked, the corresponding method is called.
	 */
	public OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnCalculate) {
				calculate();
			} 
			else {
				reset();
			}
		}
	};

	/**
	 * Resets the results text views at the bottom of the screen as well as
	 * resets the text fields and radio buttons.
	 */
	public void reset() {
		txtTipAmount.setText("");
		txtTotalToPay.setText("");
		txtTipPerPerson.setText("");
		billAmount.setText("");

		OtherAmountTip.setText("");
		rdoGroupTips.clearCheck();
		rdoGroupTips.check(R.id.radioFifteen);
		// set focus on the first field
		billAmount.requestFocus();
	}

	/**
	 * Calculate the tip as per data entered by the user.
	 */

