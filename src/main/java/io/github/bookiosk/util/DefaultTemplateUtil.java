package io.github.bookiosk.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.bookiosk.callback.ICallback;
import io.github.bookiosk.exception.AsyncException;
import io.github.bookiosk.worker.IWorker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bookiosk
 */
public class DefaultTemplateUtil extends TemplateUtil {

    private JsonObject jsonObject;
    
    public final ThreadLocal<JsonTemplate> localJsonTemplate = ThreadLocal.withInitial(JsonTemplate::new);

    /**
     * @param clazz 全限定类名
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public <T extends IWorker> T newIWorkerInstance(String clazz) throws AsyncException {
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
     * @param clazz 全限定类名
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public <T extends ICallback> T newICallBackInstance(String clazz) throws AsyncException {
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

    @Override
    public void changeFileContentToObject(String filePath) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath);
        AssertUtil.notNull(inputStream, "file is not exist");
        jsonObject = JsonUtil.parseObj(new InputStreamReader(inputStream));
    }

    @Override
    public void acquireIWorkerInstance() {
        JsonArray workArray = jsonObject.getAsJsonArray("IWorker");
        AssertUtil.isTrue(workArray.isEmpty(), "Not acquire any IWorker instance");
        for (JsonElement jsonElement : workArray) {
            JsonObject iWorker = jsonElement.getAsJsonObject();
            String iWorkerId = iWorker.get("id").getAsString();
            IWorker workerInstance = newIWorkerInstance(iWorker.get("clazz").getAsString());
            localJsonTemplate.get().getiWorkerHashMap().put(iWorkerId, workerInstance);
        }
    }

    @Override
    public void acquireICallbackInstance() {
        JsonArray callbackArray  = jsonObject.getAsJsonArray("ICallback");
        if (callbackArray == null || callbackArray.isEmpty()) {
            return;
        }
        for (JsonElement jsonElement : callbackArray) {
            JsonObject iCallback = jsonElement.getAsJsonObject();
            String iCallbackId = iCallback.get("id").getAsString();
            ICallback callbackInstance = newICallBackInstance(iCallback.get("clazz").getAsString());
            localJsonTemplate.get().getiCallbackHashMap().put(iCallbackId, callbackInstance);
        }
    }

    @Override
    public void acquireWorkerWrapperInstance() {
        JsonArray workWrapperArray = jsonObject.getAsJsonArray("WorkWrapper");
        AssertUtil.isTrue(workWrapperArray.isEmpty(), "Not acquire any WorkWrapper instance");
        for (JsonElement jsonElement : workWrapperArray) {
            JsonObject workerWrapper = jsonElement.getAsJsonObject();
            String workerWrapperId = workerWrapper.get("id").getAsString();
            String workerId = workerWrapper.get("workerId").getAsString();
            String callbackId = workerWrapper.get("callbackId").getAsString();
            // todo 这里后面借鉴下Spring的三级缓存的使用初始化wrapper
        }
    }

    public class JsonTemplate {
        private HashMap<String, IWorker> iWorkerHashMap = new HashMap<>();
        private HashMap<String, ICallback> iCallbackHashMap = new HashMap<>();

        public JsonTemplate() {

        }

        public HashMap<String, IWorker> getiWorkerHashMap() {
            return iWorkerHashMap;
        }

        public HashMap<String, ICallback> getiCallbackHashMap() {
            return iCallbackHashMap;
        }
    }
}
