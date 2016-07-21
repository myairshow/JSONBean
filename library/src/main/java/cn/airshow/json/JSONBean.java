package cn.airshow.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 继承这个类，可以使用fromJSON方法解析json数据，也可以使用toJSONObject方法将对象封装成json数据。 <br>
 * 实体类字段名需与JSON数据字段名保持一致，支持的类型有int、long、double、String以及继承JSONBean的其它实体类，
 * 需要使用public字段进行修饰， 修饰符不能使用final、static <br>
 * 支持List数组类型,List需要指明成员类型:<br>
 * 如List&lt;int>,List&lt;long>,List&lt;String>,List&lt; double><br>
 * 以及List&lt;class extends JSONBean><br>
 * 
 * @author liuxunlan
 * 
 */
public abstract class JSONBean {
	/**
	 * 解析json数据
	 * 
	 * @param json
	 */
	public void fromJSON(JSONObject json) {
		JSONHelper.fromJSON(json, this);
	}

	/**
	 * 解析json数据
	 * 
	 * @param json
	 */
	public void fromJSON(String text) {
		try {
			fromJSON(new JSONObject(text));
		} catch (Exception e) {
			F.out(e);
		}
	}

	/**
	 * 包装成json数据
	 * 
	 * @return
	 */
	public JSONObject toJSONObject() {
		return JSONHelper.toJSON(this);
	}

	/**
	 * 将JSON数组转成JSONBean数组
	 * 
	 * @param jsonArray
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getList(JSONArray jsonArray, Class<?> clazz) {
		T t = null;
		List<Object> list = new ArrayList<Object>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jobj = jsonArray.getJSONObject(i);
				Object obj = clazz.newInstance();
				if (obj instanceof JSONBean) {
					((JSONBean) obj).fromJSON(jobj);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			F.out(e);
		}
		t = (T) list;
		return t;
	}

	/**
	 * 将JSON数组转成JSONBean数组
	 * 
	 */
	public static <T> T getList(String json, Class<?> clazz) {
		try {
			return getList(new JSONArray(json), clazz);
		} catch (JSONException e) {
			F.out(e);
		}
		return null;
	}


	/**
	 * 将JSON对象转成JSONBean对象
	 * 
	 * @param jsonArray
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getJSONBean(JSONObject json, Class<?> clazz) {
		T bean = null;
		Object obj = null;
		try {
			obj = clazz.newInstance();
			if (obj instanceof JSONBean) {
				((JSONBean) obj).fromJSON(json);
			}
		} catch (Exception e) {
			F.out(e);
		} finally {
			bean = (T) obj;
		}
		return (T) bean;
	}
	

	/**
	 * 将JSON对象转成JSONBean对象
	 * 
	 */
	public static <T> T getJSONBean(String json, Class<?> clazz) {
		try {
			return getJSONBean(new JSONObject(json), clazz);
		} catch (JSONException e) {
			F.out(e);
		} catch (Exception e) {
			F.out(e);
		}
		return null;
	}

	@Override
	public String toString() {
		return toJSONObject().toString();
	}

}
