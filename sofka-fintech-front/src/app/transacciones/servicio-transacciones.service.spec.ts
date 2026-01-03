import { TestBed } from '@angular/core/testing';

import { ServicioTransaccionesService } from './servicio-transacciones.service';

describe('ServicioTransaccionesService', () => {
  let service: ServicioTransaccionesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServicioTransaccionesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
