import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserAgency } from 'app/shared/model/user-agency.model';

@Component({
  selector: 'jhi-user-agency-detail',
  templateUrl: './user-agency-detail.component.html'
})
export class UserAgencyDetailComponent implements OnInit {
  userAgency: IUserAgency | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAgency }) => (this.userAgency = userAgency));
  }

  previousState(): void {
    window.history.back();
  }
}
