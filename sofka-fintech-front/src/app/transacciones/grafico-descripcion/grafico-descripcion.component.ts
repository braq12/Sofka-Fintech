import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

import * as Highcharts from 'highcharts';
import { HighchartsChartComponent } from 'highcharts-angular';

import { RespuestaTransaccion } from '../servicio-transacciones.service';
// Optimización y lógica de código apoyadas en IA
type AcumuladoDescripcion = {
  descripcion: string;
  total: number;
  cantidad: number;
  promedio: number;
};

@Component({
  selector: 'app-grafico-descripcion',
  standalone: true,
  imports: [CommonModule, HighchartsChartComponent],
  templateUrl: './grafico-descripcion.component.html',
})
export class GraficoDescripcionComponent implements OnChanges {
  @Input({ required: true }) transacciones: RespuestaTransaccion[] = [];

  private readonly colores: string[] = [
    '#2f7ed8',
    '#0d233a',
    '#8bbc21',
    '#910000',
    '#1aadce',
    '#492970',
    '#f28f43',
    '#77a1e5',
    '#c42525',
    '#a6c96a',
  ];

  updateFlag = false;

  opciones: Highcharts.Options = {};

  ngOnChanges(): void {
    this.construirGrafico();
    this.updateFlag = true;
  }

  private construirGrafico(): void {
    const acumulados = this.acumularPorDescripcion(this.transacciones);

    const categorias = acumulados.map((x) => x.descripcion);
    const serieTotales = acumulados.map((x) => Number(x.total.toFixed(2)));
    const seriePromedios = acumulados.map((x) => Number(x.promedio.toFixed(2)));

    this.opciones = {
      title: { text: 'Transacciones por descripción' },
      xAxis: { categories: categorias, title: { text: null } },
      yAxis: [
        {
          title: { text: 'Monto total' },
          labels: {
            formatter: function () {
              return Number(this.value).toLocaleString('es-CO');
            },
          },
        },
        {
          title: { text: 'Promedio' },
          labels: {
            formatter: function () {
              return Number(this.value).toLocaleString('es-CO');
            },
          },
          opposite: true,
        },
      ],
      tooltip: {
        shared: true,
        formatter: function () {
          const puntos = this.points ?? [];
          const nombre = String(this.x ?? '');

          const total = puntos.find((p) => p.series.name === 'Total')?.y ?? 0;
          const promedio =
            puntos.find((p) => p.series.name === 'Promedio')?.y ?? 0;

          // Buscar cantidad (la llevamos en serie custom en "custom")
          const cantidad =
            (this as any).points?.[0]?.point?.custom?.cantidad ?? null;

          const totalFmt = Number(total).toLocaleString('es-CO', {
            maximumFractionDigits: 2,
          });
          const promedioFmt = Number(promedio).toLocaleString('es-CO', {
            maximumFractionDigits: 2,
          });

          const partes = [
            `<b>${nombre}</b>`,
            `Total: ${totalFmt}`,
            `Promedio: ${promedioFmt}`,
          ];

          if (cantidad !== null) {
            partes.push(`Cantidad: ${cantidad}`);
          }

          return partes.join('<br/>');
        },
      },
      legend: { enabled: true },
      plotOptions: {
        column: {
          borderRadius: 6,
        },
        series: {
          animation: false,
        },
      },
      series: [
        {
          type: 'column',
          name: 'Total',
          data: acumulados.map((x,i) => ({
            y: Number(x.total.toFixed(2)),
            color: this.colores[i % this.colores.length],
            custom: { cantidad: x.cantidad },
          })),
        },
        {
          type: 'spline',
          name: 'Promedio',
          yAxis: 1,
          marker: { enabled: true },
          data: seriePromedios,
        },
      ],
      credits: { enabled: false },
    };
  }

  private acumularPorDescripcion(
    lista: RespuestaTransaccion[]
  ): AcumuladoDescripcion[] {
    const mapa = new Map<string, { total: number; cantidad: number }>();

    for (const t of lista) {
      const descripcion = (t.descripcion ?? 'Sin descripción').trim();
      const monto = Number(t.monto) || 0;

      const actual = mapa.get(descripcion) ?? { total: 0, cantidad: 0 };
      actual.total += monto;
      actual.cantidad += 1;
      mapa.set(descripcion, actual);
    }

    const acumulados: AcumuladoDescripcion[] = Array.from(mapa.entries()).map(
      ([descripcion, v]) => ({
        descripcion,
        total: v.total,
        cantidad: v.cantidad,
        promedio: v.cantidad > 0 ? v.total / v.cantidad : 0,
      })
    );

    // Ordenar por total descendente para que se vea bien
    acumulados.sort((a, b) => b.total - a.total);

    return acumulados;
  }
}
