jest.mock('app/core/auth/account.service');

import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, RouterEvent, NavigationEnd, NavigationStart } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { Subject, of } from 'rxjs';
import { TranslateModule, TranslateService, LangChangeEvent } from '@ngx-translate/core';

import { AccountService } from 'app/core/auth/account.service';
import { FindLanguageFromKeyPipe } from 'app/shared/language/find-language-from-key.pipe';

import { MainComponent } from './main.component';

describe('MainComponent', () => {
  let comp: MainComponent;
  let fixture: ComponentFixture<MainComponent>;
  let titleService: Title;
  let translateService: TranslateService;
  let findLanguageFromKeyPipe: FindLanguageFromKeyPipe;
  let mockAccountService: AccountService;
  const routerEventsSubject = new Subject<RouterEvent>();
  const routerState: any = { snapshot: { root: { data: {} } } };
  class MockRouter {
    events = routerEventsSubject;
    routerState = routerState;
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [TranslateModule.forRoot()],
        declarations: [MainComponent],
        providers: [
          Title,
          FindLanguageFromKeyPipe,
          AccountService,
          {
            provide: Router,
            useClass: MockRouter,
          },
        ],
      })
        .overrideTemplate(MainComponent, '')
        .compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(MainComponent);
    comp = fixture.componentInstance;
    titleService = TestBed.inject(Title);
    translateService = TestBed.inject(TranslateService);
    findLanguageFromKeyPipe = TestBed.inject(FindLanguageFromKeyPipe);
    mockAccountService = TestBed.inject(AccountService);
    mockAccountService.identity = jest.fn(() => of(null));
    mockAccountService.getAuthenticationState = jest.fn(() => of(null));
  });

  describe('page title', () => {
    const defaultPageTitle = 'global.title';
    const parentRoutePageTitle = 'parentTitle';
    const childRoutePageTitle = 'childTitle';
    const navigationEnd = new NavigationEnd(1, '', '');
    const navigationStart = new NavigationStart(1, '');
    const langChangeEvent: LangChangeEvent = { lang: 'tr', translations: null };

    beforeEach(() => {
      routerState.snapshot.root = { data: {} };
      jest.spyOn(translateService, 'get').mockImplementation((key: string | string[]) => of(`${key as string} translated`));
      translateService.currentLang = 'tr';
      jest.spyOn(titleService, 'setTitle');
      comp.ngOnInit();
    });

    describe('navigation end', () => {
      it('should set page title to default title if pageTitle is missing on routes', () => {
        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(defaultPageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(defaultPageTitle + ' translated');
      });

      it('should set page title to root route pageTitle if there is no child routes', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };

        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(parentRoutePageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(parentRoutePageTitle + ' translated');
      });

      it('should set page title to child route pageTitle if child routes exist and pageTitle is set for child route', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };
        routerState.snapshot.root.firstChild = { data: { pageTitle: childRoutePageTitle } };

        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(childRoutePageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(childRoutePageTitle + ' translated');
      });

      it('should set page title to parent route pageTitle if child routes exists but pageTitle is not set for child route data', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };
        routerState.snapshot.root.firstChild = { data: {} };

        // WHEN
        routerEventsSubject.next(navigationEnd);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(parentRoutePageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(parentRoutePageTitle + ' translated');
      });
    });

    describe('navigation start', () => {
      it('should not set page title on navigation start', () => {
        // WHEN
        routerEventsSubject.next(navigationStart);

        // THEN
        expect(titleService.setTitle).not.toHaveBeenCalled();
      });
    });

    describe('language change', () => {
      it('should set page title to default title if pageTitle is missing on routes', () => {
        // WHEN
        translateService.onLangChange.emit(langChangeEvent);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(defaultPageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(defaultPageTitle + ' translated');
      });

      it('should set page title to root route pageTitle if there is no child routes', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };

        // WHEN
        translateService.onLangChange.emit(langChangeEvent);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(parentRoutePageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(parentRoutePageTitle + ' translated');
      });

      it('should set page title to child route pageTitle if child routes exist and pageTitle is set for child route', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };
        routerState.snapshot.root.firstChild = { data: { pageTitle: childRoutePageTitle } };

        // WHEN
        translateService.onLangChange.emit(langChangeEvent);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(childRoutePageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(childRoutePageTitle + ' translated');
      });

      it('should set page title to parent route pageTitle if child routes exists but pageTitle is not set for child route data', () => {
        // GIVEN
        routerState.snapshot.root.data = { pageTitle: parentRoutePageTitle };
        routerState.snapshot.root.firstChild = { data: {} };

        // WHEN
        translateService.onLangChange.emit(langChangeEvent);

        // THEN
        expect(translateService.get).toHaveBeenCalledWith(parentRoutePageTitle);
        expect(titleService.setTitle).toHaveBeenCalledWith(parentRoutePageTitle + ' translated');
      });
    });
  });

  describe('page language attribute', () => {
    it('should change page language attribute on language change', () => {
      // GIVEN
      comp.ngOnInit();

      // WHEN
      findLanguageFromKeyPipe.isRTL = jest.fn(() => false);
      translateService.onLangChange.emit({ lang: 'lang1', translations: null });

      // THEN
      expect(document.querySelector('html')?.getAttribute('lang')).toEqual('lang1');
      expect(document.querySelector('html')?.getAttribute('dir')).toEqual('ltr');

      // WHEN
      findLanguageFromKeyPipe.isRTL = jest.fn(() => true);
      translateService.onLangChange.emit({ lang: 'lang2', translations: null });

      // THEN
      expect(document.querySelector('html')?.getAttribute('lang')).toEqual('lang2');
      expect(document.querySelector('html')?.getAttribute('dir')).toEqual('rtl');
    });
  });
});
