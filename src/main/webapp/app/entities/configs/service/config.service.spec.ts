import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {Config, IConfig} from '../config.model';

import {ConfigService} from './config.service';

describe('Configs Service', () => {
  let service: ConfigService;
  let httpMock: HttpTestingController;
  let elemDefault: IConfig;
  let expectedResult: IConfig | IConfig[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ConfigService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      key: 'AAAAAAA',
      value: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Config', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Config()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Config', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          key: 'BBBBBB',
          value: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Config', () => {
      const patchObject = Object.assign(
        {
          key: 'BBBBBB',
        },
        new Config()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Configs', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          key: 'BBBBBB',
          value: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Config', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addConfigsToCollectionIfMissing', () => {
      it('should add a Config to an empty array', () => {
        const config: IConfig = { id: 123 };
        expectedResult = service.addConfigsToCollectionIfMissing([], config);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(config);
      });

      it('should not add a Config to an array that contains it', () => {
        const config: IConfig = { id: 123 };
        const configCollection: IConfig[] = [
          {
            ...config,
          },
          { id: 456 },
        ];
        expectedResult = service.addConfigsToCollectionIfMissing(configCollection, config);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Config to an array that doesn't contain it", () => {
        const config: IConfig = { id: 123 };
        const configCollection: IConfig[] = [{ id: 456 }];
        expectedResult = service.addConfigsToCollectionIfMissing(configCollection, config);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(config);
      });

      it('should add only unique Configs to an array', () => {
        const configArray: IConfig[] = [{ id: 123 }, { id: 456 }, { id: 84647 }];
        const configCollection: IConfig[] = [{ id: 123 }];
        expectedResult = service.addConfigsToCollectionIfMissing(configCollection, ...configArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const config: IConfig = { id: 123 };
        const config2: IConfig = { id: 456 };
        expectedResult = service.addConfigsToCollectionIfMissing([], config, config2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(config);
        expect(expectedResult).toContain(config2);
      });

      it('should accept null and undefined values', () => {
        const config: IConfig = { id: 123 };
        expectedResult = service.addConfigsToCollectionIfMissing([], null, config, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(config);
      });

      it('should return initial array if no Configs is added', () => {
        const configCollection: IConfig[] = [{ id: 123 }];
        expectedResult = service.addConfigsToCollectionIfMissing(configCollection, undefined, null);
        expect(expectedResult).toEqual(configCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
