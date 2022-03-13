import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {ConfigsDetailComponent} from './configs-detail.component';

describe('Configs Management Detail Component', () => {
  let comp: ConfigsDetailComponent;
  let fixture: ComponentFixture<ConfigsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfigsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ configs: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ConfigsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ConfigsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load configs on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.configs).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
