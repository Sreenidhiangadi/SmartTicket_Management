import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerEscalations } from './manager-escalations';

describe('ManagerEscalations', () => {
  let component: ManagerEscalations;
  let fixture: ComponentFixture<ManagerEscalations>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerEscalations]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerEscalations);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
