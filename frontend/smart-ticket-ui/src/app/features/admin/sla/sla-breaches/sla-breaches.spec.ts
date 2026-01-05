import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SlaBreaches } from './sla-breaches';

describe('SlaBreaches', () => {
  let component: SlaBreaches;
  let fixture: ComponentFixture<SlaBreaches>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SlaBreaches]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SlaBreaches);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
