import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';

import { ChartModule } from 'primeng/chart';

import { FormularioTransaccionComponent } from '../formulario-transaccion/formulario-transaccion.component';
import {
  RespuestaTransaccion,
  ServicioTransaccionesService,
} from '../servicio-transacciones.service';

@Component({
  selector: 'app-tablero-transacciones',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    CardModule,
    ToolbarModule,
    TagModule,
    InputTextModule,
    ButtonModule,
    DialogModule,
    ChartModule,
    FormularioTransaccionComponent,
  ],
  templateUrl: './tablero-transacciones.component.html',
})
export class TableroTransaccionesComponent implements OnInit, OnDestroy {
  private servicio = inject(ServicioTransaccionesService);

  transacciones: RespuestaTransaccion[] = [];
  filtroGlobal = '';
  estadoTiempoReal: 'CONECTADO' | 'DESCONECTADO' = 'DESCONECTADO';

  datosGrafico: any;
  opcionesGrafico: any;

  minutosVentana = 60;

  mostrarModalRegistro = false;

  private coloresBarras = [
    '#42A5F5', // azul
    '#66BB6A', // verde
    '#FFA726', // naranja
    '#AB47BC', // morado
    '#FF7043', // rojo
    '#26C6DA', // cyan
    '#D4E157', // lima
    '#8D6E63', // café
  ];

  private suscripcionSse?: Subscription;

  ngOnInit(): void {
    this.cargarInicial();
    this.iniciarSse();
  }

  cargarInicial(): void {
    this.servicio.listarTransacciones().subscribe({
      next: (data) => {
        this.transacciones = data;
        this.inicializarGrafico();
      },
    });
  }

  iniciarSse(): void {
    this.estadoTiempoReal = 'CONECTADO';

    this.suscripcionSse = this.servicio
      .escucharTransaccionesTiempoReal()
      .subscribe({
        next: (t) => {
          this.transacciones = [t, ...this.transacciones];
          this.recalcularGrafico();
        },
        error: () => {
          this.estadoTiempoReal = 'DESCONECTADO';
        },
      });
  }

  private inicializarGrafico(): void {
    this.opcionesGrafico = {
      responsive: true,
      maintainAspectRatio: false,
      indexAxis: 'y',
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (context: any) => {
              const valor = Number(context.raw || 0);
              return `Monto total: ${valor.toLocaleString('es-CO')}`;
            },
          },
        },
      },
      scales: {
        x: {
          beginAtZero: true,
          ticks: {
            callback: (v: any) => Number(v).toLocaleString('es-CO'),
          },
        },
      },
    };

    this.recalcularGrafico();
  }

  private recalcularGrafico(): void {
    const ahora = new Date();
    const inicio = new Date(ahora.getTime() - this.minutosVentana * 60_000);

    const totalPorDescripcion = new Map<string, number>();

    for (const tr of this.transacciones) {
      const fecha = this.convertirFecha(tr.fechaRegistro);
      if (!fecha) continue;

      if (fecha < inicio || fecha > ahora) continue;

      const descripcion = (tr.descripcion ?? 'Sin descripción').trim();
      const monto = Number(tr.monto) || 0;

      totalPorDescripcion.set(
        descripcion,
        (totalPorDescripcion.get(descripcion) ?? 0) + monto
      );
    }

    const top = Array.from(totalPorDescripcion.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 8);

    const etiquetas = top.map(([desc]) => desc);
    const valores = top.map(([_, total]) => total);

    this.datosGrafico = {
      labels: etiquetas,
      datasets: [
        {
          label: `Monto total por descripción (últimos ${this.minutosVentana} min)`,
          data: valores,
          backgroundColor: etiquetas.map(
            (_, i) => this.coloresBarras[i % this.coloresBarras.length]
          ),
          borderRadius: 6,
        },
      ],
    };
  }

  private convertirFecha(valor: string): Date | null {
    if (!valor) return null;

    // Normaliza fracción de segundos a 3 dígitos (ms)
    // Ej: 2026-01-02T21:51:54.1402366 -> 2026-01-02T21:51:54.140
    const normalizado = valor.replace(/\.(\d{3})\d+/, '.$1');

    const d = new Date(normalizado);
    return isNaN(d.getTime()) ? null : d;
  }

  abrirModalRegistro(): void {
    this.mostrarModalRegistro = true;
  }

  cerrarModalRegistro(): void {
    this.mostrarModalRegistro = false;
  }

  reconectarTiempoReal(): void {
    this.suscripcionSse?.unsubscribe();
    this.iniciarSse();
  }

  ngOnDestroy(): void {
    this.suscripcionSse?.unsubscribe();
  }

  severidadEstado(): 'success' | 'danger' | 'warning' {
    return this.estadoTiempoReal === 'CONECTADO' ? 'success' : 'danger';
  }
}
