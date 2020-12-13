import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Subscription} from 'rxjs';
import {flatMap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {JhiEventManager} from 'ng-jhipster';

import {ITEMS_PER_PAGE} from 'app/shared/constants/pagination.constants';
import {AccountService} from 'app/core/auth/account.service';
import {Account} from 'app/core/user/account.model';
import {UserService} from 'app/core/user/user.service';
import {User} from 'app/core/user/user.model';
import {UserManagementDeleteDialogComponent} from './user-management-delete-dialog.component';
import {IAgency} from 'app/shared/model/agency.model';
import {AgencyService} from 'app/agency/agency.service';
import {IUserAgency} from 'app/shared/model/user-agency.model';
import VocabularyUtil from 'app/shared/util/vocabulary-util';

@Component({
  selector: 'jhi-user-mgmt',
  templateUrl: './user-management.component.html'
})
export class UserManagementComponent implements OnInit, OnDestroy {
  currentAccount: Account | null = null;
  users: User[] | null = null;
  userListSubscription?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  previousPage!: number;
  ascending!: boolean;

  agencies?: IAgency[];

  constructor(
    private userService: UserService,
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private eventManager: JhiEventManager,
    private modalService: NgbModal,
    private agencyService: AgencyService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data
      .pipe(
        flatMap(
          () => this.accountService.identity(),
          (data, account) => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.ascending = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
            this.currentAccount = account;
            this.loadAll();
            this.userListSubscription = this.eventManager.subscribe('userListModification', () => this.loadAll());
          }
        )
      )
      .subscribe();

    this.agencyService
      .query({
        page: 0,
        size: 1000,
        sort: ['name,asc']
      })
      .subscribe((res: HttpResponse<IAgency[]>) => {
        this.agencies = res.body!;
      });
  }

  ngOnDestroy(): void {
    if (this.userListSubscription) {
      this.eventManager.destroy(this.userListSubscription);
    }
  }

  getAgencyName(agencyId: number): string {
    return this.agencies!.filter(a => a.id === agencyId)[0].name!;
  }

  setActive(user: User, isActivated: boolean): void {
    this.userService.update({ ...user, activated: isActivated }).subscribe(() => this.loadAll());
  }

  trackIdentity(index: number, item: User): any {
    return item.id;
  }

  loadPage(page: number): void {
    if (page !== this.previousPage) {
      this.previousPage = page;
      this.transition();
    }
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc')
      }
    });
    this.loadAll();
  }

  deleteUser(user: User): void {
    const modalRef = this.modalService.open(UserManagementDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.user = user;
  }

  private loadAll(): void {
    this.userService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<User[]>) => this.onSuccess(res.body, res.headers));
  }

  private sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  private onSuccess(users: User[] | null, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.users = users;
    this.users!.forEach(u => {
      if (u.userAgencies && u.userAgencies.length > 0) {
        u.userAgencies.sort((ua1, ua2) =>
          this.userAgencyToCompare(ua1) < this.userAgencyToCompare(ua2)
            ? -1
            : this.userAgencyToCompare(ua1) > this.userAgencyToCompare(ua2)
            ? 1
            : 0
        );
      }
    });
  }

  userAgencyToCompare(ua: IUserAgency): string {
    return ua.agencyId! + ua.agencyRole! + (ua.language ? ua.language : '');
  }

  getLangIsoFormatted(langIso: string): string {
    return VocabularyUtil.getLangIsoFormatted(langIso);
  }
}
