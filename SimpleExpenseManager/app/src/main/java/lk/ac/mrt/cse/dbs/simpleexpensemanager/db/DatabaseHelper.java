package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Thilan K Bandara on 11/20/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    protected static final String dbName = "140066T_db";
    private static DatabaseHelper databaseHelper = null;
    private static final int database_version = 1;

    public static final String accountTable = "Accounts";
    public static final String accountNoNo = "accountNo";
    public static final String bankName = "bankName";
    public static final String accountHolderName = "accountHolderName";
    public static final String balance = "balance";

    public static final String transactionTable = "transations";
    public static final String transaction_id = "transaction_id";
    public static final String date = "date";
    public static final String accountNo = "accountNo";
    public static final String expenseType = "expenseType";
    public static final String amount = "amount";


    public DatabaseHelper(Context context) {
        super(context, dbName, null, database_version);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(context);
        return databaseHelper;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String accountTable = String.format("CREATE TABLE %s(%s VARCHAR(20) NOT NULL PRIMARY KEY,%s VARCHAR(100) NULL,%s VARCHAR(100) NULL,%s DECIMAL(10,2) NULL )", "Accounts", accountNoNo, bankName, accountHolderName, balance);

        String transactionTable = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,%s VARCHAR(100) NOT NULL,%s DATE NULL,%s DECIMAL(10,2) NULL,%s VARCHAR(100) NULL, FOREIGN KEY(%s) REFERENCES %s(%s))", "transactions", transaction_id, accountNo, date, amount, expenseType, accountNo, accountTable, accountNoNo);

        sqLiteDatabase.execSQL(accountTable);
        sqLiteDatabase.execSQL(transactionTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + accountTable);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + transactionTable);
        onCreate(sqLiteDatabase);

    }
}
