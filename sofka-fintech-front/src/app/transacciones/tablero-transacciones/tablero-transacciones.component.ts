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

import { GraficoDescripcionComponent } from '../grafico-descripcion/grafico-descripcion.component';




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
    FormularioTransaccionComponent,
    GraficoDescripcionComponent,
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

  private suscripcionSse?: Subscription;

  ngOnInit(): void {
    this.cargarInicial();
    this.iniciarSse();
  }

  cargarInicial(): void {
    this.servicio.listarTransacciones().subscribe({
      next: (data) => {
        this.transacciones = data;
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
        },
        error: () => {
          this.estadoTiempoReal = 'DESCONECTADO';
        },
      });
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
