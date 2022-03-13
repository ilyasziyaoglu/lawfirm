import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {ReferencesService} from '../service/references.service';
import {IReferences, References} from '../references.model';

import {ReferencesUpdateComponent} from './references-update.component';

describe('References Management Update Component', () => {
  let comp: ReferencesUpdateComponent;
  let fixture: ComponentFixture<ReferencesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let referencesService: ReferencesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ReferencesUpdateComponent],
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
      .overrideTemplate(ReferencesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReferencesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    referencesService = TestBed.inject(ReferencesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const references: IReferences = { id: 456 };

      activatedRoute.data = of({ references });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(references));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<References>>();
      const references = { id: 123 };
      jest.spyOn(referencesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ references });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: references }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(referencesService.update).toHaveBeenCalledWith(references);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<References>>();
      const references = new References();
      jest.spyOn(referencesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ references });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: references }));
      saveSubject.complete();

      // THEN
      expect(referencesService.create).toHaveBeenCalledWith(references);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<References>>();
      const references = { id: 123 };
      jest.spyOn(referencesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ references });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(referencesService.update).toHaveBeenCalledWith(references);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
