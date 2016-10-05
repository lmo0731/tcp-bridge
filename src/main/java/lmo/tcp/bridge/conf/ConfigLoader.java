/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class ConfigLoader {

    public static Logger logger = Logger.getLogger("CONFIG");

    public static Properties load(Object o, String p, String prefix) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream(p);
        try {
            prop.load(new InputStreamReader(in, "UTF-8"));
            ConfigLoader.load(o, prop, prefix);
            return prop;
        } finally {
            try {
                in.close();
            } catch (Throwable ex) {
            }
        }
    }

    public static int load(Object o, Properties p) {
        return load(o, p, null);
    }

    public static Properties load(Object o, String p) throws FileNotFoundException, IOException {
        return load(o, p, null);
    }

    public static int load(Object o, Properties p, String prefix) {
        Logger logger = ConfigLoader.logger;
        if (prefix != null) {
            logger = Logger.getLogger(ConfigLoader.logger.getName() + "." + prefix);
        }
        int ret = 0;
        Class c;
        if (o.getClass() == Class.class) {
            c = (Class) o;
        } else {
            c = o.getClass();
        }
        for (Method f : c.getMethods()) {
            if (!f.getName().startsWith("get") || f.getName().length() < 4 || f.getParameterTypes().length > 0) {
                continue;
            }
            String key = f.getName();
            key = key.substring(3);
            Method sf = null;
            try {
                sf = c.getMethod("set" + key, f.getReturnType());
                if (!sf.getReturnType().equals(Void.TYPE)) {
                    continue;
                }
            } catch (Throwable ex) {
                continue;
            }
            key = StringUtils.decapitalize(key);
            key = (prefix == null ? "" : (prefix + ".")) + key;

            boolean required = false;
            boolean secure = false;
            if (f.isAnnotationPresent(ConfigName.class)) {
                ConfigName name = f.getAnnotation(ConfigName.class);
                if (!name.value().equals("##default")) {
                    key = (prefix == null ? "" : (prefix + ".")) + name.value();
                }
                required = name.required();
                secure = name.secure();
            }
            String value = p.getProperty(key);
            if (value != null) {
                try {
                    Type[] cs = new Type[]{};
                    try {
                        if (f.getGenericReturnType() instanceof ParameterizedType) {
                            ParameterizedType pType = (ParameterizedType) f.getGenericReturnType();
                            cs = pType.getActualTypeArguments();
                            //logger.debug(o + "." + f.getName() + " generic type: " + Arrays.toString(cs));
                        }
                    } catch (Throwable ex) {
                        logger.error(key + "", ex);
                    }
                    Object v = parse(f.getReturnType(), value, p, key, cs);
                    if (v != null) {
                        sf.invoke(o, v);
                        logger.debug(key + " = " + (secure ? "****" : f.invoke(o)));
                        ret++;
                    }
                } catch (Throwable ex) {
                    logger.error("", ex);
                    continue;
                }
            } else {
                if (required) {
                    try {
                        if (f.invoke(o) == null) {
                            logger.warn(key + " is required");
                            throw new NullPointerException(key + " is required");
                        }
                    } catch (IllegalArgumentException ex) {
                    } catch (IllegalAccessException ex) {
                    } catch (InvocationTargetException ex) {
                    }
                }
            }
        }
        for (Field f : c.getFields()) {
            String key = (prefix == null ? "" : (prefix + ".")) + f.getName();
            boolean required = false;
            boolean secure = false;
            if (f.isAnnotationPresent(ConfigName.class)) {
                ConfigName name = f.getAnnotation(ConfigName.class);
                if (!name.value().equals("##default")) {
                    key = (prefix == null ? "" : (prefix + ".")) + name.value();
                }
                required = name.required();
                secure = name.secure();
            }
            String value = p.getProperty(key);
            if (value != null) {
                try {
                    Type[] cs = new Type[]{};
                    try {
                        if (f.getGenericType() instanceof ParameterizedType) {
                            ParameterizedType pType = (ParameterizedType) f.getGenericType();
                            cs = pType.getActualTypeArguments();
                            //logger.debug(o + "." + f.getName() + " generic type: " + Arrays.toString(cs));
                        }
                    } catch (Throwable ex) {
                        logger.error(key + "", ex);
                    }
                    Object v = parse(f.getType(), value, p, key, cs);
                    if (v != null) {
                        f.set(o, v);
                        logger.debug(key + " = " + (secure ? "****" : f.get(o)));
                        ret++;
                    }
                } catch (Throwable ex) {
                    logger.error("", ex);
                    continue;
                }
            } else {
                if (required) {
                    try {
                        if (f.get(o) == null) {
                            logger.warn(key + " is required");
                            throw new NullPointerException(key + " is required");
                        }
                    } catch (IllegalArgumentException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                }
            }
        }
        return ret;
    }

    public static Object parse(Class type, String value, Properties p, String prefix, Type... genericTypes) {
        Logger logger = ConfigLoader.logger;
        if (prefix != null) {
            logger = Logger.getLogger(ConfigLoader.logger.getName() + "." + prefix);
        }
        Object v = null;
        if (type == Integer.class || type == Integer.TYPE) {
            v = Integer.parseInt(value);
        } else if (type == Long.class || type == Long.TYPE) {
            v = Long.parseLong(value);
        } else if (type == Float.class || type == Float.TYPE) {
            v = Float.parseFloat(value);
        } else if (type == Double.class || type == Double.TYPE) {
            v = Double.parseDouble(value);
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            v = Boolean.parseBoolean(value);
        } else if (type == Byte.class || type == Byte.TYPE) {
            v = Byte.parseByte(value);
        } else if (type == Short.class || type == Short.TYPE) {
            v = Short.parseShort(value);
        } else if (type == String.class) {
            v = value;
        } else if (type.isArray()) {
            if (!type.getComponentType().isPrimitive()) {
                String[] splitted = split(value, ",");
                Object[] o = (Object[]) Array.newInstance(type.getComponentType(), splitted.length);
                for (int i = 0; i < splitted.length; i++) {
                    o[i] = (parse(type.getComponentType(), splitted[i].trim(), p, prefix));
                }
                v = o;
            } else {
                logger.warn("Unsupported array type: " + type.getComponentType() + "[]");
            }
        } else if (List.class.isAssignableFrom(type)) {
            String[] splitted = split(value, ",");
            List o;
            try {
                o = (List) type.newInstance();
            } catch (Throwable ex) {
                o = new ArrayList<Object>();
            }
            for (int i = 0; i < splitted.length; i++) {
                Object e = (parse((Class) genericTypes[0], splitted[i].trim(), p, prefix));
                o.add(e);
            }
            v = o;
        } else {
            logger.warn("Unsupported type: " + type);
        }
        return v;
    }

    public static String[] split(String str, String split) {
        String[] splitted = str.split(split);
        Character k = null;
        StringBuilder sb = new StringBuilder();
        LinkedList<String> list = new LinkedList<String>();
        for (int j = 0; j < splitted.length; j++) {
            String value = splitted[j];
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (k == null) {
                    if (c == '\"' || c == '\'') {
                        k = c;
                    }
                } else if (c == k) {
                    k = null;
                }
                sb.append(c);
            }
            if (k == null) {
                list.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(split);
            }
        }
        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list.toArray(new String[]{});
    }
}
