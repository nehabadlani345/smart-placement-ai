import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Roadmap } from './roadmap';

describe('Roadmap', () => {
  let component: Roadmap;
  let fixture: ComponentFixture<Roadmap>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Roadmap]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Roadmap);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
