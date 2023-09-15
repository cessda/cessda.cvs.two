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
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAgency } from 'app/shared/model/agency.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { AgencyService } from './agency.service';
import { AgencyDeleteDialogComponent } from './agency-delete-dialog.component';

@Component({
  selector: 'jhi-agency',
  templateUrl: './agency.component.html',
})
export class AgencyComponent implements OnInit, OnDestroy {
  agencies: IAgency[] = [];
  eventSubscriber?: Subscription;
  currentSearch: string;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected agencyService: AgencyService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: JhiDataUtils,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadPage(page?: number): void {
    const pageToLoad: number = page || this.page;

    if (this.currentSearch) {
      this.agencyService
        .search({
          page: pageToLoad - 1,
          query: this.currentSearch,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe(
          (res: HttpResponse<IAgency[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
          () => this.onError(),
        );
      return;
    }

    this.agencyService
      .query({
        page: 0,
        size: 200,
        sort: ['name,asc'],
      })
      .subscribe(
        (res: HttpResponse<IAgency[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
        () => this.onError(),
      );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadPage(1);
  }

  urlCleaner(url: string): string {
    return url.replace(/^(?:https?:\/\/)?(?:www\.)?/i, '').split('/')[0];
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(data => {
      this.page = data.pagingParams.page;
      this.ascending = data.pagingParams.ascending;
      this.predicate = data.pagingParams.predicate;
      this.ngbPaginationPage = data.pagingParams.page;
      this.loadPage();
    });
    this.registerChangeInAgencies();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(_index: number, item: IAgency): number {
    return item.id || -1;
  }

  registerChangeInAgencies(): void {
    this.eventSubscriber = this.eventManager.subscribe('agencyListModification', () => this.loadPage());
  }

  delete(agency: IAgency): void {
    const modalRef = this.modalService.open(AgencyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.agency = agency;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: IAgency[] | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.ngbPaginationPage = this.page;
    this.agencies = data || [];
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page;
  }
}
