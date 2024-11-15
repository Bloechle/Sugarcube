package sugarcube.common.numerics;

import sugarcube.common.interfaces.Unjammable;

public interface Evaluable extends Unjammable
{
    Evaluable DIV2 = (value, coords) -> value / 2.0;

    Evaluable DIV3 = (value, coords) -> value / 3.0;

    Evaluable ABS = (value, coords) -> Math.abs(value);

    Evaluable SQR = (value, coords) -> value * value;

    Evaluable SQRT = (value, coords) -> Math.sqrt(value);

    Evaluable ATAN = (value, coords) -> Math.atan(value);

    Evaluable TANH = (value, coords) -> Math.tanh(value);

    Evaluable LOG = (value, coords) -> Math.log(value);

    Evaluable LOG1P = (value, coords) -> Math.log1p(value);

    Evaluable SIGMOID = new Sigmoid();

    Evaluable DSIGMOID = new DSigmoid();

    class Sigmoid implements Evaluable
    {
        private final double a;
        private final double b;

        public Sigmoid()
        {
            this(1.716, 2.0 / 3.0); //Duda-Hart values for ANN. Never change these values!!!
        }

        public Sigmoid(double a, double b)
        {
            this.a = a;
            this.b = b;
        }

        @Override
        public double eval(double value, int... coords)
        {
            return a * Math.tanh(b * value);
        }
    }

    class DSigmoid implements Evaluable
    {
        private final double a;
        private final double b;

        public DSigmoid()
        {
            this(1.716, 2.0 / 3.0); //Duda-Hart values for ANN. Never change these values!!!
        }

        public DSigmoid(double a, double b)
        {
            this.a = a;
            this.b = b;
        }

        @Override
        public double eval(double value, int... coords)
        {
            double x = Math.cosh(b * value);
            return a * b / (x * x);
        }
    }

    double eval(double value, int... coords);
}

