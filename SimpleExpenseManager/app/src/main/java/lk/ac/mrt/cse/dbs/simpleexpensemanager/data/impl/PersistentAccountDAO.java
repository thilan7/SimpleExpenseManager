package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHelper;;

/**
 * Created by Thilan K Bandara on 11/20/2016.
 */

public class PersistentAccountDAO implements AccountDAO {

    private Context context;

    //Constructor
    public PersistentAccountDAO(Context context) {
        this.context = context;
    }

    @Override

    public List<String> getAccountNumbersList() {

        //Open the database connection
        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        if( handler == null){
            System.out.print("Damn");
        }
        SQLiteDatabase db = handler.getReadableDatabase();

        //Query to select all account numbers from the account table
        String query = "SELECT "+ handler.accountNoNo+" FROM " + handler.accountTable+" ORDER BY " + handler.accountNoNo + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> resultSet = new ArrayList<>();

        //Add account numbers to a list
        while (cursor.moveToNext())
        {
            resultSet.add(cursor.getString(cursor.getColumnIndex(handler.accountNoNo)));
        }

        cursor.close();

        //Return the list of account numbers
        return resultSet;

    }

    @Override
    public List<Account> getAccountsList() {

        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = handler.getReadableDatabase();

        //Query to select all the details about all the accounts in the account table
        String query = "SELECT * FROM " + handler.accountTable+" ORDER BY "+handler.accountNoNo+" ASC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Account> resultSet = new ArrayList<>();

        //Add account details to a list
        while (cursor.moveToNext())
        {
            Account account = new Account(cursor.getString(cursor.getColumnIndex(handler.accountNoNo)),
                    cursor.getString(cursor.getColumnIndex(handler.bankName)),
                    cursor.getString(cursor.getColumnIndex(handler.accountHolderName)),
                    cursor.getDouble(cursor.getColumnIndex(handler.balance)));

            resultSet.add(account);
        }

        cursor.close();

        //Return list of account objects
        return resultSet;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = handler.getReadableDatabase();

        //Query to get details of the account specifiec by the account number
        String query = "SELECT * FROM " + handler.accountTable + " WHERE " + handler.accountNoNo + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //add the details to an account object
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(handler.accountNoNo)),
                    cursor.getString(cursor.getColumnIndex(handler.bankName)),
                    cursor.getString(cursor.getColumnIndex(handler.accountHolderName)),
                    cursor.getDouble(cursor.getColumnIndex(handler.balance)));
        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("You have selected an invalid account number...!");
        }

        cursor.close();

        //Return the account object
        return account;
    }

    @Override
    public void addAccount(Account account) {

        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();

        //Save account details to the account table
        ContentValues values = new ContentValues();
        values.put(handler.accountNo, account.getAccountNo());
        values.put(handler.bankName, account.getBankName());
        values.put(handler.accountHolderName, account.getAccountHolderName());
        values.put(handler.balance, account.getBalance());

        db.insert(handler.accountTable, null, values);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();
        //Query to delete a particular account from the account table
        String query = "SELECT * FROM " + handler.accountTable + " WHERE " + handler.accountNoNo + " =  '" + accountNo + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //Delete the account if found in the table
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(handler.accountNoNo)),
                    cursor.getString(cursor.getColumnIndex(handler.bankName)),
                    cursor.getString(cursor.getColumnIndex(handler.accountHolderName)),
                    cursor.getFloat(cursor.getColumnIndex(handler.balance)));
            db.delete(handler.accountTable, handler.accountNo + " = ?", new String[] { accountNo });
            cursor.close();

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("No such account found...!");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        DatabaseHelper handler = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = handler.getWritableDatabase();

        ContentValues values = new ContentValues();

        //Retrieve the account details of the selected account
        Account account = getAccount(accountNo);

        //Update the balance if the account is found in the table
        if (account!=null) {

            double new_amount=0;

            //Deduct the amount is it is an expense
            if (expenseType.equals(ExpenseType.EXPENSE)) {
                new_amount = account.getBalance() - amount;
            }
            //Add the amount if it is an income
            else if (expenseType.equals(ExpenseType.INCOME)) {
                new_amount = account.getBalance() + amount;
            }

            //Query to update balance in the account table
            String strSQL = "UPDATE "+handler.accountTable+" SET "+handler.balance+" = "+new_amount+" WHERE "+handler.accountNoNo+" = '"+ accountNo+"'";

            db.execSQL(strSQL);

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("No such account found...!");
        }

    }
}