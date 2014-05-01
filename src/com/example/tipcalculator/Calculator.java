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

public class MainActivity extends Activity {

	final static NumberFormat formatter = NumberFormat.getCurrencyInstance();

	EditText txtAmount;
	EditText txtTipOther;
	RadioGroup rdoGroupTips;
	Button btnCalculate;
	Button btnReset;
	Button saveTip;
	Button viewTipSaves;
	TextView txtTipAmount;
	TextView txtTotalToPay;

	DBHelper helper;
	SQLiteDatabase db;

	int radioCheckedId = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtAmount = (EditText) findViewById(R.id.txtAmount);

		txtAmount.requestFocus();

		txtTipOther = (EditText) findViewById(R.id.txtTipOther);

		rdoGroupTips = (RadioGroup) findViewById(R.id.RadioGroupTips);

		btnCalculate = (Button) findViewById(R.id.btnCalculate);
		saveTip = (Button) findViewById(R.id.btnSaveTip);
		viewTipSaves = (Button) findViewById(R.id.btnViewTips);

		btnCalculate.setEnabled(false);

		btnReset = (Button) findViewById(R.id.btnReset);

		txtTipAmount = (TextView) findViewById(R.id.txtTipAmount);
		txtTotalToPay = (TextView) findViewById(R.id.txtTotalToPay);

		txtTipOther.setEnabled(false);
		helper = new DBHelper(this);

		rdoGroupTips.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                         
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == R.id.radioFifteen
						|| checkedId == R.id.radioEighteen
						|| checkedId == R.id.radioTwenty) {
					txtTipOther.setEnabled(false);

					btnCalculate.setEnabled(txtAmount.getText().length() > 0);
				}
				if (checkedId == R.id.radioOther) {

					txtTipOther.setEnabled(true);

					txtTipOther.requestFocus();

					btnCalculate.setEnabled(txtAmount.getText().length() > 0
							&& txtTipOther.getText().length() > 0);
				}
				
				radioCheckedId = checkedId;
			}
		});

		txtAmount.setOnKeyListener(mKeyListener);
		txtTipOther.setOnKeyListener(mKeyListener);

		btnCalculate.setOnClickListener(mClickListener);
		btnReset.setOnClickListener(mClickListener);
		saveTip.setOnClickListener(mClickListener);
		viewTipSaves.setOnClickListener(mClickListener);

	}

	public OnKeyListener mKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			switch (v.getId()) {
			case R.id.txtAmount:

				btnCalculate.setEnabled(txtAmount.getText().length() > 0);
				break;
			case R.id.txtTipOther:
				btnCalculate.setEnabled(txtAmount.getText().length() > 0

				&& txtTipOther.getText().length() > 0);
				break;
			}
			return false;
		}

	};

	public OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btnCalculate) {
				calculate();
			} else {
				reset();
			}
		}
	};
/*
 * Resets screen 
 */
	public void reset() {
		txtAmount.requestFocus();
		txtTipAmount.setText("");
		txtTotalToPay.setText("");
		txtAmount.setText("");

		txtTipOther.setText("");
		rdoGroupTips.clearCheck();
		rdoGroupTips.check(R.id.radioFifteen);

	}

	public String giveDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
		return sdf.format(cal.getTime());
	}

	public void calculate() {
		Double billAmount = Double.parseDouble(txtAmount.getText().toString());
		Double percentage = null;
		boolean isError = false;
		if (billAmount < 1.0) {
			showErrorAlert("Enter a valid Total Amount.", txtAmount.getId());
			isError = true;
		}

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
			percentage = Double.parseDouble(txtTipOther.getText().toString());
			if (percentage < 1.0) {
				showErrorAlert("Enter a valid Tip percentage",
						txtTipOther.getId());
				isError = true;
			}
		}

		if (!isError) {
			double tipAmount = ((billAmount * percentage) / 100);
			double totalToPay = billAmount + tipAmount;

			txtTipAmount.setText(formatter.format(tipAmount));
			txtTotalToPay.setText(formatter.format(totalToPay));

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
}
