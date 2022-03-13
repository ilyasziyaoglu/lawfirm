import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {EmployeesService} from '../service/employees.service';
import {Employees, IEmployees} from '../employees.model';
import {IServicePoints} from 'app/entities/service-points/service-points.model';
import {ServicePointsService} from 'app/entities/service-points/service/service-points.service';
import {IServices} from 'app/entities/services/services.model';
import {ServicesService} from 'app/entities/services/service/services.service';

import {EmployeesUpdateComponent} from './employees-update.component';

describe('Employees Management Update Component', () => {
  let comp: EmployeesUpdateComponent;
  let fixture: ComponentFixture<EmployeesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let employeesService: EmployeesService;
  let servicePointsService: ServicePointsService;
  let servicesService: ServicesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EmployeesUpdateComponent],
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
      .overrideTemplate(EmployeesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmployeesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    employeesService = TestBed.inject(EmployeesService);
    servicePointsService = TestBed.inject(ServicePointsService);
    servicesService = TestBed.inject(ServicesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ServicePoints query and add missing value', () => {
      const employees: IEmployees = { id: 456 };
      const servicePoint: IServicePoints = { id: 408 };
      employees.servicePoint = servicePoint;

      const servicePointsCollection: IServicePoints[] = [{ id: 39007 }];
      jest.spyOn(servicePointsService, 'query').mockReturnValue(of(new HttpResponse({ body: servicePointsCollection })));
      const additionalServicePoints = [servicePoint];
      const expectedCollection: IServicePoints[] = [...additionalServicePoints, ...servicePointsCollection];
      jest.spyOn(servicePointsService, 'addServicePointsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      expect(servicePointsService.query).toHaveBeenCalled();
      expect(servicePointsService.addServicePointsToCollectionIfMissing).toHaveBeenCalledWith(
        servicePointsCollection,
        ...additionalServicePoints
      );
      expect(comp.servicePointsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Services query and add missing value', () => {
      const employees: IEmployees = { id: 456 };
      const services: IServices[] = [{ id: 77827 }];
      employees.services = services;

      const servicesCollection: IServices[] = [{ id: 63716 }];
      jest.spyOn(servicesService, 'query').mockReturnValue(of(new HttpResponse({ body: servicesCollection })));
      const additionalServices = [...services];
      const expectedCollection: IServices[] = [...additionalServices, ...servicesCollection];
      jest.spyOn(servicesService, 'addServicesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      expect(servicesService.query).toHaveBeenCalled();
      expect(servicesService.addServicesToCollectionIfMissing).toHaveBeenCalledWith(servicesCollection, ...additionalServices);
      expect(comp.servicesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const employees: IEmployees = { id: 456 };
      const servicePoint: IServicePoints = { id: 35955 };
      employees.servicePoint = servicePoint;
      const services: IServices = { id: 99355 };
      employees.services = [services];

      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(employees));
      expect(comp.servicePointsSharedCollection).toContain(servicePoint);
      expect(comp.servicesSharedCollection).toContain(services);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Employees>>();
      const employees = { id: 123 };
      jest.spyOn(employeesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: employees }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(employeesService.update).toHaveBeenCalledWith(employees);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Employees>>();
      const employees = new Employees();
      jest.spyOn(employeesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: employees }));
      saveSubject.complete();

      // THEN
      expect(employeesService.create).toHaveBeenCalledWith(employees);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Employees>>();
      const employees = { id: 123 };
      jest.spyOn(employeesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employees });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(employeesService.update).toHaveBeenCalledWith(employees);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackServicePointsById', () => {
      it('Should return tracked ServicePoints primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackServicePointsById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackServicesById', () => {
      it('Should return tracked Services primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackServicesById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedServices', () => {
      it('Should return option if no Services is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedServices(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Services for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedServices(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Services is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedServices(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
