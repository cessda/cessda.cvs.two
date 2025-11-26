/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
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
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription, Subject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { UserService } from 'app/core/user/user.service';
import { User } from 'app/core/user/user.model';
import { UserManagementDeleteDialogComponent } from './user-management-delete-dialog.component';
import { Agency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';
import { UserAgency } from 'app/shared/model/user-agency.model';

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.component.html',
  standalone: false,
})
export class UserManagementComponent implements OnInit, OnDestroy {
  private userService = inject(UserService);
  private accountService = inject(AccountService);
  private activatedRoute = inject(ActivatedRoute);
  private router = inject(Router);
  private modalService = inject(NgbModal);
  private agencyService = inject(AgencyService);

  private userListModification = new Subject<void>();
  private userListSubscription: Subscription | undefined;

  currentAccount: Account | null = null;
  users: User[] = [];
  totalItems = 0;
  readonly itemsPerPage = ITEMS_PER_PAGE;
  page = 1;
  predicate = 'id';
  ascending = true;

  agencies: Agency[] = [];

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));

    // Listen to query params for pagination and sorting
    this.activatedRoute.queryParamMap.subscribe(query => {
      const page = query.get('page');
      this.page = page ? Number.parseInt(page) : 1;

      const sort = query.get('sort');
      if (sort) {
        const [predicate, direction] = sort.split(',');
        this.predicate = predicate;
        this.ascending = direction === 'asc';
      } else {
        this.predicate = 'id';
        this.ascending = true;
      }

      this.loadUsers();
    });

    // Load agencies
    this.agencyService.query({ page: 0, size: 1000, sort: [] }).subscribe(res => {
      this.agencies = res.body || [];
    });

    this.userListSubscription = this.userListModification.subscribe(() => this.loadUsers());
  }

  ngOnDestroy(): void {
    this.userListSubscription?.unsubscribe();
  }

  getAgencyName(agencyId: number): string {
    const agency = this.agencies.find(a => a.id === agencyId);
    return agency ? agency.name! : `Unknown agency (${agencyId})`;
  }

  setActive(user: User, isActivated: boolean): void {
    this.userService.update({ ...user, activated: isActivated }).subscribe(() => this.loadUsers());
  }

  trackIdentity(_index: number, item: User): number | undefined {
    return item.id;
  }

  loadPage(page: number): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        page: page,
      },
      queryParamsHandling: 'merge',
    });
  }

  updateSort(): void {
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        sort: `${this.predicate},${this.ascending ? 'asc' : 'desc'}`,
      },
      queryParamsHandling: 'merge',
    });
    this.loadUsers();
  }

  deleteUser(user: User): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialogComponent, {
      size: 'lg',
      backdrop: 'static',
    });
    modalRef.componentInstance.user = user;
  }

  private loadUsers(): void {
    this.userService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(res => {
        const users = res.body || [];

        // Sort user agencies
        users.forEach(u => {
          if (u.userAgencies?.length) {
            u.userAgencies.sort((ua1, ua2) =>
              this.userAgencyToCompare(ua1) < this.userAgencyToCompare(ua2)
                ? -1
                : this.userAgencyToCompare(ua1) > this.userAgencyToCompare(ua2)
                  ? 1
                  : 0,
            );
          }
        });

        const totalCount = res.headers.get('X-Total-Count');
        this.totalItems = totalCount ? Number(totalCount) : users.length;
        this.users = users;
      });
  }

  private sort(): string[] {
    const result = [`${this.predicate},${this.ascending ? 'asc' : 'desc'}`];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  userAgencyToCompare(ua: UserAgency): string {
    return ua.agencyId! + ua.agencyRole! + (ua.language || '');
  }

  notifyUserListModification(): void {
    this.userListModification.next();
  }
}
