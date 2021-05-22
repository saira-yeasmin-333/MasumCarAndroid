package com.example.carwarehouseandroid.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.carwarehouseandroid.Model.Car;

import java.util.List;


public class CarRepositoryRoom {
    private CarDao carDao;
    private LiveData<List<Car>> allCars;
    public CarRepositoryRoom(Application application){
        CarDatabase database=CarDatabase.getInstance(application);
        carDao=database.carDao();
        allCars=carDao.getALLCars();
    }

    public void insert(Car car){
        new InsertCarAsyncTask(carDao).execute(car);
    }

    public void update(Car car){
        new UpdateCarAsyncTask(carDao).execute(car);
    }
    public void delete(Car car){
        new DeleteCarAsyncTask(carDao).execute(car);
    }
    public void deleteAllCars(){
        new DeleteAllCarsAsyncTask(carDao).execute();
    }

    public LiveData<List<Car>> getAllCars() {
        return allCars;
    }

    private static class InsertCarAsyncTask extends AsyncTask<Car,Void,Void> {
        private CarDao carDao;
        private InsertCarAsyncTask(CarDao carDao){
            this.carDao=carDao;
        }
        @Override
        protected Void doInBackground(Car... cars) {
            carDao.insert(cars[0]);
            return null;
        }
    }

    private static class UpdateCarAsyncTask extends AsyncTask<Car,Void,Void>{
        private CarDao carDao;
        private UpdateCarAsyncTask(CarDao carDao){
            this.carDao=carDao;
        }
        @Override
        protected Void doInBackground(Car... cars) {
            carDao.update(cars[0]);
            return null;
        }
    }

    private static class DeleteCarAsyncTask extends AsyncTask<Car,Void,Void>{
        private CarDao carDao;
        private DeleteCarAsyncTask(CarDao carDao){
            this.carDao=carDao;
        }
        @Override
        protected Void doInBackground(Car... cars) {
            carDao.delete(cars[0]);
            return null;
        }
    }

    private static class DeleteAllCarsAsyncTask extends AsyncTask<Void,Void,Void>{
        private CarDao carDao;
        private DeleteAllCarsAsyncTask(CarDao carDao){
            this.carDao=carDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            carDao.deleteAllCars();
            return null;
        }
    }
}
