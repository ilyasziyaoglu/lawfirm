import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {ConfigsService} from '../service/configs.service';
import {Configs, IConfigs} from '../configs.model';

import {ConfigsUpdateComponent} from './configs-update.component';

describe('Configs Management Update Component', () => {
  let comp: ConfigsUpdateComponent;
  let fixture: ComponentFixture<ConfigsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let configsService: ConfigsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ConfigsUpdateComponent],
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
      .overrideTemplate(ConfigsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConfigsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    configsService = TestBed.inject(ConfigsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const configs: IConfigs = { id: 456 };

      activatedRoute.data = of({ configs });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(configs));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Configs>>();
      const configs = { id: 123 };
      jest.spyOn(configsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ configs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: configs }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(configsService.update).toHaveBeenCalledWith(configs);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Configs>>();
      const configs = new Configs();
      jest.spyOn(configsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ configs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: configs }));
      saveSubject.complete();

      // THEN
      expect(configsService.create).toHaveBeenCalledWith(configs);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Configs>>();
      const configs = { id: 123 };
      jest.spyOn(configsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ configs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(configsService.update).toHaveBeenCalledWith(configs);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
