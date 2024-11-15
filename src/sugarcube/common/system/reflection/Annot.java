package sugarcube.common.system.reflection;

import sugarcube.common.interfaces.Unjammable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Annot implements Unjammable
{

    @Retention(RetentionPolicy.RUNTIME)
    public @interface _Xml
    {
        String tag() default "";

        String key() default "";

        String ns() default "";

        String def() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface _Bean
    {
        String name() default "";

        String desc() default "";

        boolean hide() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface _Json
    {
        String key() default "";

        boolean hide() default false;
    }

}
