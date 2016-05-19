/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.conf;

/**
 *
 * @author LMO-PC
 */
public class StringUtils {

    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1
                && Character.isUpperCase(name.charAt(1))
                && Character.isUpperCase(name.charAt(0))) {

            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String join(String splitter, Object... os) {
        StringBuilder sb = new StringBuilder();
        for (Object o : os) {
            if (o != null) {
                if (sb.length() != 0) {
                    sb.append(splitter);
                }
                sb = sb.append(o);
            }
        }
        return sb.toString();
    }

    public static void main(String args[]) {
        System.out.println(StringUtils.join(",", "a", "b", "c", null, "d"));
    }
}
