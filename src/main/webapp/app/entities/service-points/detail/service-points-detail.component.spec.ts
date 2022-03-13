import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {ServicePointsDetailComponent} from './service-points-detail.component';

describe('ServicePoints Management Detail Component', () => {
  let comp: ServicePointsDetailComponent;
  let fixture: ComponentFixture<ServicePointsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServicePointsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ servicePoints: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ServicePointsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ServicePointsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load servicePoints on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.servicePoints).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
