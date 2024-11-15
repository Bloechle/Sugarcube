package sugarcube.common.system.reflection;

import sugarcube.common.system.log.Log;
import sugarcube.common.data.Zen;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Set3;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.interfaces.Unjammable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Reflect implements Unjammable
{
    public static void Copy(Object from, Object to)
    {
        Copy(from, to, Object.class, true);
    }

    public static void Copy(Object from, Object to, boolean onlyPublic)
    {
        Copy(from, to, Object.class, onlyPublic);
    }

    public static void Copy(Object from, Object to, Class depth, boolean onlyPublic)
    {
        try
        {
            ArrayList<Field> fieldsA = ListFields(from.getClass(), depth, onlyPublic);
            ArrayList<Field> fieldsB = ListFields(to.getClass(), depth, onlyPublic);
            Field target;
            for (Field source : fieldsA)
                if ((target = FindAndRemove(source, fieldsB)) != null)
                {
                    boolean isRestricted = !source.isAccessible();
                    if (isRestricted)
                    {
                        source.setAccessible(true);
                        target.setAccessible(true);
                    }
                    target.set(to, source.get(from));
                    if (isRestricted)
                    {
                        source.setAccessible(false);
                        target.setAccessible(false);
                    }
                }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<Field> ListFields(Class c, Class depth, boolean onlyPublic)
    {
        ArrayList<Field> fields = new ArrayList<>();
        do
        {
            for (Field field : c.getDeclaredFields())
            {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers) && (!onlyPublic || Modifier.isPublic(modifiers)))
                    fields.add(field);
            }
        } while ((c = c.getSuperclass()) != null && c != depth);
        return fields;
    }

    public static Field FindAndRemove(Field field, List<Field> fields)
    {
        for (Iterator<Field> it = fields.iterator(); it.hasNext(); )
        {
            Field f = it.next();
            if (field.getName().equals(f.getName()) && field.getType().equals(f.getType()))
            {
                it.remove();
                return f;
            }
        }
        return null;
    }

    public static Object Get(Field field, Object bean, Object def)
    {
        Object value = def;
        try
        {
            boolean isRestricted = !field.isAccessible();
            if (isRestricted)
                field.setAccessible(true);
            try
            {
                value = field.get(bean);
                if (isRestricted)
                    field.setAccessible(false);
                return value;
            } catch (Exception e)
            {
                e.printStackTrace();
                if (isRestricted)
                    field.setAccessible(false);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public static void Set(Field field, Object bean, Object value)
    {
        new ClassField(field, bean).set(value);
    }

    public static void Set(Field field, Object bean, String value)
    {
        new ClassField(field, bean).set(value);
    }

    public static boolean HasClass(String classPath)
    {
        try
        {
            if (Class.forName(classPath) != null)
                return true;
        } catch (ClassNotFoundException ex)
        {
        }
        return false;
    }

    public static Class LoadClass(String classPath)
    {
        try
        {
            return Class.forName(classPath);
        } catch (ClassNotFoundException ex)
        {
            return null;
        }
    }

    public static StringMap<Field> FieldMap(Class cls, Class stop)
    {
        StringMap<Field> map = new StringMap<>();
        for (Field field : Fields(cls, stop))
            map.put(field.getName(), field);
        return map;
    }

    public static Field[] Fields(Class cls)
    {
        return Fields(cls, false);
    }

    public static Field[] Fields(Class cls, boolean dig)
    {
        return Fields(cls, dig ? null : cls);
    }

    public static Field[] Fields(Class root, Class stop)
    {
        Class cls = root;
        List3<Class> classes = new List3<>(cls);

        while (cls != null && (stop == null || !cls.equals(stop)))
            classes.add(cls = cls.getSuperclass());

        Set3<Field> set = new Set3<Field>();
        // we want to get parent fields first
        for (Class c : classes.reverse())
            set.addAll3(c.getDeclaredFields());

        Field[] fields = set.toArray(new Field[0]);

        // ensures cdata is last field in order to correctly write XML
        for (int i = 0; i < fields.length - 1; i++)
            if (fields[i].getName().equals("cdata"))
            {
                Field last = fields[fields.length - 1];
                fields[fields.length - 1] = fields[i];
                fields[i] = last;
                break;
            }

        return fields;
    }

    public static Field Field(Class c, String name)
    {
        try
        {
            return c.getDeclaredField(name);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object UpdateArray(Class cls, Object o, String fieldName, Object... array)
    {
        try
        {
            return UpdateArray(cls, o, o.getClass().getField(fieldName), array);
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.warn(Reflect.class, ".UpdateArray - exception: " + e.getMessage());
        }
        return null;
    }

    public static Object UpdateArray(Class cls, Object o, Field field, Object... array)
    {
        if (field != null && array != null && array.length > 0)
            try
            {
                boolean isRestricted = !field.isAccessible();
                if (isRestricted)
                    field.setAccessible(true);
                Class type = field.getType();
                if (type.isArray())
                {
                    cls = cls == null ? type.getComponentType() : cls;
                    // Log.debug(Reflect.class, ".updateArray - cls="+cls);
                    try
                    {
                        Object[] a0 = (Object[]) field.get(o);
                        int size = a0 == null ? 0 : a0.length;
                        Object[] a1 = (Object[]) Array.newInstance(cls, size + array.length);
                        System.arraycopy(a0, 0, a1, 0, a0.length);
                        for (int i = 0; i < array.length; i++)
                            a1[a0.length + i] = array[i];
                        field.set(o, a1);
                        return array[0];
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                if (isRestricted)
                    field.setAccessible(false);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        return null;
    }

    public static boolean Main(String classPath)
    {
        try
        {
            Class c = Class.forName(classPath);
            c.getMethod("main").invoke(c);
            return true;

        } catch (Exception ex)
        {
            Log.warn(Reflect.class, ".Main - main method not found: " + classPath);
        }
        return false;
    }

    public static Object Instance(final String classPath, final Object... params)
    {
        try
        {
            return Instance(Class.forName(classPath), params);
        } catch (ClassNotFoundException e)
        {
            Log.debug(Reflect.class, ".Instance - hiccup: " + classPath);
        }
        return null;
    }

    public static Object Instance(final Class c, final Object... params)
    {
        try
        {
            Class[] types = new Class[params.length];
            for (int i = 0; i < types.length; i++)
                types[i] = params[i].getClass();
            if (types.length == 0)
                return c.newInstance();
            else
            {
                Constructor constructor = c.getDeclaredConstructor(types);
                constructor.setAccessible(true);
                Object obj = constructor.newInstance(params);
                constructor.setAccessible(false);
                return obj;
            }

        } catch (Exception ex)
        {
            Log.debug(Reflect.class, ".Instance - hiccup: " + c.getName());
            ex.printStackTrace();
        }
        return null;
    }

    public static Object Method(final String classPath, final String methodName)
    {
        return Method(classPath, methodName, new Object[0]);
    }

    public static Object Method(final String classPath, final String methodName, final Object[] params)
    {
        return Method(false, classPath, methodName, params);
    }

    public static Object StaticMethod(final String classPath, final String methodName, final Object param)
    {
        return Method(true, classPath, methodName, param == null ? new Object[0] : new Object[]
                {param});
    }

    public static Object StaticMethod(final String classPath, final String methodName, final Object param, final Class type)
    {
        return Method(true, classPath, methodName, param == null ? new Object[0] : new Object[]
                {param}, type == null ? new Class[0] : new Class[]
                {type});
    }

    public static Object Method(final boolean isStatic, final String classPath, final String methodName, final Object[] params)
    {
        return Method(isStatic, classPath, methodName, params, null);
    }

    public static Object Method(final boolean isStatic, final String classPath, final String methodName, final Object[] params, Class[] types)
    {
        try
        {
            if (params != null && types == null || types.length < params.length)
            {
                types = new Class[params.length];
                for (int i = 0; i < types.length; i++)
                    types[i] = params[i].getClass();
            }
            Class c = Class.forName(classPath);
            return types == null || types.length == 0 ? c.getMethod(methodName).invoke(isStatic ? c : c.newInstance())
                    : c.getMethod(methodName, types).invoke(isStatic ? c : c.newInstance(), params);
        } catch (Exception ex)
        {
            Log.debug(Reflect.class, ".Method - method not found: " + classPath + "." + methodName + ", msg=" + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static Object Method(final Object o, final String methodName, final Object... params)
    {
        if (o == null)
            return null;
        Class[] types = new Class[params.length];
        for (int i = 0; i < types.length; i++)
            types[i] = params[i].getClass();
        try
        {
            if (types.length == 0)
                return o.getClass().getMethod(methodName).invoke(o);
            else
                return o.getClass().getMethod(methodName, types).invoke(o, params);
        } catch (Exception ex)
        {
            Log.warn(Reflect.class,
                    ".Method - method not found: " + o.getClass().getCanonicalName() + "." + methodName + ", params[" + Zen.Array.String(params) + "]");
            ex.printStackTrace();
        }
        return null;
    }
}
