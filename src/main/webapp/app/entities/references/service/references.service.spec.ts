import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {IReferences, References} from '../references.model';

import {ReferencesService} from './references.service';

describe('References Service', () => {
  let service: ReferencesService;
  let httpMock: HttpTestingController;
  let elemDefault: IReferences;
  let expectedResult: IReferences | IReferences[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ReferencesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      order: 0,
      imageContentType: 'image/png',
      image: 'AAAAAAA',
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

    it('should create a References', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new References()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a References', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          order: 1,
          image: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a References', () => {
      const patchObject = Object.assign(
        {
          order: 1,
        },
        new References()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of References', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          order: 1,
          image: 'BBBBBB',
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

    it('should delete a References', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addReferencesToCollectionIfMissing', () => {
      it('should add a References to an empty array', () => {
        const references: IReferences = { id: 123 };
        expectedResult = service.addReferencesToCollectionIfMissing([], references);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(references);
      });

      it('should not add a References to an array that contains it', () => {
        const references: IReferences = { id: 123 };
        const referencesCollection: IReferences[] = [
          {
            ...references,
          },
          { id: 456 },
        ];
        expectedResult = service.addReferencesToCollectionIfMissing(referencesCollection, references);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a References to an array that doesn't contain it", () => {
        const references: IReferences = { id: 123 };
        const referencesCollection: IReferences[] = [{ id: 456 }];
        expectedResult = service.addReferencesToCollectionIfMissing(referencesCollection, references);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(references);
      });

      it('should add only unique References to an array', () => {
        const referencesArray: IReferences[] = [{ id: 123 }, { id: 456 }, { id: 51662 }];
        const referencesCollection: IReferences[] = [{ id: 123 }];
        expectedResult = service.addReferencesToCollectionIfMissing(referencesCollection, ...referencesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const references: IReferences = { id: 123 };
        const references2: IReferences = { id: 456 };
        expectedResult = service.addReferencesToCollectionIfMissing([], references, references2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(references);
        expect(expectedResult).toContain(references2);
      });

      it('should accept null and undefined values', () => {
        const references: IReferences = { id: 123 };
        expectedResult = service.addReferencesToCollectionIfMissing([], null, references, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(references);
      });

      it('should return initial array if no References is added', () => {
        const referencesCollection: IReferences[] = [{ id: 123 }];
        expectedResult = service.addReferencesToCollectionIfMissing(referencesCollection, undefined, null);
        expect(expectedResult).toEqual(referencesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
