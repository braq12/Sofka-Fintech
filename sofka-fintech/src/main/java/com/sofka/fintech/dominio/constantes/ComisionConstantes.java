package com.sofka.fintech.dominio.constantes;

import java.math.BigDecimal;

public final class ComisionConstantes {

    public static final BigDecimal UMBRAL = new BigDecimal("10000");
    public static final BigDecimal TASA_MAYOR = new BigDecimal("0.05");
    public static final BigDecimal TASA_MENOR_IGUAL = new BigDecimal("0.02");
    private ComisionConstantes() {
    }
}
