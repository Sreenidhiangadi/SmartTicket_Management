import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEscalation } from './admin-escalation';

describe('AdminEscalation', () => {
  let component: AdminEscalation;
  let fixture: ComponentFixture<AdminEscalation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminEscalation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminEscalation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
