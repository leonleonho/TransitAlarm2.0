package com.example.leon.transitalarm20;

/**
 * Created by Jens on 2/9/2015.
 */
public interface AsyncTaskCompletedListener<T> {
    public void onTaskComplete(T result, Translink.TaskType type);
}
