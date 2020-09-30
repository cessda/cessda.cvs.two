import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IAgency } from 'app/shared/model/agency.model';
import { AgencyService } from 'app/agency/agency.service';

@Component({
  selector: 'jhi-agency-detail',
  templateUrl: './agency-detail.component.html'
})
export class AgencyDetailComponent implements OnInit {
  agency: IAgency | null = null;
  agencyStatistic?: any;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute, protected agencyService: AgencyService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ agency }) => (this.agency = agency));
    this.agencyService.statistic(this.agency!.id!).subscribe(res => (this.agencyStatistic = res.body));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }
}
