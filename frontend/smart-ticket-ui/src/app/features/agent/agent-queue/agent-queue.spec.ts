import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentQueue } from './agent-queue';

describe('AgentQueue', () => {
  let component: AgentQueue;
  let fixture: ComponentFixture<AgentQueue>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentQueue]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgentQueue);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
