import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const interceptorError: HttpInterceptorFn = (req, next) => {
  const messageService = inject(MessageService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const mensaje = obtenerMensaje(error);

      messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: mensaje,
      });

      return throwError(() => new Error(mensaje));
    })
  );
};

function obtenerMensaje(error: HttpErrorResponse): string {
  const cuerpo = error.error;

  if (cuerpo && typeof cuerpo === 'object') {
    const mensaje = (cuerpo as any).mensaje;
    const detalles = (cuerpo as any).detalles;

    if (mensaje) {
      if (Array.isArray(detalles) && detalles.length > 0) {
        return `${mensaje}. ${detalles.join(', ')}`;
      }
      return mensaje;
    }
  }

  if (error.status === 0) {
    return 'No fue posible conectarse al servidor';
  }

  if (error.status === 404) {
    return 'Recurso no encontrado';
  }

  return 'Ocurrió un error al procesar la solicitud';
}
