package timeOut;


import com.google.gson.Gson;
import depend.User;
import io.github.bookiosk.executor.Async;
import io.github.bookiosk.wrapper.WorkerWrapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;


/**
 * bookiosk wrote on 2024-02-27
 */
public class Test {

    public Test() {
        super();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Gson gson = new Gson();
        User user = gson.fromJson("{name:sdadsa}", User.class);
        System.out.println(user);
    }
}
