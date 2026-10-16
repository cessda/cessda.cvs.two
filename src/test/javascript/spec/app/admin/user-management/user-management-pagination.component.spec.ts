/*
 * Copyright © 2017-2026 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { of, Subject } from 'rxjs';

import { CvsTestModule } from '../../../test.module';
import { QueryParamsNavigation } from '../../../helpers/query-params-navigation';
import { UserManagementComponent } from 'app/admin/user-management/user-management.component';
import { UserService } from 'app/core/user/user.service';
import { User } from 'app/core/user/user.model';
import { AgencyService } from 'app/agency/agency.service';
import { VocabularyLanguageFromKeyPipe } from 'app/shared/language/vocabulary-language-from-key.pipe';

/*
 * Renders the real template, so that the pagination control itself takes part (#819, #820).
 * Directives the list does not need for paging, such as the translations, are left unknown.
 */
describe('Component Tests', () => {
  describe('User Management Pagination', () => {
    let fixture: ComponentFixture<UserManagementComponent>;
    let navigation: QueryParamsNavigation;
    let query: jasmine.Spy;

    const usersPage = (total: number): HttpResponse<User[]> =>
      new HttpResponse({ body: [{ id: 1, login: 'jdoe' }], headers: new HttpHeaders({ 'X-Total-Count': String(total) }) });

    // the pagination reports a page change asynchronously, so a change it makes on its own only
    // shows once the fixture is stable
    const settle = async (): Promise<void> => {
      fixture.detectChanges();
      await fixture.whenStable();
      fixture.detectChanges();
    };

    const element = (): HTMLElement => fixture.nativeElement;
    const activePage = (): string | undefined => element().querySelector('ngb-pagination .page-item.active')?.textContent?.trim();
    const pageLink = (label: string): HTMLElement =>
      Array.from(element().querySelectorAll<HTMLElement>('ngb-pagination a.page-link')).find(a => a.textContent?.trim() === label)!;

    beforeEach(waitForAsync(() => {
      navigation = new QueryParamsNavigation();
      TestBed.configureTestingModule({
        imports: [CvsTestModule, NgbPaginationModule],
        declarations: [UserManagementComponent, VocabularyLanguageFromKeyPipe],
        providers: navigation.providers(),
        schemas: [NO_ERRORS_SCHEMA],
      }).compileComponents();
    }));

    beforeEach(() => {
      query = spyOn(TestBed.inject(UserService), 'query').and.returnValue(of(usersPage(100)));
      spyOn(TestBed.inject(AgencyService), 'query').and.returnValue(of(new HttpResponse({ body: [] })));
      fixture = TestBed.createComponent(UserManagementComponent);
    });

    it('should stay on the page in the URL while the users are still loading', async () => {
      const pending = new Subject<HttpResponse<User[]>>();
      query.and.returnValue(pending);
      navigation.open({ page: '3' });

      await settle();
      pending.next(usersPage(100));
      await settle();

      expect(navigation.navigate).not.toHaveBeenCalled();
      expect(navigation.current).toEqual({ page: '3' });
      expect(activePage()).toContain('3');
    });

    it('should go to the page clicked in the pagination', async () => {
      await settle();

      pageLink('2').click();
      await settle();

      expect(navigation.current).toEqual({ page: 2 });
      expect(query.calls.mostRecent().args[0].page).toBe(1);
      expect(activePage()).toContain('2');
    });

    it('should offer the last page and reach it', async () => {
      await settle();

      pageLink('»»').click();
      await settle();

      // 100 users at 30 a page
      expect(navigation.current).toEqual({ page: 4 });
      expect(activePage()).toContain('4');
    });

    it('should keep the page shown when the URL is opened again', async () => {
      navigation.open({ page: '2' });
      await settle();

      navigation.open({ page: '2' });
      await settle();

      expect(navigation.navigate).not.toHaveBeenCalled();
      expect(activePage()).toContain('2');
    });

    it('should leave the pagination out until there is a user to show', async () => {
      query.and.returnValue(of(usersPage(0)));

      await settle();

      expect(element().querySelector('ngb-pagination')).toBeNull();
    });

    it('should order the list by the column clicked', async () => {
      await settle();

      const loginHeader = Array.from(element().querySelectorAll<HTMLElement>('th')).find(th => th.textContent?.includes('Login'))!;
      loginHeader.click();
      await settle();

      expect(navigation.current).toEqual({ sort: 'login,desc' });
      expect(query.calls.mostRecent().args[0].sort).toEqual(['login,desc', 'id']);
    });
  });
});
