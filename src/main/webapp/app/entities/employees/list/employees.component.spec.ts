import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';

import {EmployeesService} from '../service/employees.service';

import {EmployeesComponent} from './employees.component';

describe('Employees Management Component', () => {
  let comp: EmployeesComponent;
  let fixture: ComponentFixture<EmployeesComponent>;
  let service: EmployeesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [EmployeesComponent],
    })
      .overrideTemplate(EmployeesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmployeesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EmployeesService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.employees?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
