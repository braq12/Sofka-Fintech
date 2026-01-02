package com.sofka.fintech.dominio;

import java.math.BigDecimal;

public interface PoliticaComision {
    BigDecimal calcular(BigDecimal monto);
}
