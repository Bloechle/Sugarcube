package sugarcube.common.ui.fx.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface _MouseY
{
    String action() default "click";
}
