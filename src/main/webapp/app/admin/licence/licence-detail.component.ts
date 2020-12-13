import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {ILicence} from 'app/shared/model/licence.model';

@Component({
  selector: 'jhi-licence-detail',
  templateUrl: './licence-detail.component.html'
})
export class LicenceDetailComponent implements OnInit {
  licence: ILicence | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licence }) => (this.licence = licence));
  }

  previousState(): void {
    window.history.back();
  }
}
