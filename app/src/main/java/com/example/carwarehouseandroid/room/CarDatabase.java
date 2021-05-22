package com.example.carwarehouseandroid.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.carwarehouseandroid.Model.Car;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Car.class},version = 1)
public abstract class CarDatabase extends RoomDatabase {
    private static CarDatabase instance;

    public abstract CarDao carDao();

    public static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static synchronized CarDatabase getInstance(Context context){
        if (instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(),
                    CarDatabase.class,"car_database").fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }

    private static CarDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsynTask(instance).execute();
        }
    };


    private static class PopulateDbAsynTask extends AsyncTask<Void,Void,Void> {
        private CarDao carDao;
        private PopulateDbAsynTask(CarDatabase db){
            carDao=db.carDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
           // carDao.insert(new Car("Title 1","Description 1",1));
            //carDao.insert(new Car("Title 2","Description 2",2));
            //carDao.insert(new Car("Title 3","Description 3",3));
            carDao.deleteAllCars();
            return null;
        }
    }

}
