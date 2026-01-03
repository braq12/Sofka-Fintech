package com.sofka.fintech.dominio.implementacion;


import com.sofka.fintech.dominio.PoliticaComision;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PoliticaComisionPorUmbralImpl implements PoliticaComision {

    private static final BigDecimal UMBRAL = new BigDecimal("10000");
    private static final BigDecimal TASA_MAYOR = new BigDecimal("0.05");
    private static final BigDecimal TASA_MENOR_IGUAL = new BigDecimal("0.02");

    @Override
    public BigDecimal calcular(BigDecimal monto) {
        BigDecimal tasa = (monto.compareTo(UMBRAL) > 0) ? TASA_MAYOR : TASA_MENOR_IGUAL;
        return monto.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
    }
}

