import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SolicitudCrearTransaccion {
  monto: number;
  descripcion: string;
}

export interface RespuestaTransaccion {
  idTransaccion: number;
  monto: number;
  comision: number;
  fechaRegistro: string;
  descripcion: string;
}

@Injectable({
  providedIn: 'root',
})
export class ServicioTransaccionesService {
  private readonly baseUrl = `${environment.apiBase}/transacciones`;

  constructor(private http: HttpClient, private zona: NgZone) {}

  listarTransacciones(): Observable<RespuestaTransaccion[]> {
    return this.http.get<RespuestaTransaccion[]>(this.baseUrl);
  }

  registrarTransaccion(
    solicitud: SolicitudCrearTransaccion
  ): Observable<RespuestaTransaccion> {
    return this.http.post<RespuestaTransaccion>(this.baseUrl, solicitud);
  }

  escucharTransaccionesTiempoReal(): Observable<RespuestaTransaccion> {
    const sujeto = new Subject<RespuestaTransaccion>();
    const urlStream = `${this.baseUrl}/stream`;

    const fuente = new EventSource(urlStream);

    fuente.addEventListener(
      'transaccion_registrada',
      (evento: MessageEvent) => {
        this.zona.run(() => {
          const data = JSON.parse(evento.data) as RespuestaTransaccion;
          sujeto.next(data);
        });
      }
    );

    fuente.onerror = () => {
      this.zona.run(() => {
        sujeto.error('Error en la conexión de tiempo real');
        fuente.close();
      });
    };

    return sujeto.asObservable();
  }
}
