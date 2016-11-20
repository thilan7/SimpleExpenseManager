package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHelper;


/**
 * Created by Thilan K Bandara on 11/20/2016.
 */


public class PersistentTransactionDAO implements TransactionDAO {

    private Context context;

    //Constructor
    public PersistentTransactionDAO(Context context) {
        this.context = context;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();

        //Save transaction details to the transaction_log table
        ContentValues values = new ContentValues();
        values.put(handler.accountNo, accountNo);
        values.put(handler.date, convertDateToString(date));
        values.put(handler.amount, amount);
        values.put(handler.expenseType, expenseType.toString());

        db.insert(handler.transactionTable,null,values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return getPaginatedTransactionLogs(0);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();

        //Query to get details of all the transactions
        String query = "SELECT "+ handler.accountNo + ", " +
                handler.date + ", " +
                handler.expenseType+", " +
                handler.amount +
                " FROM " + handler.transactionTable + " ORDER BY " + handler.transaction_id + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Transaction> transactionLogs = new ArrayList<>();

        //Add the transaction details to a list
        while (cursor.moveToNext())
        {
            try {

                ExpenseType expenseType = null;
                if (cursor.getString(cursor.getColumnIndex(handler.expenseType)).equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                }
                else{
                    expenseType = ExpenseType.EXPENSE;
                }

                String dateString = cursor.getString(cursor.getColumnIndex(handler.date));
                Date date = convertStringToDate(dateString);

                Transaction tans = new Transaction(
                        date,
                        cursor.getString(cursor.getColumnIndex(handler.accountNo)),
                        expenseType,
                        cursor.getDouble(cursor.getColumnIndex(handler.amount)));

                transactionLogs.add(tans);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //Return the list of transactions
        return transactionLogs;
    }

    //Method to convert a date object to a string
    public static String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        return dateString;

    }

    //Method to convert a string to a date object
    public static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = dateFormat.parse(date);
        return strDate;
    }
}