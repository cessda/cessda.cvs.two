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
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Licence } from 'app/shared/model/licence.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { LicenceService } from './licence.service';
import { LicenceDeleteDialogComponent } from './licence-delete-dialog.component';

@Component({
  selector: 'jhi-licence',
  templateUrl: './licence.component.html',
  standalone: false,
})
export class LicenceComponent implements OnInit, OnDestroy {
  protected licenceService = inject(LicenceService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  protected eventManager = inject(JhiEventManager);
  protected modalService = inject(NgbModal);

  licences: Licence[] = [];
  eventSubscriber?: Subscription;
  currentSearch: string;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor() {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadPage(page?: number): void {
    const pageToLoad: number = page || this.page;

    if (this.currentSearch) {
      this.licenceService
        .search({
          page: pageToLoad - 1,
          query: this.currentSearch,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe(
          (res: HttpResponse<Licence[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
          () => this.onError(),
        );
      return;
    }

    this.licenceService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<Licence[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
        () => this.onError(),
      );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadPage(1);
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(data => {
      this.page = data.pagingParams.page;
      this.ascending = data.pagingParams.ascending;
      this.predicate = data.pagingParams.predicate;
      this.ngbPaginationPage = data.pagingParams.page;
      this.loadPage();
    });
    this.registerChangeInLicences();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: Licence): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInLicences(): void {
    this.eventSubscriber = this.eventManager.subscribe('licenceListModification', () => this.loadPage());
  }

  delete(licence: Licence): void {
    const modalRef = this.modalService.open(LicenceDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.licence = licence;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: Licence[] | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.ngbPaginationPage = this.page;
    this.licences = data || [];
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page;
  }
}
