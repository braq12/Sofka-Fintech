package com.sofka.fintech.dominio.implementacion;

import com.sofka.fintech.dominio.PoliticaComision;
import com.sofka.fintech.dominio.constantes.ComisionConstantes;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PoliticaComisionPorUmbralImpl implements PoliticaComision {

    @Override
    public BigDecimal calcular(BigDecimal monto) {
        BigDecimal tasa = (monto.compareTo(ComisionConstantes.UMBRAL) > 0)
                ? ComisionConstantes.TASA_MAYOR
                : ComisionConstantes.TASA_MENOR_IGUAL;

        return monto.multiply(tasa).setScale(2, RoundingMode.HALF_UP);
    }
}
