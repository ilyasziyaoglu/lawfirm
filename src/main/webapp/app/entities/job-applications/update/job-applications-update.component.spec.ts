import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {from, of, Subject} from 'rxjs';

import {JobApplicationsService} from '../service/job-applications.service';
import {IJobApplications, JobApplications} from '../job-applications.model';

import {JobApplicationsUpdateComponent} from './job-applications-update.component';

describe('JobApplications Management Update Component', () => {
  let comp: JobApplicationsUpdateComponent;
  let fixture: ComponentFixture<JobApplicationsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let jobApplicationsService: JobApplicationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [JobApplicationsUpdateComponent],
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
      .overrideTemplate(JobApplicationsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JobApplicationsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    jobApplicationsService = TestBed.inject(JobApplicationsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const jobApplications: IJobApplications = { id: 456 };

      activatedRoute.data = of({ jobApplications });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(jobApplications));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<JobApplications>>();
      const jobApplications = { id: 123 };
      jest.spyOn(jobApplicationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobApplications });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobApplications }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(jobApplicationsService.update).toHaveBeenCalledWith(jobApplications);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<JobApplications>>();
      const jobApplications = new JobApplications();
      jest.spyOn(jobApplicationsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobApplications });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobApplications }));
      saveSubject.complete();

      // THEN
      expect(jobApplicationsService.create).toHaveBeenCalledWith(jobApplications);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<JobApplications>>();
      const jobApplications = { id: 123 };
      jest.spyOn(jobApplicationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobApplications });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(jobApplicationsService.update).toHaveBeenCalledWith(jobApplications);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
