import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {ServicePointsService} from '../service/service-points.service';
import {IServicePoints, ServicePoints} from '../service-points.model';

import {ServicePointsUpdateComponent} from './service-points-update.component';

describe('ServicePoints Management Update Component', () => {
  let comp: ServicePointsUpdateComponent;
  let fixture: ComponentFixture<ServicePointsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let servicePointsService: ServicePointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ServicePointsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ServicePointsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ServicePointsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    servicePointsService = TestBed.inject(ServicePointsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const servicePoints: IServicePoints = { id: 456 };

      activatedRoute.data = of({ servicePoints });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(servicePoints));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ServicePoints>>();
      const servicePoints = { id: 123 };
      jest.spyOn(servicePointsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicePoints });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: servicePoints }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(servicePointsService.update).toHaveBeenCalledWith(servicePoints);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ServicePoints>>();
      const servicePoints = new ServicePoints();
      jest.spyOn(servicePointsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicePoints });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: servicePoints }));
      saveSubject.complete();

      // THEN
      expect(servicePointsService.create).toHaveBeenCalledWith(servicePoints);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ServicePoints>>();
      const servicePoints = { id: 123 };
      jest.spyOn(servicePointsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ servicePoints });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(servicePointsService.update).toHaveBeenCalledWith(servicePoints);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
