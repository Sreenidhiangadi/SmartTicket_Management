import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerTicketList } from './manager-ticket-list';

describe('ManagerTicketList', () => {
  let component: ManagerTicketList;
  let fixture: ComponentFixture<ManagerTicketList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerTicketList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerTicketList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
