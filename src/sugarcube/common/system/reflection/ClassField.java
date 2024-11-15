package sugarcube.common.system.reflection;

import javafx.beans.property.*;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.interfaces.Validable;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.beans.*;
import sugarcube.common.data.xml.Nb;

import java.awt.*;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ClassField implements Validable, Unjammable
{
    private Field field;
    private Object bean;
    private Class cls;
    private Object obj;
    private boolean valid = true;

    public ClassField(Field field, Object bean)
    {
        this.field = field;
        this.bean = bean;
        try
        {
            boolean access = field.isAccessible();
            if (!access)
                field.setAccessible(true);
            cls = field.getType();
            obj = field.get(bean);
            if (!access)
                field.setAccessible(false);
        } catch (Exception e)
        {
            e.printStackTrace();
            valid = false;
        }
    }

    @Override
    public boolean isValid()
    {
        return valid;
    }

    public boolean hasValue()
    {
        return obj != null;
    }

    public Object bean()
    {
        return bean;
    }

    public Object obj()
    {
        return obj;
    }

    public Object object()
    {
        return obj;
    }

    public String name()
    {
        return field.getName();
    }

    public String labelName()
    {
        String name=Str.Labelize(name());
        if(name.isEmpty())
            return "";
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public boolean hasAnnot(Class<? extends Annotation> annot)
    {
        return field.isAnnotationPresent(annot);
    }

    public boolean isClass(Class cls)
    {
        return obj != null && cls != null && obj.getClass().equals(cls);
    }

    public boolean isString()
    {
        return cls == String.class || obj instanceof StringProperty;
    }

    public boolean isReal()
    {
        return cls == float.class || cls == double.class || cls == Float.class || cls == Double.class || obj instanceof FloatProperty
                || obj instanceof DoubleProperty;
    }

    public boolean isInteger()
    {
        return cls == int.class || cls == Integer.class || obj instanceof IntegerProperty;
    }

    public boolean isBool()
    {
        return cls == boolean.class || cls == Boolean.class || obj instanceof BooleanProperty;
    }

    public boolean isProperty()
    {
        return obj instanceof Property;
    }

    public String value()
    {
        if (cls == String.class)
            return (String) obj;
        if (obj instanceof StringProperty)
            return ((StringProperty) obj).get();
        return obj == null ? null : obj.toString();
    }

    public float real()
    {
        try
        {
            if (cls == float.class)
                return field.getFloat(bean);
            if (cls == double.class)
                return (float) field.getDouble(bean);
            if (cls == int.class)
                return field.getInt(bean);
            if (cls == Float.class)
                return (Float) obj;
            if (cls == Double.class)
                return (float) (double) ((Double) obj);
            if (cls == Integer.class)
                return (int) ((Integer) obj);
            if (cls == String.class)
                return Nb.Float((String) obj);
            if (obj instanceof FloatProperty)
                return ((FloatProperty) obj).get();
            if (obj instanceof DoubleProperty)
                return (float) ((DoubleProperty) obj).get();
            if (obj instanceof IntegerProperty)
                return ((IntegerProperty) obj).get();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public int integer()
    {
        try
        {
            if (cls == int.class)
                return field.getInt(bean);
            if (cls == Integer.class)
                return (Integer) obj;
            if (obj instanceof IntegerProperty)
                return ((IntegerProperty) obj).get();
            if (cls == float.class)
                return Math.round(field.getFloat(bean));
            if (cls == Float.class)
                return Math.round((Float) obj);
            if (obj instanceof IntegerProperty)
                return Math.round(((FloatProperty) obj).get());
            if (cls == double.class)
                return (int) Math.round(field.getDouble(bean));
            if (cls == Double.class)
                return (int) Math.round((Double) obj);
            if (obj instanceof IntegerProperty)
                return (int) Math.round(((DoubleProperty) obj).get());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean bool()
    {
        try
        {
            if (cls == boolean.class)
                return field.getBoolean(bean);
            if (cls == Boolean.class)
                return (Boolean) obj;
            if (obj instanceof BooleanProperty)
                return ((BooleanProperty) obj).get();
            return Nb.Bool(value(), false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString()
    {
        if (obj != null && obj instanceof Property)
            obj = ((Property) obj).getValue();
        return obj == null ? null : obj.toString();
    }

    public void set(String value)
    {
        try
        {
            // Log.debug(this,
            // ".set - "+cls.getSimpleName()+" "+this.field.getName()+"="+value);
            boolean isRestricted = !field.isAccessible();
            if (isRestricted)
                field.setAccessible(true);
            if (cls == String.class)
                field.set(bean, value);
            else if (cls == double.class)
                field.setDouble(bean, Nb.toDouble(value));
            else if (cls == Double.class)
                field.set(bean, (Double) Nb.toDouble(value));
            else if (cls == float.class)
                field.setFloat(bean, Nb.Float(value));
            else if (cls == Float.class)
                field.set(bean, (Float) Nb.Float(value));
            else if (cls == int.class)
                field.setInt(bean, Nb.Int(value));
            else if (cls == Integer.class)
                field.set(bean, (Integer) Nb.Int(value));
            else if (cls == boolean.class)
                field.setBoolean(bean, Nb.Bool(value));
            else if (cls == Boolean.class)
                field.set(bean, (Boolean) Nb.Bool(value));
            else if (StringProperty.class.isAssignableFrom(cls))
                field.set(bean, PString.New(value));
            else if (BooleanProperty.class.isAssignableFrom(cls))
                field.set(bean, PBool.New(value));
            else if (IntegerProperty.class.isAssignableFrom(cls))
                field.set(bean, PInteger.New(value));
            else if (FloatProperty.class.isAssignableFrom(cls))
                field.set(bean, PFloat.New(value));
            else if (DoubleProperty.class.isAssignableFrom(cls))
                field.set(bean, PDouble.New(value));
            if (isRestricted)
                field.setAccessible(false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean set(Object value)
    {
        try
        {
            boolean isRestricted = !field.isAccessible();
            if (isRestricted)
                field.setAccessible(true);
            try
            {
                field.set(bean, cast(value, field.getType()));
                if (isRestricted)
                    field.setAccessible(false);
                return true;
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
        return false;
    }

    public static Object cast(Object value, Class cls)
    {
        if (value == null || value instanceof Bean)
            return value;
        if (cls.equals(String.class))
            value = value instanceof String ? (String) value : value.toString();
        else if (cls.equals(Boolean.class) || cls.equals(boolean.class))
            value = value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(value.toString());
        else if (cls.equals(Byte.class) || cls.equals(byte.class))
            value = value instanceof Byte ? (Byte) value : Byte.parseByte(value.toString());
        else if (cls.equals(Short.class) || cls.equals(short.class))
            value = value instanceof Short ? (Short) value : Short.parseShort(value.toString());
        else if (cls.equals(Float.class) || cls.equals(float.class))
            value = value instanceof Float ? (Float) value : Nb.Float(value.toString());
        else if (cls.equals(Double.class) || cls.equals(double.class))
            value = value instanceof Double ? (Double) value : Nb.toDouble(value.toString());
        else if (cls.equals(Integer.class) || cls.equals(int.class))
            value = value instanceof Integer ? (Integer) value : Nb.Int(value.toString());
        else if (cls.equals(Long.class) || cls.equals(long.class))
            value = value instanceof Long ? (Long) value : Long.parseLong(value.toString());
        else if (cls.equals(Character.class) || cls.equals(char.class))
            value = value instanceof Character ? (Character) value : value.toString().charAt(0);
        else if (cls.equals(Color3.class) || cls.equals(Color.class))
            value = value instanceof Color3 ? (Color3) value : new Color3(value.toString());
        else if (cls.equals(File3.class) || cls.equals(File.class))
            value = value instanceof File3 ? (File3) value : new File3(value.toString());
        else if (cls.equals(float[].class))
            value = value instanceof float[] ? (float[]) value : Nb.toFloats(value.toString());
        return value;
    }

    public static void copy(Field field, Object src, Object dest)
    {
        ClassField f0 = ClassField.Get(field, src);
        ClassField f1 = ClassField.Get(field, dest);
        f1.set(f0.toString());
    }

    public static FieldMap Map(Object bean, boolean dig)
    {
        FieldMap map = new FieldMap();
        for (ClassField field : Get(bean, dig))
            map.put(field.name(), field);
        return map;
    }

    public static ClassField[] Get(Object bean, boolean dig)
    {
        List3<ClassField> list = new List3<>();
        for (Field f : Reflect.Fields(bean.getClass(), dig))
            list.addNonNull(GetValid(f, bean));
        return list.toArray(new ClassField[0]);
    }

    public static ClassField GetValid(Field field, Object bean)
    {
        ClassField f3 = new ClassField(field, bean);
        return f3.valid ? f3 : null;
    }

    public static ClassField Get(Field field, Object bean)
    {
        return new ClassField(field, bean);
    }

    public static boolean GetBool(Field field, Object bean)
    {
        return Get(field, bean).bool();
    }

    public static float GetFloat(Field field, Object bean)
    {
        return Get(field, bean).real();
    }

    public static String GetString(Field field, Object bean)
    {
        return Get(field, bean).toString();
    }

    public static ClassField SetValue(Field field, Object bean, Object value)
    {
        ClassField f = Get(field, bean);
        f.set(value);
        return f;
    }

    public static void main(String... args)
    {
        Log.debug(ClassField.class, " - " + new SimpleIntegerProperty(10).toString());
    }

}
