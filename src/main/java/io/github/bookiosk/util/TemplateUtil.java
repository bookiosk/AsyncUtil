package io.github.bookiosk.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.bookiosk.callback.ICallback;
import io.github.bookiosk.exception.AsyncException;
import io.github.bookiosk.worker.IWorker;
import io.github.bookiosk.wrapper.WorkerWrapper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bookiosk
 */
public class TemplateUtil {

    private static Map<String, IWorker> iWorkerMap = new HashMap<>();
    private static Map<String, ICallback> iCallbackHashMap = new HashMap<>();
    private static Map<String, WorkerWrapper> iWorkerWrapperMap = new HashMap<>();

    public static void main(String[] args) {
        loadResourceFile("test.json");
    }
    public static void loadResourceFile(String filePath) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath);
        AssertUtil.notNull(inputStream, "file is not exist");
        JsonObject jsonObject = JsonUtil.parseObj(new InputStreamReader(inputStream));
        JsonArray workArray = jsonObject.getAsJsonArray("IWorker");
        JsonArray callbackArray = jsonObject.getAsJsonArray("ICallback");
        JsonArray workWrapperArray = jsonObject.getAsJsonArray("WorkWrapper");
        for (JsonElement jsonElement : workArray) {
            JsonObject iWorker = jsonElement.getAsJsonObject();
            String iWorkerId = iWorker.get("id").getAsString();
            IWorker workerInstance = newIWorkerInstance(iWorker.get("clazz").getAsString());
            iWorkerMap.put(iWorkerId, workerInstance);
        }
        for (JsonElement jsonElement : callbackArray) {
            JsonObject iCallback = jsonElement.getAsJsonObject();
            String iCallbackId = iCallback.get("id").getAsString();
            ICallback callbackInstance = newICallBackInstance(iCallback.get("clazz").getAsString());
            iCallbackHashMap.put(iCallbackId, callbackInstance);
        }
        for (JsonElement jsonElement : workWrapperArray) {
            JsonObject workerWrapper = jsonElement.getAsJsonObject();
            String workWrapperId = workerWrapper.get("id").getAsString();
            String workerId = workerWrapper.get("workerId").getAsString();
            String callbackId = workerWrapper.get("callbackId").getAsString();
        }
    }

    /**
     *
     * @param clazz 全限定类名
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T extends IWorker> T newIWorkerInstance(String clazz) throws AsyncException {
        AssertUtil.notEmpty(clazz);
        try {
            Class<?> instanceClass = Class.forName(clazz);
            if (!IWorker.class.isAssignableFrom(instanceClass)) {
                throw new AsyncException("Invalid clazz, expect to be assigned from IWorker but got:" + instanceClass);
            }
            return (T) Class.forName(clazz).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AsyncException(e, "Instance class [{}] error!", clazz);
        }
    }

    /**
     *
     * @param clazz 全限定类名
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T extends ICallback> T newICallBackInstance(String clazz) throws AsyncException {
        AssertUtil.notEmpty(clazz);
        try {
            Class<?> instanceClass = Class.forName(clazz);
            if (!ICallback.class.isAssignableFrom(instanceClass)) {
                throw new AsyncException("Invalid clazz, expect to be assigned from ICallback but got:" + instanceClass);
            }
            return (T) Class.forName(clazz).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new AsyncException(e, "Instance class [{}] error!", clazz);
        }
    }
}
