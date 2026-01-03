import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableroTransaccionesComponent } from './tablero-transacciones.component';

describe('TableroTransaccionesComponent', () => {
  let component: TableroTransaccionesComponent;
  let fixture: ComponentFixture<TableroTransaccionesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableroTransaccionesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TableroTransaccionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
