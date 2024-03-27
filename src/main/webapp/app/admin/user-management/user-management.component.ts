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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiPaginationUtil } from 'ng-jhipster';

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
})
export class UserManagementComponent implements OnInit, OnDestroy {
  currentAccount: Account | null = null;
  users: User[] = [];
  userListSubscription: Subscription | undefined;
  totalItems = 0;
  readonly itemsPerPage = ITEMS_PER_PAGE;
  page = 1;
  predicate = 'id';
  ascending = true;

  agencies: Agency[] = [];

  constructor(
    private userService: UserService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private eventManager: JhiEventManager,
    private modalService: NgbModal,
    private agencyService: AgencyService,
    private paginationUtil: JhiPaginationUtil,
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.currentAccount = account));

    this.activatedRoute.queryParamMap.subscribe(query => {
      const page = query.get('page');
      if (page) {
        this.page = Number.parseInt(page);
      } else {
        this.page = 1;
      }

      const sort = query.get('sort');
      if (sort) {
        this.predicate = this.paginationUtil.parsePredicate(sort);
        this.ascending = this.paginationUtil.parseAscending(sort);
      } else {
        this.predicate = 'id';
        this.ascending = true;
      }

      this.loadUsers();
    });

    this.agencyService
      .query({
        page: 0,
        size: 1000,
      })
      .subscribe(res => (this.agencies = res.body || []));

    this.userListSubscription = this.eventManager.subscribe('userListModification', () => this.loadUsers());
  }

  ngOnDestroy(): void {
    if (this.userListSubscription) {
      this.eventManager.destroy(this.userListSubscription);
    }
  }

  getAgencyName(agencyId: number): string {
    for (let i = 0; i < this.agencies.length; i++) {
      const agency = this.agencies[i];
      if (agency.id === agencyId) {
        return agency.name!;
      }
    }
    return `Unknown agency (${agencyId})`;
  }

  setActive(user: User, isActivated: boolean): void {
    this.userService.update({ ...user, activated: isActivated }).subscribe(() => this.loadUsers());
  }

  trackIdentity(_index: number, item: User): any {
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
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc'),
      },
      queryParamsHandling: 'merge',
    });
    this.loadUsers();
  }

  deleteUser(user: User): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.user = user;
  }

  private loadUsers(): void {
    const userObservable = this.userService.query({
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
    });

    userObservable.subscribe(res => {
      const users = res.body || [];

      // Sort user agencies
      users.forEach(u => {
        if (u.userAgencies && u.userAgencies.length > 0) {
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
      if (totalCount) {
        this.totalItems = Number.parseInt(totalCount);
      } else {
        this.totalItems = users.length;
      }
      this.users = users;
    });
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  userAgencyToCompare(ua: UserAgency): string {
    return ua.agencyId! + ua.agencyRole! + (ua.language ? ua.language : '');
  }
}
