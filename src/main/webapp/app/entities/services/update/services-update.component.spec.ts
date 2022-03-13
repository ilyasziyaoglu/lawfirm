import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {ServicesService} from '../service/services.service';
import {IServices, Services} from '../services.model';

import {ServicesUpdateComponent} from './services-update.component';

describe('Services Management Update Component', () => {
  let comp: ServicesUpdateComponent;
  let fixture: ComponentFixture<ServicesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let servicesService: ServicesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ServicesUpdateComponent],
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
      .overrideTemplate(ServicesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ServicesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    servicesService = TestBed.inject(ServicesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const services: IServices = { id: 456 };

      activatedRoute.data = of({ services });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(services));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Services>>();
      const services = { id: 123 };
      jest.spyOn(servicesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ services });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: services }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(servicesService.update).toHaveBeenCalledWith(services);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Services>>();
      const services = new Services();
      jest.spyOn(servicesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ services });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: services }));
      saveSubject.complete();

      // THEN
      expect(servicesService.create).toHaveBeenCalledWith(services);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Services>>();
      const services = { id: 123 };
      jest.spyOn(servicesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ services });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(servicesService.update).toHaveBeenCalledWith(services);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
