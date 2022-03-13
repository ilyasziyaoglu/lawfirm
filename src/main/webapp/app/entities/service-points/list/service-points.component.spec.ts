import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';

import {ServicePointsService} from '../service/service-points.service';

import {ServicePointsComponent} from './service-points.component';

describe('ServicePoints Management Component', () => {
  let comp: ServicePointsComponent;
  let fixture: ComponentFixture<ServicePointsComponent>;
  let service: ServicePointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ServicePointsComponent],
    })
      .overrideTemplate(ServicePointsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ServicePointsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ServicePointsService);

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
    expect(comp.servicePoints?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
