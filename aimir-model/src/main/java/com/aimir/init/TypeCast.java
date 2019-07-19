package com.aimir.init;

/**
 * Java Object Type Cast Util
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
public final class TypeCast {

    public static <t> Object cast(Class<t> castClass, Object obj) {

        if (castClass.getName().equals("java.lang.Double")) {
            return new Double(obj.toString());
        } else if (castClass.getName().equals("java.lang.Integer")) {
            return new Integer(obj.toString());
        } else if (castClass.getName().equals("java.lang.Boolean")) {
            return new Boolean(obj.toString());
        } else if (castClass.getName().equals("java.lang.Long")) {
            return new Long(obj.toString());
        } else if (castClass.getName().equals("java.lang.Float")) {
            return new Float(obj.toString());
        } else if (castClass.getName().equals("java.lang.Short")) {
            return new Short(obj.toString());
        } else if (castClass.getName().equals("java.lang.Byte")) {
            return new Byte(obj.toString());
        } else if (castClass.getName().equals("java.lang.String")) {
            return obj.toString();
        }
        return obj;
    }

    public static <t> Object stringTocast(Class<t> castClass, String obj) {

        if (castClass.getName().equals("java.lang.Double")) {
            return new Double(obj.toString());
        } else if (castClass.getName().equals("java.lang.Integer")) {
            return new Integer(obj.toString());
        } else if (castClass.getName().equals("java.lang.Boolean")) {
            return new Boolean(obj.toString());
        } else if (castClass.getName().equals("java.lang.Long")) {
            return new Long(obj.toString());
        } else if (castClass.getName().equals("java.lang.Float")) {
            return new Float(obj.toString());
        } else if (castClass.getName().equals("java.lang.Short")) {
            return new Short(obj.toString());
        } else if (castClass.getName().equals("java.lang.Byte")) {
            return new Byte(obj.toString());
        }
        return obj;
    }

    public static boolean match(String fieldName) {

        if (fieldName.equals("double")) {
            return true;
        }
        if (fieldName.equals("int")) {
            return true;
        }
        if (fieldName.equals("boolean")) {
            return true;
        }
        if (fieldName.equals("long")) {
            return true;
        }
        if (fieldName.equals("float")) {
            return true;
        }
        if (fieldName.equals("short")) {
            return true;
        }
        if (fieldName.equals("byte")) {
            return true;
        }
        if (fieldName.equals("char")) {
            return true;
        }
        return false;
    }
}
