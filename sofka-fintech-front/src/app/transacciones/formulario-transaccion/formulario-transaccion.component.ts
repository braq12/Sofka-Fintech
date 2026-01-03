import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';

import { MessageService } from 'primeng/api';
import { ServicioTransaccionesService } from '../servicio-transacciones.service';

@Component({
  selector: 'app-formulario-transaccion',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputNumberModule,
    ButtonModule,
    DividerModule,
    InputTextModule,
  ],
  templateUrl: './formulario-transaccion.component.html',
})
export class FormularioTransaccionComponent {
  @Output() finalizarModal = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private servicio = inject(ServicioTransaccionesService);
  private messageService = inject(MessageService);

  cargando = false;

  formulario = this.fb.group({
    monto: [null as number | null, [Validators.required, Validators.min(0)]],
    descripcion: [null as string | null, [Validators.required, Validators.maxLength(50),Validators.minLength(5)]],
  });

  registrar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.messageService.add({
        severity: 'warn',
        summary: 'Validación',
        detail: 'Formulario invalido',
      });
      return;
    }

    const monto = this.formulario.value.monto as number;
    const descripcion = this.formulario.value.descripcion as string;

    this.cargando = true;
    this.servicio.registrarTransaccion({ monto,descripcion}).subscribe({
      next: () => {
        this.cargando = false;
        this.formulario.reset();

        this.messageService.add({
          severity: 'success',
          summary: 'Registro',
          detail: 'Transacción registrada',
        });
        this.finalizarModal.emit();
      },
      error: () => {
        this.cargando = false;
        this.finalizarModal.emit();
      },
    });
  }

cancelar(): void {
    this.finalizarModal.emit();
  }
  get monto() {
    return this.formulario.get('monto');
  }

  get descripcion(){
    return this.formulario.get('descripcion');
  }

}
