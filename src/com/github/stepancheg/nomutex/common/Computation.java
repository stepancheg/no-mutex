package com.github.stepancheg.nomutex.common;

import java.math.BigInteger;

/**
 * @author Stepan Koltsov
 */
public class Computation {
    private BigInteger sum = BigInteger.valueOf(Long.MAX_VALUE);

    public BigInteger getSum() {
        return sum;
    }

    public void update(BigInteger param) {
        sum = sum.multiply(param);
    }
}
