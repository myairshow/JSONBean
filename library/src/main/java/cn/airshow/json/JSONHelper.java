package cn.airshow.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 将JSON转化JSONBean对象的辅助类
 *
 * @author airshow
 */
public class JSONHelper {

    public static void fromJSON(JSONObject json, Object obj) {
        try {
            initObject(obj, json);
        } catch (Throwable e) {
            F.out(e);
        }
    }

    private static void initObject(Object object, JSONObject json) {
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    if (Modifier.isStatic(field.getModifiers())
                            || Modifier.isFinal(field.getModifiers())
                            || Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }
                    Class<?> fieldClass = field.getType();
                    String fieldName = field.getName();
                    field.setAccessible(true);
                    if (List.class.isAssignableFrom(fieldClass)) {
                        if (fieldClass.isInterface()) {
                            field.set(object, new ArrayList());
                        } else {
                            field.set(object, fieldClass.newInstance());
                        }
                    }
                    if (!json.has(fieldName)) {
                        continue;
                    }
                    if (fieldClass.equals(int.class)
                            || fieldClass.equals(Integer.class)) {
                        field.set(object, json.optInt(fieldName));
                    } else if (fieldClass.equals(boolean.class)
                            || fieldClass.equals(Boolean.class)) {
                        field.set(object, json.optBoolean(fieldName));
                    } else if (fieldClass.equals(double.class)
                            || fieldClass.equals(Double.class)) {
                        field.set(object, json.optDouble(fieldName));
                    } else if (fieldClass.equals(long.class)
                            || fieldClass.equals(Long.class)) {
                        field.set(object, json.optLong(fieldName));
                    } else if (fieldClass.equals(String.class)) {
                        field.set(object, json.optString(fieldName));
                    } else if (List.class.isAssignableFrom(fieldClass)) {
                        List list;
                        if (fieldClass.isInterface()) {
                            list = new ArrayList();
                        } else {
                            list = (List) fieldClass.newInstance();
                        }
                        JSONArray array = json.optJSONArray(fieldName);
                        if (array != null && array.length() > 0) {
                            // 获取List中成员的类型
                            String generic = field.getGenericType().toString();
                            String TClass = generic.substring(
                                    generic.indexOf('<') + 1,
                                    generic.indexOf('>'));
                            Class<?> entityClass = Class.forName(TClass);
                            if (entityClass.equals(String.class)) {
                                for (int i = 0; i < array.length(); i++) {
                                    list.add(array.optString(i));
                                }
                            } else {
                                // List<JSONBean>类型对象:递归
                                for (int i = 0; i < array.length(); i++) {
                                    Object item = entityClass.newInstance();
                                    JSONObject jsonarrayItem = array
                                            .optJSONObject(i);
                                    if (item instanceof JSONBean
                                            && jsonarrayItem != null) {
                                        ((JSONBean) item)
                                                .fromJSON(jsonarrayItem);
                                        list.add(item);
                                    }
                                }
                            }
                        }
                        field.set(object, list);
                    } else if (JSONBean.class.isAssignableFrom(fieldClass)) {
                        Object t = fieldClass.newInstance();
                        if (t instanceof JSONBean
                                && json.optJSONObject(fieldName) != null) {
                            JSONBean bean = (JSONBean) t;
                            // JSONBean类型对象:递归
                            bean.fromJSON(json.optJSONObject(fieldName));
                        }
                        field.set(object, t);
                    }
                } catch (Exception e) {
                    F.out(e);
                }
            }
        }
    }

    public static JSONObject toJSON(JSONBean object) {
        JSONObject json = new JSONObject();
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    if (Modifier.isStatic(field.getModifiers())
                            || Modifier.isFinal(field.getModifiers())
                            || Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }
                    Class<?> fieldClass = field.getType();
                    String fieldName = field.getName();
                    field.setAccessible(true);
                    if (fieldClass.equals(int.class)
                            || fieldClass.equals(Integer.class)) {
                        json.put(fieldName, field.getInt(object));
                    } else if (fieldClass.equals(boolean.class)
                            || fieldClass.equals(Boolean.class)) {
                        json.put(fieldName, field.getBoolean(object));
                    } else if (fieldClass.equals(double.class)
                            || fieldClass.equals(Double.class)) {
                        json.put(fieldName, field.getDouble(object));
                    } else if (fieldClass.equals(long.class)
                            || fieldClass.equals(Long.class)) {
                        json.put(fieldName, field.getLong(object));
                    } else if (fieldClass.equals(String.class)) {
                        json.put(fieldName, field.get(object));
                    } else if (fieldClass.equals(List.class)) {
                        JSONArray array = new JSONArray();
                        String genric = field.getGenericType().toString();
                        String TClass = genric.substring(
                                genric.indexOf('<') + 1, genric.indexOf('>'));
                        Class<?> entityClass = Class.forName(TClass);
                        if (entityClass.equals(String.class)) {
                            List<?> list = (List<?>) field.get(object);
                            for (int i = 0; i < list.size(); i++) {
                                array.put(list.get(i));
                            }
                            json.put(fieldName, list);
                        } else {
                            // List<JSONBean>类型对象:递归
                            List<?> list = (List<?>) field.get(object);
                            array = new JSONArray();
                            for (int i = 0; list != null && i < list.size(); i++) {
                                Object item = list.get(i);
                                JSONObject jsonarrayItem;
                                if (item != null && item instanceof JSONBean) {
                                    jsonarrayItem = ((JSONBean) item)
                                            .toJSONObject();
                                    array.put(jsonarrayItem);
                                }
                            }
                            json.put(fieldName, array);
                        }
                    } else {
                        Object t = field.get(object);
                        JSONObject jo = null;
                        if (t != null && t instanceof JSONBean) {
                            JSONBean bean = (JSONBean) t;
                            // JSONBean类型对象:递归
                            jo = bean.toJSONObject();
                        }
                        json.put(fieldName, jo);
                    }
                } catch (Exception e) {
                    F.out(e);
                }
            }
        }
        return json;
    }

}
