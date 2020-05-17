import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IResolver } from 'app/shared/model/resolver.model';

@Component({
  selector: 'jhi-resolver-detail',
  templateUrl: './resolver-detail.component.html'
})
export class ResolverDetailComponent implements OnInit {
  resolver: IResolver | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resolver }) => (this.resolver = resolver));
  }

  previousState(): void {
    window.history.back();
  }
}
