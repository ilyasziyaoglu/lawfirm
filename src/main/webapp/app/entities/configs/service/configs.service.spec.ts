import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {Configs, IConfigs} from '../configs.model';

import {ConfigsService} from './configs.service';

describe('Configs Service', () => {
  let service: ConfigsService;
  let httpMock: HttpTestingController;
  let elemDefault: IConfigs;
  let expectedResult: IConfigs | IConfigs[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ConfigsService);
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

    it('should create a Configs', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Configs()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Configs', () => {
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

    it('should partial update a Configs', () => {
      const patchObject = Object.assign(
        {
          key: 'BBBBBB',
        },
        new Configs()
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

    it('should delete a Configs', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addConfigsToCollectionIfMissing', () => {
      it('should add a Configs to an empty array', () => {
        const configs: IConfigs = { id: 123 };
        expectedResult = service.addConfigsToCollectionIfMissing([], configs);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(configs);
      });

      it('should not add a Configs to an array that contains it', () => {
        const configs: IConfigs = { id: 123 };
        const configsCollection: IConfigs[] = [
          {
            ...configs,
          },
          { id: 456 },
        ];
        expectedResult = service.addConfigsToCollectionIfMissing(configsCollection, configs);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Configs to an array that doesn't contain it", () => {
        const configs: IConfigs = { id: 123 };
        const configsCollection: IConfigs[] = [{ id: 456 }];
        expectedResult = service.addConfigsToCollectionIfMissing(configsCollection, configs);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(configs);
      });

      it('should add only unique Configs to an array', () => {
        const configsArray: IConfigs[] = [{ id: 123 }, { id: 456 }, { id: 84647 }];
        const configsCollection: IConfigs[] = [{ id: 123 }];
        expectedResult = service.addConfigsToCollectionIfMissing(configsCollection, ...configsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const configs: IConfigs = { id: 123 };
        const configs2: IConfigs = { id: 456 };
        expectedResult = service.addConfigsToCollectionIfMissing([], configs, configs2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(configs);
        expect(expectedResult).toContain(configs2);
      });

      it('should accept null and undefined values', () => {
        const configs: IConfigs = { id: 123 };
        expectedResult = service.addConfigsToCollectionIfMissing([], null, configs, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(configs);
      });

      it('should return initial array if no Configs is added', () => {
        const configsCollection: IConfigs[] = [{ id: 123 }];
        expectedResult = service.addConfigsToCollectionIfMissing(configsCollection, undefined, null);
        expect(expectedResult).toEqual(configsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
